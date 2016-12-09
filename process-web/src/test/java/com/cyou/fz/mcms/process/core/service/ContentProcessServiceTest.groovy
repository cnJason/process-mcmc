package com.cyou.fz.mcms.process.core.service

import com.cyou.fz.mcms.base.SpockBaseTest
import com.cyou.fz.mcms.process.core.ArticleProcess
import com.cyou.fz.mcms.process.core.bean.ArticleProcessDTO
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by cnJason on 2016/12/9.
 */
class ContentProcessServiceTest  extends SpockBaseTest{

    @Autowired
    ContentProcessService contentProcessService;


    def "testArticleProces" (){
        expect:
        ArticleProcessDTO articleProcessDTO = new ArticleProcessDTO();
        articleProcessDTO.setContentKey("1_1_10009");
        articleProcessDTO.setContent("<div class=\"gb-final-mod-article gb-final-mod-article-p2em\" id=\"mod_article\">\n" +
                "        <p style=\"text-indent: 0px;\"><strong>【17173专稿，转载请注明出处】</strong></p>\n" +
                "<p>暴雪日前发布了一款全新供粉丝收藏的《守望先锋》“死神”手办，喜欢这个致命刺客的玩家不容错过。这款手办细节非常好，高约12英寸。在暴雪在线商店可以预购这款手办，售价150美元。下面一起看看视频。</p>\n" +
                "<p class=\"p-video\" style=\"text-align: center;\"><embed src=\"http://f.v.17173cdn.com/player_f2/Mzc0ODEwOTY.swf\" allowfullscreen=\"true\" quality=\"high\" width=\"580\" height=\"400\" align=\"middle\" allowscriptaccess=\"always\" wmode=\"transparent\" type=\"application/x-shockwave-flash\"></p>\n" +
                "<p>暴雪也发布了一段视频展示这款手办的制作涂装细节。死神面具细节很出色，腰带和披风都凸显这款手办的精妙之处。手办的动作是死神开大招之前的样子。</p>\n" +
                "<p class=\"p-image\" style=\"text-align: center;\"><a href=\"http://i3.17173cdn.com/2fhnvk/YWxqaGBf/cms3/CsVYKGblcpgperD.jpg\" title=\"226_161126063626_1.jpg\" target=\"_blank\"><img src=\"http://i3.17173cdn.com/2fhnvk/YWxqaGBf/cms3/CsVYKGblcpgperD.jpg!a-3-540x.jpg\" width=\"540\" height=\"223\" alt=\"226_161126063626_1.jpg\"></a></p>\n" +
                "   \n" +
                "</div>");
        articleProcessDTO.setChannelCode("10009");
        ArticleProcess articleProcess = new ArticleProcess();
        articleProcess.process(articleProcessDTO);
        where:
        id | index
        5  | 1
    }


}
