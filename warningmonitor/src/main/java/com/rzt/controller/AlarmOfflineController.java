package com.rzt.controller;

import com.rzt.entity.AlarmOffline;
import com.rzt.service.AlarmOfflineService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("AlarmOffline")
public class AlarmOfflineController extends CurdController<AlarmOffline, AlarmOfflineService> {

}
