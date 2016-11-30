package com.cyou.fz.mcms.process.core;

import com.cyou.fz.mcms.process.bean.ArticleDTO;
import com.cyou.fz.mcms.process.bean.ContentDTO;

/**
 * Created by cnJason on 2016/11/24.
 */
public interface IProcess {

    public ContentDTO process(ContentDTO contentDTO);
}
