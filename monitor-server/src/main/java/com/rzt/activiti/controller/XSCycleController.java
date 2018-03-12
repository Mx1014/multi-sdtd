package com.rzt.activiti.controller;

import com.rzt.activiti.service.impl.XSCycleServiceImpl;
import com.rzt.util.WebApiResponse;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

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


    /**
     * 开启流程
     * @return
     */
    @GetMapping("/start")
    public WebApiResponse start(String key , String userName,String XSID,String flag,String info){
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
    public WebApiResponse complete(String taskId,String XSID,String flag,String info,String currentUserId){
        if(null != flag && "0".equals(flag)){
        //说明不同意变更计划取消任务
        }
        Map<String, Object> map = new HashMap<>();
        map.put("taskId",taskId);
        map.put("XSID",XSID);
        map.put("flag",flag);
        map.put("info",info);
        xsCycleService.complete1(taskId,map,currentUserId);

        return WebApiResponse.success("");
    }

    @GetMapping("/history")
    public WebApiResponse gethi(String currentUserId,Integer page,Integer size
            ,String tdId,String lineName,String vLevel,String startTime,String endTime){
        if(null == currentUserId || "".equals(currentUserId)){
            return WebApiResponse.erro("当前用户没有权限查看记录");
        }
        return xsCycleService.historyActInstanceList(currentUserId,page,size,tdId,lineName,vLevel,startTime,endTime);
    }

    /**
     * 查看所有待办任务
     * @param currentUserId 登录人
     * @param page
     * @param size
     * @param tdId   通道公司
     * @param lineName  线路名称
     * @param vLevel    电压等级
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return
     */
    @GetMapping("/findTaskByUserName")
    public WebApiResponse toTask(String currentUserId,Integer page,Integer size
            ,String tdId,String lineName,String vLevel,String startTime,String endTime){
        return xsCycleService.checkTasks(currentUserId,page,size,tdId,lineName,vLevel,startTime,endTime);

    }

    /**
     * 查看待办任务
     * @param currentUserId 执行人
     * @param taskId   当前任务
     * @return
     */
    @GetMapping("/task")
    public WebApiResponse task(String currentUserId,String taskId){
        return WebApiResponse.success(xsCycleService.checkTask(taskId,currentUserId));

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
