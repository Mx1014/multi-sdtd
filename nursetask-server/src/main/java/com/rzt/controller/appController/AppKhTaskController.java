package com.rzt.controller.appController;

import com.rzt.controller.CurdController;
import com.rzt.entity.KhTask;
import com.rzt.entity.KhTaskWpqr;
import com.rzt.eureka.UserCenter;
import com.rzt.service.KhTaskService;
import com.rzt.service.app.AppKhTaskService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * Created by admin on 2017/12/8.
 */
@RestController
@RequestMapping("AppKhTask")
public class AppKhTaskController extends
        CurdController<KhTask,AppKhTaskService> {



    @ApiOperation(value = "看护任务展示", notes = "查看当前用户的已办和待办的任务 1 未办 进行中 2  ")
    @GetMapping("/appListkhTask.do")
    @ResponseBody
    public WebApiResponse appListkhTask(int dbyb, Pageable pageable,String userId){
        try {
            return WebApiResponse.success(this.service.appListkhTask(dbyb, pageable, userId));
        }catch (Exception e){
            return WebApiResponse.erro("数据获取失败");
        }
    }

    @ApiOperation(value = "看护任务详情", notes = "查看当前看护任务的详细信息  ")
    @GetMapping("/appListkhTaskById")
    @ResponseBody
    public WebApiResponse appListkhTaskById(String taskId){
        return this.service.appListkhTaskById(taskId);
    }

    //任务详情 → 人员信息收集
    @ApiOperation(value = "人员信息收集", notes = "身份确认接口  ")
    @GetMapping("/appListUserInfoById")
    @ResponseBody
    public WebApiResponse appListUserInfoById(String userId,String taskId){
        return this.service.appListUserInfoById(userId,taskId);
    }

    //人员收集  → 物品提示  图片信息 未完成！！！！！！！
    @ApiOperation(value = "物品提示", notes = "收集看护人照片信息  ")
    @GetMapping("/appSavePhoto")
    @ResponseBody
    public WebApiResponse appSavePhoto(String userId,String taskId){
        return this.service.appSavePhoto(userId,taskId);
    }

    //物品提示 → 看护提醒   目前施工进度从哪里来
    @ApiOperation(value = "物品提示", notes = "收集看护人照片信息  ")
    @GetMapping("/appSaveWpzt")
    @ResponseBody
    public WebApiResponse appSaveWpzt(KhTaskWpqr task){
        return this.service.appSaveWpzt(task);
    }

    //到达现场 → 开始看护
    @ApiOperation(value = "物品提示", notes = "收集看护人照片信息  ")
    @GetMapping("/appDdcx")
    @ResponseBody
    public WebApiResponse appDdcx(String taskId){
        return this.service.appDdcx(taskId);
    }
   /* @GetMapping("/updateTaskTime.do")
    @ResponseBody
    public void updateTaskTime(String step, Date time, String id){

        if (step.equals("1")){
            //设置到达现场时间
            this.service.updateDDTime(time,Long.parseLong(id));
        }else if(step.equals("2")){
            //设置身份确认时间
            this.service.updateSFQRTime(time,Long.parseLong(id));
        }else if(step.equals("3")){
            //设置物品确认时间
            this.service.updateWPQRTime(time,Long.parseLong(id));
        }
        else if(step.equals("4")){
            //设置实际开始时间 修改看护任务状态
            this.service.updateRealStartTime(time,Long.parseLong(id));
        }else{
            //交接班,设置世界结束时间
        }
    }*/
}