package com.cyou.fz.mcms.process.web.common.scheduler.utils;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.cyou.fz.mcms.process.web.common.scheduler.BaseJobBean;
import com.cyou.fz.mcms.process.web.common.scheduler.QuartzManager;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public abstract class BaseJobUtils {

    private Logger logger = LoggerFactory.getLogger(BaseJobUtils.class);

    @Autowired
    private QuartzManager quartzManager;

    private BaseJobBean baseJobBean;

    private String triggerKey;

    private String jobKey;

    private Map<String, String> map;

    private String cronExpression;

    /**
     * 添加任务
     */
    public void addJob(Class clazz) throws SchedulerException {


        Trigger trigger = quartzManager.getTrigger(triggerKey);
        //更新信息
        if (trigger != null) {
            //构建map
            makeJobDataMap();
            quartzManager.updateTrigger(trigger, map, cronExpression);
            logger.info("更新定时器成功triggerKey："+triggerKey+", cronExpression:"+cronExpression);
        } else {
            //插入信息
            JobDetail jobDetail = quartzManager.addJob(jobKey, clazz);
            //构建map
            makeJobDataMap();
            quartzManager.addTrigger(triggerKey, jobDetail, map, cronExpression);
            logger.info("插入定时器成功triggerKey："+triggerKey+", cronExpression:"+cronExpression+", jobKey:"+jobKey);
        }


    }

    protected abstract void makeJobDataMap();


    /**
     * 删除任务.
     *
     * @param id 任务id
     * @throws SchedulerException
     */
    public void deleteJob(String id) throws SchedulerException {
        String triggerValue = triggerKey + id;
        quartzManager.deleteTrigger(triggerValue);
    }

    public BaseJobBean getBaseJobBean() {
        return baseJobBean;
    }

    public void setBaseJobBean(BaseJobBean baseJobBean) {
        this.baseJobBean = baseJobBean;
    }

    public String getTriggerKey() {
        return triggerKey;
    }

    public void setTriggerKey(String triggerKey) {
        this.triggerKey = triggerKey;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
