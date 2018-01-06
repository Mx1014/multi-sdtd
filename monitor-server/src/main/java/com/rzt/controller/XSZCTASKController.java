package com.rzt.controller;

import com.rzt.service.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    /**
     * 按照taskId查询当前任务的详细信息
     * @param taskId
     * @return
     */
  /*  @GetMapping("/getTASkXQ")
    public WebApiResponse getTASkXQ(String taskId){
        return service.findByTaskId(taskId);
    }*/

    /**
     * 根据taskId获取当前任务的隐患信息
     * 根据taskId 查询当前任务进度
     * @param taskId
     * @return
     */
    @GetMapping("/fingYHByTaskId")
    public WebApiResponse fingYHByTaskId(String taskId,String TASKTYPE){
        if(null!= taskId && !"".equals(taskId)){
            return service.findYHByTaskId(taskId,TASKTYPE);
        }
        return WebApiResponse.erro("参数错误");
    }

    /**
     * 查询抽查表内的所有数据
     * @param taskType  任务类型
     * @param status  查询状态
     * @return
     */
    @GetMapping("/getXsTaskAll")
    public WebApiResponse getXsTaskAll(Integer page,Integer size, String taskType,Integer status){
        return service.getXsTaskAll(page,size,taskType,status);
    }

    @PostMapping("checkOff")
    public WebApiResponse checkOff(Long questionTaskId){
        try {
            service.checkOff(questionTaskId);
            return WebApiResponse.success("success");
        }catch (Exception e){
            return WebApiResponse.erro("更改状态失败："+e.getMessage());
        }
    }

}
