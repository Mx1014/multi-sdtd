package com.rzt.eureka;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("USERCENTER")
public interface UserCenterService {
    @GetMapping("/userCenter/pcMapShow/updateKhInfoStatusInredis")
    void updateKhInfoStatusInredis(@RequestParam("userId") String userId);

}
