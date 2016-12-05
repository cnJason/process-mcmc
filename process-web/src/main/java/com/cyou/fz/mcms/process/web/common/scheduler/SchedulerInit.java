package com.cyou.fz.mcms.process.web.common.scheduler;

import com.cyou.fz.mcms.process.web.task.job.TaskStatusJobUtils;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by cnJason on 2016/12/5.
 */
@Service
public class SchedulerInit {

    private Logger logger = Logger.getLogger(SchedulerInit.class);

    @Autowired
    private TaskStatusJobUtils taskStatusJobUtils;


    public void init(){

        try {
            logger.info("tasktatus初始化注册定时corn表达式");
            taskStatusJobUtils.addTaskStatusJobTrigger("1 1/5 * * * ?");
        } catch (SchedulerException e) {
            logger.error("actStatus初始化注册定时corn表达式异常", e);
        }

    }
}
