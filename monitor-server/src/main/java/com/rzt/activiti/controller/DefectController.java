package com.rzt.activiti.controller;

import com.rzt.activiti.service.impl.DefectServiceImpl;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * 李成阳
 * 2018/1/18
 * 缺陷上报
 */
@RestController
@RequestMapping("/def")
public class DefectController {
    @Autowired
    private DefectServiceImpl defectService;
    /**
     * 开启流程
     * @return
     */
    @GetMapping("/start")
    public WebApiResponse start(String key , String userName,String flag,String qxId,String info){
        HashMap<String, Object> map = new HashMap<>();
        map.put("userName",userName);
        map.put("flag",flag);
        map.put("qxId",qxId);
        map.put("info",info);

        defectService.start(key,map);
        return WebApiResponse.success("");
    }

    /**
     * 进入流程节点
     * 此处可以选择当前待办是否进入  下一步流程
     * @param taskId   当前任务id
     * @param flag          选择节点使用的标志
     * @return
     */
    @GetMapping("/complete")
    public WebApiResponse complete(String taskId,String flag){
        HashMap<String, Object> map = new HashMap<>();
        map.put("flag",flag);
        defectService.complete(taskId,map);
        return WebApiResponse.success("");
    }

    /**
     * 查看所有待办任务
     * @param currentUserId   传入当前节点名
     * @return
     */
    @GetMapping("/findTaskByUserName")
    public WebApiResponse toTask(String currentUserId,Integer page,Integer size){
        return defectService.checkTask(currentUserId,page,size);

    }

    /**
     * 在每一个流程开始时都需要先部署当前的流程
     * 部署流程
     * @return
     */
    @GetMapping("/deploy")
    private WebApiResponse deploy(){
        defectService.deploy();
        return WebApiResponse.success("");

    }

}
