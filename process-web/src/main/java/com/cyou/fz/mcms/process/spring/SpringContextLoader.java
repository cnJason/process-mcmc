package com.cyou.fz.mcms.process.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by cnJason on 2016/12/2.
 */
@Component
public class SpringContextLoader  implements ApplicationContextAware{


    //上下文对象.
    private static ApplicationContext applicationContext;



    public static Object getBean(String beanName){
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz){
        return  applicationContext.getBean(clazz);
    }

    public static boolean isBeanExist(String beanName){
        return applicationContext.containsBean(beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextLoader.applicationContext = applicationContext;
    }
}
