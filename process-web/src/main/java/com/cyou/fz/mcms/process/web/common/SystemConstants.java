package com.cyou.fz.mcms.process.web.common;

/**
 * Created by cnJason on 2016/12/2.
 * 系统参数配置.
 */
public class SystemConstants {

    public static final int DEFAULT_PAGE_NO = 1;

    public static final int DEFAULT_PAGE_SIZE = 200;


    /**
     * s3 endpoint
     */
    public static String endpoint;

    /**
     * s3 accessKey
     */
    public static String accessKey;

    /**
     * s3 secretKey
     */
    public static String secretKey;


    /**
     * cdn domain.
     */
    public static String cdnDomain;


    /**
     * cdn domain key.
     */
    public static String cdnDomainKey;

    public void setCdnDomainKey(String cdnDomainKey) {
        this.cdnDomainKey = cdnDomainKey;
    }

    public String getCdnDomainKey() {
        return cdnDomainKey;
    }

    public String getCdnDomain() {
        return cdnDomain;
    }

    public void setCdnDomain(String cdnDomain) {
        this.cdnDomain = cdnDomain;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
