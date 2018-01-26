package com.rzt.controller;

import com.rzt.websocket.service.WarningMonitorPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("WarningOneKey")
public class WarningOneKeyController {

    @Autowired
    private WarningMonitorPushService service;

    @PostMapping("warningKey")
    public void warningOneKey(Long id){
        service.sendMsgs(id);
    }

}
