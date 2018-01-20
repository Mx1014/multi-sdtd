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
    public WebApiResponse start(String key , String userName,String XSID,String flag,String info){
        HashMap<Object, Object> map = new HashMap<>();
        map.put("userName",userName);
        map.put("flag",flag);
        map.put("info",info);
        map.put("XSID",XSID);

        ProcessInstance start = xsCycleService.start(key, map);
        System.out.println(start);
        return WebApiResponse.success("");
    }

    /**
     * 进入流程节点
     * 此处可以选择当前待办是否进入  下一步流程
     * @param taskId   当前任务id
     * @param YHID         当前上报隐患id
     * @param flag          选择节点使用的标志
     * @return
     */
    @GetMapping("/complete")
    public WebApiResponse complete(String taskId,String YHID,String flag,String isKH){
        Map<String, Object> map = new HashMap<>();
        map.put("YHID",YHID);
        map.put("flag",flag);
        map.put("isKH",isKH);
        xsCycleService.complete(taskId,map);

        return WebApiResponse.success("");
    }

    /**
     * 详细页中的处理按钮   针对监控中心
     * 当发送处理请求 准备派出稽查人员  并且判断是否生成临时看护任务
     * 生成稽查任务时需要带有 当前任务id   当稽查人员查看完毕时根据任务id判断节点方向
     * @param taskId   当前任务id
     * @param YHID      隐患id
     * @param flag      节点判断标记
     * @param isKH      是否派出看护任务
     * @param info      描述
     * @param proId      当前流程实例id
     * @return
     */
    @GetMapping("/proClick")
    public WebApiResponse chuLi(String taskId,String YHID,String flag,String isKH,String info,String proId){

        return WebApiResponse.success("");

    }

    /**
     * 稽查任务回调   回调时需要传递当前任务id  和flag  隐患id
     * @param taskId
     * @param YHID
     * @param flag
     * @return
     */
    @GetMapping("/jchd")
    public WebApiResponse jicha(String taskId,String YHID,String flag,String isKH){
        //稽查任务回调   回调时需要传递当前任务id  和flag  隐患id





     /*   Map<String, Object> map = new HashMap<>();
        map.put("YHID",YHID);
        map.put("flag",flag);
        xsCycleService.complete(taskId,map);*/
        return null;
    }
    /**
     * 查看所有待办任务
     * @param userId   传入当前节点名
     * @return
     */
    @GetMapping("/findTaskByUserName")
    public WebApiResponse toTask(String userId,Integer page,Integer size){
        // 根据userId 获取当前 用户权限
        //String roleId = RedisUtil.findRoleIdByUserId(redisTemplate, userId);
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
