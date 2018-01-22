package com.rzt.eureka;
import com.rzt.util.WebApiResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("MONITORSERVER")
public interface MonitorService {
    @GetMapping("/monitorServer/pro/start")
    WebApiResponse start(@RequestParam("key") String key ,
                         @RequestParam("userName") String userName,
                         @RequestParam("YHID")String YHID,
                         @RequestParam("flag")String flag,
                         @RequestParam("info")String info,
                         @RequestParam("khid")String khid);
    @GetMapping("warningmonitorServer/GJKH/khWFDW")
    void  khWFDW(Long taskid, String userid);
}
