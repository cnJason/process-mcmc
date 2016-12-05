package com.cyou.fz.mcms.process.web.content.job;

import com.cyou.fz.mcms.process.web.common.scheduler.BaseJob;
import com.cyou.fz.mcms.process.web.content.query.ContentTaskParams;
import com.cyou.fz.mcms.process.web.content.service.ContentBaseService;
import com.cyou.fz.mcms.process.web.spring.SpringContextLoader;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * Created by cnJason on 2016/12/2.
 */
public class ContentTaskJob extends BaseJob {

    private Logger logger = Logger.getLogger(ContentTaskJob.class);


    private ContentBaseService contentBaseService  = SpringContextLoader.getBean(ContentBaseService.class);


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        // 获取查询参数对象.
        Integer channelCode = (Integer)context.getMergedJobDataMap().get("channelCode");
        Integer contentType = (Integer)context.getMergedJobDataMap().get("contentType");

        ContentTaskParams contentTaskParams = new ContentTaskParams();
        contentTaskParams.setChannelCode(channelCode);
        // 定时执行操作.
        logger.info("执行内容任务开始"+channelCode+"_"+contentType);
        contentBaseService.processContentTask(contentTaskParams);
        logger.info("执行内容任务结束"+channelCode+"_"+contentType);

    }
}
