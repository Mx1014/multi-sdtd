package com.rzt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
/**
* @Class XstaskServerApplication
* @Description 巡视任务微服务
*
* @date 2017/12/10 15:57
* @author nwz
*/
public class XstaskServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(XstaskServerApplication.class, args);
    }

}
