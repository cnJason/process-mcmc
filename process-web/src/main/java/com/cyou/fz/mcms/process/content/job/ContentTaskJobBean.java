package com.cyou.fz.mcms.process.content.job;

import com.cyou.fz.mcms.process.common.scheduler.BaseJobBean;

import java.io.Serializable;

/**
 * Created by cnJason on 2016/12/2.
 */
public class ContentTaskJobBean extends BaseJobBean implements Serializable {

    private static final long serialVersionUID = -5775401346874109579L;

    private Integer channelCode;

    private Integer contentType;


    public Integer getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(Integer channelCode) {
        this.channelCode = channelCode;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }
}
