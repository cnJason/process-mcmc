package com.cyou.fz.mcms.process.web.common.redis;

import com.cyou.fz.mcms.process.web.service.content.query.ContentQueryParam;
import com.cyou.fz.mcms.process.web.service.content.request.ContentRequest;
import com.cyou.fz.mcms.process.web.service.content.result.ContentQueueResult;

import java.util.HashSet;
import java.util.List;

/**
 * Created by cnJason on 2016/11/28.
 */
public interface IQueueService {

    /**
     * 添加任务到待执行队列
     *
     * @param contentRequests  任务
     * @param removeDumplicated 当存在相同任务时是否移除相同任务
     * @return
     */
    boolean pushTaskToWaitingQueue(List<ContentRequest> contentRequests, boolean removeDumplicated);


    /**
     * 从待执行队列中拿取指定数目的任务
     *
     * @param taskCount 任务数目
     * @return
     */
    List<ContentRequest> fetchTasksFromWaitingQueue(long taskCount);

    /**
     * 将任务从待执行移到执行中队列
     *
     * @param contentRequests
     */
    boolean moveWaitingTaskToRunningQueue(List<ContentRequest> contentRequests);

    /**
     * 当任务执行失败后将任务移到失败队列
     * @param code
     * @return
     */
    boolean moveRunningTaskToFailQueue(String code,String message);

    /**
     * 从执行中队列把成功的任务移到成功队列
     *
     * @param  crawlerResult
     * @return
     */
    boolean moveRunningTaskToSuccessQueue(ContentRequest crawlerResult);


    /**
     * 刷新超时队列（把超时的运行中队列任务重新加入待执行队列）
     *
     * @param timeout
     */
    void refreshBreakedQueue(Long timeout);

    /**
     * 查询待执行队列
     * @param queryParam
     * @return
     */
    ContentQueueResult queryWaitingQueues(ContentQueryParam queryParam);
    /**
     * 查询执行中队列
     * @param queryParam
     * @return
     */
    ContentQueueResult queryRunningQueues(ContentQueryParam queryParam);

    /**
     * 查询失败队列
     * @param queryParam
     * @return
     */
    ContentQueueResult queryFailQueues(ContentQueryParam queryParam);

    /**
     * 查询成功队列
     * @param queryParam
     * @return
     */
    ContentQueueResult querySuccessQueues(ContentQueryParam queryParam);
}
