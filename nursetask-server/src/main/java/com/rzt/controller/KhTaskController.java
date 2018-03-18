/**
 * 文件名：KhTaskController
 * 版本信息：
 * 日期：2017/11/28 14:43:44
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.KhSite;
import com.rzt.entity.KhTask;
import com.rzt.entity.KhYhTower;
import com.rzt.entity.model.KhTaskModel;
import com.rzt.repository.KhYhTowerRepository;
import com.rzt.service.KhTaskService;
import com.rzt.service.KhYhHistoryService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.redis.core.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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
    @Autowired
    private KhYhTowerRepository towerRepository;

    /***
     * 所有看护计划查询
     * @return
     */
    @GetMapping("/listAllKhTask.do")
    @ResponseBody
    public WebApiResponse listAllKhTask(KhTaskModel task, String status, Integer page, Integer size, String yworg, String currentUserId, String home, String tdOrg) {
        Pageable pageable = new PageRequest(page, size);
        try {
            //分页参数 page size
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", currentUserId).toString());
            Object o = this.service.listAllKhTask(task, status, pageable, jsonObject, yworg, currentUserId, home, tdOrg);
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
    public WebApiResponse updateTaskById(String khfzrId1, String id, String time) {
        try {
            List<Map<Object, String>> list = (List<Map<Object, String>>) JSONObject.parse(time);
            //修改看护人的时间
            for (Map map : list) {
                this.service.updateSiteTimeById(Long.parseLong(map.get("ID").toString()), DateUtil.parseDate(map.get("PLAN_START_TIME").toString()), DateUtil.parseDate(map.get("PLAN_END_TIME").toString()));
            }
            //分页参数 page size
            if (!StringUtils.isEmpty(khfzrId1)) {
                this.service.updateTaskById(khfzrId1, id);
            }
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
    public void createTask() {
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

    @GetMapping("/listTowerPoint")
    @ResponseBody
    public WebApiResponse listTowerPoint(String id) {
        try {
            return this.service.listTowerPoint(Long.parseLong(id));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    @GetMapping("/listTowerPoint2")
    @ResponseBody
    public WebApiResponse listTowerPoint2(String id) {
        try {
            return this.service.listTowerPoint2(Long.parseLong(id));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    @GetMapping("/listTime")
    @ResponseBody
    public WebApiResponse listTime(String id) {
        try {
            return WebApiResponse.success(this.service.listTime(Long.parseLong(id)));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("获取失败");
        }
    }

    //更新redis中每个看护点涉及塔的坐标
    @GetMapping("flushKhSite")
    public WebApiResponse flushKhSite() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String condition = " WHERE status=1 and PLAN_START_TIME<=sysdate and PLAN_END_TIME>=sysdate and IS_DW=1 and SFDJ=1 and ddxc_time is not null";
        String userSql = "select distinct(user_id),k.id,YH_ID from KH_TASK k LEFT JOIN KH_YH_HISTORY y on y.id = k.YH_ID  " + condition;
       //获取能够开启脱岗的隐患，刷新redis缓存
        String sql = "select DISTINCT YH_ID from KH_TASK k LEFT JOIN KH_YH_HISTORY y on y.id = k.YH_ID " + condition;
        List<Map<String, Object>> maps = this.service.execSql(sql);
        String towerSql = "SELECT TOWER_ID,LONGITUDE,LATITUDE,NAME FROM KH_YH_TOWER t left JOIN CM_TOWER tt on t.TOWER_ID=tt.ID WHERE yh_id = ?";
        for (Map map : maps) {
            List<Map<String, Object>> towerMaps = this.service.execSql(towerSql, Long.parseLong(map.get("YH_ID").toString()));
            valueOperations.set("KhSite:" + map.get("YH_ID").toString(), towerMaps);
        }
        return WebApiResponse.success("刷新成功");
    }
    @GetMapping("offPost")
    public void offPost() {
        GeoOperations<String, Object> geoOperations = redisTemplate.opsForGeo();
        this.service.offPost(geoOperations);
    }
    //插入杆塔
    @GetMapping("instetKhTower")
    @Transactional(rollbackFor = Exception.class)
    public void ceshi(String yhid) {
//        Long yhId = Long.parseLong(yhid);
        String sql = "SELECT DISTINCT YH_ID FROM KH_YH_TOWER";
        List<Map<String, Object>> maps = this.service.execSql(sql);
        JSONObject json = new JSONObject();
        for (Map map : maps) {
            json.put("YH_ID", map.get("YH_ID").toString());
        }
        sql = "SELECT DISTINCT  (s.YH_ID) FROM KH_YH_HISTORY y, KH_SITE s  WHERE s.YH_ID = y.ID AND s.STATUS = 1 AND y.yhzt = 0 and y.sfdj=1";
        List<Map<String, Object>> maps1 = this.service.execSql(sql);
        for (Map yh : maps1) {
            if (!json.containsKey(yh.get("YH_ID").toString())) {
                sql = "SELECT START_TOWER,END_TOWER FROM KH_YH_HISTORY where id=?";
                try {
                    Map<String, Object> map = this.service.execSqlSingleResult(sql, Long.parseLong(yh.get("YH_ID").toString()));
                    if (map.get("START_TOWER")!=null &&map.get("START_TOWER").toString().length()<7&&map.get("END_TOWER")!=null &&map.get("END_TOWER").toString().length()<7 ) {
                        Long start = Long.parseLong(map.get("START_TOWER").toString());
                        Long end = Long.parseLong(map.get("END_TOWER").toString());
                        for (Long i = start; i <= end; i++) {
                            KhYhTower tower = new KhYhTower();
                            tower.setId(0L);
                            tower.setRadius(500);
                            tower.setTowerId(i);
                            tower.setYhId(Long.parseLong(yh.get("YH_ID").toString()));
                            towerRepository.save(tower);
                        }
                    }else {
                        System.err.println(yh.get("YH_ID").toString()+"-----------------------------");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


