package com.cyou.fz.mcms.process.core;

import com.cyou.fz.mcms.process.core.bean.ContentProcessDTO;
import com.cyou.fz.mcms.process.core.bean.VlogProcessDTO;

/**
 * Created by cnJason on 2016/11/25.
 */
public class VlogProcess implements IProcess {


    @Override
    public ContentProcessDTO process(ContentProcessDTO contentProcessDTO) {
        if(contentProcessDTO instanceof VlogProcessDTO){

        }
        return null;
    }
}
