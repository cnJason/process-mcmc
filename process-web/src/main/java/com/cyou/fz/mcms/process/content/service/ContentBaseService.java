package com.cyou.fz.mcms.process.content.service;

import com.cyou.fz.common.utils.mybatis.service.BaseServiceImpl;
import com.cyou.fz.mcms.process.content.bean.ContentBase;
import com.cyou.fz.mcms.process.content.bean.ContentTask;
import com.cyou.fz.mcms.process.content.query.ContentTaskParams;
import mjson.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by cnJason on 2016/12/2.
 */
@Service
public class ContentBaseService extends BaseServiceImpl<ContentBase> {


    /**
     * 添加内容任务对象.
     * @param contentTaskParams 内容任务参数
     * @return 成功失败标识.
     */
    public Boolean addContentTask(ContentTaskParams contentTaskParams) {

        ContentTask contentTask = new ContentTask();
        contentTask.setChannelCode(contentTaskParams.getChannelCode());
        contentTask.setCronExpression(contentTaskParams.getCronExpression());
        //删除不需要存储的三个字段.
        contentTask.setQueryParams(Json.make(contentTaskParams).atDel("channelCode").atDel("cronExpression").atDel("needStore").toString());
        //是否需要持久化.
        if(contentTaskParams.getNeedStore()){
            //保存任务至job对象中.

        }else {
            processContentTask(contentTask);
        }
        return true;
    }


    /**
     * 内容执行任务.
     * 包含获取列表，推送到分析队列.
     * @param contentTask 内容任务列表.
     */
    private void processContentTask(ContentTask contentTask){

    }


}
