package com.rzt.controller;

import com.rzt.service.TaskCheckService;
import com.rzt.service.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    public WebApiResponse taskCheckConduct(String currentUserId){
        return taskCheckService.taskCheckConduct(currentUserId);
    }

    /**
     * 后台稽查以完成
     * @param currentUserId  当前登录用户id
     * @return
     */
    @RequestMapping("/taskCheckComplete")
    public WebApiResponse taskCheckComplete(String currentUserId){
        return taskCheckService.taskCheckComplete(currentUserId);
    }






}
