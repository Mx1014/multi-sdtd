package com.rzt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@ServletComponentScan
@EnableAutoConfiguration
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class NursetaskServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NursetaskServerApplication.class, args);
	}

}
