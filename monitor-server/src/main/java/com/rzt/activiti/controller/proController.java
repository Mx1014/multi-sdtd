package com.rzt.activiti.controller;

import com.rzt.activiti.service.ProService;
import com.rzt.util.WebApiResponse;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 隐患上报处理流程
 * 李成阳
 * 2018/1/15
 */
@RestController
@RequestMapping("/pro")
public class proController {
    @Autowired
    private ProService proService;

    /**
     * 开启流程
     * @return
     */
    @GetMapping("/start")
    public WebApiResponse start(String key , String userName,String YHID,String flag,String info,String khid){
        HashMap<Object, Object> map = new HashMap<>();
        map.put("userName",userName);
        map.put("YHID",YHID);
        map.put("flag",flag);
        map.put("info",info);
        map.put("khid",khid);
        ProcessInstance start = proService.start(key, map);
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
    public WebApiResponse complete(String taskId,String YHID,String flag){
        Map<String, Object> map = new HashMap<>();
        map.put("YHID",YHID);
        map.put("flag",flag);
        proService.complete(taskId,map);

        return WebApiResponse.success("");
    }

    /**
     * 详细页中的处理按钮
     * 当发送处理请求 准备派出稽查人员  并且 生成临时看护任务
     * 生成稽查任务时需要带有 当前任务id   当稽查人员查看完毕时根据任务id判断节点方向
     *
     */
    public WebApiResponse chuLi(String taskId,String YHID,String flag){
        //将稽查和看护任务派发     完成后拿到看护任务的id  进入下一个节点 稽查节点

        Map<String, Object> map = new HashMap<>();

        map.put("YHID",YHID);
        map.put("flag",flag);
        proService.complete(taskId,map);
        return null;
    }

    /**
     * 稽查任务回调   回调时需要传递当前任务id  和flag  隐患id
     * @param taskId
     * @param YHID
     * @param flag
     * @return
     */
    @GetMapping("/jchd")
    public WebApiResponse jicha(String taskId,String YHID,String flag){
        //稽查任务回调   回调时需要传递当前任务id  和flag  隐患id





     /*   Map<String, Object> map = new HashMap<>();
        map.put("YHID",YHID);
        map.put("flag",flag);
        proService.complete(taskId,map);*/
        return null;
    }
    /**
     * 查看所有待办任务
     * @param userName   传入当前节点名
     * @return
     */
    @GetMapping("/findTaskByUserName")
    public WebApiResponse toTask(String userName,Integer page,Integer size){
        return proService.checkTask(userName,page,size);

    }

    /**
     * 查看待办任务
     * @param userName 执行人
     * @param taskId   当前任务
     * @return
     */
    @GetMapping("/task")
    public WebApiResponse task(String userName,String taskId){
        return WebApiResponse.success(proService.checkTask(taskId,userName));

    }

    /**
     * 在每一个流程开始时都需要先部署当前的流程
     * 部署流程
     * @return
     */
    @GetMapping("/deploy")
    private WebApiResponse deploy(){
        proService.deploy();
        return WebApiResponse.success("");

    }




}
