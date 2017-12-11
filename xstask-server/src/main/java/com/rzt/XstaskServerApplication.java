package com.rzt;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

@ServletComponentScan
@SpringBootApplication
/**
* @Class XstaskServerApplication
* @Description 巡视任务微服务
*
* @date 2017/12/10 15:57
* @author nwz
*/
public class XstaskServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(XstaskServerApplication.class, args);
    }

   @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        //创建封装对象
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
       FastJsonConfig fastJsonConfig = new FastJsonConfig();
       fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteDateUseDateFormat);
       fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue);

       //处理中文乱码问题(不然出现中文乱码)
       List<MediaType> fastMediaTypes = new ArrayList<MediaType>();
       fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
       converter.setSupportedMediaTypes(fastMediaTypes);
       converter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(converter);
    }
}
