package com.cyou.fz.mcms.process.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by vlog接口返回值 on 2016/11/22.
 */
public class VideoInfoDTO implements Serializable{


    private static final long serialVersionUID = -3798617019264792484L;



    @SerializedName(value = "pic_big")
    private String bigPic;


    @SerializedName(value = "pic_small")
    private String smallPic;

    @SerializedName(value = "m3u8")
    private List<M3u8DTO> m3u8s;


    public String getBigPic() {
        return bigPic;
    }

    public void setBigPic(String bigPic) {
        this.bigPic = bigPic;
    }

    public String getSmallPic() {
        return smallPic;
    }

    public void setSmallPic(String smallPic) {
        this.smallPic = smallPic;
    }

    public List<M3u8DTO> getM3u8s() {
        return m3u8s;
    }

    public void setM3u8s(List<M3u8DTO> m3u8s) {
        this.m3u8s = m3u8s;
    }
}
