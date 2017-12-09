package com.rzt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class NursetaskServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NursetaskServerApplication.class, args);
	}
}
