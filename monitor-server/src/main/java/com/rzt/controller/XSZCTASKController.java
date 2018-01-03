package com.rzt.controller;

import com.netflix.discovery.converters.Auto;
import com.rzt.service.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

/**
 * 李成阳
 * 2018/1/2
 */
@RestController
@RequestMapping("XSZCTASKController")
public class XSZCTASKController extends
        CurdController<XSZCTASKController,XSZCTASKService>{



    /**
     * 按照taskId查询当前任务的详细信息
     * @param taskId
     * @return
     */
    @GetMapping("/getTASkXQ")
    public WebApiResponse getTASkXQ(String taskId){
        return service.findByTaskId(taskId);
    }

    /**
     * 查询抽查表内的所有数据
     * @return
     */
    @GetMapping("/getXsTaskAll")
    public WebApiResponse getXsTaskAll(){
        return service.getXsTaskAll();
    }

}
