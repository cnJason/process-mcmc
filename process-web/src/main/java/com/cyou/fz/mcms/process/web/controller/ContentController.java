package com.cyou.fz.mcms.process.web.controller;

import com.cyou.fz.mcms.process.web.common.JsonResult;
import com.cyou.fz.mcms.process.web.common.ResultCode;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by cnJason on 2016/11/25.
 * 内容控制层.
 */
@RestController
@RequestMapping("/api/content")
public class ContentController {


    /**
     * 列出正在执行的内容.
     * @return
     */
    @RequestMapping("/listInProcessContent")
    public JsonResult listInProcessContent(){
        return new JsonResult(ResultCode.SUCCESS, "登录成功！", null);
    }


    /**
     * 列出已经完成的内容.
     * @return
     */
    @RequestMapping("/listFinishContent")
    public JsonResult listFinishContent(){
        return new JsonResult(ResultCode.SUCCESS, "登录成功！", null);
    }

    /**
     * 列出失败队列的内容.
     * @return
     */
    @RequestMapping("/listFailureContent")
    public JsonResult listFailureContent(){
        return new JsonResult(ResultCode.SUCCESS, "登录成功！", null);
    }




}
