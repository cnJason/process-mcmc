package com.cyou.fz.mcms.process.content.bean;

import com.cyou.fz.common.utils.mybatis.annotations.Id;
import com.cyou.fz.common.utils.mybatis.annotations.Table;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.io.Serializable;

/**
 * Created by cnJason on 2016/12/2.
 */
@ApiModel(value = "内容任务对象")
public class ContentTask implements Serializable {


    private static final long serialVersionUID = -4698654851877929609L;


    @ApiModelProperty(value = "频道号")
    private Integer channelCode;


    @ApiModelProperty(value = "查询参数")
    private String queryParams;

    @ApiModelProperty(value = "表达式")
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

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }
}
