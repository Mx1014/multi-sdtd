package com.rzt.config;

import com.rzt.util.WebApiResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("MONITORSERVER")
public interface MonitorFeign {

    /**
     * 稽查任务回调   回调时需要传递当前任务id  和flag  隐患id
     * @param taskId
     * @param YHID
     * @param flag
     * @return
     */
    @GetMapping("/pro/jchd")
    public WebApiResponse jicha(@RequestParam(name = "taskId") String taskId,
                                @RequestParam(name = "YHID") String YHID,
                                @RequestParam(name = "flag") String flag);

}