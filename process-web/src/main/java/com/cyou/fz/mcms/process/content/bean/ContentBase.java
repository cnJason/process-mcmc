package com.cyou.fz.mcms.process.content.bean;

import com.cyou.fz.common.utils.mybatis.annotations.Id;
import com.cyou.fz.common.utils.mybatis.annotations.Table;

import java.io.Serializable;

/**
 * Created by cnJason on 2016/12/2.
 */
@Table(value = "t_content_base")
public class ContentBase implements Serializable {


    private static final long serialVersionUID = 8630761458262267059L;

    /**
     * 待清洗状态.
     */
    public static final Integer STATUS_PENDING = 2;
    /**
     * 清洗成功状态
     */
    public static final Integer STATUS_SUCCESS = 1;
    /**
     * 清洗失败状态
     */
    public static final Integer STATUS_FAILURE = -1;


    @Id(value = "id")
    private Integer id;


    private String contentKey;

    private Integer contentType;

    private Integer status;

    private String vlogId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContentKey() {
        return contentKey;
    }

    public void setContentKey(String contentKey) {
        this.contentKey = contentKey;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getVlogId() {
        return vlogId;
    }

    public void setVlogId(String vlogId) {
        this.vlogId = vlogId;
    }
}
