package com.rzt.activiti.Eureka;

import com.rzt.entity.KhYhHistory;
import com.rzt.util.WebApiResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("NURSETASK")
public interface nurseTaskService {
    @PostMapping("/nurseTask/KhLsCycle/saveLsCycle")
    WebApiResponse saveLsCycle(@RequestParam(name = "yhId") String YHID);

}