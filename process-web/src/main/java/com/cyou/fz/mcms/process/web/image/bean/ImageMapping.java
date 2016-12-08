package com.cyou.fz.mcms.process.web.image.bean;

import com.cyou.fz.common.utils.mybatis.annotations.Id;
import com.cyou.fz.common.utils.mybatis.annotations.Table;

import java.io.Serializable;

/**
 * Created by cnJason on 2016/12/8.
 */
@Table(value = "t_image_mapping")
public class ImageMapping implements Serializable {

    private static final long serialVersionUID = 4797816162027295544L;
    public static final int STATUS_SUCCES = 1;

    //图片id
    @Id(value = "image_id")
    private Integer id;


    /**
     * 原图url
     */
    private String originUrl;

    /**
     * 当前图url
     */
    private String currentUrl;

    /**
     * 是否已经在魔图中.
     */
    private Integer status;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
