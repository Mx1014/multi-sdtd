/**
 * 文件名：KhCycleService
 * 版本信息：
 * 日期：2017/11/28 14:43:44
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.CheckLiveTask;
import com.rzt.entity.KhSite;
import com.rzt.entity.KhTask;
import com.rzt.entity.KhYhHistory;
import com.rzt.entity.model.KhTaskModel;
import com.rzt.repository.KhSiteRepository;
import com.rzt.repository.KhTaskRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 类名称：KhCycleService
 * 类描述：InnoDB free: 536576 kB
 * 创建人：张虎成
 * 创建时间：2017/11/28 14:43:44
 * 修改人：张虎成
 * 修改时间：2017/11/28 14:43:44
 * 修改备注：
 */
@Service
public class KhSiteService extends CurdService<KhSite, KhSiteRepository> {

    @Autowired
    private KhTaskRepository taskRepository;
    @Autowired
    private KhYhHistoryService yhservice;
    @Autowired
    private CheckLiveTaskService checkService;

    public Page listAllTaskNotDo(KhTaskModel task, Pageable pageable, String userName) {
        // task = timeUtil(task);
        String result = " k.id as id,k.task_name as taskName,k.tdyw_org as yworg,y.yhms as ms,y.yhjb as jb,k.create_time as createTime,k.COUNT as COUNT ";
        StringBuffer buffer = new StringBuffer();
        buffer.append(" where k.status = ?");// 0为未派发的任务
        List params = new ArrayList<>();
        params.add(task.getStatus());
        if (task.getPlanStartTime() != null && !task.getPlanStartTime().equals("")) {
            buffer.append(" and k.create_time between to_date(?,'YYYY-MM-DD hh24:mi') and to_date(?,'YYYY-MM-DD hh24:mi') ");
            params.add(task.getPlanStartTime());
            params.add(task.getPlanEndTime());
        }
        if (task.getTaskName() != null && !task.getTaskName().equals("")) {  //线路名查询
            task.setTaskName("%" + task.getTaskName() + "%");
            buffer.append(" and k.task_name like ? ");
            params.add(task.getTaskName());
        }
        if (userName != null) {
            buffer.append(" and u.realname like ? ");
            params.add(userName);
        }
        buffer.append(" order by k.create_time desc ");
       String sql = "select " + result + " from kh_site k left join kh_yh_history y on k.yh_id = y.id" + buffer.toString();
       //String sql = "select * from listAllTaskNotDo "+buffer.toString();
        Page<Map<String, Object>> maps1 = this.execSqlPage(pageable, sql, params.toArray());
        List<Map<String, Object>> content1 = maps1.getContent();
        for (Map map : content1) {
            map.put("ID", map.get("ID") + "");
        }
        return maps1;
    }

    public void updateQxTask(long id) {
        this.reposiotry.updateQxTask(id, new Date());
        this.reposiotry.updateDoingTask(id, new Date());
        KhSite site = this.reposiotry.site(id);
        this.reposiotry.updateYH(site.getYhId(), new Date());
        //将带稽查 已完成稽查的看护任务状态修改
        this.reposiotry.updateCheckTask(id, new Date());
    }

    public List findAll() {
        String sql = "select * from kh_site";
        List<Map<String, Object>> maps = this.execSql(sql);
        List<KhSite> all = this.reposiotry.findAll();
        return all;
    }

    public void paifaTask(String id, KhSite site) {
        //将看护人信息保存到表中

        this.reposiotry.updateSite(Long.parseLong(id), site.getKhfzrId1(), site.getKhdyId1(), site.getKhfzrId2(), site.getKhdyId2());

    }

    public List listKhtaskByid(long id) {
        String sql = "select k.task_name,y.yhms as ms,y.yhjb as jb,a.name as khfzr1,b.name as khfzr2,c.name as khdy1,d.name as khdy2 from kh_site k left join " +
                " (select u.realname as name,k1.id from kh_site k1 left join rztsysuser u on u.id =k1.khfzr_id1) a " +
                " on a.id=k.id left join " +
                " (select u.realname as name,k1.id from kh_site k1 left join rztsysuser u on u.id =k1.khfzr_id2) b " +
                " on b.id=k.id left join  " +
                " (select u.realname as name,k1.id from kh_site k1 left join rztsysuser u on u.id =k1.khdy_id1) c " +
                " on c.id=k.id left join " +
                " (select u.realname as name,k1.id from kh_site k1 left join rztsysuser u on u.id =k1.khdy_id2) d " +
                " on d.id=k.id " +
                " left join kh_yh_history y on k.yh_id = y.id  where k.id=? ";
        return this.execSql(sql, id);
    }

    @Transactional
    public WebApiResponse saveYh(KhYhHistory yh, String fxtime) {
        try {
            yh.setYhfxsj(DateUtil.parseDate(fxtime));
            yh.setSfdj("未定级");
            yh.setYhzt("0");//隐患未消除
            yh.setId(0L);
            yh.setCreateTime(DateUtil.dateNow());
            yh.setSection(yh.getStartTower() + "-" + yh.getEndTower() + " 区段");
            yhservice.add(yh);
            KhSite task = new KhSite();
            String taskName = yh.getVtype() + yh.getLineName() + yh.getStartTower() + "-" + yh.getEndTower() + "号杆塔看护任务";
            task.setVtype(yh.getVtype());
            task.setLineName(yh.getLineName());
            task.setTdywOrg(yh.getTdywOrg());
            task.setTaskName(taskName);
            task.setStatus(0);// 未派发
            task.setCount(0);//生成任务次数0
            task.setYhId(yh.getId());
            task.setCreateTime(DateUtil.dateNow());
            task.setId(0L);
            this.add(task);
            CheckLiveTask check = new CheckLiveTask();
            check.setCheckType("0"); //0为 看护类型稽查
            check.setTaskId(task.getId());
            check.setTaskType("1");// 1 为正常稽查
            check.setStatus("0");  // 0 为未派发
            check.setTdwhOrg(yh.getTdywOrg());
            check.setCreateTime(DateUtil.dateNow());
            check.setCheckDept("0"); // 0为属地公司
            check.setYhId(yh.getId());
            check.setCheckCycle("1");// 1 为周期1天
            check.setId(0L);
            check.setDzwl("1");
            check.setTaskName(yh.getVtype() + yh.getLineName() + yh.getStartTower() + "-" + yh.getEndTower() + "号杆塔稽查任务");
            checkService.add(check);
            CheckLiveTask check1 = new CheckLiveTask();
            check1.setCheckType("0"); //0为 看护类型稽查
            check1.setTaskId(task.getId());
            check1.setTaskType("1");// 1 为正常稽查
            check1.setStatus("0");  // 0 为未派发
            check1.setTdwhOrg(yh.getTdywOrg());
            check1.setCreateTime(DateUtil.dateNow());
            check1.setCheckDept("1"); // 1为北京公司
            check1.setCheckCycle("3"); // 周期为3天
            check1.setTaskName(check.getTaskName());
            check1.setYhId(yh.getId());
            check1.setDzwl("1");
            check1.setId(0L);
            checkService.add(check1);
            return WebApiResponse.success("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    public WebApiResponse deleteById(String id) {
        try {
            String[] split = id.split(",");
            if (split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    this.reposiotry.deleteById(Long.parseLong(split[i].toString()));
                }
            } else {
                this.reposiotry.deleteById(Long.parseLong(id));
            }
            return WebApiResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("删除失败");
        }
    }

    public KhSite findSite(String id) {
        return this.reposiotry.findSite(Long.parseLong(id));
    }
}