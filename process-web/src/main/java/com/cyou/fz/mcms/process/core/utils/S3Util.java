package com.cyou.fz.mcms.process.core.utils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.io.InputStream;
import java.net.*;

/**
 * Created by cnJason on 2016/12/7.
 */
public class S3Util {


    private String accessKey;

    private AmazonS3 s3Client;

    private static S3Util s3Util = null;

    // 默认的最大连接数.
    private static final int MAX_CONNECTION = 200;

    // 默认的链接超时时间.
    private static final int CONNECTION_TIMEOUT = 5000;
    // 重试次数.
    private static final int ERROR_RETRY = 3;
    // socket 超时时间
    private static final int SOCKET_TIMEOUT = 20000;
    // http连接超时时间
    private static final int HTTP_CONNECTION_TIMEOUT = 10000;
    // http读取超时时间
    private static final int HTTP_READ_TIMEOUT = 10000;
    // 成功response code
    private static final int SUCCESS_RESPONSE = 200;




    private S3Util(){

    }

    /**
     * 获取实例.
     * @param endPoint
     * @param accessKey
     * @param secretKey
     * @return
     */
    public  static  synchronized S3Util getInstance(String endPoint,String accessKey,String secretKey){
        if(s3Util == null){
            s3Util = new S3Util();
            ClientConfiguration conf = new ClientConfiguration();
            conf.setMaxConnections(MAX_CONNECTION);
            conf.setConnectionTimeout(CONNECTION_TIMEOUT);
            conf.setMaxErrorRetry(ERROR_RETRY);
            conf.setSocketTimeout(SOCKET_TIMEOUT);

            BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey,secretKey);
            AmazonS3Client client = new AmazonS3Client(basicAWSCredentials,conf);
            client.setEndpoint(endPoint);
            s3Util.accessKey = accessKey;
            s3Util.s3Client =client;
        }
        return s3Util;
    }

    /**
     * 直接把url地址转换成s3地址.
     * @param imageUrl url地址
     * @param imageKey 转换的key
     *
     * @return s3对象
     * @throws Exception 异常
     */
    public PutObjectResult putRemoteImage(String imageUrl,String imageKey) throws Exception {
        ObjectMetadata metadata = new ObjectMetadata();
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        PutObjectResult result = null;

        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent","17173AppV3");
            HttpURLConnection.setFollowRedirects(false);
            connection.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);
            connection.setReadTimeout(HTTP_READ_TIMEOUT);
            if(connection.getResponseCode() != 200){
                throw  new Exception("http resonse code is "+ connection.getResponseCode());
            }
            inputStream = connection.getInputStream();

            metadata.setContentLength((long)connection.getContentLength());
            result = this.s3Client.putObject(new PutObjectRequest(this.accessKey,imageKey,inputStream,metadata));

        }catch (Exception e){
            throw e;
        }finally {

            if(connection != null){
                connection.disconnect();
            }

            if(inputStream != null){
                inputStream.close();
            }
        }

        return result;
    }

    /**
     * 传输本地文件
     * @param file  文件对象
     * @param imageKey 转换的key
     * @return  s3对象
     * @throws Exception
     */
    public PutObjectResult putLocalImage(File file, String imageKey)throws Exception{
        try{
            PutObjectResult result = this.s3Client.putObject(new PutObjectRequest(accessKey,imageKey,file));
            return result;
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * 传输本地文件流
     * @param inputStream
     * @param length
     * @param imageKey
     * @return
     * @throws Exception
     */
    public PutObjectResult putLocalImageInputStream(InputStream inputStream,long length, String imageKey)throws Exception{
        PutObjectResult result ;
        try{
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(length);
            result = s3Client.putObject(new PutObjectRequest(accessKey,imageKey,inputStream,metadata));
        }catch (Exception e){
            throw e;
        }finally {
            if(inputStream != null){
                inputStream.close();
            }
        }
        return result;
    }

    public ObjectListing getS3ImageList(String prefix)throws Exception{
        try{
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
            listObjectsRequest.setBucketName(accessKey);
            listObjectsRequest.setPrefix(prefix);
            return s3Client.listObjects(listObjectsRequest);
        }catch (Exception e){
            throw e;
        }
    }


    public void deleteImage(String imageKey)throws Exception{
        try {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(accessKey,imageKey);
            s3Client.deleteObject(deleteObjectRequest);
        }catch (Exception e){
            throw e;
        }
    }


    public static void main(String[] args) throws Exception {

        S3Util s3Util = getInstance("http://ds.internal.17173cdn.com","07qu2c","9QrMEf1DevBwSzpXOa");
        PutObjectResult result = s3Util.putRemoteImage("http://img03.3dmgame.com/uploads/allimg/161207/271_161207062655_1.jpg","YWxqaGBf/mobileme/pic/cms/2016/12/7/2413.jpg");
        System.out.println(result.getETag());
    }

}
