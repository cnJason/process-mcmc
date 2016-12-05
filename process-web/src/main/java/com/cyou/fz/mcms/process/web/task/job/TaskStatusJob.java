package com.cyou.fz.mcms.process.web.task.job;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.cyou.fz.mcms.process.core.service.ContentProcessService;
import com.cyou.fz.mcms.process.web.common.scheduler.BaseJob;
import com.cyou.fz.mcms.process.web.content.redis.ContentQueueService;
import com.cyou.fz.mcms.process.web.content.request.ContentRequest;
import com.cyou.fz.mcms.process.web.spring.SpringContextLoader;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * Created by cnJason on 2016/12/5.
 */
public class TaskStatusJob extends BaseJob {

    private  Integer batchSize = 1000;


    private ContentQueueService contentQueueService = (ContentQueueService) SpringContextLoader.getBean("contentQueueService");

    private ContentProcessService contentProcessService = (ContentProcessService)SpringContextLoader.getBean("contentProcessService");


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        int runningQueneNum = (int)contentQueueService.getRunningQueneNum();
        int canAssignCount = batchSize - runningQueneNum;

        if (canAssignCount <= 0) {
            return;
        }

        List<ContentRequest> requestList = contentQueueService.fetchTasksFromWaitingQueue(canAssignCount);

        if (requestList.size() == 0) {
            return;
        }
        contentQueueService.moveWaitingTaskToRunningQueue(requestList);
        contentProcessService.processRequestList(requestList);
    }
}
