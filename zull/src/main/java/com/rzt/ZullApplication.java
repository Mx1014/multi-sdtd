package com.rzt;

import com.rzt.filter.AccessTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@EnableZuulProxy
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class ZullApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZullApplication.class, args);
    }

    @Bean
    public AccessTokenFilter accessFilter() {
        return new AccessTokenFilter();
    }


}
