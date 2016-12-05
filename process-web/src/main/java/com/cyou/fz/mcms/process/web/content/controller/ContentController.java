package com.cyou.fz.mcms.process.web.content.controller;

import com.cyou.fz.mcms.process.web.common.JsonResult;
import com.cyou.fz.mcms.process.web.common.ResultCode;
import com.cyou.fz.mcms.process.web.content.query.ContentTaskParams;
import com.cyou.fz.mcms.process.web.content.service.ContentBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;


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

    @Autowired
    private ContentBaseService contentBaseService;


    @ResponseBody
    @RequestMapping(value = "/addContentTask")
    @ApiOperation(value = "添加内容任务",notes = "添加内容任务",httpMethod = "POST",position = 1)
    public JsonResult addContentTask(@ApiParam(value = "内容任务对象", required = true) @Valid ContentTaskParams contentTaskParams, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            logger.error(bindingResult.getFieldError().getDefaultMessage());
            return new JsonResult(ResultCode.PARAMS_ERROR,"参数验证错误",null);
        }
        if(contentTaskParams.getStartTime().compareTo(contentTaskParams.getEndTime()) >= 0){
            return new JsonResult(ResultCode.PARAMS_ERROR,"开始时间不能大于结束时间",null);
        }


        Boolean result =contentBaseService.addContentTask(contentTaskParams);

        if(!result){
            return  new JsonResult(ResultCode.EXCEPTION,"任务添加失败",result);
        }
        return new JsonResult(ResultCode.EXCEPTION,"任务添加失败",result);

    }


}
