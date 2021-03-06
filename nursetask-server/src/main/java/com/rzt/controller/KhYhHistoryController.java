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
import com.rzt.repository.KhYhHistoryRepository;
import com.rzt.service.KhYhHistoryService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    private KhYhHistoryRepository repository;

    @ApiOperation(notes = "施工情况", value = "施工情况")
    @PostMapping("/saveYh")
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
    public WebApiResponse listCoordinate(String yhjb, String yhlb, String currentUserId,String queryAll,String deptId) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        Object userInformation = hash.get("UserInformation", currentUserId);
        JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
        return this.service.listCoordinate(yhjb, yhlb, jsonObject,queryAll,deptId);
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
    public WebApiResponse updateYhHistory(KhYhHistory yh, String startTowerName, String endTowerName,String ids) {
        return service.updateYhHistory(yh, startTowerName, endTowerName,ids);
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
    public WebApiResponse updateTowerById(long id, String lon, String lat, String userId, String lineName, String detailId) {
        return this.service.updateTowerById(id, lon, lat, userId, lineName, detailId);
    }

    @ApiOperation(value = "判断线路是否属于通州公司、门头沟公司", notes = "隐患台账删除")
    @GetMapping("findLineOrg")
    public WebApiResponse findLineOrg(long towerId) {
        return this.service.findLineOrg(towerId);
    }

    @ApiOperation(value = "隐患台账图片展示", notes = "隐患台账图片展示")
    @GetMapping("findYhPicture")
    public WebApiResponse findYhPicture(String yhId) {
        return this.service.findYhPicture(Long.parseLong(yhId));
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
                this.service.addTdOrgId(Long.parseLong(map.get("ID").toString()), map.get("TD").toString(), map.get("WX"));
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


    //隐患采集记录
    @Transactional
    @GetMapping("listUpdateRecord")
    public WebApiResponse listUpdateRecord(Pageable pageable, String deptId) {
        try {
            String s = "";
            if (deptId != null && !deptId.equals("")) {
                s = " and u.DEPTID='" + deptId + "' ";
            }
            String sql = "SELECT\n" +
                    "  zt.TASK_NAME,\n" +
                    "  D.DEPTNAME,\n" +
                    "  U.REALNAME,\n" +
                    "  L.V_LEVEL,\n" +
                    "  t.NAME,\n" +
                    "  R.*\n" +
                    " FROM CM_TOWER_UPDATE_RECORD R LEFT JOIN RZTSYSUSER U ON U.ID = R.USER_ID\n" +
                    "  LEFT JOIN CM_TOWER T ON T.ID = R.TOWER_ID\n" +
                    "  LEFT JOIN CM_LINE L ON L.ID = T.LINE_ID LEFT JOIN RZTSYSDEPARTMENT D ON D.ID =U.DEPTID \n" +
                    "  LEFT JOIN XS_ZC_TASK_EXEC_DETAIL ED on ed.id = r.DETAIL_ID LEFT JOIN XS_ZC_TASK_EXEC TE ON ED.XS_ZC_TASK_EXEC_ID = TE.ID\n" +
                    "  LEFT JOIN XS_ZC_TASK ZT ON ZT.ID = TE.XS_ZC_TASK_ID\n" +
                    " WHERE r.status = 0\n" + s +
                    " ORDER BY r.create_time DESC";
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("失败");
        }
    }

    @GetMapping("updateTower")
    public WebApiResponse updateTower(String towerId, String lon, String lat, String id) {
        try {
            repository.updateTowerById(Long.parseLong(towerId), lon, lat);
            repository.deleteRecord2(Long.parseLong(id));
            return WebApiResponse.success("成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("失败");
        }
    }

    //生成看护点
    @Transactional
    @GetMapping("saveCycle")
    public WebApiResponse saveCycle(String yhId) {
        try {
            this.service.saveCycle(Long.parseLong(yhId));
            return WebApiResponse.success("成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("失败");
        }
    }

    @Transactional
    @GetMapping("listTowerPicture")
    public WebApiResponse listTowerPicture(String detailId) {
        try {
            /* String sql = "SELECT OPERATE_NAME FROM XS_ZC_TASK_EXEC_DETAIL where id = ?";
            Map<String, Object> map = this.service.execSqlSingleResult(sql, Long.parseLong(detailId));*/
            String sql = "SELECT * FROM PICTURE_TOUR WHERE PROCESS_id =?";
            List<Map<String, Object>> list = this.service.execSql(sql, detailId);
            return WebApiResponse.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("失败");
        }
    }

    @GetMapping("/deleteRecord")
    public WebApiResponse deleteRecord(String id) {
        try {
            repository.deleteRecord(Long.parseLong(id));
            return WebApiResponse.success("成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("失败");
        }
    }
}