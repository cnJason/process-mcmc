package com.cyou.fz.mcms.process.web.task.job;

import com.cyou.fz.mcms.process.web.common.scheduler.utils.BaseJobUtils;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cnJason on 2016/12/5.
 */
@Component
public class TaskStatusJobUtils extends BaseJobUtils {

    public static final String JOB_TASK_STATUS = "job_taskstatus";
    public static final String TRIGGER_TASK_STATUS = "trigger_taskstatus";

    @Override
    protected void makeJobDataMap() {
        Map<String, String> map = new HashMap<String, String>();
        this.setMap(map);
    }

    public void addTaskStatusJobTrigger(String cronExpression) throws SchedulerException {

        setJobKey(JOB_TASK_STATUS);
        setTriggerKey(TRIGGER_TASK_STATUS);
        TaskStatusJobBean taskStatusJobBean = new TaskStatusJobBean();
        setBaseJobBean(taskStatusJobBean);
        setCronExpression(cronExpression);
        addJob(TaskStatusJob.class);
    }
}
