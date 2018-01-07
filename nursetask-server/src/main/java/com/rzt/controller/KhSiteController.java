/**
 * 文件名：KhCycleController
 * 版本信息：
 * 日期：2017/11/28 14:43:44
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

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
 * @version
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


    //  数据没有设置完成  稽查任务实体类有部分修改
    @PostMapping("/saveYh.do")
    @ResponseBody
    public WebApiResponse saveYh(KhYhHistory yh, String fxtime,String startTowerName,String endTowerName,String pictureId) {
        return this.service.saveYh(yh, fxtime,startTowerName,endTowerName,pictureId);
    }


    /***
     * 获取 待安排的看护任务
     * @return
     */
    @GetMapping("/listAllTaskNotDo.do")
    @ResponseBody
    public WebApiResponse listAllTaskNotDo(HttpServletResponse response, KhTaskModel task, Pageable pageable, String userName,String deptId) {
        try {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", task.getUserId()).toString());
            return WebApiResponse.success(this.service.listAllTaskNotDo(task, pageable, userName,deptId,jsonObject.get("ROLETYPE").toString()));
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
    @GetMapping("/listKhtaskById.do")
    @ResponseBody
    public WebApiResponse listKhtaskByid(HttpServletResponse response,String id) {
        try {
            response.setHeader("Access-Control-Allow-Origin","*");
            List list = this.service.listKhtaskByid(Long.parseLong(id));
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
    public WebApiResponse paifaTask(String id, String tasks, KhTaskModel model) {
        return this.service.paifaTask(id,tasks,model);
    }
    //  PC查看任务详情
    @PostMapping("/listJpgById.do")
    @ResponseBody
    public WebApiResponse listJpgById(String taskId) {
        return this.service.listJpgById(taskId);
    }

    @GetMapping("/listOverdueKh.do")
    @ResponseBody
    public WebApiResponse listOverdueKh(){
        return this.service.listOverdueKh();
    }

    /**
     * 导出文件的接口
     * @param request
     * @param response
     */
    @GetMapping("/exportNursePlan.do")
    public void exportNursePlan(HttpServletRequest request, HttpServletResponse response) {
        this.service.exportNursePlan(request,response);
    }

	 /* *//**
     * 审批隐患后
     * @param id
     * @return
     *//*
    @GetMapping("/shenpiYh")
    @ResponseBody
    public WebApiResponse shenpiYh(String id, KhYhHistory yh1) {
        try {
//			yhservice.update(); 修改隐患审批状态

            KhYhHistory yh = yhservice.findOne(id);
            KhSite task = new KhSite();
            String taskName = yh.getVtype() + yh.getLineName() + yh.getStartTower() + "-" + yh.getEndTower() + "号杆塔看护任务";
            task.setCreateTime(new Date());
            task.setVtype(yh.getVtype());
            task.setLineName(yh.getLineName());
            task.setTdywOrg(yh.getTdywOrg());
            task.setTaskName(taskName);
            task.setStatus(0);//隐患未消除
            task.setStatus(0);//未停用
            task.setCount(0);//生成任务次数0
            task.setYhId(yh.getId());
            task.setCreateTime(new Date());
            this.service.add(task);
            //	yh1.setYhdm("已定级");
            //yh1.setTaskId(task.getId());
            yhservice.update(yh1, id);
            return WebApiResponse.success("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }*/
}