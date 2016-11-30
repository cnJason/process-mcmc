package com.cyou.fz.mcms.process.web.service.content.query;

/**
 * Created by cnJason on 2016/11/28.
 */
public class ContentQueryParam {

    private String queueName;

    private String contentKey;

    private Integer vlogId;

    private String channelCode;

    private int pageNo;

    private int pageSize;


    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
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

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
