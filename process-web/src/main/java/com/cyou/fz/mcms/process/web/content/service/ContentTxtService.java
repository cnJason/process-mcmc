package com.cyou.fz.mcms.process.web.content.service;

import com.cyou.fz.common.utils.mybatis.bean.Query;
import com.cyou.fz.common.utils.mybatis.service.BaseServiceImpl;
import com.cyou.fz.mcms.process.web.content.bean.ContentTxt;
import org.springframework.stereotype.Service;


/**
 * Created by cnJason on 2016/12/2.
 */
@Service
public class ContentTxtService extends BaseServiceImpl<ContentTxt> {


    public ContentTxt getByContentKey(String contentKey){
        Query<ContentTxt> q = Query.build(ContentTxt.class);
        q.addEq(ContentTxt.COLUMN_CONTENT_KEY,contentKey);
        return  get(q);
    }

}
