package com.rzt.controller;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by Administrator on 2017/12/17.
 */
@FeignClient("USERCENTER")
public interface UserCenter {

	@GetMapping(value = "/RztSysUser/userQuery")
	WebApiResponse userQuery(String classname, String realname);
}
