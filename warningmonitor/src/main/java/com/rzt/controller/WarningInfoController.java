package com.rzt.controller;

import com.rzt.entity.Monitorcheckyj;
import com.rzt.service.Monitorcheckyjservice;
import com.rzt.util.WebApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by huyuening on 2018/1/17.
 */
@RestController
@RequestMapping("WarningInfo")
public class WarningInfoController extends
        CurdController<Monitorcheckyj, Monitorcheckyjservice>{

    @GetMapping("/taskInfo")
    public WebApiResponse taskInfo(Long taskId, Integer warningType,Integer taskType){
        try {
            return WebApiResponse.success(service.taskInfo(taskId,warningType,taskType));
        }catch (Exception e){
            return WebApiResponse.erro(e.getMessage());
        }
    }

    @GetMapping("/userInfo")
    public WebApiResponse userInfo(String userId,Integer warningType,Long taskId){
        try {
            return WebApiResponse.success(service.userInfo(userId,warningType,taskId));
        }catch (Exception e){
            return WebApiResponse.erro(e.getMessage());
        }
    }

    /**
     *柱状图查询
     * type 任务类型
     */
    @GetMapping("/sumInfo")
    public WebApiResponse sumInfo(String userId,Integer type){
        try {
            return WebApiResponse.success(service.sumInfo(userId,type));
        }catch (Exception e){
            return WebApiResponse.erro(e.getMessage());
        }
    }

    /**
     * 总柱状图展示
     * type 任务类型
     */
    @GetMapping("/totalSumInfo")
    public WebApiResponse totalSumInfo(String userId,Integer type){
        try {
            return WebApiResponse.success(service.totalSumInfo(userId,type));
        }catch (Exception e){
            return WebApiResponse.erro(e.getMessage());
        }
    }

    @GetMapping("pictureInfo")
    public WebApiResponse pictureInfo(String taskId,Integer type,Integer warningType){
        try {
            return WebApiResponse.success(service.pictureInfo(taskId,type,warningType));
        }catch (Exception e){
            return WebApiResponse.erro(e.getMessage());
        }
    }

}
