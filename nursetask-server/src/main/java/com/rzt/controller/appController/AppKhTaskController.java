package com.rzt.controller.appController;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import com.rzt.controller.CurdController;
import com.rzt.entity.KhTask;
import com.rzt.entity.KhTaskWpqr;
import com.rzt.service.KhTaskService;
import com.rzt.service.app.AppKhTaskService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.Constances;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/12/8.
 */
@RestController
@RequestMapping("AppKhTask")
public class AppKhTaskController extends
        CurdController<KhTask,AppKhTaskService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


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
    @GetMapping("/getYbCount.do")
    @ResponseBody
    public WebApiResponse getYbCount(String userId){
        return this.service.getYbCount(userId);
    }
    @GetMapping("/getDbCount.do")
    @ResponseBody
    public WebApiResponse getDbCount(String userId){
        return this.service.getDbCount(userId);
    }

    @ApiOperation(value = "看护任务详情", notes = "查看当前看护任务的详细信息  ")
    @GetMapping("/appListkhTaskById.do")
    @ResponseBody
    public WebApiResponse appListkhTaskById(String taskId){
        return this.service.appListkhTaskById(taskId);
    }

    //任务详情 → 人员信息收集
    @ApiOperation(value = "人员信息收集", notes = "身份确认接口  ")
    @GetMapping("/appListUserInfoById.do")
    @ResponseBody
    public WebApiResponse appListUserInfoById(String userId,String taskId){
        return this.service.appListUserInfoById(userId,taskId);
    }

    //人员收集  → 物品提示  图片信息 未完成！！！！！！！
    @ApiOperation(value = "物品提示", notes = "收集看护人照片信息  ")
    @GetMapping("/appSavePhoto.do")
    @ResponseBody
    public WebApiResponse appSavePhoto(String userId,String taskId){
        return this.service.appSavePhoto(userId,taskId);
    }

    //物品提示 → 看护提醒   目前施工进度从哪里来
    @ApiOperation(value = "看护提醒", notes = "收集物品照片信息  ")
    @GetMapping("/appSaveWpzt.do")
    @ResponseBody
    public WebApiResponse appSaveWpzt(KhTaskWpqr task){
        return this.service.appSaveWpzt(task);
    }


    // 开始看护 →  交接班   现场环境照片保存
    // 问题：上报的危机信息保存到哪里 现场工况采集如何保存
    @ApiOperation(value = "开始看护", notes = "收集现场环境照片、工况、危机信息  ")
    @GetMapping("/appExchange.do")
    @ResponseBody
    public WebApiResponse appExchange(String taskId){
        return this.service.appExchange(taskId);
    }

    @ApiOperation(value = "查看接班人", notes = "查看接班人  ")
    @GetMapping("/appListjbr.do")
    @ResponseBody
    public WebApiResponse appListjbr(String userId,long taskId){
        return this.service.appListjbr(userId,taskId);
    }

   //前端传回用户id  获取多个用户坐标
    @GetMapping("/listPoint")
    public List getUser(String ids,String taskId) {
        String[] str = ids.split(",");
        GeoOperations<String, Object> geoOperations = redisTemplate.opsForGeo();
        List<Point> location = geoOperations.geoPos("location", "liuzeID");
        // Point point = this.service.getPoint(taskId);();
        // list.add(point);
        return location;
    }
    //获取中心点坐标
    @GetMapping("listYhPoint")
    public List<Map<String,Object>> listYhPoint(String taskId){
        return this.service.getPoint(Long.parseLong(taskId));
    }

    //KHOffSet(String userId,long taskId)
    //获取交接班的
    /*@GetMapping("listYhPoint")
    public List<Map<String,Object>> listYhPoint(long taskId){
        return this.service.getPoint(taskId);
    }*/
}