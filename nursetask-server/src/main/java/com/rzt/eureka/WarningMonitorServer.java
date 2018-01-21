package com.rzt.eureka;

import com.rzt.util.WebApiResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("WARNINGMONITORSERVER")
public interface WarningMonitorServer {

    @GetMapping("warningmonitorServer/GJKH/khWFDW")
    void khWFDW(@RequestParam("taskid") Long taskid, @RequestParam("userid") String userid);
}

