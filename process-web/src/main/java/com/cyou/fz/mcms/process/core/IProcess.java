package com.cyou.fz.mcms.process.core;

import com.cyou.fz.mcms.process.core.bean.ContentProcessDTO;

/**
 * Created by cnJason on 2016/11/24.
 */
public interface IProcess {

    public ContentProcessDTO process(ContentProcessDTO contentProcessDTO);
}
