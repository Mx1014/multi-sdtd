/**
 * 文件名：KHYHHISTORYController
 * 版本信息：
 * 日期：2017/11/30 18:31:34
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.netflix.discovery.converters.Auto;
import com.rzt.entity.KhTask;
import com.rzt.entity.KhYhHistory;
import com.rzt.entity.XsSbYh;
import com.rzt.service.KhYhHistoryService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名称：KHYHHISTORYController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/11/30 18:31:34
 * 修改人：张虎成
 * 修改时间：2017/11/30 18:31:34
 * 修改备注：
 */
@RestController
@RequestMapping("kyYhHistory")
public class KhYhHistoryController extends
        CurdController<KhYhHistory, KhYhHistoryService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @ApiOperation(notes = "施工情况", value = "施工情况")
    @PostMapping("/saveYh")
    @ResponseBody
    public WebApiResponse saveYh(XsSbYh yh, String startTowerName, String endTowerName, String pictureId) {
        return this.service.saveYh(yh, startTowerName, endTowerName, pictureId);
    }

    @ApiOperation(notes = "隐患台账保存坐标", value = "隐患台账保存坐标")
    @PostMapping("/saveCoordinate")
    @ResponseBody
    public WebApiResponse saveCoordinate(String yhId, String lat, String lon, String radius) {
        return this.service.saveCoordinate(yhId, lat, lon, radius);
    }

    @ApiOperation(notes = "地图撒坐标点", value = "地图撒坐标点")
    @GetMapping("/listCoordinate")
    @ResponseBody
    public WebApiResponse listCoordinate(String yhjb, String yhlb) {
        return this.service.listCoordinate(yhjb, yhlb);
    }

    @ApiOperation(notes = "地图查看隐患信息", value = "地图查看隐患信息")
    @GetMapping("/listYhById")
    @ResponseBody
    public WebApiResponse listYhById(String yhId) {
        return this.service.listYhById(yhId);
    }

    @ApiOperation(value = "隐患导入接口", notes = "隐患导入接口")
    @PostMapping("ImportYh")
    public WebApiResponse ImportYh(MultipartFile file) {
        if (file.getName().contains("xls")) {
            return service.ImportYh(file);
        } else {
            return service.ImportYh2(file);
        }
    }

    @ApiOperation(value = "隐患导入模板", notes = "隐患导入模板")
    @GetMapping("ImportYhExam")
    public void ImportYhExam(HttpServletResponse response) {
        service.ImportYhExam(response);
    }

    @ApiOperation(value = "隐患导出接口", notes = "隐患导出接口")
    @GetMapping("exportYhHistory")
    public WebApiResponse exportYhHistory(HttpServletResponse response, String currentUserId) {
     /* MultipartFile file */
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", currentUserId).toString());
        return service.exportYhHistory(response, jsonObject, currentUserId);
    }

    @ApiOperation(value = "修改隐患信息", notes = "修改隐患信息")
    @PatchMapping("updateYhHistory")
    public WebApiResponse updateYhHistory(KhYhHistory yh, String startTowerName, String endTowerName) {
        return service.updateYhHistory(yh, startTowerName, endTowerName);
    }

    @ApiOperation(value = "隐患重新定级", notes = "隐患重新定级")
    @PatchMapping("updateYhjb")
    public WebApiResponse updateYhjb(String yhjb) {
        return service.updateYhjb(yhjb);
    }

    @ApiOperation(value = "区镇村三级联动", notes = "区镇村三级联动")
    @GetMapping("lineArea")
    public WebApiResponse lineArea(Integer id) {
     /* MultipartFile file */
        return service.lineArea(id);

    }

    @GetMapping("/a123")
    public void a123() {
        this.service.find();
    }

    @ApiOperation(value = "隐患审核通过", notes = "隐患审核通过")
    @GetMapping("reviewYh")
    public WebApiResponse reviewYh(long yhId) {
        return this.service.reviewYh(yhId);
    }

    @ApiOperation(value = "隐患台账删除", notes = "隐患台账删除")
    @DeleteMapping("deleteYhById")
    public WebApiResponse deleteYhById(long yhId) {
        return this.service.deleteYhById(yhId);
    }

    @ApiOperation(value = "杆塔坐标采集", notes = "杆塔坐标采集")
    @GetMapping("updateTowerById")
    public WebApiResponse updateTowerById(long id, String lon, String lat,String userId, String lineName,String detailId) {
        return this.service.updateTowerById(id, lon, lat,userId,lineName,detailId);
    }

    @ApiOperation(value = "判断线路是否属于通州公司、门头沟公司", notes = "隐患台账删除")
    @GetMapping("findLineOrg")
    public WebApiResponse findLineOrg(long towerId) {
        return this.service.findLineOrg(towerId);
    }

    @ApiOperation(value = "隐患台账图片展示", notes = "隐患台账图片展示")
    @GetMapping("findYhPicture")
    public WebApiResponse findYhPicture(long yhId) {
        return this.service.findYhPicture(yhId);
    }

    @ApiOperation(value = "隐患台账图片展示", notes = "隐患台账图片展示")
    @GetMapping("addTdOrgId")
    public void addTdOrgId() {
        try {
            String sql = "select s.tdyw_orgid td,s.wx_orgid wx,k.id id  from kh_task k left join kh_site s on s.id = k.site_id where k.YWORG_ID is null";
            List<Map<String, Object>> maps = this.service.execSql(sql);
            for (Map map : maps) {
                long id = Long.parseLong(map.get("ID").toString());
                String td = map.get("TD").toString();
                this.service.addTdOrgId(Long.parseLong(map.get("ID").toString()), map.get("TD").toString(),map.get("WX"));
//                try {
//                    this.service.addTdOrgId2(Long.parseLong(map.get("ID").toString()), );
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    /*@ApiOperation(value = "隐患台账图片展示", notes = "隐患台账图片展示")
    @GetMapping("addadd")
    public Map addadd() {
        try {
            Map message = new HashMap<String, Object>();
            message.put("module", 11);
            String module11 = "select * from TIMED_CONFIG where id LIKE 'TIME_CONFIG'";
            Map<String, Object> timeConfig = this.service.execSqlSingleResult(module11);
            String newTime = "SELECT THREEDAY,CREATETIME FROM (SELECT * FROM TIMED_TASK where THREEDAY=? ORDER BY CREATETIME DESC ) WHERE rownum =1 ";
            Map<String, Object> towHour = this.service.execSqlSingleResult(newTime, 0);
            Map<String, Object> threeDay = this.service.execSqlSingleResult(newTime, 1);
            Map<Object, Object> returnMap = new HashMap<>();
            returnMap.put("dqsj",DateUtil.getNowDate());
            Date yjcreatetime = DateUtil.parseDate(towHour.get("CREATETIME").toString());
            Date ercreatetime = DateUtil.parseDate(towHour.get("CREATETIME").toString());
            returnMap.put("xcsjyj",DateUtil.addDate(yjcreatetime,72));
            returnMap.put("yjjg","72小时/次");
            if (ercreatetime.getTime()>= DateUtil.getScheduleTime(timeConfig.get("START_TIME").toString())) {
                returnMap.put("xcsjej",DateUtil.addDate(ercreatetime,Double.parseDouble(timeConfig.get("DAY_ZQ").toString())));
                returnMap.put("ejjg",timeConfig.get("DAY_ZQ")+"小时/次");
            }else if(ercreatetime.getTime() <=DateUtil.getScheduleTime(timeConfig.get("END_TIME").toString())){
                returnMap.put("xcsjej",DateUtil.addDate(ercreatetime,Double.parseDouble(timeConfig.get("NIGHT_ZQ").toString())));
                returnMap.put("ejjg",timeConfig.get("NIGHT_ZQ")+"小时/次");
            }
            message.put("data", returnMap);
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }*/
}