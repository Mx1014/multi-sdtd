/**
 * 文件名：KhCycleController
 * 版本信息：
 * 日期：2017/11/28 14:43:44
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.alibaba.druid.sql.visitor.functions.If;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.CheckLiveTask;
import com.rzt.entity.KhSite;
import com.rzt.entity.KhTask;
import com.rzt.entity.KhYhHistory;
import com.rzt.entity.model.KhTaskModel;
import com.rzt.service.CheckLiveTaskService;
import com.rzt.service.KhSiteService;
import com.rzt.service.KhTaskService;
import com.rzt.service.KhYhHistoryService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类名称：KhCycleController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/11/28 14:43:44
 * 修改人：张虎成
 * 修改时间：2017/11/28 14:43:44
 * 修改备注：
 */
@RestController
@RequestMapping("KhSite")
public class KhSiteController extends
        CurdController<KhSite, KhSiteService> {
    /* @Autowired
     private KhYhHistoryService yhservice;
     @Autowired
     private KhTaskService taskService;
     @Autowired
     private CheckLiveTaskService checkService;*/
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @PostMapping("/saveYh.do")
    @ResponseBody
    public WebApiResponse saveYh(KhYhHistory yh, String fxtime, String startTowerName, String endTowerName, String pictureId,String ids) {
        return this.service.saveYh(yh, fxtime, startTowerName, endTowerName, pictureId,ids);
    }
    //新建无隐患看护
    @PostMapping("/saveNoYh")
    @ResponseBody
    public WebApiResponse saveNoYh(KhYhHistory yh, String startTowerName, String endTowerName,String ids) {
        return this.service.saveNoYh(yh, startTowerName, endTowerName,ids);
    }

    /***
     * 获取 待安排的看护任务
     * @return
     */
    @GetMapping("/listAllTaskNotDo.do")
    @ResponseBody
    public WebApiResponse listAllTaskNotDo(String yhjb,String yworg,KhTaskModel task, Pageable pageable, String userName,String currentUserId) {
        try {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            Object userInformation = hashOperations.get("UserInformation", currentUserId);
            JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
            String roleType = jsonObject.get("ROLETYPE").toString();
            return WebApiResponse.success(this.service.listAllTaskNotDo(task, pageable, userName, roleType,yhjb,yworg,currentUserId));
//            return WebApiResponse.success(this.service.listAllTaskNotDo(task, pageable, userName,"0",yhjb,yworg));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    /**
     * 消缺待安排任务   同时将隐患状态修改？
     */
    @PatchMapping("/xiaoQueTask.do")
    @ResponseBody
    public WebApiResponse updateQxTask(String id) {
        try {
            String[] split = id.split(",");
            if (split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    this.service.updateQxTask(Long.parseLong(split[i]));
                }
            }
            return WebApiResponse.success("任务消缺成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("任务消缺失败" + e.getMessage());
        }
    }

    //消缺未派发的任务
    @PatchMapping("/xiaoQueCycle.do")
    @ResponseBody
    public WebApiResponse xiaoQueCycle(String id) {
        try {
            String[] split = id.split(",");
            if (split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    this.service.xiaoQueCycle(Long.parseLong(split[i]));
                }
            }
            return WebApiResponse.success("任务消缺成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("任务消缺失败" + e.getMessage());
        }
    }

    /**
     * 周期维护页面查看任务详情
     */
    @GetMapping("/listKhtaskById.do")
    @ResponseBody
    public WebApiResponse listKhtaskById(HttpServletResponse response, String id) {
        try {
            List list = this.service.listKhtaskById(Long.parseLong(id));
            return WebApiResponse.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    /**
     * 删除待安排任务  请求方式 DELETE  删除看护点
     */
    @DeleteMapping("/deleteById/{id}")
    @ResponseBody
    @Transactional
    public WebApiResponse deleteById(@PathVariable("id") String id) {
        return this.service.deleteById(id);
    }

    //派发任务  参数传递taskList[0].planStartTime
    @PostMapping("/paifaTask.do")
    @ResponseBody
    @Transactional
    public WebApiResponse paifaTask(String id, String tasks) {
        return this.service.paifaTask(id, tasks);
    }

    //  PC查看任务详情
    @PostMapping("/listJpgById.do")
    @ResponseBody
    public WebApiResponse listJpgById(String taskId) {
        return this.service.listJpgById(taskId);
    }

    @GetMapping("/listOverdueKh.do")
    @ResponseBody
    public WebApiResponse listOverdueKh() {
        return this.service.listOverdueKh();
    }

    /**
     * 导出文件的接口
     *
     * @param request
     * @param response
     */
    @GetMapping("/exportNursePlan.do")
    public void exportNursePlan(HttpServletRequest request, HttpServletResponse response, String currentUserId) {

        try {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", currentUserId).toString());
            this.service.exportNursePlan(request, response, jsonObject, currentUserId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}