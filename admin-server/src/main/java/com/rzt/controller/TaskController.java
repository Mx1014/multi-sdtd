package com.rzt.controller;

import com.rzt.entity.TimedTask;
import com.rzt.service.TasksService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
