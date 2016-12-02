package com.cyou.fz.mcms.process.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by cnJason on 2016/11/28.
 */
@Component
public class ContentTask {


    @Scheduled(fixedRate = 50000)
    public  void  fetchTask(){

        System.out.println("现在时间："+ new Date());
    }
}
