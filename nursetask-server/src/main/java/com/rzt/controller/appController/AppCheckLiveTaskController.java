package com.rzt.controller.appController;

import com.rzt.service.app.AppCheckLiveTaskService;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value = "任务展示", notes = "查看待稽查 和已完成的稽查任务")
    @GetMapping("/appListCheckTask.do")
    public WebApiResponse listCheckTask(@RequestParam("userId") String userId, int status) {
        return this.service.listCheckTask(userId, status);
    }

    @ApiOperation(value = "任务展示", notes = "查看某条稽查内的所有子任务")
    @GetMapping("/appListAllCheckTask.do")
    public WebApiResponse appListAllCheckTask(Long execId, String userId) {
        return this.service.appListAllCheckTask(execId, userId);
    }

    /**
     * 查看某条子任务的详情  如果是看护任务  查  kh_task  表  如果是稽查任务 查稽查任务表
     */
    @ApiOperation(value = "任务展示", notes = "看护详情页面数据展示")
    @GetMapping("/appListKhTaskById.do")
    public WebApiResponse appListKhTaskById(String userId, Long detailId) {
        return this.service.appListKhTaskById(userId, detailId);
    }


    //看护详情 → 到岗到位检查页面   头像未返回  坐标怎么接收,判断是否到达现场
    @ApiOperation(value = "看护详情", notes = "修改稽查人到达现场时间  ")
    @GetMapping("/updateDdxcTime.do")
    public WebApiResponse updateDdxcTime(String userId,  Long KhTaskId, String date, Long detailId) {
        return this.service.updateDdxcTime(userId, KhTaskId, date, detailId);
    }


    // 到岗到位检查页面 → 现场稽查稽查问卷  需要参数：khTaskId  exceId（detailId）taskId(稽查子任务id) sfzg(0 1) ryyz(0 1) dydj(电子围栏)
    //人员照片和现场环境未上传  电子围栏怎么设置
    @ApiOperation(value = "任务展示", notes = "到岗到位检查页面")
    @GetMapping("/appDgdwCheck.do")
    public WebApiResponse appDgdwCheck(CheckLiveTaskDetail detail) {
        return this.service.appDgdwCheck(detail);
    }

    //到岗到位 → 现场稽查稽查问卷  其他问题是要保存问题吗
    @ApiOperation(value = "任务展示", notes = "完成稽查")
    @GetMapping("/appCompleteTask.do")
    public WebApiResponse appCompleteTask(CheckLiveTaskDetail detail) {
        return this.service.appCompleteTask(detail);
    }


    /**
     * 查看某个杆塔的所有巡视任务，点选稽查   未完成  未完成
     */
    @ApiOperation(value = "任务展示", notes = "查看某个杆塔的所有巡视任务，点选稽查  ")
    @GetMapping("/appListXSTaskByTowerId.do")
    public WebApiResponse appListXSTaskByTowerId(String userId, Long towerId) {
        return this.service.appListXSTaskByTowerId(userId, towerId);
    }

    @ApiOperation(value = "任务展示", notes = "查看某条巡视任务的详细信息 xslx 0 特殊巡视 1 保电巡视 2 正常巡视")
    @GetMapping("/appListXSTaskById.do")
    public WebApiResponse appListXSTaskById(String userId, Long xsId, int xslx) {
        return this.service.appListXSTaskById(userId, xsId, xslx);
    }


}
