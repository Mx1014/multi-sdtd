package com.rzt;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ServletComponentScan
@Configuration
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan
public class MicroserviceRztUserCenterApplication {

  	public static void main(String[] args) {

		SpringApplication.run(MicroserviceRztUserCenterApplication.class, args);
        Logger logger = LoggerFactory.getLogger(MicroserviceRztUserCenterApplication.class);
	}

	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters(){
		//创建封装对象
		FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
		FastJsonConfig config = new FastJsonConfig();
		config.setSerializerFeatures(SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
		converter.setFastJsonConfig(config);
		return new HttpMessageConverters(converter);
	}

}