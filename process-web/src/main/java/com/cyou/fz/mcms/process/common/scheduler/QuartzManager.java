package com.cyou.fz.mcms.process.common.scheduler;

import com.cyou.fz.mcms.process.common.scheduler.utils.CronExpressionUtil;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 任务管理主要方法.
 */
@Service
public class QuartzManager {
    private static Logger logger = LoggerFactory.getLogger(QuartzManager.class);
    /**
     * 任务调度器.
     */
    @Autowired
    private Scheduler scheduler;

    /**
     * 默认构造函数.
     */
    private QuartzManager() {
    }

    /**
     * 添加一个单纯JobDetail任务
     * (此任务无任何关联trigger,trigger关联任务请看 @see addTrigger).
     *
     * @param jobName 任务名
     * @param job     任务
     * @return JobDetail 任务
     * @throws org.quartz.SchedulerException 任务调度异常
     */
    public JobDetail addJob(String jobName, Class<? extends Job> job) throws SchedulerException {

        // 任务名，任务组，任务执行类
        JobDetail jobDetail = JobBuilder.newJob(job).withIdentity(jobName).storeDurably(true).build();

        if (!scheduler.checkExists(jobDetail.getKey())) {
            scheduler.addJob(jobDetail, true);
        }

        return jobDetail;
    }

    /**
     * 根据任务名和任务组名获取一个JobDetail任务.
     *
     * @param jobName 任务名
     * @return JobDetail 任务
     * @throws org.quartz.SchedulerException 任务调度异常
     */
    public JobDetail getJob(String jobName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName);
        return scheduler.getJobDetail(jobKey);
    }


    /**
     * 添加一个关联JobDetail的定时触发器.
     *
     * @param triggerName    触发器名
     * @param jobDetail      关联任务
     * @param dataMap        触发器内容
     * @param cronExpression 定时时间
     * @throws org.quartz.SchedulerException 任务调度异常
     */
    public void addTrigger(String triggerName, JobDetail jobDetail,
                           Map<String, String> dataMap, String cronExpression) throws SchedulerException {
        //触发器相关内容
        JobDataMap jobDataMap = new JobDataMap();
        for (String key : dataMap.keySet()) {
            jobDataMap.put(key, dataMap.get(key));
        }

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerName).
                usingJobData(jobDataMap).withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).
                forJob(jobDetail).build();
        //添加触发器.
        scheduler.scheduleJob(trigger);
    }

    /**
     * 根据触发器名和触发器组名获取触发器
     *
     * @param triggerName 触发器名
     * @return Trigger 触发器
     * @throws org.quartz.SchedulerException
     */
    public Trigger getTrigger(String triggerName) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(triggerName);
        return scheduler.getTrigger(triggerKey);
    }

    /**
     * 根据工作名和工作组名获取某个job任务的所有触发器
     *
     * @param jobName 触发器名
     * @return List<? extends Trigger> 触发器列表
     * @throws org.quartz.SchedulerException
     */
    public List<? extends Trigger> getTriggersOfJob(String jobName) throws SchedulerException {
        return scheduler.getTriggersOfJob(new JobKey(jobName));
    }

    /**
     * 更新触发器
     *
     * @param oldTrigger     原触发器
     * @param dataMap        触发器内容
     * @param cronExpression 定时表达式
     * @throws org.quartz.SchedulerException
     */
    public void updateTrigger(Trigger oldTrigger, Map<String, String> dataMap, String cronExpression) throws SchedulerException {
        //触发器相关内容
        JobDataMap jobDataMap = new JobDataMap();
        for (String key : dataMap.keySet()) {
            jobDataMap.put(key, dataMap.get(key));
        }
        Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(oldTrigger.getKey().getName()).usingJobData(jobDataMap).withSchedule(
                CronScheduleBuilder.cronSchedule(cronExpression)).build();
        scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
    }

    /**
     * 更新触发器
     *
     * @param oldTrigger 原触发器
     * @param jobDataMap 触发器内容
     * @param date       定时触发器时间
     * @throws org.quartz.SchedulerException
     */
    public void updateTrigger(Trigger oldTrigger, JobDataMap jobDataMap, Date date) throws SchedulerException {
        Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(oldTrigger.getKey().getName()).usingJobData(jobDataMap).withSchedule(
                CronScheduleBuilder.cronSchedule(CronExpressionUtil.DateToTimingExpression(date))).build();
        scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
    }


    /**
     * 删除一个触发器
     *
     * @param triggerName,triggerGroup 触发器
     * @throws org.quartz.SchedulerException
     */
    public void deleteTrigger(String triggerName) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(triggerName);
        // 触发器
        scheduler.pauseTrigger(triggerKey);
        scheduler.unscheduleJob(triggerKey);
    }
}
