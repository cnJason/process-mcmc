package com.cyou.fz.mcms.process.core.bean;

import java.io.Serializable;

/**
 * Created by cnJason on 2016/11/21.
 */
public class ContentDTO implements Serializable{


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
}
