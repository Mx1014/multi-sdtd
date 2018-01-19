package com.rzt.controller;

import com.rzt.service.ScheduledTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController("system")
public class XstaskServerFeignServerController {
    @Autowired
    private ScheduledTaskService scheduledTaskService;

//    @GetMapping("bornTask")
//    public WebApiResponse bornTask() {
//        scheduledTaskService.autoInsertTourTask();
//        return webApire
//    }
}
