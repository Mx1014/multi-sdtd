package com.rzt.controller;

import com.rzt.service.TaskCheckService;
import com.rzt.service.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 李成阳
 * 2018/3/12
 * 后台稽查任务
 */
@RestController
@RequestMapping("/TaskCheck")
public class TaskCheckController extends
        CurdController<XsTaskController,XSZCTASKService>{
    @Autowired
    private TaskCheckService taskCheckService;

    /**
     * 后台稽查进行中
     * @param currentUserId  当前登录用户id
     * @return
     */
    @RequestMapping("/taskCheckConduct")
    public WebApiResponse taskCheckConduct(String currentUserId,String page,String size){
        return taskCheckService.taskCheckConduct(currentUserId,page,size);
    }

    /**
     * 后台稽查以完成
     * @param currentUserId  当前登录用户id
     * @return
     */
    @RequestMapping("/taskCheckComplete")
    public WebApiResponse taskCheckComplete(String currentUserId,String page,String size){
        return taskCheckService.taskCheckComplete(currentUserId,page,size);
    }



    /**
     *
     * 按照任务类型和 查询结果类型查询
     * @param flag     标识 0有问题  1以审核  2全部  3 未审核
     * @param taskType  任务类型
     * @return
     */
    @GetMapping("/findCompleteTaskByFlag")
    public WebApiResponse findCompleteTaskByFlag(String flag,String taskType,String deptid,String taskTime,Integer page,Integer size){
        return taskCheckService.findCompleteTaskByFlag(flag,taskType,deptid,taskTime,page,size);
    }

    /**
     * 根据任务id和任务类型查询任务的详细信息
     * @param taskId
     * @param taskType
     * @return
     */
    @GetMapping("/")
    public WebApiResponse findTaskInfoByTaskId(String taskId,String taskType){
        return taskCheckService.findTaskInfoByTaskId(taskId,taskType);
    }



}
