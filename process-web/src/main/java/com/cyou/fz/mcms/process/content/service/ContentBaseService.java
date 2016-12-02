package com.cyou.fz.mcms.process.content.service;

import com.cyou.fz.common.utils.mybatis.page.Constant;
import com.cyou.fz.common.utils.mybatis.service.BaseServiceImpl;
import com.cyou.fz.mcms.process.common.SystemConstants;
import com.cyou.fz.mcms.process.content.bean.ContentBase;
import com.cyou.fz.mcms.process.content.bean.ContentTask;
import com.cyou.fz.mcms.process.content.job.ContentTaskJobUtils;
import com.cyou.fz.mcms.process.content.query.ContentTaskParams;
import com.cyou.fz.services.cms.ContentService;
import com.cyou.fz.services.cms.commons.CmsQueryResult;
import com.cyou.fz.services.cms.dto.ContentDTO;
import com.cyou.fz.services.cms.model.ContentQueryParam;
import mjson.Json;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by cnJason on 2016/12/2.
 */
@Service
public class ContentBaseService extends BaseServiceImpl<ContentBase> {


    /**
     * 定时器任务.
     */
    @Autowired
    private ContentTaskJobUtils contentTaskJobUtils;

    @Autowired
    private ContentService contentService;

    /**
     * 添加内容任务对象.
     * @param contentTaskParams 内容任务参数
     * @return 成功失败标识.
     */
    public Boolean addContentTask(ContentTaskParams contentTaskParams) {

        ContentTask contentTask = new ContentTask();
        contentTask.setChannelCode(contentTaskParams.getChannelCode());
        contentTask.setContentType(contentTaskParams.getContentType());
        contentTask.setCronExpression(contentTaskParams.getCronExpression());
        //保存任务至job对象中.
        try {
            contentTaskJobUtils.addContentTaskJob(contentTask);
        } catch (SchedulerException e) {
            return false;
        }
        return true;
    }




    /**
     * 内容执行任务.
     * 包含获取列表，推送到分析队列.
     * @param params 内容任务列表.
     */
    public void processContentTask(ContentTaskParams params){
        ContentQueryParam contentQueryParam = new ContentQueryParam();
        contentQueryParam.setChannelCode(params.getChannelCode());
        contentQueryParam.setContentType(params.getContentType());
        contentQueryParam.setPageNo(SystemConstants.DEFAULT_PAGE_NO);
        contentQueryParam.setPageSize(SystemConstants.DEFAULT_PAGE_SIZE);

        CmsQueryResult<ContentDTO> result = contentService.queryContent(contentQueryParam);

        int totalCount = (int) result.getTotalCount();
        int range = totalCount%SystemConstants.DEFAULT_PAGE_SIZE;




    }


    public static void main(String[] args) {
        System.out.println(49999%1000);
    }

}
