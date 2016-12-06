package com.cyou.fz.mcms.process.core.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 内容处理原子对象.
 * Created by cnJason on 2016/11/21.
 */
public class ArticleProcessDTO extends ContentProcessDTO implements Serializable{


    /**
     * 内容字段
     */
    private String content;

    /**
     * 图片列表
     */
    private List<String> picList;


    /**
     * 视频列表
     */
    private List<VideoInfoDTO> vPicList;


    /**
     * 是否缩放
     */
    private int isScale;

    /**
     * channelId
     */
    private String channelCode;

    /**
     * newsId
     */
    private int newsId;

    /**
     * 内容key
     */
    private String contentKey;


    public ArticleProcessDTO() {
    }



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getPicList() {
        return picList;
    }

    public void setPicList(List<String> picList) {
        this.picList = picList;
    }

    public List<VideoInfoDTO> getvPicList() {
        return vPicList;
    }

    public void setvPicList(List<VideoInfoDTO> vPicList) {
        this.vPicList = vPicList;
    }

    public int getIsScale() {
        return isScale;
    }

    public void setIsScale(int isScale) {
        this.isScale = isScale;
    }


    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getContentKey() {
        return contentKey;
    }

    public void setContentKey(String contentKey) {
        this.contentKey = contentKey;
    }

    /**
     * 是否成功.
     * @return 是否成功.
     */
    public boolean isSuccess() {
        return  getStatus().intValue() == STATUS_SUCCESS;

    }
}
