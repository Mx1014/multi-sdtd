package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.TimedTask;
import com.rzt.service.TasksService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 李成阳
 * 2018/1/31
 * 管理app任务查询数据统计
 */
@RestController
@RequestMapping("/tasks")
public class TaskController extends CurdController<TimedTask,TasksService>  {
    @Autowired
    private TasksService tasksService;

    /**
     * 首页使用
     * 查询任务详情   总任务数  已完成数  未开始数
     * @return
     */
    @GetMapping("/findTaskSum")
    public WebApiResponse findTaskInfo(){
        return tasksService.findTaskInfo();
    }
    /**
     * 二级页面使用
     * 按照部门分组查询任务信息
     * @return
     */
    @GetMapping("/findTasksGroupDept")
    public WebApiResponse findTasksGroupDept(){
        return tasksService.findTasksGroupDept();
    }

    /**
     * 三级页面使用
     * 查询一个单位中的任务详情
     * @param deptId
     * @return
     */
    @GetMapping("/threeTasks")
    public WebApiResponse threeTasks(String deptId){
        return tasksService.threeTasks(deptId);
    }

    /**
     *  四级页面使用  查询本单位不同状态的任务
     * @param page
     * @param size
     * @param deptId 部门id
     * @param flag   0 未开始  1 进行中  2 以完成
     * @return
     */
    @GetMapping("/findTasksByStatus")
    public WebApiResponse findTasksByStatus(Integer page,Integer size,String deptId,String flag){
      return   tasksService.findTasksByStatus(page,size,deptId,flag);
    }

    /**
     * 五级页面使用  查看当前任务详情
     * @param taskType
     * @param taskId
     * @param deptId   当任务类型为4时 需要使用部门id和抽查时间查询后台稽查任务
     * @param realTime
     * @return
     */
    @GetMapping("/findTaskInfoByTaskId")
    public WebApiResponse findTaskInfoByTaskId(String taskType,String taskId,String deptId,String realTime){
       return tasksService.findTaskInfoByTaskId(taskType,taskId,deptId,realTime);
    }

    /**
     * 二级页
     * @return
     */
    @GetMapping("deptDaZhu")
    public WebApiResponse deptDaZhu() {
       return tasksService.deptDaZhu();
    }

}
