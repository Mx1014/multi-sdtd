package com.rzt.eureka;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("CENSUSSERVER")
public interface StaffLine {
    @RequestMapping(method = RequestMethod.POST, value = "/censusServer/ListData/ListDataService")
    void aa();

   /* @GetMapping("warningServer/warning/aa")
    void bb();*/

}
