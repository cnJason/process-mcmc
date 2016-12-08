package com.cyou.fz.mcms.process.web.content.service;

import com.cyou.fz.common.utils.mybatis.bean.Query;
import com.cyou.fz.common.utils.mybatis.service.BaseServiceImpl;
import com.cyou.fz.mcms.process.core.bean.ArticleProcessDTO;
import com.cyou.fz.mcms.process.web.common.SystemConstants;
import com.cyou.fz.mcms.process.web.content.bean.*;
import com.cyou.fz.mcms.process.web.content.job.ContentTaskJobUtils;
import com.cyou.fz.mcms.process.web.content.query.ContentTaskParams;
import com.cyou.fz.mcms.process.web.content.redis.ContentQueueService;
import com.cyou.fz.mcms.process.web.content.request.ContentRequest;
import com.cyou.fz.services.cms.ContentService;
import com.cyou.fz.services.cms.commons.CmsQueryResult;
import com.cyou.fz.services.cms.dto.ContentDTO;
import com.cyou.fz.services.cms.model.ContentQueryParam;
import com.google.common.collect.Lists;
import mjson.Json;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

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


    private Logger logger = Logger.getLogger("contentBaseService");


    /**
     * 添加内容任务对象.
     *
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

    public Boolean deleteContentTask(ContentTaskParams contentTaskParams) {
        ContentTask contentTask = new ContentTask();
        contentTask.setChannelCode(contentTaskParams.getChannelCode());
        contentTask.setContentType(contentTaskParams.getContentType());
        //保存任务至job对象中.
        contentTaskJobUtils.deleteContentTaskJob(contentTask);
        return true;
    }

    /**
     * 内容执行任务.
     * 包含获取列表，推送到分析队列.
     *
     * @param params 内容任务列表.
     */
    public void processContentTask(ContentTaskParams params) {
        ContentQueryParam contentQueryParam = new ContentQueryParam();
        contentQueryParam.setChannelCode(params.getChannelCode());
        contentQueryParam.setContentType(params.getContentType());
        contentQueryParam.setPageNo(SystemConstants.DEFAULT_PAGE_NO);
        contentQueryParam.setPageSize(SystemConstants.DEFAULT_PAGE_SIZE);

        CmsQueryResult<ContentDTO> result = contentService.queryContent(contentQueryParam);

        //发送到清洗队列中进行清洗.
        sendListToProcess(result);
        int totalCount = (int) result.getTotalCount();

        int range = (totalCount / SystemConstants.DEFAULT_PAGE_SIZE) + 1;
        if(range > 6){
            //目前最多支持一个频道1200条数据.
            range = 6;
        }

        for (int i = 2; i <= range; i++) {
            contentQueryParam.setPageNo(i);
            CmsQueryResult<ContentDTO> pagedResult = contentService.queryContent(contentQueryParam);

            //分页发送到清洗队列中进行清洗.
            sendListToProcess(pagedResult);
        }
    }


    private void sendListToProcess(CmsQueryResult<ContentDTO> contentDTOCmsQueryResult) {
        List<ContentDTO> contentDTOList = contentDTOCmsQueryResult.getList();
        //contentRequestList(contentRequest对象是供redis队列使用)
        boolean insertContentCms = false;
        List<ContentRequest> requestList = Lists.newArrayList();
        for (ContentDTO contentDTO : contentDTOList) {
            String contentKey = contentDTO.getContentKey();

            if(contentKey ==null){
                continue;
            }
            ContentBase contentBase = getByContentKey(contentKey);

            if (contentBase == null) {
                contentBase = new ContentBase();
                // 设置内容key
                contentBase.setContentKey(contentDTO.getContentKey());
                // 设置内容类型
                contentBase.setContentType(contentDTO.getContentType());
                // 设置状态为待处理
                contentBase.setStatus(ContentBase.STATUS_PENDING);
                // 插入基础对象.
                insert(contentBase);
            }
            if (contentBase != null) {
                // 插入信息对象.
                ContentCms contentCms = contentCmsService.getByContentKey(contentKey);
                 insertContentCms = false;
                if(contentCms == null){
                    contentCms = new ContentCms();
                    insertContentCms = true;
                }
                // 内容key
                contentCms.setContentKey(contentDTO.getContentKey());
                // 频道编号
                contentCms.setChannelCode(contentDTO.getChannelCode());
                // 作者
                contentCms.setAuthor(contentDTO.getAuthor());
                // 分类id
                contentCms.setCategoryIds(contentDTO.getCategoryIds());
                // 默认分类
                contentCms.setDefaultCategory(contentDTO.getDefaultCategory() + "");
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
                // 标题
                contentCms.setTitle(contentDTO.getTitle());
                // 发布时间
                contentCms.setPublishTime(new Date(contentDTO.getPublishTime()));
                // 副标题
                contentCms.setSmallTitle(contentDTO.getSmallTitle());
                // 设置url
                contentCms.setPageUrl(contentDTO.getPageUrl());

                if(insertContentCms){
                    contentCmsService.insert(contentCms);
                }else {
                    contentCmsService.update(contentCms);
                }
            }
            ContentRequest contentRequest = makeContentRequest(contentDTO);
            if(contentBase.getStatus().intValue() != ContentBase.STATUS_SUCCESS){
                requestList.add(contentRequest);

            }
        }
        logger.info("当前已经发送清洗对象数量为："+requestList.size());
        contentQueueService.pushTaskToWaitingQueue(requestList, true);
    }

    public ContentBase getByContentKey(String contentKey) {
        Query<ContentBase> query = Query.build(ContentBase.class);
        query.addEq(ContentBase.COLUMN_CONTENT_KEY, contentKey);
        return get(query);
    }


    /**
     * 存储文本和发送内容至待处理列表.
     *
     * @param contentDTO 文本对象.
     */
    private ContentRequest makeContentRequest(ContentDTO contentDTO) {
        ContentDTO txtObject = contentService.getContent(contentDTO.getContentKey());

        // 设置初始文本对象.
        if (txtObject != null) {
            ContentTxt contentTxt = contentTxtService.getByContentKey(contentDTO.getContentKey());
            // 未清洗文本
            if(contentTxt == null) {
                contentTxt = new ContentTxt();
                contentTxt.setOriginalText(txtObject.getContentText());
                contentTxt.setContentKey(contentDTO.getContentKey());
                contentTxtService.insert(contentTxt);
            }else {
                contentTxt.setOriginalText(txtObject.getContentText());
                contentTxtService.update(contentTxt);
            }
        }

        // 创建内容处理对象
        ContentRequest contentRequest = new ContentRequest();
        // 频道号
        contentRequest.setChannelCode(contentDTO.getChannelCode() + "");
        // 内容key
        contentRequest.setContentKey(contentDTO.getContentKey());
        // 原始文本信息
        contentRequest.setOriginalText(txtObject.getContentText());
        // 来源系统
        contentRequest.setSourceSystem(ContentRequest.SOURCE_SYSTEM_CMS);

        return contentRequest;
    }


    /**
     * 设置内容为失败.
     *
     * @param contentRequest
     * @param articleProcessDTO
     */
    public void markContentFailure(ContentRequest contentRequest, ArticleProcessDTO articleProcessDTO) {
        String contentKey = contentRequest.getContentKey();
        // 设置主对象为清洗失败.且附加时间
        markContentStatus(contentKey, ContentBase.STATUS_FAILURE);
        // 转移对象至成功持久队列.
        contentQueueService.moveRunningTaskToFailQueue(contentKey, articleProcessDTO.getMessage());
    }

    /**
     * 设置内容为完成.
     *
     * @param contentRequest    内容请求对象
     * @param articleProcessDTO 处理后对象.
     */
    public void markContentFinish(ContentRequest contentRequest, ArticleProcessDTO articleProcessDTO) {
        String contentKey = contentRequest.getContentKey();
        // 赋值处理后的文本对象.
        ContentTxt contentTxt = contentTxtService.getByContentKey(contentKey);
        contentTxt.setProcessText(articleProcessDTO.getContent());
        // 赋值处理后的图片对象.
        contentTxt.setPicList(Json.make(articleProcessDTO.getPicList()).toString());
        // 赋值处理后的视频图片对象.
        contentTxt.setvPicList(Json.make(articleProcessDTO.getvPicList()).toString());
        // 修改内容字段.
        contentTxtService.update(contentTxt);

        // 设置主对象为清洗成功.且附加时间
        markContentStatus(contentKey, ContentBase.STATUS_SUCCESS);
        // 转移对象至成功持久队列.
        logger.info("内容清洗完毕:"+contentTxt.getContentKey());
        contentQueueService.moveRunningTaskToSuccessQueue(contentRequest);
    }

    /**
     * 更新内容基础对象.
     *
     * @param contentKey
     */
    private void markContentStatus(String contentKey, Integer status) {
        // 修改主对象的信息.
        Query<ContentBase> query = Query.build(ContentBase.class);
        query.addEq(ContentBase.COLUMN_CONTENT_KEY, contentKey);
        ContentBase contentBase = get(query);
        if (contentBase != null) {
            contentBase.setStatus(status);
            update(contentBase);
        }
        if (status == ContentBase.STATUS_SUCCESS) {
            // 修改内容基本表的时间
            Query<ContentCms> cmsQuery = Query.build(ContentCms.class);
            cmsQuery.addEq(ContentCms.COLUMN_CONTENT_KEY, contentKey);
            ContentCms contentCms = contentCmsService.get(cmsQuery);
            if (contentCms != null) {
                contentCms.setProcessTime(new Date());
                contentCmsService.update(contentCms);
            }
        }

    }


    public McmsContentDTO getMcmsContentDTOByContentKey(String contentKey) {
        ContentBase contentBase = getByContentKey(contentKey);
        if(contentBase ==null || contentBase.getStatus().intValue() != ContentBase.STATUS_SUCCESS){
            return null;
        }
        McmsContentDTO contentDTO = new McmsContentDTO();
        contentDTO.setContentKey(contentBase.getContentKey());

        BeanUtils.copyProperties(contentBase,contentDTO);
        ContentCms contentCms = contentCmsService.getByContentKey(contentKey);
        if(contentBase != null){
            BeanUtils.copyProperties(contentCms,contentDTO);
            contentDTO.setTitle(contentCms.getTitle());
        }
        ContentTxt contentTxt = contentTxtService.getByContentKey(contentKey);

        if(contentTxt != null){
            Json json = Json.read(contentTxt.getPicList());
            List<Object> picList = json.asList();
            List<String> pList = Lists.newArrayList();
            for (Object o : picList) {
                pList.add((String) o);
            }
            contentDTO.setPicList(pList);
            contentDTO.setvPicList(Lists.newArrayList());
            if(contentTxt.getvPicList()!= null){

            }
            contentDTO.setContentText(contentTxt.getProcessText());
        }
        return contentDTO;
    }



}
