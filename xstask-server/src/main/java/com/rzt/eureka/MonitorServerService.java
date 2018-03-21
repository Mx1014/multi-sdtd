package com.rzt.eureka;

import com.rzt.util.WebApiResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("MONITORSERVER")
public interface MonitorServerService {

    /**
     * 开启流程
     * @param key       xssh
     * @param userName 当前登录用户id
     * @param XSID     巡视周期id
     * @param flag     1
     * @param info
     * @return
     */
    @GetMapping("/monitorServer/XSCycle/start")
    WebApiResponse start(@RequestParam("key")String key , @RequestParam("userName")String userName, @RequestParam("XSID")Long XSID, @RequestParam("flag")String flag, @RequestParam("info")String info);

    }
