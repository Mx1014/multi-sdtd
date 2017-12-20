package com.rzt;

import com.rzt.utils.YmlConfigUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ServletComponentScan
@EnableScheduling
@Import({YmlConfigUtil.class})
@EnableEurekaClient //启动EnableEureka客户端
@EnableFeignClients
public class FileserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileserverApplication.class, args);
	}
}
