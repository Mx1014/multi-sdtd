package com.rzt.eureka;

/**
 * Created by admin on 2017/12/18.
 */

import com.rzt.util.WebApiResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("SDTD27-LINEDATA")
public interface LineData {

    @GetMapping("/CMLINETOWER/getLineInfoCommOptions")
    WebApiResponse getLine(String kv);

    @GetMapping("/CMLINETOWER/getTowerInfoCommOptions")
    WebApiResponse getTower(String lineId);
}
