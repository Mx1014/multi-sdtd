/**
 * 文件名：KhTaskService
 * 版本信息：
 * 日期：2017/11/28 14:43:44
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.entity.KhSite;
import com.rzt.entity.model.KhTaskModel;
import com.rzt.repository.KhSiteRepository;
import com.rzt.repository.KhTaskRepository;
import com.rzt.entity.KhTask;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rzt.service.CurdService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class KhTaskService extends CurdService<KhTask, KhTaskRepository> {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private KhSiteRepository siteRepository;

    public Object listAllKhTask(KhTaskModel task, Pageable pageable, int roleType) {
        task = timeUtil(task);
        String result = "k.id as id, k.task_name as taskName,k.tdyw_org as yworg,k.CREATE_TIME as createTime,k.plan_start_time as startTime,k.plan_end_time as endTime,k.status as status,u.realname as userName,d.DEPTNAME as class";
        List params = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" where k.create_time between to_date(?,'YYYY-MM-DD hh24:mi') and to_date(?,'YYYY-MM-DD hh24:mi') ");
        params.add(task.getPlanStartTime());
        params.add(task.getPlanEndTime());
        /*if (task.getPlanStartTime()!=null){

        }*/
        if (task.getTaskName() != null && !task.getTaskName().equals("")) {
            task.setTaskName("%" + task.getTaskName() + "%");
            buffer.append(" and k.task_name like ? ");
            params.add(task.getTaskName());
        }
        //此处的状态要改
        if (task.getStatus() != null && !task.getStatus().equals("")) {
            task.setStatus("%" + task.getStatus() + "%");
            buffer.append(" and k.status like ? ");
            params.add(task.getStatus());
        }
        if (task.getUserName() != null && !task.getUserName().equals("")) {
            task.setUserName("%" + task.getUserName() + "%");
            buffer.append(" and u.realname like ?");
            params.add(task.getUserName());
        }
        String sql = "";

        if (roleType == 1 || roleType == 2) {
            sql = "select " + result + " from kh_task k  left join rztsysuser u on u.id = k.user_id left join RZTSYSDEPARTMENT d on u.classname = d.id " + buffer.toString() + " and k.tdyw_org = (select d.deptname from rztsysuser u, RZTSYSDEPARTMENT d where d.id = u.deptid and u.id = ?)";
            params.add(task.getUserId());
        } else if (roleType == 3) {
            sql = "select " + result + " from kh_task k  left join rztsysuser u on u.id = k.user_id left join RZTSYSDEPARTMENT d on u.classname = d.id " + buffer.toString() + " and k.wx_org = (select d.COMPANYNAME from rztsysuser u,RZTSYSCOMPANY d where u.companyid = d.id and u.id = ?)";
            params.add(task.getUserId());
        } else if (roleType == 4) {
            sql = "select " + result + " from kh_task k  left join rztsysuser u on u.id = k.user_id left join RZTSYSDEPARTMENT d on u.classname = d.id " + buffer.toString() + " and u.classname = (select u.classname from rztsysuser u, RZTSYSDEPARTMENT d where d.id = u.deptid and u.id = ?)";
            params.add(task.getUserId());
        } else if (roleType == 5) {
            sql = "select " + result + " from kh_task k  left join rztsysuser u on u.id = k.user_id left join RZTSYSDEPARTMENT d on u.classname = d.id " + buffer.toString() + " and k.user_id = ?";
            params.add(task.getUserId());
        } else {
            sql = "select " + result + " from kh_task k  left join rztsysuser u on u.id = k.user_id left join RZTSYSDEPARTMENT d on u.classname = d.id " + buffer.toString();
        }
        buffer.append(" order by k.create_time desc ");
        sql = sql +" order by k.create_time desc";
        //String sql = "select * from listAllKhTask "+buffer.toString();
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql, params.toArray());
        List<Map<String, Object>> content1 = maps.getContent();
        for (Map map : content1) {
            map.put("ID", map.get("ID") + "");
        }
        return maps;
    }

    public KhTaskModel timeUtil(KhTaskModel task) {
        if (task.getPlanStartTime() == null || task.getPlanStartTime().equals("")) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_MONTH, -7);
            Date m = c.getTime();
            String mon = df.format(m);
            task.setPlanStartTime(mon + " 00:00");
            task.setPlanEndTime(df.format(new Date()) + " 23:59");
        }
        return task;
    }

    public List<Map<String, Object>> getKhTaskById(long id) {
        String result = " k.task_name as taskName,y.yhms as yhms,y.yhjb as yhjb,u.realname as userName,u.phone as phone ";
        String sql = "select " + result + " from kh_task k left join kh_yh_history y on k.yh_id=y.id left join rztsysuser u on u.id=k.user_id  where k.id=?";
        return this.execSql(sql, id);
    }

    public int getCount(long id, String userId) {
        return this.reposiotry.getCount(id, userId);
    }

    /*public void updateDDTime(Date time, long id) {
        this.reposiotry.updateDDTime(time,id);
    }

    public void updateSFQRTime(Date time, long id) {
        this.reposiotry.updateSFQRTime(time,id);
    }*/

    public void updateWPQRTime(Date time, long id) {
        this.reposiotry.updateWPQRTime(time, id);
    }

    public void updateRealStartTime(Date time, long id) {
        this.reposiotry.updateRealStartTime(time, id);
    }

    public void updateTaskById(String userId, String id) {
        //KhSite one = siteRepository.findSite(Long.parseLong(id));
        /*if (site.getKhfzrId1()== null){
            site.setKhfzrId1(one.getKhfzrId1());
        }
        if (site.getKhfzrId2()== null){
            site.setKhfzrId2(one.getKhfzrId2());
        }
        if (site.getKhdyId1()== null){
            site.setKhdyId1(one.getKhdyId1());
        }
        if (site.getKhdyId2()== null){
            site.setKhdyId2(one.getKhdyId2());
        }
        this.reposiotry.updateTaskById(Long.parseLong(id),site.getKhfzrId1(),site.getKhfzrId2(),site.getKhdyId1(),site.getKhdyId2());*/
        this.reposiotry.updateTaskById(userId, id);
    }


    public WebApiResponse listCurrentTaskByUserId(String userId) {
        try {
            String date = DateUtil.getCurrentDate();
            String sql = "select * from kh_task k where k.user_id = ? and k.plan_start_time>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and k.plan_start_time<=to_date(?,'yyyy-mm-dd hh24:mi:ss')";
            String start = date + " 00:00:00";
            String end = date + " 23:59:59";
            return WebApiResponse.success(this.execSql(sql, userId, start, end));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    //电压等级  任务执行人 线路名称  杆塔号 开始时间  通道单位 外协单位  区段
    public WebApiResponse listTaskInfoById(String yhId) {
        try {
            String sql = "select l.v_level as voltage,l.line_name as linename,s.section as section,s.tdyw_org as yworg,y.TDWX_ORG as wxorg,U.REALNAME as name,T.PLAN_START_TIME as starttime,T.PLAN_END_TIME as endtime \n" +
                    "from KH_SITE S,KH_YH_HISTORY y,KH_TASK T,RZTSYSUSER U,cm_line l \n" +
                    "where s.YH_ID = ? and l.id = y.line_id and t.PLAN_END_TIME>=sysdate and y.id = s.YH_ID AND S.ID = T.SITE_ID AND T.USER_ID = U.ID order by t.plan_start_time";
            return WebApiResponse.success(this.execSql(sql, yhId));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public void CreateTask() {
        siteRepository.findSites();
    }
}

