package com.cyou.fz.mcms.process.core.service;

import com.cyou.fz.mcms.process.core.ArticleProcess;
import com.cyou.fz.mcms.process.core.bean.ArticleProcessDTO;
import com.cyou.fz.mcms.process.core.bean.ContentProcessDTO;
import com.cyou.fz.mcms.process.web.content.bean.ContentBase;
import com.cyou.fz.mcms.process.web.content.bean.ContentTxt;
import com.cyou.fz.mcms.process.web.content.request.ContentRequest;
import com.cyou.fz.mcms.process.web.content.service.ContentBaseService;
import com.cyou.fz.mcms.process.web.content.service.ContentTxtService;
import mjson.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by cnJason on 2016/12/5.
 */
@Service
public class ContentProcessService {

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);


    /**
     * 内容文本服务
     */
    @Autowired
    private ContentTxtService contentTxtService;

    /**
     * 内容基础服务
     */
    @Autowired
    private ContentBaseService contentBaseService;


    public void processRequestList(List<ContentRequest> requestList) {
        for (ContentRequest contentRequest : requestList) {
           processRequest(contentRequest);
        }
    }

    private void processRequest( ContentRequest contentRequest) {
        executor.submit(() -> {
            if(contentRequest.getSourceSystem().contentEquals(ContentRequest.SOURCE_SYSTEM_CMS)){
                proceessContent(contentRequest);
            }else if(contentRequest.getSourceSystem().contentEquals(ContentRequest.SOURCE_SYSTEM_VLOG)) {
                processVlog(contentRequest);
            }
        });
    }


    private void processVlog(ContentRequest contentRequest) {

    }

    private void proceessContent(ContentRequest contentRequest) {
        // 获取内容文本对象.
        ContentTxt contentTxt = contentTxtService.getByContentKey(contentRequest.getContentKey());
        ArticleProcessDTO articleProcessDTO = new ArticleProcessDTO();
        // 内容key.
        articleProcessDTO.setContentKey(contentRequest.getContentKey());
        // 内容文本
        articleProcessDTO.setContent(contentTxt.getOriginalText());
        // 频道编号
        articleProcessDTO.setChannelCode(contentRequest.getChannelCode());

        ArticleProcess articleProcess = new ArticleProcess();
        // 获取处理的返回值.
        articleProcessDTO = (ArticleProcessDTO) articleProcess.process(articleProcessDTO);

        if(articleProcessDTO.getStatus() == ContentProcessDTO.STATUS_SUCCESS){
            contentBaseService.markContentFinish(contentRequest,articleProcessDTO);
        }else if(articleProcessDTO.getStatus() == ContentProcessDTO.STATUS_FAILURE){
            contentBaseService.markContentFailure(contentRequest,articleProcessDTO);
        }





    }
}
