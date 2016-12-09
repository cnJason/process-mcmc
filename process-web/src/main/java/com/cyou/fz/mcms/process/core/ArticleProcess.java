package com.cyou.fz.mcms.process.core;

import com.cyou.fz.mcms.process.core.bean.ArticleProcessDTO;
import com.cyou.fz.mcms.process.core.bean.ContentProcessDTO;
import com.cyou.fz.mcms.process.core.bean.M3u8DTO;
import com.cyou.fz.mcms.process.core.bean.VideoInfoDTO;
import com.cyou.fz.mcms.process.core.system.Constants;
import com.cyou.fz.mcms.process.core.utils.RegexUtils;
import com.cyou.fz.mcms.process.core.utils.S3Util;
import com.cyou.fz.mcms.process.core.utils.SecurityUtil;
import com.cyou.fz.mcms.process.web.common.SystemConstants;
import com.cyou.fz.mcms.process.web.image.service.ImageMappingService;
import com.cyou.fz.mcms.process.web.spring.SpringContextLoader;
import mjson.Json;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;


/**
 * Created by cnJason on 2016/11/21.
 */
public class ArticleProcess implements IProcess {


    private ImageMappingService imageMappingService = (ImageMappingService) SpringContextLoader.getBean("imageMappingService");


    /**
     * 主方法实现.
     *
     * @param articleDTO 输入待清洗的文章对象
     * @return 输出清洗后的文章对象.
     */
    public ContentProcessDTO process(ContentProcessDTO articleDTO) {
        // 获取文章内容.
        ArticleProcessDTO dto = new ArticleProcessDTO();
        if (articleDTO instanceof ArticleProcessDTO) {

            dto = (ArticleProcessDTO) articleDTO;
            String content = dto.getContent();
            // 过滤敏感词.
            content = filterSensitiveWord(content);
            // 内容过滤.
            content = processContent(content);
            dto.setContent(content);
            //替换图片信息且获得picList.
            processPicList(dto);
            //替换图片且获得vlogPicList
            return processVlogPicList(dto);
        } else {
            return dto;
        }

    }

    private ArticleProcessDTO processVlogPicList(ArticleProcessDTO articleDTO) {
        List<VideoInfoDTO> vpicList = new ArrayList<VideoInfoDTO>();

        String content = articleDTO.getContent();

        Document document = Jsoup.parse(content);
        Elements elementEmbeds = document.getElementsByTag("embed");
        Elements elementIframes = document.getElementsByTag("iframe");

        for (Element elementEmbed : elementEmbeds) new MakeVideoInfo(vpicList, elementEmbed).invoke();
        for (Element elementIframe : elementIframes) new MakeVideoInfo(vpicList, elementIframe).invoke();

        String LastNewsContent = document.body().toString().replaceAll(
                "<[iI][fF][rR][aA][mM][eE][^>]?((?!(youtube).com)[^>])*>[^([iI][fF][rR][aA][mM][eE])]*?[iI][fF][rR][aA][mM][eE]>", "");
        if (LastNewsContent.length() < 50) {
            //  logger.error("get newsId : " + articleDTO.getContentKey() + " info error or newscontent is not conform !");
            /**if (StringUtils.isNotBlank(articleDTO.getContentKey())) {
             memcacheTemplate.set(Consts.JOB_NEWS_MEMCACHE_KEY.SAVE_NEWS_ERROR_KEY.getValue() + newsBo.getNewsKey(), newsBo.getNewsKey(), 0);
             }**/
            return null;
        }


        if (vpicList != null && vpicList.size() > 0) {
            articleDTO.setvPicList(vpicList);
        }
        articleDTO.setContent(LastNewsContent);

        articleDTO.setStatus(articleDTO.STATUS_SUCCESS);
        return articleDTO;
    }

    private String fetchVideoId(String element) throws Exception {
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
        String videoId = "";
        Matcher mm3u8 = pm3u8.matcher(element.toString());
        if(mm3u8.find()){
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
        }
        return videoId;
    }

    /**
     * 处理图片.
     *
     * @param articleDTO
     * @return
     */
    private void processPicList(ArticleProcessDTO articleDTO) {
        List<String> picList = new ArrayList<String>();
        Document doc = Jsoup.parse(articleDTO.getContent());
        Elements scripts = doc.getElementsByTag("img");
        for (Element element : scripts) {
            try {
                //判断是否需要传cdn.如果需要传魔图，则直接传，且返回url。如果不需要。则直接返回url
                String url = proxyPictureByS3(element.attr("src"));
                //添加图片至picList.
                picList.add(url);
                element.attr("src", url);

                element.removeAttr("alt").removeAttr("title").removeAttr("hspace").removeAttr("border");
                if (element.hasAttr("_src")) {
                    element.removeAttr("_src");
                }
            } catch (Exception e) {
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

        // 从旧系统转换.
        url = S3Util.convertFromOldSystem(url);

        if (url.contains("/YWxqaGBf/") || url.contains("/z6mhfw/") || url.contains("/i8nvgq/")) {
            return url;
        } else {
            // 设置日期格式.
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
            // 获取日期.
            String date = df.format(new Date());
            //获取文件格式.
            String imageType = RegexUtils.getImageType(url);
            int randomNum = RandomUtils.nextInt(1000, 10000);

            StringBuffer urlBuffer = new StringBuffer();
            //生成随机数图片名称.
            urlBuffer.append("http://" + SystemConstants.cdnDomain + "/").append("mobileme/pic/cms").append("/").append(date).append("/").append(randomNum).append(".").append(imageType);

            StringBuffer cdnPathBuffer = new StringBuffer();
            String cdnDomainKeyStr = "";
            if (SystemConstants.cdnDomainKey != null) {
                cdnDomainKeyStr = SystemConstants.cdnDomainKey;
            }
            cdnPathBuffer.append(cdnDomainKeyStr).append("/mobileme/pic/cms/").append(date).append("/").append(randomNum).append(".").append(imageType);

            return imageMappingService.uploadImage(url, urlBuffer.toString(), cdnPathBuffer.toString());
        }
    }


    /**
     * 去除敏感词.
     *
     * @param content 文章内容
     * @return 去除敏感词之后的内容.
     */
    private String filterSensitiveWord(String content) {
        //TODO: 去除敏感词.
        /*try {
            Map<String, Object> map = tabooService.filterTaboo(articleDTO.getContent(), "MOBILEME_YXKK", "NEWS_CONTENT", true,
                    false);
            if (map != null && map.containsKey(Consts.TABOO_FILTER_RESULT_KEY_CONTENT)) {
                newsBo.setContent(String.valueOf(map.get(Consts.TABOO_FILTER_RESULT_KEY_CONTENT)));
            }
        } catch (Exception e) {
            logger.error("filter news content error newsId : " + newsBo.getNewsKey() + e.getMessage(), e);
        }*/
        return content;
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

        java.util.regex.Pattern pf = java.util.regex.Pattern.compile("<[pP][^>]*>.*?</[pP]>");
        Matcher mf = pf.matcher(content);
        StringBuffer sf = new StringBuffer();
        while (mf.find()) {
            java.util.regex.Pattern pc = java.util.regex.Pattern
                    .compile("(<[pP][^>]*>)(.*?)<[aA][^>]+href=['\"]?[^'\"]+['\"]?[^>]*>(.*?)</[aA]>(.*?)(</[pP]>)");
            Matcher mc = pc.matcher(mf.group(0));
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
        Matcher mx = px.matcher(content);
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


    private class MakeVideoInfo {
        private List<VideoInfoDTO> vpicList;
        private Element elementEmbed;

        public MakeVideoInfo(List<VideoInfoDTO> vpicList, Element elementEmbed) {
            this.vpicList = vpicList;
            this.elementEmbed = elementEmbed;
        }

        public void invoke() {
            VideoInfoDTO videoInfoDTO = new VideoInfoDTO();
            String videoId = "";
            try {
                videoId = fetchVideoId(elementEmbed.toString());
            } catch (Exception e) {
                e.printStackTrace();
                elementEmbed.html("");
                return;
            }

            if (StringUtils.isNotBlank(videoId) && StringUtils.isNumeric(videoId)) {
                String response = null;
                try {
                    response = Jsoup.connect(Constants.VLOG_VIDEO_INFO_URL + videoId).ignoreContentType(true).get().body().text();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Json jsonResponse = Json.read(response);
                if (StringUtils.isNotBlank(jsonResponse.toString()) && jsonResponse.has("data")) {

                    Json vlogData = jsonResponse.at("data");

                    String videoPic = vlogData.at("pic_small").asString();

                    videoInfoDTO.setSmallPic(videoPic);

                    List<Json> m3u8s = vlogData.at("m3u8").asJsonList();

                    List<M3u8DTO> m3u8DTOList = new ArrayList<M3u8DTO>();


                    for (Json m3u8 : m3u8s) {
                        M3u8DTO m3u8DTO = new M3u8DTO();
                        m3u8DTO.setUrl(m3u8.at("url").asString());
                        m3u8DTO.setQuality(m3u8.at("quality").asInteger());
                        m3u8DTOList.add(m3u8DTO);
                    }

                    videoInfoDTO.setM3u8s(m3u8DTOList);

                    if (StringUtils.isBlank(videoPic) || m3u8DTOList.size() <= 0) {
                        //如果没找到的话。会被删除掉.
                        elementEmbed.html("");
                    } else {
                        final String[] qualityString = {""};
                        //设置小图.
                        m3u8DTOList.stream().forEach(m3u8DTO -> {
                            qualityString[0] += m3u8DTO.getQuality() + ",";
                        });
                        String videoInfo = "<p align=center><a id=\"" + qualityString[0] + "\" href=\"" + m3u8DTOList.get(0).getUrl()
                                + "\"><img src=\"" + videoPic + "\" /></a></p>";
                        elementEmbed.html(videoInfo);
                    }
                } else {
                    // logger.info("no find videoInfo by videoId " + videoId);
                    //如果没找到的话。会被删除掉.
                    elementEmbed.html("");
                }
            }
            vpicList.add(videoInfoDTO);
        }
    }
}


