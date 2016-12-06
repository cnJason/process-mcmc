package com.cyou.fz.mcms.process.web.content.service;

import com.cyou.fz.common.utils.mybatis.bean.Query;
import com.cyou.fz.common.utils.mybatis.service.BaseServiceImpl;
import com.cyou.fz.mcms.process.web.content.bean.ContentCms;
import org.springframework.stereotype.Service;

/**
 * Created by cnJason on 2016/12/2.
 */
@Service
public class ContentCmsService extends BaseServiceImpl<ContentCms>{


    /**
     * 获取内容对象.
     * @param contentKey
     * @return
     */
    public ContentCms getByContentKey(String contentKey) {
        Query<ContentCms> query = Query.build(ContentCms.class);
        query.addEq(ContentCms.COLUMN_CONTENT_KEY,contentKey);
        return get(query);
    }

}
