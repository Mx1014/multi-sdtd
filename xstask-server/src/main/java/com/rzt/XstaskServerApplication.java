package com.rzt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@ServletComponentScan
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
//@EnableZuulProxy//跟进该注解可以看到该注解整合了@EnableCircuitBreaker、@EnableDiscoveryClient，是个组合注解，目的是简化配置。
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
