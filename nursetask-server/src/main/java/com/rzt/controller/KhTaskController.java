/**
 * 文件名：KhTaskController
 * 版本信息：
 * 日期：2017/11/28 14:43:44
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.KhSite;
import com.rzt.entity.KhTask;
import com.rzt.entity.model.KhTaskModel;
import com.rzt.service.KhTaskService;
import com.rzt.service.KhYhHistoryService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("KhTask")
public class KhTaskController extends
        CurdController<KhTask, KhTaskService> {
    @Autowired
    private KhYhHistoryService yhservice;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /***
     * 所有看护计划查询
     * @return
     */
    @GetMapping("/listAllKhTask.do")
    @ResponseBody
    public WebApiResponse listAllKhTask(KhTaskModel task, String status, Pageable pageable, String yworg, String currentUserId) {
        try {
            //分页参数 page size
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            JSONObject jsonObject = new JSONObject();
                jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", currentUserId).toString());
            Object o = this.service.listAllKhTask(task, status, pageable, Integer.valueOf(jsonObject.get("ROLETYPE").toString()), yworg,currentUserId);
            //Object o = this.service.listAllKhTask(	task, status,pageable,0);
            return WebApiResponse.success(o);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }
    /**
     * 修改已安排任务
     */
    @GetMapping("/updateTaskById.do")
    @ResponseBody
    public WebApiResponse updateTaskById(String khfzrId1, String id) {
        // 提交申请给 管理员  如何提交待定  还是说没有修改功能
        try {
            //分页参数 page size
            this.service.updateTaskById(khfzrId1, id);
            return WebApiResponse.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("修改失败" + e.getMessage());
        }
    }

    /**
     * 查看已安排任务详情
     *
     * @param id
     * @return
     */
    @GetMapping("/getKhTaskById.do")
    @ResponseBody
    public WebApiResponse listKhTaskById(String id) {
        try {
            List<Map<String, Object>> maps = this.service.getKhTaskById(Long.parseLong(id));
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败" + e.getMessage());
        }
    }

    //任务查询页面的导出文件
    @GetMapping("/exportKhTask.do")
    public void exportKhTask(HttpServletRequest request, HttpServletResponse response, String currentUserId) {
        try {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", currentUserId).toString());
            List<Map<String, Object>> taskList = this.service.findAlls(jsonObject, currentUserId);
            this.service.exportNursePlan(taskList, request, response);
            //return WebApiResponse.success("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取某人当前的看护任务
     */
    @GetMapping("/listCurrentTaskByUserId")
    public WebApiResponse listCurrentTaskByUserId(String userId, String startDate) {
        return this.service.listCurrentTaskByUserId(userId, startDate);
    }

    /**
     * 隐患台账获取看护任务详情
     *
     * @param yhId
     * @return
     */
    @GetMapping("/listTaskInfoByYhId")
    @ResponseBody
    public WebApiResponse listTaskInfoByYhId(String yhId) {
        return this.service.listTaskInfoByYhId(yhId);
    }

    //隐患台账展示隐患图片
    @GetMapping("/listPictureByYhId")
    @ResponseBody
    public WebApiResponse listPictureByYhId(String yhId) {
        return this.service.listPictureByYhId(yhId);
    }


    //地图展示某人的具体任务信息
    @GetMapping("/listTaskInfoById")
    @ResponseBody
    public WebApiResponse listTaskInfoById(String taskId) {
        return this.service.listTaskInfoById(taskId);
    }

    //地图展示任务图片
    @ApiOperation(value = "获取任务图片", notes = "获取任务图片  ")
    @GetMapping("/appListPicture")
    @ResponseBody
    public WebApiResponse appListPicture(long taskId, Integer zj) {
        return this.service.appListPicture(taskId, zj);
    }

    //定时任务
    @GetMapping("/createTask")
    @ResponseBody
    public void createTask(String taskId) {
        this.service.createTask();
    }

    //删除单、多个周期
    @DeleteMapping("/deleteSiteById")
    @ResponseBody
    public WebApiResponse deleteSiteById(String id) {
        try {
            String[] split = id.split(",");
            if (split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    this.service.deleteSiteById(Long.parseLong(split[i]));
                }
            }
            return WebApiResponse.success("任务删除成功");
        } catch (Exception e) {
            //e.printStackTrace();
            return WebApiResponse.erro("任务删除成功" + e.getMessage());
        }
    }

    //删除单、多个任务
    @DeleteMapping("/deleteTaskById")
    @ResponseBody
    public WebApiResponse deleteTaskById(String id) {
        try {
            String[] split = id.split(",");
            if (split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    this.service.deleteTaskById(Long.parseLong(split[i]));
                }
            }
            return WebApiResponse.success("任务删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("任务删除失败" + e.getMessage());
        }
    }
}


