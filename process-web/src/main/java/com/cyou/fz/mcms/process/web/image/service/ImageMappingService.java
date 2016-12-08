package com.cyou.fz.mcms.process.web.image.service;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.cyou.fz.common.utils.mybatis.bean.Query;
import com.cyou.fz.common.utils.mybatis.service.BaseServiceImpl;
import com.cyou.fz.mcms.process.core.utils.S3Util;
import com.cyou.fz.mcms.process.web.common.SystemConstants;
import com.cyou.fz.mcms.process.web.image.bean.ImageMapping;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by cnJason on 2016/12/8.
 */
@Service
public class ImageMappingService  extends BaseServiceImpl<ImageMapping> {

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);


    public void refreshDisabledImage(){

    }

    /**
     * 上传图片
     * @param originImage
     * @param currentImage
     */
    public  String uploadImage(String originImage,String currentImage,String cdnPathUrl){

        ImageMapping imageMapping = new ImageMapping();
        imageMapping.setOriginUrl(originImage);
        imageMapping.setCurrentUrl(currentImage);

        Query<ImageMapping> imageMappingQuery = Query.build(ImageMapping.class);
        imageMappingQuery.addEq("originUrl",originImage);
        List<ImageMapping> imageMappingList = findByQuery(imageMappingQuery);
        if(imageMappingList.size() > 0 ){
            if(imageMappingList.get(0).getStatus().intValue() == ImageMapping.STATUS_SUCCES) {
                return imageMapping.getCurrentUrl();
            }else {
                processImage(imageMappingList.get(0).getId(),originImage,currentImage,cdnPathUrl);
            }
        }else {
            processImage(null,originImage,currentImage,cdnPathUrl);
        }
        return currentImage;
    }

    private void processImage(Integer imageId,String originImage,String currentImage, String cdnPathUrl) {
        executor.submit(() ->{
            Integer imgId= 0;
            ImageMapping imageMapping = new ImageMapping();
            imageMapping.setOriginUrl(originImage);
            imageMapping.setCurrentUrl(currentImage);
            if(imageId == null){
               ImageMapping retObject =  insert(imageMapping);
               if(retObject != null){
                   imgId = retObject.getId();
               }
            }else {
                imgId = imageId;
            }


            S3Util s3Util = S3Util.getInstance(SystemConstants.endpoint,SystemConstants.accessKey,SystemConstants.secretKey);
            PutObjectResult result = null;
            try {
                result =s3Util.putRemoteImage(originImage,cdnPathUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(result.getETag() != null){
                imageMapping.setId(imgId);
                imageMapping.setStatus(ImageMapping.STATUS_SUCCES);
                update(imageMapping);
            }

        });
    }

}
