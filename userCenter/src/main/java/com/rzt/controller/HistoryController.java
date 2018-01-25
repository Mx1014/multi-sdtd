package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("HISTORY")
public class HistoryController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("historyList")
    public WebApiResponse historyList(String currentUserId, String startTime, String endTime, String deptId) {
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        List listLike = new ArrayList();
        List listLike1 = new ArrayList();
        List listLike2 = new ArrayList();
        List listLike3 = new ArrayList();
        String s = "";
        String s1 = "";
        String s2 = "";
        String s3 = "";
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND YWORG_ID = ?" + listLike.size();
            listLike1.add(deptid);
            s1 += " AND YWORG_ID = ?" + listLike1.size();
            listLike2.add(deptid);
            s2 += " AND YWORG_ID = ?" + listLike2.size();
            listLike3.add(deptid);
            s3 += " AND ID=?" + listLike3.size();
        }
        if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND YWORG_ID = ?" + listLike.size();
            listLike1.add(deptId);
            s1 += " AND YWORG_ID = ?" + listLike1.size();
            listLike2.add(deptId);
            s2 += " AND YWORG_ID = ?" + listLike2.size();
            listLike3.add(deptid);
            s3 += " AND ID=?" + listLike3.size();

        }
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(endTime);
            s += " AND CREATE_TIME<= to_date(?" + listLike.size() + ",'yyyy-MM-dd hh24:mi:ss')";
            listLike.add(startTime);
            s += " AND CREATE_TIME>= to_date( ?" + listLike.size() + ",'yyyy-MM-dd hh24:mi:ss')";
        } else {
            s += "  AND CREATE_TIME is not null ";
        }
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike1.add(endTime);
            s1 += " AND UPDATE_TIME<= to_date(?" + listLike1.size() + ",'yyyy-MM-dd hh24:mi:ss')";
            listLike1.add(startTime);
            s1 += " AND UPDATE_TIME>= to_date( ?" + listLike1.size() + ",'yyyy-MM-dd hh24:mi:ss')";
        } else {
            s1 += "  AND UPDATE_TIME is not null ";
        }
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike2.add(endTime);
            s2 += " AND YHXQ_TIME<= to_date(?" + listLike2.size() + ",'yyyy-MM-dd hh24:mi:ss')";
            listLike2.add(startTime);
            s2 += " AND YHXQ_TIME>= to_date( ?" + listLike2.size() + ",'yyyy-MM-dd hh24:mi:ss')";
        } else {
            s2 += "  AND YHXQ_TIME is not null ";
        }

        String creatHistory = " SELECT " +
                "  sum(decode(YHJB1, '树木隐患', 1, 0)) as creatshu, " +
                "  sum(decode(YHJB1, '建筑隐患', 1, 0)) as creatjian, " +
                "  sum(decode(YHJB1, '异物隐患', 1, 0)) as creatyi, " +
                "  sum(decode(YHJB1, '施工隐患', 1, 0)) as creatshi, " +
                "  YWORG_ID " +
                " FROM KH_YH_HISTORY " +
                " WHERE 1=1 AND YHZT = 0 " + s + " GROUP BY YWORG_ID ";
        String updateHistory = " SELECT " +
                "  sum(decode(YHJB1, '树木隐患', 1, 0)) as UPDATEshu, " +
                "  sum(decode(YHJB1, '建筑隐患', 1, 0)) as UPDATEjian, " +
                "  sum(decode(YHJB1, '异物隐患', 1, 0)) as UPDATEyi, " +
                "  sum(decode(YHJB1, '施工隐患', 1, 0)) as UPDATEshi ," +
                "  YWORG_ID" +
                " FROM KH_YH_HISTORY " +
                " WHERE 1=1 AND YHZT = 0 " + s1 + " GROUP BY YWORG_ID ";
        String yhxqHistory = " SELECT " +
                "  sum(decode(YHJB1, '树木隐患', 1, 0)) as YHXQshu, " +
                "  sum(decode(YHJB1, '建筑隐患', 1, 0)) as YHXQjian, " +
                "  sum(decode(YHJB1, '异物隐患', 1, 0)) as YHXQyi, " +
                "  sum(decode(YHJB1, '施工隐患', 1, 0)) as YHXQshi ," +
                "  YWORG_ID" +
                " FROM KH_YH_HISTORY " +
                " WHERE 1=1 AND YHZT = 0 " + s2 + " GROUP BY YWORG_ID ";
        String deptname = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL " + s3 + " ORDER BY t.DEPTSORT ";
        List<Map<String, Object>> list = this.service.execSql(creatHistory, listLike.toArray());
        List<Map<String, Object>> list1 = this.service.execSql(updateHistory, listLike1.toArray());
        List<Map<String, Object>> list2 = this.service.execSql(yhxqHistory, listLike2.toArray());
        List<Map<String, Object>> list3 = this.service.execSql(deptname, listLike3.toArray());
        Map<String, Map> map = new HashMap();
        Map<String, Map> map1 = new HashMap();
        Map<String, Map> map2 = new HashMap();
        for (int i = 0; i < list.size(); i++) {
            Object yworg_id = list.get(i).get("YWORG_ID");
            if (!StringUtils.isEmpty(yworg_id)) {
                map.put(yworg_id.toString(), list.get(i));
            }
        }
        for (int i = 0; i < list1.size(); i++) {
            Object yworg_id = list1.get(i).get("YWORG_ID");
            if (!StringUtils.isEmpty(yworg_id)) {
                map1.put(yworg_id.toString(), list1.get(i));
            }
        }
        for (int i = 0; i < list2.size(); i++) {
            Object yworg_id = list2.get(i).get("YWORG_ID");
            if (!StringUtils.isEmpty(yworg_id)) {
                map2.put(yworg_id.toString(), list2.get(i));
            }
        }
        for (Map map3 : list3) {
            Map id = map.get(map3.get("ID"));
            if (id == null) {
                map3.put("CREATSHU", 0);
                map3.put("CREATJIAN", 0);
                map3.put("CREATYI", 0);
                map3.put("CREATSHI", 0);
            } else {
                map3.putAll(id);
            }
            Map id1 = map1.get(map3.get("ID"));
            if (id1 == null) {
                map3.put("UPDATESHU", 0);
                map3.put("UPDATEJIAN", 0);
                map3.put("UPDATEYI", 0);
                map3.put("UPDATESHI", 0);
            } else {
                map3.putAll(id1);
            }
            Map id2 = map2.get(map3.get("ID"));
            if (id2 == null) {
                map3.put("YHXQSHU", 0);
                map3.put("YHXQJIAN", 0);
                map3.put("YHXQYI", 0);
                map3.put("YHXQSHI", 0);
            } else {
                map3.putAll(id2);
            }
        }
        try {
            return WebApiResponse.success(list3);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("'");
        }
    }

    @RequestMapping("historyLm")
    public WebApiResponse historyLm(String userId, String startTime, String endTime, String deptId) {
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", userId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        List list = new ArrayList();
        String s = "";
        if (roletype == 1 || roletype == 2) {
            list.add(deptid);
            s += " AND ID= ?" + list.size();
        }
        if (!StringUtils.isEmpty(deptId)) {
            list.add(deptId);
            s += " AND ID= ?" + list.size();
        }
        String sql = " SELECT * " +
                "FROM (SELECT " +
                "        sum(decode(YHJB1, '树木隐患', 1, 0)) AS shu, " +
                "        sum(decode(YHJB1, '建筑隐患', 1, 0)) AS jian, " +
                "        sum(decode(YHJB1, '异物隐患', 1, 0)) AS yi, " +
                "        sum(decode(YHJB1, '施工隐患', 1, 0)) AS shi, " +
                "        YWORG_ID  FROM KH_YH_HISTORY WHERE 1 = 1 AND YHZT = 0 " +
                "      GROUP BY YWORG_ID) a RIGHT JOIN (SELECT t.ID, t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL  " + s +
                "                                       ORDER BY t.DEPTSORT) r  ON a.YWORG_ID = r.ID ";
        try {
            return WebApiResponse.success(this.service.execSql(sql));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }
}
