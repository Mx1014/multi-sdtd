package com.rzt;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.rzt.utils.YmlConfigUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ServletComponentScan
@EnableScheduling
@Import({YmlConfigUtil.class})
public class LinedataServerApplication {

	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters() {
		//创建封装对象
		FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
		converter.setFeatures(SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue);
		return new HttpMessageConverters(converter);
	}

	public static void main(String[] args) {
		SpringApplication.run(LinedataServerApplication.class, args);
	}
}
