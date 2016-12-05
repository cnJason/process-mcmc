package com.cyou.fz.mcms.process.core;

import com.cyou.fz.mcms.process.core.bean.ContentDTO;
import com.cyou.fz.mcms.process.core.bean.VlogDTO;

/**
 * Created by cnJason on 2016/11/25.
 */
public class VlogProcess implements IProcess {


    @Override
    public ContentDTO process(ContentDTO contentDTO) {
        if(contentDTO instanceof VlogDTO){

        }
        return null;
    }
}
