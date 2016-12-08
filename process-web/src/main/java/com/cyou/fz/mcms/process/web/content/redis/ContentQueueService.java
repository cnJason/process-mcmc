package com.cyou.fz.mcms.process.web.content.redis;

import com.cyou.fz.mcms.process.web.common.redis.IQueueService;
import com.cyou.fz.mcms.process.web.content.query.ContentQueryParam;
import com.cyou.fz.mcms.process.web.content.request.ContentRequest;
import com.cyou.fz.mcms.process.web.content.result.ContentQueueResult;
import com.google.common.collect.Lists;
import mjson.Json;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by cnJason on 2016/11/28.
 * 内容队列服务.
 */
@Component
public class ContentQueueService implements IQueueService {


    /**
     * logger.
     * */
    private Logger logger = LogManager.getRootLogger();

    /**
     * 一个批次的最大数量.
     */
    private int batchSize = 2000;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 防重复列表前缀.
     */
    public static final String DUMPLICATE_REMOVAL_PREFIX = "history:set_";

    /**
     * 内容信息详情对象前缀.
     */
    public static final String CONTENT_REQUEST_HASH_PREFIX ="detail:hash_";

    /**
     * 等待队列前缀.
     */
    public static final String WAITING_QUEUE_PREFIX="waiting:queue_";

    /**
     * 运行队列前缀.
     */
    public static final String RUNNING_QUEUE_PREFIX="running:queue_";

    /**
     * 失败队列前缀.
     */
    public static final String FAILURE_QUEUE_PREFIX="fail:queue_";

    /**
     * 失败信息前缀.
     */
    public static final String FAILURE_INFO_HASH_PREFIX="faul_info:hash_";

    /**
     * 成功队列前缀.
     */
    public static final String SUCCESS_QUEUE_PREFIX="success:queue_";

    /**
     * 成功信息hash前缀.
     */
    public static final String SUCCESS_INFO_HASH_PREFIX ="success_info:hash_";


    /**
     * 添加任务.
     * @param contentRequests  任务
     * @param removeDumplicated 当存在相同任务时是否移除相同任务
     * @return
     */
    @Override
    public boolean pushTaskToWaitingQueue(List<ContentRequest> contentRequests, boolean removeDumplicated) {
        for (ContentRequest contentRequest : contentRequests) {
            if(!removeDumplicated && isDumplicate(contentRequest)){
                continue;
            }
            pushSingleToWaitingQueue(contentRequest);
        }
        return true;
    }


    /**
     * 从待执行队列中获取指定数量的内容.
     * @param contentCount 内容数目
     * @return
     */
    @Override
    public List<ContentRequest> fetchTasksFromWaitingQueue(long contentCount) {
        HashSet<String>  contentCountList = (HashSet<String>) (redisTemplate.opsForZSet().range(getWaitingQueueIdentification(),0,contentCount-1));
        return getContentRequestByContentKey(contentCountList);
    }


    /**
     * 将内容从任务队列转移到执行队列中.
     * @param contentRequests 内容请求列表
     * @return 成功标示
     */
    @Override
    public boolean moveWaitingTaskToRunningQueue(List<ContentRequest> contentRequests) {
        //获取redis zsetoperations.
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        for (ContentRequest contentRequest : contentRequests) {
            String contentKey = contentRequest.getContentKey();
            //添加到运行中队列.
            zSetOperations.add(getRunningQueueIdentification(),contentKey,System.currentTimeMillis());
            //从待执行队列中删除.
            zSetOperations.remove(getWaitingQueueIdentification(),contentKey);
        }
        return true;
    }

    /**
     *  当任务失败的时候移到任务失败列表.
     * @param contentKey
     * @return
     */
    @Override
    public boolean moveRunningTaskToFailQueue(String contentKey,String message) {
        if(redisTemplate.opsForZSet().rank(getSuccessQueueIdentification(),contentKey) != null){
            redisTemplate.opsForZSet().remove(getSuccessQueueIdentification(),contentKey);
        }

        redisTemplate.opsForZSet().add(getFailQueueIdentification(),contentKey,System.currentTimeMillis());
        redisTemplate.opsForHash().put(getFailInfoHashIdentification(),contentKey,message);
        redisTemplate.opsForZSet().remove(getRunningQueueIdentification(),contentKey);
        //清理去重列表
        redisTemplate.opsForSet().remove(getUniqueKeySetIdentification(),contentKey);
        return true;
    }

    @Override
    public boolean moveRunningTaskToSuccessQueue(ContentRequest contentRequest) {
        String contentKey = contentRequest.getContentKey();
        if(redisTemplate.opsForZSet().rank(getSuccessQueueIdentification(),contentKey) !=null){
            redisTemplate.opsForZSet().remove(getSuccessQueueIdentification(),contentKey);
        }

        redisTemplate.opsForZSet().add(getSuccessQueueIdentification(),contentKey,System.currentTimeMillis());
        redisTemplate.opsForHash().put(getSuccessInfoHashIdentification(),contentKey, Json.make(contentRequest).toString());
        redisTemplate.opsForZSet().remove(getRunningQueueIdentification(),contentKey);
        //清理去重列表.
        redisTemplate.opsForSet().remove(getUniqueKeySetIdentification(),contentKey);
        return true;
    }


    /**
     * 刷新超时队列.
     * @param timeout 超时时间.
     */
    @Override
    public void refreshBreakedQueue(Long timeout) {
        Long endTime = System.currentTimeMillis() - timeout;
        Set<String> timeoutContentKeySet = (Set<String>)redisTemplate.opsForZSet().rangeByScore(getRunningQueueIdentification(),0,endTime);

        for (String contentKey : timeoutContentKeySet) {
            //加入待执行队列
            redisTemplate.opsForZSet().add(getWaitingQueueIdentification(),contentKey,System.currentTimeMillis());
            //加入任务去重集合.
            redisTemplate.opsForSet().add(getUniqueKeySetIdentification(),contentKey);
        }
        redisTemplate.opsForZSet().removeRangeByScore(getRunningQueueIdentification(),0,endTime);

    }



    @Override
    public ContentQueueResult queryWaitingQueues(ContentQueryParam queryParam) {

        return queueQueryByParam(queryParam);
    }

    @Override
    public ContentQueueResult queryRunningQueues(ContentQueryParam queryParam) {
        return queueQueryByParam(queryParam);
    }

    @Override
    public ContentQueueResult queryFailQueues(ContentQueryParam queryParam) {
        return queueQueryByParam(queryParam);
    }

    @Override
    public ContentQueueResult querySuccessQueues(ContentQueryParam queryParam) {
        return queueQueryByParam(queryParam);
    }

/************************************任务集合 start***************************************************/
    /**
     * 防止任务重复的key集合.
     *
     * @return 集合名
     */
    private String getUniqueKeySetIdentification() {
        return  DUMPLICATE_REMOVAL_PREFIX + "data";
    }

    /**
     * 获取等待队列唯一标示.
     * @return
     */
    private String getWaitingQueueIdentification() {
        return WAITING_QUEUE_PREFIX + "data";
    }

    /**
     * 获取内容详细信息的hash表标识
     *
     * @return
     */
    private String getContentRequestHashIdentification() {
        return CONTENT_REQUEST_HASH_PREFIX + "data";
    }

    /**
     * 获取运行时队列标识.
     * @return
     */
    private String getRunningQueueIdentification() {
        return RUNNING_QUEUE_PREFIX + "data";
    }

    /**
     * 获取成功队列标识.
     * @return 成功标识
     */
    private String getSuccessQueueIdentification() {
        return SUCCESS_QUEUE_PREFIX + "data";
    }

    /**
     * 获取失败队列标识
     * @return 失败标识
     */
    private String getFailQueueIdentification() {

        return FAILURE_QUEUE_PREFIX + "data";
    }

    /**
     * 失败信息标识.
     * @return 失败信息标识
     */
    private String getFailInfoHashIdentification(){
        return FAILURE_INFO_HASH_PREFIX +"data";
    }


    /**
     * 成功信息标识.
     * @return 成功信息标识
     */
    private String getSuccessInfoHashIdentification() {
        return SUCCESS_INFO_HASH_PREFIX + "data";
    }



    /**
     * 加入任务到等待队列.
     *
     * @param requests 批量队列
     * @return
     */
    private boolean pushSingleToWaitingQueue(ContentRequest requests) {
        //加入待执行队列
        redisTemplate.opsForZSet().add(getWaitingQueueIdentification(), requests.getContentKey(), System.currentTimeMillis());
        //加入任务信息表
        Json infoJson = Json.object().set("originalText",requests.getOriginalText())
                                     .set("contentKey",requests.getContentKey())
                                     .set("sourceSystem",requests.getSourceSystem())
                                     .set("channelCode",requests.getChannelCode())
                                     .set("vlogId",requests.getVlogId());
        redisTemplate.opsForHash().put(getContentRequestHashIdentification(), requests.getContentKey(), infoJson.toString());
        //加入任务去重集合
        redisTemplate.opsForSet().add(getUniqueKeySetIdentification(), requests.getContentKey());
        return true;
    }


    /**
     * 判断这个任务内容是否重复.
     * @param contentRequest
     * @return
     */
    private boolean isDumplicate(ContentRequest contentRequest) {
        return redisTemplate.opsForSet().isMember(getUniqueKeySetIdentification(),contentRequest.getContentKey());
    }


    /**
     * 通过contentkey从redis中获取缓存内容.
     * @param contentKeyList 内容key列表
     * @return
     */
    private List<ContentRequest> getContentRequestByContentKey(HashSet<String> contentKeyList) {
        List<String> contentDetailList = redisTemplate.opsForHash().multiGet(getContentRequestHashIdentification(),contentKeyList);
        List<ContentRequest> contentList = Lists.newArrayList();

        for (String contentDetailInfo : contentDetailList) {
            Json contentDetailInfoJson = Json.read(contentDetailInfo);
            ContentRequest contentRequest = new ContentRequest();

            contentRequest.setSourceSystem(contentDetailInfoJson.at("sourceSystem").asString());
            if(contentRequest.getSourceSystem().contentEquals(ContentRequest.SOURCE_SYSTEM_CMS)){
                contentRequest.setOriginalText(contentDetailInfoJson.at("originalText").asString());
                contentRequest.setContentKey(contentDetailInfoJson.at("contentKey").asString());
                contentRequest.setChannelCode(contentDetailInfoJson.at("channelCode").asString());
            }
            if(contentRequest.getSourceSystem().contentEquals( ContentRequest.SOURCE_SYSTEM_VLOG)){
                contentRequest.setVlogId(contentDetailInfoJson.at("vlogId").asInteger());
            }
            contentList.add(contentRequest);
        }
        return contentList;
    }

    /**
     * 根据参数查询队列结果
     *
     * @param queryParam
     * @return
     */
    private ContentQueueResult queueQueryByParam(ContentQueryParam queryParam) {
        int pageIndex = queryParam.getPageNo();
        int pageSize = queryParam.getPageSize();
        String queueName = queryParam.getQueueName();

        String queueIdentification = "";
        String infoDataIdentification = null;
        if (Objects.equals(queueName,"waiting")) {
            queueIdentification = getWaitingQueueIdentification();
        } else if (Objects.equals(queueName,"running")) {
            queueIdentification = getRunningQueueIdentification();
        } else if (Objects.equals(queueName,"success")) {
            queueIdentification = getSuccessQueueIdentification();
            infoDataIdentification = getSuccessInfoHashIdentification();
        } else if (Objects.equals(queueName,"fail")) {
            queueIdentification = getFailQueueIdentification();
            infoDataIdentification = getFailInfoHashIdentification();
        }

        long total = redisTemplate.opsForZSet().size(queueIdentification);
        long pageCount = total / pageSize + (total % pageSize == 0 ? 0 : 1);

        ContentQueueResult queryResult = new ContentQueueResult();
        queryResult.setTotalCount(total);
        queryResult.setTotalPages(pageCount);

        List<ContentRequest> contentRequestList = Lists.newArrayList();

        Set<DefaultTypedTuple> tupleList = redisTemplate.opsForZSet().rangeWithScores(queueIdentification, (pageIndex) * pageSize, (pageIndex + 1) * pageSize - 1);
        for (DefaultTypedTuple tuple : tupleList) {
            String contentKey = String.valueOf(tuple.getValue());
            String contentRequestJson = String.valueOf(redisTemplate.opsForHash().get(infoDataIdentification, contentKey));
            Json contentDetailInfoJson = Json.make(contentRequestJson);
            ContentRequest contentRequest = new ContentRequest();
            contentRequest.setContentKey(contentDetailInfoJson.at("contentKey").asString());
            contentRequest.setChannelCode(contentDetailInfoJson.at("channelCode").asString());
            contentRequest.setSourceSystem(contentDetailInfoJson.at("sourceSystem").asString());
            contentRequest.setVlogId(contentDetailInfoJson.at("vlogId").asInteger());

            if (contentRequest == null) {
                continue;
            }
            contentRequestList.add(contentRequest);
        }
        queryResult.setRows(contentRequestList);
        return queryResult;
    }


    /**
     * 获取正在运行队列.
     * @return
     */
    public long getRunningQueneNum() {
        return redisTemplate.opsForZSet().size(getRunningQueueIdentification());
    }

    public void removeFromWaitingQueue(ContentRequest contentRequest) {
        redisTemplate.opsForZSet().remove(getWaitingQueueIdentification(),contentRequest.getContentKey());

    }

    public List<String> deleteBreakedQueue(long timeout) {
        List<String> requestList = Lists.newArrayList();
        Long endTime = System.currentTimeMillis() - timeout;
        Set<DefaultTypedTuple>  tupleList = redisTemplate.opsForZSet().rangeByScore(getRunningQueueIdentification(),0,endTime);
        for (DefaultTypedTuple tuple : tupleList) {
            String contentKey = String.valueOf(tuple.getValue());

            if (contentKey == null) {
                continue;
            }
            requestList.add(contentKey);
        }
        redisTemplate.opsForZSet().removeRangeByScore(getRunningQueueIdentification(),0,endTime);
        return requestList;
    }
}
