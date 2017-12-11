package com.rzt.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public class YmlConfigUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;

    private static Map<String,String> propertiesMap =null;

    public YmlConfigUtil() {
    }

    public static String getConfigByKey(String key) {
        if (propertiesMap ==null){
            YmlConfig ymlConfig = applicationContext.getBean(YmlConfig.class);
            propertiesMap = ymlConfig.getMapProps();
        }
        return propertiesMap.get(key);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(YmlConfigUtil.applicationContext == null){
            YmlConfigUtil.applicationContext  = applicationContext;
        }
    }
}