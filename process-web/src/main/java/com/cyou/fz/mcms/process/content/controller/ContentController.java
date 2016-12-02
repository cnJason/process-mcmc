package com.cyou.fz.mcms.process.content.controller;

import com.cyou.fz.mcms.process.common.JsonResult;
import com.cyou.fz.mcms.process.content.query.ContentTaskParams;
import com.cyou.fz.mcms.process.task.ContentTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Created by cnJason on 2016/12/2.
 */
@Controller
@RequestMapping("/content")
@Api(value = "/content",tags = {"内容管理"},position = 1)
public class ContentController {

    /**
     * Logger.
     */
    private Logger logger = Logger.getLogger(ContentController.class);



    @ResponseBody
    @RequestMapping(value = "/addContentTask")
    @ApiOperation(value = "添加内容任务",notes = "添加内容任务",httpMethod = "POST",position = 1)
    public JsonResult addContentTask(@ApiParam(value = "内容任务对象", required = true) ContentTaskParams contentTaskParams){

        return null;
    }


}
