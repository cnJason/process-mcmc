package com.cyou.fz.mcms.process.web.content.query;

import com.cyou.fz.mcms.process.web.common.query.BaseQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Created by cnJason on 2016/12/2.
 */
@ApiModel(value = "内容任务对象")
public class ContentTaskParams extends BaseQueryParams implements Serializable {

    private static final long serialVersionUID = -742261376214652729L;

    /**
     * 频道编号.
     */
    @ApiModelProperty(value = "频道编号")
    private Integer channelCode;

    /**
     * 文章类型.
     */
    @ApiModelProperty(value = "文章类型")
    private Integer contentType;



    @ApiModelProperty(value = "cron表达式")
    private String cronExpression;




    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

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
