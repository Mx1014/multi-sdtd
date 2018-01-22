package com.rzt.eureka;

import com.rzt.util.WebApiResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("WARNINGMONITORSERVER")
public interface StaffLine {
    @GetMapping(value = "warningmonitorServer/GJKH/KHSX")
    WebApiResponse KHSX(@RequestParam("userId") String userId, @RequestParam("taskType") Integer taskType);

    @GetMapping(value = "warningmonitorServer/GJKH/KHXX")
    WebApiResponse KHXX(@RequestParam("userId") String userId, @RequestParam("taskType") Integer taskType);
}
