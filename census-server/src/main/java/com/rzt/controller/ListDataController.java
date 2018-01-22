package com.rzt.controller;

import com.rzt.websocket.service.ListDataPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ListData")
public class ListDataController {
    @Autowired
    ListDataPushService listDataPushService;

    @PostMapping("ListDataService")
    public void ListDataService() {
        listDataPushService.listData();
    }
}
