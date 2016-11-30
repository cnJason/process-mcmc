package com.cyou.fz.mcms.process.web.controller;

import com.cyou.fz.mcms.process.web.common.JsonResult;
import com.cyou.fz.mcms.process.web.common.ResultCode;
import com.sun.org.apache.regexp.internal.RE;
import mjson.Json;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by cnJason on 2016/11/28.
 */
@RestController
@RequestMapping("/api/task")
public class TaskController {

    /**
     * 列出正在执行的内容.
     * @return
     */
    @RequestMapping("/addTask")
    public JsonResult addTask(){
        return new JsonResult(ResultCode.SUCCESS, "任务添加成功！", null);
    }


    @RequestMapping("/addTask")
    public JsonResult addTask(@RequestParam String channelCode,@RequestParam String categoryId,@RequestParam Integer pageNo,@RequestParam  Integer pageSize){

        return new JsonResult(ResultCode.SUCCESS,"任务添加成功！",null);
    }


    @RequestMapping("/addSingleTask")
    public JsonResult addSingleTask(@RequestParam List<String> contentKeys){

    }
}
