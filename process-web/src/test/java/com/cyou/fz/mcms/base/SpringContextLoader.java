package com.cyou.fz.mcms.base;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by cnJason on 2016/12/9.
 */
public class SpringContextLoader {

    private static final String CONFIG_FILE_LOCATION = "classpath:application-test.xml";


    private static ClassPathXmlApplicationContext context;


    public static void init(){
        context = new ClassPathXmlApplicationContext(new String[]{CONFIG_FILE_LOCATION});
    }

    public static ApplicationContext getSpringContext(){
        if(context == null){
            synchronized (SpringContextLoader.class){
                SpringContextLoader.init();
            }
        }
        return context;
    }

    public static Object getBean(String beanName){
        return getSpringContext().getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz){
        return  getSpringContext().getBean(clazz);
    }


}
