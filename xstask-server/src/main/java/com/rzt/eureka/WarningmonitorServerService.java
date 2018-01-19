package com.rzt.eureka;

import com.rzt.util.WebApiResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("WARNINGMONITORSERVER")
public interface WarningmonitorServerService {

    /***
    * @Method xsTourScope
    * @Description  巡视脱离范围
    * @param [taskid, userid]
    * @return com.rzt.util.WebApiResponse
    * @date 2018/1/17 16:52
    * @author nwz
    */
    @GetMapping(value = "warningmonitorServer/GJKH/xsTourScope")
    WebApiResponse xsTourScope(@RequestParam("taskid") Long taskid, @RequestParam("userid") String userid);


    /***
    * @Method takePhoto
    * @Description 未按标准速率拍照片 
    * @param [taskid, userid]
    * @return void
    * @date 2018/1/17 16:52
    * @author nwz
    */
    @GetMapping(value = "warningmonitorServer/GJKH/takePhoto")
    void takePhoto(@RequestParam("taskid") Long taskid, @RequestParam("userid") String userid);
}
