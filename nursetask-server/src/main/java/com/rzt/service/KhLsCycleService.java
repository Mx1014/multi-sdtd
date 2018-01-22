package com.rzt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netflix.discovery.converters.Auto;
import com.rzt.entity.*;
import com.rzt.eureka.MonitorService;
import com.rzt.repository.KhLsCycleRepository;
import com.rzt.repository.KhTaskRepository;
import com.rzt.repository.KhYhHistoryRepository;
import com.rzt.repository.XsSbYhRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2018/1/20.
 */

@Service
public class KhLsCycleService extends CurdService<KhLsCycle, KhLsCycleRepository> {

    @Autowired
    private KhTaskRepository taskRepository;
    @Autowired
    private XsSbYhRepository yhRepository;
    @Autowired
    private MonitorService monitorService;

    @Transactional
    public WebApiResponse saveLsCycle(String yhId) {
        try {
            XsSbYh yh = yhRepository.findYh(Long.parseLong(yhId));
            String kv = yh.getVtype();//s
            if (kv.contains("kV")) {
                kv = kv.substring(0, kv.indexOf("k"));
            }
            String taskName = kv + "-" + yh.getLineName() + yh.getSection() + "临时看护任务";
            KhLsCycle cycle = new KhLsCycle();
            cycle.setCreateTime(new Date());
            cycle.setVtype(yh.getVtype());
            cycle.setLineName(yh.getLineName());
            cycle.setYhId(yh.getId());
            cycle.setTdywOrg(yh.getTdywOrg());
            cycle.setTdywOrgId(yh.getTdorgId());
//            cycle.setWxOrg(yh.getTdwxOrg());
//            cycle.setWxOrgId(yh.getWxorgId());
            cycle.setId();
            cycle.setLineId(yh.getLineId());
            cycle.setStatus(0);
            cycle.setSection(yh.getSection());
            cycle.setTaskName(taskName);
            this.reposiotry.save(cycle);
            monitorService.start("wtsh", yh.getTbrid(), yhId, "1", "", "");
            return WebApiResponse.success(cycle.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("保存失败" + e.getMessage());
        }
    }


    public WebApiResponse listLsNotDo(String taskName, String yworg, String startTime, String endTime, JSONObject josn, Pageable pageable, String yhjb) {
        try {
            List params = new ArrayList<>();
            StringBuffer buffer = new StringBuffer();
            Map jsonObject = JSON.parseObject(josn.toString(), Map.class);
            Integer roleType = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
            Object tdId = jsonObject.get("DEPTID");
//            Object classid = jsonObject.get("CLASSID");
            Object companyid = jsonObject.get("COMPANYID");
            String result = " k.id as id,k.task_name as taskName,k.tdyw_org as yworg,y.yhms as ms,y.yhjb as jb,k.create_time as createTime ";


            // 搜索框
            if (startTime != null && !startTime.equals("")) {
                buffer.append(" and k.create_time between to_date(?,'YYYY-MM-DD hh24:mi') and to_date(?,'YYYY-MM-DD hh24:mi') ");
                params.add(startTime);
                params.add(endTime);
            }
            if (taskName != null && !taskName.equals("")) {  //线路名查询
                buffer.append(" and k.task_name like ? ");
                params.add("%" + taskName + "%");
            }
            if (yhjb != null && !yhjb.equals("")) {
                buffer.append(" and y.yhjb like ? ");
                params.add(("%" + yhjb + "%"));
            }
            if (yworg != null && !yworg.equals("")) {
                buffer.append(" and k.tdyw_org like ? ");
                params.add(("%" + yworg + "%"));
            }

            //权限
            if (roleType != null) {
                if (roleType == 1 || roleType == 2) {
                    buffer.append(" and k.TDYW_ORGID = " + tdId);
                }
                if (roleType == 3) {
                    buffer.append(" and k.WX_ORGID = " + companyid);
                }
                if (roleType == 0) {

                } else {
                    buffer.append(" and y.id=0 ");
                }
            }


            String sql = "select " + result + " from kh_ls_cycle k left join xs_sb_yh y on k.yh_id=y.id where status=0 " + buffer.toString();
            Page<Map<String, Object>> maps = this.execSqlPage(pageable, sql, params.toArray());
            List<Map<String, Object>> content1 = maps.getContent();
            for (Map map : content1) {
                map.put("ID", map.get("ID") + "");
            }
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("获取失败" + e.getMessage());
        }
    }

    @Transactional
    public WebApiResponse paifaLsTask(String id, String tasks) {
        try {
            List<Map<Object, String>> list = (List<Map<Object, String>>) JSONObject.parse(tasks);
            KhLsCycle cycle = this.reposiotry.findCycle(Long.parseLong(id));
            for (Map map : list) {
                String userId = map.get("userId").toString();
                String startTime = map.get("planStartTime").toString();
                String endTime = map.get("planEndTime").toString();
                KhTask task = new KhTask();
                try {
                    String sql = "select c.ID ID,c.COMPANYNAME NAME from RZTSYSCOMPANY C LEFT JOIN RZTSYSUSER U ON C.ID=U.COMPANYID WHERE U.ID=?";
                    Map<String, Object> map1 = this.execSqlSingleResult(sql, userId);
                    String wxname = map1.get("NAME").toString();
                    String wxid = map1.get("ID").toString();
                    task.setWxOrg(wxname);
                    this.reposiotry.updateCycle(wxname, wxid, cycle.getId());
                } catch (Exception e) {

                }
                task.setTaskType(1);
                task.setId();
                task.setTdywOrg(cycle.getTdywOrg());
                task.setPlanStartTime(DateUtil.getPlanStartTime(startTime));
                task.setPlanEndTime(DateUtil.getPlanStartTime(endTime));
                task.setZxysNum(0);
                task.setCount(1);
                task.setStatus(0);
                task.setCreateTime(new Date());
                task.setYhId(cycle.getYhId());
                task.setUserId(userId);
                task.setSiteId(cycle.getId());
                task.setTaskName(cycle.getTaskName());
                taskRepository.save(task);
            }
            return WebApiResponse.success("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("获取失败" + e.getMessage());
        }
    }

    public WebApiResponse deleteCycle(String id) {
        try {
            String[] split = id.split(",");
            if (split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    this.reposiotry.deleteCycle(Long.parseLong(split[i].toString()));
                }
            }
            return WebApiResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("删除失败");
        }
    }
}