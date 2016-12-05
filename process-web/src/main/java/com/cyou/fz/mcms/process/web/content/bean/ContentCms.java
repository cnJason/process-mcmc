package com.cyou.fz.mcms.process.web.content.bean;

import com.cyou.fz.common.utils.mybatis.annotations.Id;
import com.cyou.fz.common.utils.mybatis.annotations.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by cnJason on 2016/12/2.
 */
@Table(value = "t_content_cms")
public class ContentCms  implements Serializable{
    private static final long serialVersionUID = 4396372259762903379L;
    public static final String COLUMN_CONTENT_KEY = "contentKey";

    @Id(value = "content_id")
    private Integer id;


    private Integer channelCode;

    private String defaultCategory;

    private String contentKey;

    private String categoryIds;

    private String tags;

    private String title;

    private String smallTitle;

    private String keywords;

    private String author;

    private String description;

    private String imgPath;

    private String imgTitle;

    private Date publishTime;

    private Date processTime;

    private String gameCodes;

    private String pageUrl;

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(Integer channelCode) {
        this.channelCode = channelCode;
    }

    public String getDefaultCategory() {
        return defaultCategory;
    }

    public void setDefaultCategory(String defaultCategory) {
        this.defaultCategory = defaultCategory;
    }

    public String getContentKey() {
        return contentKey;
    }

    public void setContentKey(String contentKey) {
        this.contentKey = contentKey;
    }

    public String getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSmallTitle() {
        return smallTitle;
    }

    public void setSmallTitle(String smallTitle) {
        this.smallTitle = smallTitle;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getImgTitle() {
        return imgTitle;
    }

    public void setImgTitle(String imgTitle) {
        this.imgTitle = imgTitle;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Date getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Date processTime) {
        this.processTime = processTime;
    }

    public String getGameCodes() {
        return gameCodes;
    }

    public void setGameCodes(String gameCodes) {
        this.gameCodes = gameCodes;
    }
}
