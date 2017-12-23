package com.rzt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableFeignClients
@ServletComponentScan
@SpringBootApplication
@EnableEurekaClient
public class NursetaskServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NursetaskServerApplication.class, args);
	}

}
