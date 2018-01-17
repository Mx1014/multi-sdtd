package com.rzt.eureka;

import com.rzt.util.WebApiResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("WARNINGMONITORSERVER")
public interface StaffLine {
    @GetMapping(value = "warningmonitorServer/GJKH/khtg")
    WebApiResponse khtg(@RequestParam("userId") String userId, @RequestParam("taskId") Long taskId);

}
