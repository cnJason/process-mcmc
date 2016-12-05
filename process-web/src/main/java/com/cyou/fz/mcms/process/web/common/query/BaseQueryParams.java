package com.cyou.fz.mcms.process.web.common.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by cnJason on 2016/12/2.
 */
@ApiModel(value = "基础查询参数")
public class BaseQueryParams implements Serializable {

    private static final long serialVersionUID = 595065859772847196L;



    @ApiModelProperty(value = "页码")
    private Integer pageNo;

    @ApiModelProperty(value = "单页大小")
    private Integer pageSize;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;


    @ApiModelProperty(value = "结束时间")
    private Date endTime;


    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
