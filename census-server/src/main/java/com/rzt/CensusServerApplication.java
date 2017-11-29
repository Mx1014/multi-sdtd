package com.rzt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
/**
 * Druid监控配置注解
 */
@ServletComponentScan
public class CensusServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CensusServerApplication.class, args);
    }
}
