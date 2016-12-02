package com.cyou.fz.mcms.process.content.query;

import com.cyou.fz.mcms.process.common.query.BaseQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;

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
     * 分类id.
     */
    @ApiModelProperty(value = "分类id")
    private Integer categoryId;

    /**
     * 文章类型.
     */
    @ApiModelProperty(value = "文章类型")
    private Integer contentType;


    @ApiModelProperty(value = "是否需要持久化")
    private Boolean needStore;


    @ApiModelProperty(value = "cron表达式")
    private String cronExpression;


    public Boolean getNeedStore() {
        return needStore;
    }

    public void setNeedStore(Boolean needStore) {
        this.needStore = needStore;
    }

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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

}
