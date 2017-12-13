package com.example.demo;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Administrator on 2017/12/11.
 */
@FeignClient("AUTHENTICATION")
public interface Authentication {
	@RequestMapping(value = "/authentication/auth")
	String auth();
}
