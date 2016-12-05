package com.cyou.fz.mcms.process.web.content.bean;

import com.cyou.fz.common.utils.mybatis.annotations.Id;
import com.cyou.fz.common.utils.mybatis.annotations.Table;

import java.io.Serializable;

/**
 * Created by cnJason on 2016/12/2.
 */
@Table(value = "t_content_txt")
public class ContentTxt implements Serializable{


    private static final long serialVersionUID = -3483121670509182093L;
    public static final String COLUMN_CONTENT_KEY = "contentKey";

    @Id(value = "content_id")
    private Integer id;


    private String contentKey;

    private String originalText;

    private String processText;
    
    private String picList;
    
    private String vPicList;


    public String getContentKey() {
        return contentKey;
    }

    public void setContentKey(String contentKey) {
        this.contentKey = contentKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getProcessText() {
        return processText;
    }

    public void setProcessText(String processText) {
        this.processText = processText;
    }

    public String getPicList() {
        return picList;
    }

    public void setPicList(String picList) {
        this.picList = picList;
    }

    public String getvPicList() {
        return vPicList;
    }

    public void setvPicList(String vPicList) {
        this.vPicList = vPicList;
    }
}
