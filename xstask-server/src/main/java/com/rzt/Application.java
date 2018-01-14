package com.rzt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@ServletComponentScan
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableScheduling
/**
* @Class XstaskServerApplication
* @Description 巡视任务微服务
*
* @date 2017/12/10 15:57
* @author nwz
*/
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Bean
//    public ConversionService conversionService() {
//        FormattingConversionServiceFactoryBean factory = new FormattingConversionServiceFactoryBean();
//        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
//        registrar.setUseIsoFormat(true);
//        factory.setFormatterRegistrars(Collections.singleton(registrar));
//        factory.afterPropertiesSet();
//        return factory.getObject();
//    }
}
