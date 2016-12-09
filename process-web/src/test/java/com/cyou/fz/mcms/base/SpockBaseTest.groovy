package com.cyou.fz.mcms.base

import com.cyou.fz.common.utils.mybatis.service.BaseServiceImpl
import org.apache.commons.beanutils.BeanMap
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import java.beans.PropertyDescriptor
import java.lang.reflect.InvocationTargetException

/**
 * Created by cnJason on 2016/12/9.
 */
@ContextConfiguration(locations = "classpath:application-test.xml")
class SpockBaseTest extends Specification implements ApplicationContextAware {


    @Autowired
    ApplicationContext applicationContext;


    void setup() {

    }

    void cleanup() {
        for (Object obj : cacheObject) {
            getService(obj.getClass()).delete(obj);
        }
    }

    List<Object> cacheObject = new ArrayList<Object>();

    public <T> T getBeanInDB(Class<T> clazz) {
        for (PropertyDescriptor descriptor : BeanUtils.getPropertyDescriptors(clazz)) {
            try {

                if (refBean == null) {
                    continue;
                }
                getService(refBean.getClass()).insert(refBean);
                cacheObject.add(refBean);

                if (refBean != null) {
                    BeanMap beanMap = new BeanMap(refBean);
                    descriptor.getWriteMethod().invoke(object, beanMap.get(ColumnUtils.getIdFieldName(refBean.getClass())))
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        getService(clazz).insert(object);
        cacheObject.add(object);
        return object;
    }


    public <T> BaseServiceImpl<T> getService(Class<T> t) {
        String serviceName = lowerTop(t.getSimpleName()) + "Service";
        if (applicationContext.containsBean(serviceName)) {
            Object service = applicationContext.getBean(serviceName);
            if (service != null) {
                return (BaseServiceImpl<T>) service;
            } else {
                throw new RuntimeException("no service bean:" + t.getName());
            }

        }
        return null;
    }

    /**
     * Returns a String which capitalizes the first letter of the string.
     */
    public String lowerTop(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        name = name.replace("PO", "");
        return name.substring(0, 1).toLowerCase(ENGLISH) + name.substring(1);
    }

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}