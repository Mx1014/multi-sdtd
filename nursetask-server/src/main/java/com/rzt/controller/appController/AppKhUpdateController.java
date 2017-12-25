package com.rzt.controller.appController;

import com.rzt.controller.CurdController;
import com.rzt.entity.KhTask;
import com.rzt.entity.KhTaskWpqr;
import com.rzt.service.app.AppKhUpdateService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * Created by admin on 2017/12/22.
 */
@RestController
@RequestMapping("AppUpdate")
public class AppKhUpdateController  extends
        CurdController<KhTask,AppKhUpdateService> {

    @ApiOperation(value = "修改实际开始时间", notes = "修改实际开始时间、执行页数 ")
    @PatchMapping("/updateRealTime")
    @ResponseBody
    public WebApiResponse updateRealTime(long taskId){
        return this.service.updateRealTime(taskId);
    }

    @ApiOperation(value = "修改身份确认时间", notes = "修改身份确认时间、执行页数 ")
    @PatchMapping("/updateSfqrTime")
    @ResponseBody
    public WebApiResponse updateSfqrTime(long taskId){
        return this.service.updateSfqrTime(taskId);
    }

    @ApiOperation(value = "修改物品确认时间", notes = "修改物品确认时间、执行页数 ")
    @PatchMapping("/updateWpqrTime")
    @ResponseBody
    public WebApiResponse updateWpqrTime(KhTaskWpqr task){
        return this.service.updateWpqrTime(task);
    }

    @ApiOperation(value = "修改看护提醒执行页数", notes = "修改执行页数 ")
    @PatchMapping("/updateKhtx")
    @ResponseBody
    public WebApiResponse updateKhtx(long taskId){
        return this.service.updateKhtx(taskId);
    }

    @ApiOperation(value = "修改到达现场时间", notes = "修改到达现场时间、执行页数 ")
    @PatchMapping("/updateDdxcTime")
    @ResponseBody
    public WebApiResponse updateDdxcTime(long taskId){
        return this.service.updateDdxcTime(taskId);
    }

    @ApiOperation(value = "开始看护页面", notes = "开始看护页面 ")
    @PatchMapping("/updateClzt")
    @ResponseBody
    public WebApiResponse updateClzt(String clzt,long taskId){
        return this.service.updateClzt(clzt,taskId);

    }

    @ApiOperation(value = "交接班", notes = "交接班 ")
    @PatchMapping("/updateEndTime")
    @ResponseBody
    public WebApiResponse updateEndTime(long taskId){
        return this.service.updateEndTime(taskId);
    }
  /*  @ApiOperation(value = "开始看护页面", notes = "开始看护页面 ")
    @PatchMapping("/updateClzt")
    @ResponseBody
    public WebApiResponse updateClzt(String clzt,long taskId){
        return this.service.updateClzt(clzt,taskId);
    }*/
}
