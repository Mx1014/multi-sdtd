package com.rzt.eureka;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by admin on 2017/12/20.
 */
@FeignClient("MICROSERVICE-FILE-SERVER")
public interface PictureCenter {

    @GetMapping(value = "/PICTUREJC/getImgsBytaskId")
    Map<String, Object> getImgsBytaskId(@RequestParam("taskId") Long taskId);


}
