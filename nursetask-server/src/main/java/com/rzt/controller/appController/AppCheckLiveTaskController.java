package com.rzt.controller.appController;

import com.rzt.service.app.AppCheckLiveTaskService;
import org.springframework.web.bind.annotation.*;
import com.rzt.entity.CheckLiveTask;
import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.entity.KhTask;
import com.rzt.entity.model.CheckLiveTaskDetailModel;
import com.rzt.entity.model.CheckLiveTasks;
import com.rzt.service.CheckLiveTaskDetailService;
import com.rzt.service.CheckLiveTaskService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rzt.controller.CurdController;

import java.util.List;
import java.util.Map;
/**
 * Created by admin on 2017/12/13.
 */
@RestController
@RequestMapping("AppCheckTask")
public class AppCheckLiveTaskController extends CurdController<CheckLiveTask, AppCheckLiveTaskService> {

    /**
     * 查看待稽查 和已完成的稽查任务
     * @param userId  用户id
     * @param status  稽查任务完成状态
     * @return
     */
    @GetMapping("/appListCheckTask.do")
    public WebApiResponse listCheckTask(@RequestParam("userId")String userId,String status){
        return this.service.listCheckTask(userId,status);
    }
    /**
     * 查看某条稽查内的所有子任务
     * @return
     */
    @GetMapping("/appListAllCheckTask.do")
    public WebApiResponse appListAllCheckTask(String execId,String userId){
        return this.service.appListAllCheckTask(execId,userId);
    }

    /**
     * 查看某条子任务的详情  如果是看护任务  查  kh_task  表  如果是稽查任务 查稽查任务表
     * @param userId
     * @param detailId
     * @return
     */
    @GetMapping("/appListKhTaskById.do")
    public WebApiResponse appListKhTaskById(String userId,String detailId){
        return this.service.appListKhTaskById(userId,detailId);
    }

    /**
     * 查看某条巡视任务的详细信息
     * @param userId
     * @param xslx 0 特殊巡视 1 保电巡视 2 正常巡视
     * @param xsId
     * @return
     */
    @GetMapping("/appListXSTaskById.do")
    public WebApiResponse appListXSTaskById(String userId,String xsId,int xslx){
        return this.service.appListXSTaskById(userId,xsId,xslx);
    }
    /**
     * 查看某个杆塔的所有巡视任务，点选稽查   未完成
     *@param userId 用户名
     *@param towerId 杆塔id
     */
    @GetMapping("/appListXSTaskByTowerId.do")
    public WebApiResponse appListXSTaskById(String userId,String towerId){
        return this.service.appListXSTaskByTowerId(userId,towerId);
    }



}
