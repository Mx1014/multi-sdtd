package com.rzt.eureka;

import com.rzt.util.WebApiResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("CENSUSSERVER")
public interface CensusServer {

    @GetMapping("censusServer/WarningOneKey/warningKey\n")
    void warningKey(@RequestParam("id") Long  id);

}

