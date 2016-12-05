package com.cyou.fz.mcms.process.web.content.service;

import com.cyou.fz.common.utils.mybatis.service.BaseServiceImpl;
import com.cyou.fz.mcms.process.web.common.SystemConstants;
import com.cyou.fz.mcms.process.web.content.bean.ContentBase;
import com.cyou.fz.mcms.process.web.content.bean.ContentCms;
import com.cyou.fz.mcms.process.web.content.bean.ContentTask;
import com.cyou.fz.mcms.process.web.content.bean.ContentTxt;
import com.cyou.fz.mcms.process.web.content.job.ContentTaskJobUtils;
import com.cyou.fz.mcms.process.web.content.query.ContentTaskParams;
import com.cyou.fz.mcms.process.web.content.redis.ContentQueueService;
import com.cyou.fz.mcms.process.web.content.request.ContentRequest;
import com.cyou.fz.services.cms.ContentService;
import com.cyou.fz.services.cms.commons.CmsQueryResult;
import com.cyou.fz.services.cms.dto.ContentDTO;
import com.cyou.fz.services.cms.model.ContentQueryParam;
import com.google.common.collect.Lists;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by 内容处理对象 on 2016/12/2.
 */
@Service
public class ContentBaseService extends BaseServiceImpl<ContentBase> {


    /**
     * 定时器任务.
     */
    @Autowired
    private ContentTaskJobUtils contentTaskJobUtils;


    /**
     * cms内容对象DAO.
     */
    @Autowired
    private ContentCmsService contentCmsService;

    /**
     * cms内容文本对象DAO
     */
    @Autowired
    private ContentTxtService contentTxtService;


    /**
     * 内容队列服务
     */
    @Autowired
    private ContentQueueService contentQueueService;


    /**
     * 第三方服务.cmsContentService.
     */
    @Autowired
    private ContentService contentService;


    /**
     * 添加内容任务对象.
     * @param contentTaskParams 内容任务参数
     * @return 成功失败标识.
     */
    public Boolean addContentTask(ContentTaskParams contentTaskParams) {

        ContentTask contentTask = new ContentTask();
        contentTask.setChannelCode(contentTaskParams.getChannelCode());
        contentTask.setContentType(contentTaskParams.getContentType());
        contentTask.setCronExpression(contentTaskParams.getCronExpression());
        //保存任务至job对象中.
        try {
            contentTaskJobUtils.addContentTaskJob(contentTask);
        } catch (SchedulerException e) {
            return false;
        }
        return true;
    }




    /**
     * 内容执行任务.
     * 包含获取列表，推送到分析队列.
     * @param params 内容任务列表.
     */
    public void processContentTask(ContentTaskParams params){
        ContentQueryParam contentQueryParam = new ContentQueryParam();
        contentQueryParam.setChannelCode(params.getChannelCode());
        contentQueryParam.setContentType(params.getContentType());
        contentQueryParam.setPageNo(SystemConstants.DEFAULT_PAGE_NO);
        contentQueryParam.setPageSize(SystemConstants.DEFAULT_PAGE_SIZE);

        CmsQueryResult<ContentDTO> result = contentService.queryContent(contentQueryParam);
        //发送到清洗队列中进行清洗.
        sendListToProcess(result);
        int totalCount = (int) result.getTotalCount();

        int range = totalCount / SystemConstants.DEFAULT_PAGE_SIZE + 1 ;

        for (int i = 2; i <= range; i++) {
            contentQueryParam.setPageNo(i);
            CmsQueryResult<ContentDTO> pagedResult = contentService.queryContent(contentQueryParam);
            //分页发送到清洗队列中进行清洗.
            sendListToProcess(pagedResult);
        }
    }


    private void sendListToProcess(CmsQueryResult<ContentDTO> contentDTOCmsQueryResult){
        List<ContentDTO> contentDTOList =  contentDTOCmsQueryResult.getList();

        //contentRequestList(contentRequest对象是供redis队列使用)
        List<ContentRequest> requestList = Lists.newArrayList();

        for (ContentDTO contentDTO : contentDTOList) {
            ContentBase contentBase = new ContentBase();
            // 设置内容key
            contentBase.setContentKey(contentDTO.getContentKey());
            // 设置内容类型
            contentBase.setContentType(contentDTO.getContentType());
            // 设置状态为待处理
            contentBase.setStatus(ContentBase.STATUS_PENDING);
            // 插入基础对象.
            contentBase = insert(contentBase);

            if(contentBase != null){
                // 插入信息对象.
                ContentCms contentCms = new ContentCms();
                // 内容key
                contentCms.setContentKey(contentDTO.getContentKey());
                // 频道编号
                contentCms.setChannelCode(contentDTO.getChannelCode());
                // 作者
                contentCms.setAuthor(contentDTO.getAuthor());
                // 分类id
                contentCms.setCategoryIds(contentDTO.getCategoryIds());
                // 默认分类
                contentCms.setDefaultCategory(contentDTO.getDefaultCategory()+"");
                // 描述
                contentCms.setDescription(contentDTO.getDescription());
                // 游戏编号
                contentCms.setGameCodes(contentDTO.getGameCodes());
                // 图片地址
                contentCms.setImgPath(contentDTO.getImgPath());
                // 图片标题
                contentCms.setImgTitle(contentDTO.getImgTitle());
                // 关键词
                contentCms.setKeywords(contentDTO.getKeywords());
                // 标签
                contentCms.setTags(contentDTO.getTags());
                // 发布时间
                contentCms.setPublishTime(new Date(contentDTO.getPublishTime()));
                // 副标题
                contentCms.setSmallTitle(contentDTO.getSmallTitle());
                // 设置id
                contentCms.setId(contentBase.getId());

                contentCmsService.insert(contentCms);

                ContentRequest contentRequest = makeContentRequest(contentDTO,contentBase.getId());
                requestList.add(contentRequest);
            }

            // 发送至pending list.此处为初始添加。不清除重复的.
            contentQueueService.pushTaskToWaitingQueue(requestList,false);
        }
    }


    /**
     * 存储文本和发送内容至待处理列表.
     * @param contentDTO 文本对象.
     */
    private ContentRequest makeContentRequest(ContentDTO contentDTO, Integer contentId){
        ContentTxt contentTxt = new ContentTxt();
        ContentDTO txtObject = contentService.getContent(contentDTO.getContentKey());

        // 设置初始文本对象.
        if(txtObject != null){
            // 未清洗文本
            contentTxt.setOriginalText(txtObject.getContentText());
            contentTxt.setId(contentId);
            contentTxtService.insert(contentTxt);
        }

        // 创建内容处理对象
        ContentRequest contentRequest = new ContentRequest();
        // 频道号
        contentRequest.setChannelCode(contentDTO.getChannelCode() + "");
        // 内容key
        contentRequest.setContentKey(contentDTO.getContentKey());
        // 来源系统
        contentRequest.setSourceSystem(ContentRequest.SOURCE_SYSTEM_CMS);

        return contentRequest;
    }



}