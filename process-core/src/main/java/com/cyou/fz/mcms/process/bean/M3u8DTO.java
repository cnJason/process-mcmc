package com.cyou.fz.mcms.process.bean;

import java.io.Serializable;

/**
 * Created by cnJason on 2016/11/22.
 */
public class M3u8DTO implements Serializable {


    private static final long serialVersionUID = -7931322926679266244L;



    private int quality;

    private String url;


    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
