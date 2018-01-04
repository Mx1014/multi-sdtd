package com.rzt.controller;

import com.rzt.entity.OffPostUser;
import com.rzt.service.WarningOffPostUserService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by huyuening on 2017/12/28.
 */
@RestController
@RequestMapping("warningInfo")
public class WarningInfoController  extends CurdController<OffPostUser,WarningOffPostUserService> {

    @Autowired
    private WarningOffPostUserService service;

    @RequestMapping("KHPostUserInfo")
    public Object KHPostUserInfo(String userIds){
        return WebApiResponse.success(service.KHPostUserInfo(userIds));
    }

}
