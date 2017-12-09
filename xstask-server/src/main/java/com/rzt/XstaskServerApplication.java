package com.rzt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class XstaskServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(XstaskServerApplication.class, args);
    }

    /*@Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        //创建封装对象
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setFeatures(SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue);
        return new HttpMessageConverters(converter);
    }*/
}
