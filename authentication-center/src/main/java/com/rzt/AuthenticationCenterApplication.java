package com.rzt;

		import org.springframework.boot.SpringApplication;
		import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
		import org.springframework.boot.autoconfigure.SpringBootApplication;
		import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
		import org.springframework.cloud.netflix.feign.EnableFeignClients;

@EnableEurekaClient
@SpringBootApplication
@EnableAutoConfiguration
@EnableFeignClients
public class AuthenticationCenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationCenterApplication.class, args);
		System.out.println("service 启动完毕");
	}

}
