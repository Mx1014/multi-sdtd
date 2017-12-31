package com.rzt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
@ServletComponentScan
public class WarningApplication {
	public static void main(String[] args) {
		SpringApplication.run(WarningApplication.class, args);
	}
}
