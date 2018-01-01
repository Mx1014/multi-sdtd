package com.rzt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients
@ServletComponentScan
@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
public class NursetaskServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NursetaskServerApplication.class, args);
	}

}
