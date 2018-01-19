package com.rzt.activiti.Eureka;

import com.rzt.entity.KhYhHistory;
import com.rzt.util.WebApiResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 李成阳
 * 2018/1/19
 */
@FeignClient("NURSETASK")
public interface ProEureka {
    @PostMapping("/nurseTask/KhSite/saveYh.do")
            WebApiResponse saveYh(KhYhHistory yh, String fxtime, String startTowerName, String endTowerName, String pictureId);

}

