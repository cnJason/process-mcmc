package com.cyou.fz.mcms.process.core;

import com.com.cyou.fz.mcms.process.system.Constants;
import com.cyou.fz.mcms.process.bean.ArticleDTO;
import com.cyou.fz.mcms.process.bean.ContentDTO;
import com.cyou.fz.mcms.process.bean.M3u8DTO;
import com.cyou.fz.mcms.process.bean.VideoInfoDTO;
import com.cyou.fz.mcms.process.utils.SecurityUtil;

import mjson.Json;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;


/**
 * Created by cnJason on 2016/11/21.
 */
public class ArticleProcess  implements IProcess{

    private Logger logger = LogManager.getRootLogger();


    /**
     * 主方法实现.
     * @param articleDTO 输入待清洗的文章对象
     * @return 输出清洗后的文章对象.
     */
    public ContentDTO process(ContentDTO articleDTO) {
        // 获取文章内容.
        ArticleDTO dto = new ArticleDTO();
        if(articleDTO instanceof  ArticleDTO){

            dto = (ArticleDTO)articleDTO;
            String content = dto.getContent();
            // 过滤敏感词.
            content = filterSensitiveWord(content);
            // 内容过滤.
            content = processContent(content);
            dto.setContent(content);
            //替换图片信息且获得picList.
            processPicList(dto); //替换图片且获得vlogPicList
            return  processVlogPicList(dto);
        }else {
            return dto;
        }

    }

    private ArticleDTO processVlogPicList(ArticleDTO articleDTO) {

        String content = articleDTO.getContent();
        StringBuilder sb = new StringBuilder().append(content);
        StringBuffer sb2 = new StringBuffer();
        StringBuffer checkM3u8str = new StringBuffer();

        //初始化vlog正则表达式.
        checkM3u8str.append("<[eE][mM][bB][eE][dD] [^>]*?src=['\"]?[^> ]+Flvid=(\\d+)[^>]*>(.*</[eE][mM][bB][eE][dD]>)?");
        checkM3u8str
                .append("|<[eE][mM][bB][eE][dD] [^>]*?src=['\"]?http://17173.tv.sohu.com/(\\d+)/[^>]*>(.*</[eE][mM][bB][eE][dD]>)?");
        checkM3u8str
                .append("|<[eE][mM][bB][eE][dD] [^>]*?src=['\"]?http://v.17173.com/(\\d+)/[^>]*>(.*</[eE][mM][bB][eE][dD]>)?");
        checkM3u8str
                .append("|<[eE][mM][bB][eE][dD] [^>]*?src=['\"]?http://f.v.17173cdn.com.*?/([a-zA-Z0-9=]+)\\.swf[^>]*>(.*</[eE][mM][bB][eE][dD]>)?");
        checkM3u8str
                .append("|<[iI][fF][rR][aA][mM][eE] [^>]*?src=['\"]?http://17173.tv.sohu.com/player_ifrm/([a-zA-Z0-9=]+)\\.html[^>]*>(.*</[iI][fF][rR][aA][mM][eE]>)?");
        checkM3u8str
                .append("|<[iI][fF][rR][aA][mM][eE] [^>]*?src=['\"]?http://v.17173.com/player_ifrm/([a-zA-Z0-9=]+)\\.html[^>]*>(.*</[iI][fF][rR][aA][mM][eE]>)?");
        checkM3u8str
                .append("|<[eE][mM][bB][eE][dD] [^>]*?src=['\"]?http://v.17173.com/live/playerVideo/PreloaderFileCustomer.swf.*?url=['\"]?http://v.17173.com.*?/([a-zA-Z0-9=]+)\\.html[^>]*>(.*</[eE][mM][bB][eE][dD]>)?");
        checkM3u8str.append("|<[eE][mM][bB][eE][dD][^>]*>(.*</[eE][mM][bB][eE][dD]>)?");
        java.util.regex.Pattern pm3u8 = java.util.regex.Pattern.compile(checkM3u8str.toString());
        java.util.regex.Matcher mm3u8 = pm3u8.matcher(sb.toString());
        List<VideoInfoDTO> vpicList = new ArrayList<VideoInfoDTO>();
        while (mm3u8.find()) {

            VideoInfoDTO videoInfoDTO = new VideoInfoDTO();
            String videoId = "";
            //获取videoId.
            try {
                videoId = fetchVideoId(mm3u8);
            } catch (Exception e) {
                logger.info("no find videoInfo by videoId " + videoId);
                //如果没找到的话。会被删除掉.
                mm3u8.appendReplacement(sb2, "");
                continue;
            }

            if (StringUtils.isNotBlank(videoId) && StringUtils.isNumeric(videoId)) {
                Json response = Json.read(Constants.VLOG_VIDEO_INFO_URL + videoId);
                if (StringUtils.isNotBlank(response.toString()) && response.has("data")) {
                    Json vlogData = response.at("data");

                    String videoPic = vlogData.at("pic_small").asString();
                    videoInfoDTO.setSmallPic(videoPic);

                    List<Json> m3u8s = vlogData.at("m3u8").asJsonList();
                    List<M3u8DTO> m3u8DTOList = new ArrayList<M3u8DTO>();
                    //TODO:判断是否在sohucdn上
                       /* if (!videoPushSohuCdn(videoId)) {
                            if (StringUtils.isNotBlank(newsBo.getNewsKey())) {
                                memcacheTemplate.set(Consts.JOB_NEWS_MEMCACHE_KEY.SAVE_NEWS_ERROR_KEY.getValue() + newsBo.getNewsKey(), newsBo.getNewsKey(), 0);
                            }
                            return null;
                        }*/

                    for (Json m3u8 : m3u8s) {
                        M3u8DTO m3u8DTO = new M3u8DTO();
                        m3u8DTO.setUrl(m3u8.at("url").asString());
                        m3u8DTO.setQuality(m3u8.at("quality").asInteger());
                        m3u8DTOList.add(m3u8DTO);
                    }

                    videoInfoDTO.setM3u8s(m3u8DTOList);

                    if (StringUtils.isBlank(videoPic) || m3u8DTOList.size() <= 0) {
                        //如果没找到的话。会被删除掉.
                        mm3u8.appendReplacement(sb2, "");
                    } else {
                        final String[] qualityString = {""};
                        //设置小图.
                        m3u8DTOList.stream().forEach(m3u8DTO -> {
                            qualityString[0] += m3u8DTO.getQuality() + ",";
                        });
                        String videoInfo = "<p align=center><a id=\"" + qualityString[0] + "\" href=\"" + m3u8DTOList.get(0).getUrl()
                                + "\"><img src=\"" + videoPic + "\" /></a></p>";
                        mm3u8.appendReplacement(sb2, videoInfo);

                    }
                } else {
                    logger.info("no find videoInfo by videoId " + videoId);
                    //如果没找到的话。会被删除掉.
                    mm3u8.appendReplacement(sb2, "");
                }
            } else {
                logger.info("no find allowed videoId from " + mm3u8.group(0));
                //TODO:删除缓存.
                   /* if (StringUtils.isNotBlank(newsBo.getNewsKey())) {
                        memcacheTemplate.set(Consts.JOB_NEWS_MEMCACHE_KEY.SAVE_NEWS_ERROR_KEY.getValue() + newsBo.getNewsKey(), newsBo.getNewsKey(), 0);
                    }*/
            }
        }

        mm3u8.appendTail(sb2);
        String newC = sb2.toString().replaceAll(
                "<[iI][fF][rR][aA][mM][eE][^>]?((?!(youtube).com)[^>])*>[^([iI][fF][rR][aA][mM][eE])]*?[iI][fF][rR][aA][mM][eE]>", "");//<[iI][fF][rR][aA][mM][eE][^>]*?>[^([iI][fF][rR][aA][mM][eE])]*?[iI][fF][rR][aA][mM][eE]>
        if (newC.length() < 50) {
            logger.error("get newsId : " + articleDTO.getContentKey() + " info error or newscontent is not conform !");
            /**if (StringUtils.isNotBlank(articleDTO.getContentKey())) {
                memcacheTemplate.set(Consts.JOB_NEWS_MEMCACHE_KEY.SAVE_NEWS_ERROR_KEY.getValue() + newsBo.getNewsKey(), newsBo.getNewsKey(), 0);
            }**/
            return null;
        }


        if (vpicList != null && vpicList.size() > 0) {
            articleDTO.setvPicList(vpicList);
        }
        articleDTO.setContent(newC);

        return articleDTO;
    }

    private String fetchVideoId(Matcher mm3u8) throws Exception {
        String videoId = "";
        int[] groupIndex = new int[]{1, 3, 5, 7, 9, 11, 13};
        for (int index : groupIndex) {
            String groupValue = mm3u8.group(index);
            if (groupValue != null) {
                if (index <= 5) {
                    videoId = groupValue;
                } else {
                    if (StringUtils.endsWith(groupValue, "==")) {
                        videoId = new String(SecurityUtil.decryptBASE64(groupValue));
                    } else {
                        videoId = new String(SecurityUtil.decryptBASE64(groupValue + "=="));
                    }
                }
                break;
            }
        }
        return videoId;
    }

    /**
     * 处理图片.
     *
     * @param articleDTO
     * @return
     */
    private void processPicList(ArticleDTO articleDTO) {
        List<String> picList = new ArrayList<String>();
        Document doc = Jsoup.parse(articleDTO.getContent());
        Elements scripts = doc.getElementsByTag("img");
        for (Element element : scripts) {
            try {
                //判断是否需要传cdn.如果需要传魔图，则直接传，且返回url。如果不需要。则直接返回url
                String url = proxyPictureByS3(element.attr("src"));
                picList.add(url);
                element.attr("src", url);

                element.removeAttr("alt").removeAttr("title").removeAttr("hspace").removeAttr("border");
                if (element.hasAttr("_src")) {
                    element.removeAttr("_src");
                }

            } catch (Exception e) {
                logger.error("处理图片错误", e);
                element.wrap("<a href=\"" + element.attr("src") + "\"></a>");
            }
        }
        StringBuffer sb = new StringBuffer(doc.body().children().toString());
        articleDTO.setContent(sb.toString());
        articleDTO.setPicList(picList);
    }

    /**
     * 判断是否存在S3.
     *
     * @param url
     * @return 这个功能主要是为了判断是否存在魔图上面，如果不存在。则上传魔图，并且返回.
     */
    private String proxyPictureByS3(String url) {
       /* Map<String, String> map = new HashMap<String, String>();
        if (element.attr("src") != null && !(element.attr("src").contains("/YWxqaGBf/") && element.attr("src").matches("[^!]+(!a-.*-.*)"))) {
            map = PrivateUtil.putPictureByS3(element.attr("src"), newsBo.getContentPicList());
        }
        String width = "";
        String height = "";
        if (map.containsKey("src")) {
            picList.add(map.get("src"));
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!StringUtils.equalsIgnoreCase(entry.getKey(), "src")) {
                if (StringUtils.isBlank(width)) {
                    width = width.concat(entry.getKey());
                    height = height.concat(entry.getValue());
                } else {
                    width = width.concat(",").concat(entry.getKey());
                    height = height.concat(",").concat(entry.getValue());
                }
            }
        }*/
        return null;
    }


    /**
     * 去除敏感词.
     *
     * @param content 文章内容
     * @return 去除敏感词之后的内容.
     */
    private String filterSensitiveWord(String content) {
        /*try {
            Map<String, Object> map = tabooService.filterTaboo(articleDTO.getContent(), "MOBILEME_YXKK", "NEWS_CONTENT", true,
                    false);
            if (map != null && map.containsKey(Consts.TABOO_FILTER_RESULT_KEY_CONTENT)) {
                newsBo.setContent(String.valueOf(map.get(Consts.TABOO_FILTER_RESULT_KEY_CONTENT)));
            }
        } catch (Exception e) {
            logger.error("filter news content error newsId : " + newsBo.getNewsKey() + e.getMessage(), e);
        }*/
        return null;
    }

    /**
     * 生成内容.
     *
     * @param content
     * @return
     */
    private String processContent(String content) {
        Document doc = Jsoup.parse(content);
        doc.getElementsByClass("vote").remove();
        content = doc.body().children().toString();
        content = content
                .replaceAll("<[fF][oO][rR][mM][^>]*?>[^([fF][oO][rR][mM])]*?[fF][oO][rR][mM]>", "")
                .replaceAll("<[tT][aA][bB][lL][eE][^>]*?>[^([tT][aA][bB][lL][eE])]*?[tT][aA][bB][lL][eE]>", "")
                .replaceAll("<!--info[kK]ey[wW]ord-->", "")
                .replaceAll("\\(点此看大图\\)", "")
                .replaceAll("注:点图片,即可查看下一张", "")
                //.replaceAll("<[Pp]>[　 ]+", "<P>")//这是去掉每个段落首部的空格的，编辑和产品要求去掉
                .replaceAll("<[Hh][Rr][^>]*>", "")
                .replaceAll("<[aA][^>]+href=['\"]?[^'\"]+['\"]?[^>]*>(<[iI][mM][gG][^>]+>)</[aA]>", "$1")
                .replaceAll("《<[aA][^>]+href=['\"]?[^'\"]+['\"]?[^>]*>(.+?)</[aA]>》", "《$1》")
                .replaceAll("<[aA][^>]+href=['\"]?[^'\"]+['\"]?[^>]*>(《.+?》)</[aA]>", "$1")
                .replaceAll("([\\【\\[\\（\\(\\“\"])[ ]*?<[aA][^>]+href=['\"]?[^'\"]+['\"]?[^>]*>(.+?)</[aA]>[ ]*?([\\】\\]\\）\\)\"\\”])", "");
      /*
       * if(newsBo.getNewsChannelId() == 90103) { content = content.replaceAll(
       * "<[aA][^>]+href=['\"]?[^'\"]+['\"]?[^>]*>(<[iI][mM][gG][^>]+>)</[aA]>",
       * "$1") .replaceAll(
       * "<[aA][^>]+href=['\"]?[^'\"]+['\"]?[^>]*>.+?</[aA]>|<[aA][^>]*>|</[aA]>"
       * , ""); } else { content =
       * content.replaceAll("<[aA][^>]+>https?://[^>]+</[aA]>|<[aA][^>]*>|</[aA]>"
       * , ""); }
       */
        java.util.regex.Pattern pf = java.util.regex.Pattern.compile("<[pP][^>]*>.*?</[pP]>");
        java.util.regex.Matcher mf = pf.matcher(content);
        StringBuffer sf = new StringBuffer();
        while (mf.find()) {
            java.util.regex.Pattern pc = java.util.regex.Pattern
                    .compile("(<[pP][^>]*>)(.*?)<[aA][^>]+href=['\"]?[^'\"]+['\"]?[^>]*>(.*?)</[aA]>(.*?)(</[pP]>)");
            java.util.regex.Matcher mc = pc.matcher(mf.group(0));
            StringBuffer sc = new StringBuffer();
            while (mc.find()) {
                String ss2 = mc.group(2).replace("$", "\\$");
                String ss3 = mc.group(3).replace("$", "\\$");
                if (StringUtils.isNotBlank(mc.group(4)) || StringUtils.isNotBlank(mc.group(2))) {
                    String s4 = "";
                    String s2 = "";
                    if (StringUtils.isNotBlank(mc.group(4))) {
                        // 处理段落中全是a标签的
                        s4 = mc.group(4).replaceAll("<[aA][^>]+href=['\"]?[^'\"]+['\"]?[^>]*>.*?</[aA]>", "")
                                .replaceAll("<.*?>", "").replaceAll("&nbsp;", "").trim();
                    }
                    if (StringUtils.isNotBlank(mc.group(2))) {
                        s2 = mc.group(2).replaceAll("<.*?>", "").replaceAll("&nbsp;", "").trim();
                    }
                    if (StringUtils.isBlank(s2) && StringUtils.isBlank(s4)) {
                        mc.appendReplacement(sc, "");
                    } else {
                        // 连接在段落最后
                        if (StringUtils.isNotBlank(s2) && StringUtils.isBlank(s4)) {
                            mc.appendReplacement(sc, "$1" + ss2 + "$5");
                        }
                        // 段落中有连接
                        if (StringUtils.isNotBlank(s4)) {
                            String subString = mc.group(4).replaceAll("<[aA][^>]+href=['\"]?[^'\"]+['\"]?[^>]*>(.*?)</[aA]>", "$1");
                            mc.appendReplacement(sc, "$1" + ss2 + ss3 + subString.replace("$", "\\$") + "$5");
                        }
                    }
                } else {
                    mc.appendReplacement(sc, "");
                }
            }
            mc.appendTail(sc);
            mf.appendReplacement(sf, sc.toString().replace("$", "\\$"));
        }
        mf.appendTail(sf);
        content = sf.toString();
        content = content.replaceAll("<[aA][^>]+href=['\"]?[^'\"]+['\"]?[^>]*>.*?</[aA]>|<[aA][^>]*>|</[aA]>", "");

        java.util.regex.Pattern px = java.util.regex.Pattern.compile("(<[pP][^>]*>)(.*?)(相关链接+?[:：]?|相关报道+?[:：]?|相关新闻+?[:：]?|相关新闻阅读+?[:：]?)(.*?)(</[pP]>)");
        java.util.regex.Matcher mx = px.matcher(content);
        StringBuffer sx = new StringBuffer();
        while (mx.find()) {
            String subString = mx.group(2).replaceAll("<.*?>", "").trim();
            String dd = mx.group(4).replaceAll("<.*?>", "").trim();
            if (StringUtils.isBlank(subString) && StringUtils.isBlank(dd)) {
                mx.appendReplacement(sx, "");
            }
        }
        mx.appendTail(sx);
        return sx.toString();
    }


}


