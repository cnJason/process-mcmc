package com.cyou.fz.mcms.process.web.content.request;

import java.io.Serializable;

/**
 * Created by cnJason on 2016/11/28.
 */
public class ContentRequest implements Serializable {


    private static final long serialVersionUID = -1588793346766428364L;
    /**
     * 来源系统: 17173 cms
     *
     */
    public static final String SOURCE_SYSTEM_CMS = "17173cms";

    /**
     * 来源系统: 17173 vlog
     */
    public static final String SOURCE_SYSTEM_VLOG = "17173vlog";


    /**
     * 内容key.
     */
    private String contentKey;

    /**
     * 视频id.
     */
    private Integer vlogId;

    /**
     * 频道编号.
     */
    private String channelCode;

    /**
     * 来源系统.
     */
    private String sourceSystem;


    private String originalText;

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getContentKey() {
        return contentKey;
    }

    public void setContentKey(String contentKey) {
        this.contentKey = contentKey;
    }

    public Integer getVlogId() {
        return vlogId;
    }

    public void setVlogId(Integer vlogId) {
        this.vlogId = vlogId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }
}
