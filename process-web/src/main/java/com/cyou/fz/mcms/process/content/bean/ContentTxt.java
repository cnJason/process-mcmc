package com.cyou.fz.mcms.process.content.bean;

import com.cyou.fz.common.utils.mybatis.annotations.Id;
import com.cyou.fz.common.utils.mybatis.annotations.Table;

import java.io.Serializable;

/**
 * Created by cnJason on 2016/12/2.
 */
@Table(value = "t_content_txt")
public class ContentTxt implements Serializable{


    private static final long serialVersionUID = -3483121670509182093L;

    @Id(value = "content_id")
    private Integer id;

    private String originalText;

    private String processText;


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
}
