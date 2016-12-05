package com.cyou.fz.mcms.process.core.bean;

import java.io.Serializable;

/**
 * Created by cnJason on 2016/11/21.
 */
public class ContentProcessDTO implements Serializable{


    public static final int STATUS_SUCCESS=1;
    public static final int STATUS_FAILURE=2;

    /**
     * 文章类型。【1.文章；2.组图；3.链接；5.视频；9:vlog】
     */
    private Integer contentType;

    /**
     * 清洗状态；sucess-1 ;failure-2
     */
    private Integer status;


    /**
     * error message
     */
    private String message;


    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
