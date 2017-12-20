package com.rzt;

import com.rzt.utils.YmlConfigUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableEurekaClient //启动EnableEureka客户端
@EnableFeignClients
@SpringBootApplication
@ServletComponentScan
@EnableScheduling
@Import({YmlConfigUtil.class})
public class LinedataServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinedataServerApplication.class, args);
	}
}
