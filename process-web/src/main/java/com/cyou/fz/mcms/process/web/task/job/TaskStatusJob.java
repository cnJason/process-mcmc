package com.cyou.fz.mcms.process.web.task.job;

import com.cyou.fz.mcms.process.core.service.ContentProcessService;
import com.cyou.fz.mcms.process.web.common.scheduler.BaseJob;
import com.cyou.fz.mcms.process.web.content.redis.ContentQueueService;
import com.cyou.fz.mcms.process.web.content.request.ContentRequest;
import com.cyou.fz.mcms.process.web.spring.SpringContextLoader;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by cnJason on 2016/12/5.
 * 循环任务列表中任务.
 */
public class TaskStatusJob extends BaseJob {


    private Logger logger = Logger.getLogger("taskStatusJob");
    private Integer batchSize = 1000;


    private ContentQueueService contentQueueService = (ContentQueueService) SpringContextLoader.getBean("contentQueueService");

    private ContentProcessService contentProcessService = (ContentProcessService) SpringContextLoader.getBean("contentProcessService");


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        int runningQueneNum = (int) contentQueueService.getRunningQueneNum();
        logger.info("当前运行任务数为:"+ runningQueneNum);
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
        logger.info("本次任务分配结束:"+ canAssignCount);
    }
}
