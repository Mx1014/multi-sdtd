package com.rzt.activiti.controller;

import com.rzt.activiti.service.impl.XSCycleServiceImpl;
import com.rzt.util.WebApiResponse;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 巡视周期审核
 * 李成阳
 * 2018/1/19
 */
@RestController
@RequestMapping("/XSCycle")
public class XSCycleController {
    @Autowired
    private XSCycleServiceImpl xsCycleService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 开启流程
     * @return
     */
    @GetMapping("/start")
    public WebApiResponse start(String key , String userName,String userId,String XSID,String flag,String info){
        HashMap<Object, Object> map = new HashMap<>();
        map.put("userName",userName);
        map.put("flag",flag);
        map.put("info",info);
        map.put("XSID",XSID);

        ProcessInstance start = xsCycleService.start(key, map);
        return WebApiResponse.success("");
    }

    /**
     * 进入流程节点
     * 此处可以选择当前待办是否进入  下一步流程
     * @param taskId   当前任务id
     * @param XSID         当前上报id
     * @param flag          选择节点使用的标志
     * @return
     */
    @GetMapping("/complete")
    public WebApiResponse complete(String taskId,String XSID,String flag,String info,String userId){
        Map<String, Object> map = new HashMap<>();
        map.put("taskId",taskId);
        map.put("XSID",XSID);
        map.put("flag",flag);
        map.put("info",info);
        xsCycleService.complete1(taskId,map,userId);

        return WebApiResponse.success("");
    }




    /**
     * 查看所有待办任务
     * @param userId   传入当前节点名
     * @return
     */
    @GetMapping("/findTaskByUserName")
    public WebApiResponse toTask(String userId,Integer page,Integer size){
        return xsCycleService.checkTask(userId,page,size);

    }

    /**
     * 查看待办任务
     * @param userName 执行人
     * @param taskId   当前任务
     * @return
     */
    @GetMapping("/task")
    public WebApiResponse task(String userName,String taskId){
        return WebApiResponse.success(xsCycleService.checkTask(taskId,userName));

    }

    /**
     * 在每一个流程开始时都需要先部署当前的流程
     * 部署流程
     * @return
     */
    @GetMapping("/deploy")
    private WebApiResponse deploy(){
        xsCycleService.deploy();
        return WebApiResponse.success("");

    }





}
