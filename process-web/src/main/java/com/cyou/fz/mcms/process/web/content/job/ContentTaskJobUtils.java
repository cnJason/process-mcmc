package com.cyou.fz.mcms.process.web.content.job;

import com.cyou.fz.mcms.process.web.common.scheduler.utils.BaseJobUtils;
import com.cyou.fz.mcms.process.web.content.bean.ContentTask;
import com.google.common.collect.Maps;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by cnJason on 2016/12/2.
 * 内容发布
 */
@Component
public class ContentTaskJobUtils extends BaseJobUtils {


    public static final String TRIGGER_NAME_PREFIX = "triggerByContentTask_";


    public static final String JOB_NAME_PREFIX = "jobByContentTask_";


    public void addContentTaskJob(ContentTask contentTask)throws SchedulerException{
        String key =  contentTask.getChannelCode()+"_"+contentTask.getContentType();
        setJobKey(JOB_NAME_PREFIX + key);
        setTriggerKey(TRIGGER_NAME_PREFIX + key);

        ContentTaskJobBean contentTaskJobBean = new ContentTaskJobBean();
        contentTaskJobBean.setContentType(contentTask.getContentType());
        contentTaskJobBean.setChannelCode(contentTask.getChannelCode());

        setBaseJobBean(contentTaskJobBean);
        setCronExpression(contentTask.getCronExpression());
        addJob(ContentTaskJob.class);

    }


    @Override
    protected void makeJobDataMap() {
        Map<String,String> map = Maps.newHashMap();
        ContentTaskJobBean bean = (ContentTaskJobBean) getBaseJobBean();
        map.put("channelCode",bean.getChannelCode()+"");
        map.put("contentType",bean.getContentType()+"");
        this.setMap(map);
    }

    public void deleteContentTaskJob(ContentTask contentTask) {
        String key =  contentTask.getChannelCode()+"_"+contentTask.getContentType();
        setJobKey(JOB_NAME_PREFIX + key);
        setTriggerKey(TRIGGER_NAME_PREFIX + key);
        try {
            deleteJob(key);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
