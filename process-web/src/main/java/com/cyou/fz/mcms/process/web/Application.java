package com.cyou.fz.mcms.process.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by cnJason on 2016/11/25.
 * MainClass.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
public class Application {


    /**
     * 主要入口类.
     * @param args  入口类
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
