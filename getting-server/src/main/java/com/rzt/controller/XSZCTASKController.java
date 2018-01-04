package com.rzt.controller;

import com.netflix.discovery.converters.Auto;
import com.rzt.service.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 李成阳
 * 2018/1/2
 */
@RestController
@RequestMapping("XSZCTASKController")
public class XSZCTASKController extends
        CurdController<XSZCTASKController,XSZCTASKService>{
    @Autowired
    private XSZCTASKService xszctaskService;

    @GetMapping("/getXSZCTASK")
    public WebApiResponse getXSZCTASK(String id){
       return  xszctaskService.findXSTASK(id);
    }



}
