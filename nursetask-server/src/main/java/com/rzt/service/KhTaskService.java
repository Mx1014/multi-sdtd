/**    
 * 文件名：KhTaskService           
 * 版本信息：    
 * 日期：2017/11/28 14:43:44    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.entity.KhSite;
import com.rzt.repository.KhSiteRepository;
import com.rzt.repository.KhTaskRepository;
import com.rzt.entity.KhTask;
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
@Transactional
public class KhTaskService extends CurdService<KhTask,KhTaskRepository> {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private KhSiteRepository siteRepository;
    
    public Object listAllKhTask(KhTask task, Pageable pageable) {
        task = timeUtil(task);
        String result = "k.id as id, k.task_name as taskName,k.tdyw_org as yworg,k.CREATE_TIME as createTime,k.plan_start_time as startTime,k.plan_end_time as endTime,k.status as status,u.user_name as userName,u.class as class";
        List params = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" where k.create_time between to_date(?,'YYYY-MM-DD hh24:mi') and to_date(?,'YYYY-MM-DD hh24:mi') ");
        params.add(task.getPlanStartTime());
        params.add(task.getPlanEndTime());
        /*if (task.getPlanStartTime()!=null){

        }*/
        if (task.getTaskName() != null && !task.getTaskName().equals("")){
            task.setTaskName("%"+task.getTaskName()+"%");
            buffer.append(" and k.task_name like ? ");
            params.add(task.getTaskName());
        }
 //此处的状态要改
         if (task.getStatus() != null && !task.getStatus().equals("")){
            task.setStatus("%"+task.getStatus()+"%");
            buffer.append(" and k.status like ? ");
            params.add(task.getStatus());
         }
         if (task.getUserId() != null && !task.getUserId().equals("")){
             task.setUserId("%"+task.getUserId()+"%");
             buffer.append(" and u.user_name like ?");
             params.add(task.getUserId());
         }
         //此处加分页 人员表换成真实表
         buffer.append(" order by k.create_time desc ) a) b ");
         buffer.append(" where b.rn>? and b.rn <=?");
        String sql = "select * from (select a.*,rownum rn from (select "+result+" from kh_task k " +
               " left join cm_user u on u.id = k.user_id "+ buffer.toString();
        params.add(pageable.getPageNumber()*pageable.getPageSize());
        params.add((pageable.getPageNumber()+1)*pageable.getPageSize());
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql, params.toArray());
        long count = this.reposiotry.count();
        //int count = this.reposiotry.getcount();
       /* Map<String, Object> map = new HashMap<>();
        map.put("COUNT",count);
        maps.add(map);*/
        return maps;
    }
    public List listAllTaskDoing(KhTask task, Pageable pageable,String userName) {
        task = timeUtil(task);
        String result = "k.task_name as taskName,k.status as status,k.create_time as createTime,k.plan_start_time as startTime,k.plan_end_time as end_time";
       //,u.user_name as userName
        StringBuffer buffer = new StringBuffer();
        buffer.append(" where k.status like '%已安排%'");
        List params = new ArrayList<>();
        if (task.getPlanStartTime()!=null){
            buffer.append(" and k.create_time between to_date(?,'YYYY-MM-DD') and to_date(?,'YYYY-MM-DD') ");
            params.add(task.getPlanStartTime());
            params.add(task.getPlanEndTime());
        }
        if (task.getTaskName() != null){  //线路名查询
            task.setTaskName("%"+task.getTaskName()+"%");
            buffer.append(" and k.task_name like ? ");
            params.add(task.getTaskName());
        }
        if (userName != null){
            buffer.append(" and u.user_name like ? ");
            params.add(userName);
        }
        params.add(pageable.getPageNumber()*pageable.getPageSize());
        params.add((pageable.getPageNumber()+1)*pageable.getPageSize());
        buffer.append(" order by k.create_time desc ) a) b ");
        buffer.append(" where b.rn>? and b.rn <=?");
        String sql = "select * from (select a.*,rownum rn from (select "+result+" from kh_task k " + buffer.toString();

        return execSql(sql, params.toArray());
    }

    public KhTask timeUtil(KhTask task){
        if (task.getPlanStartTime() == null||task.getPlanStartTime().equals("")){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_MONTH, -7);
            Date m = c.getTime();
            String mon = df.format(m);
            task.setPlanStartTime(mon+" 00:00");
            task.setPlanEndTime(df.format(new Date())+" 23:59");
        }
        return task;
    }


//    public static void main(String[] args) {
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        Calendar c = Calendar.getInstance();
//        c.setTime(new Date());
//        c.add(Calendar.DAY_OF_MONTH, -7);
//        Date m = c.getTime();
//        String mon = df.format(m);
//        System.out.println(mon);
//    }

    public List<Map<String,Object>> getKhTaskById(String id) {
        String result=" k.task_name as taskName,y.yhms as yhms,y.yhjb as yhjb,u.user_name as userName,u.phone_num as phone ";
        String sql = "select "+result+" from kh_task k left join kh_yh_history y on k.yh_id=y.id left join cm_user u on u.id=k.user_id  where k.id=?";
        return   this.execSql(sql,id);
    }

    public int getCount(String id, String userId) {
         return this.reposiotry.getCount(id,userId);
    }

    public void updateDDTime(Date time, String id) {
        this.reposiotry.updateDDTime(time,id);
    }

    public void updateSFQRTime(Date time, String id) {
        this.reposiotry.updateSFQRTime(time,id);
    }

    public void updateWPQRTime(Date time, String id) {
        this.reposiotry.updateWPQRTime(time,id);
    }

    public void updateRealStartTime(Date time, String id) {
        this.reposiotry.updateRealStartTime(time,id);
    }

    public void updateTaskById(KhSite site, String id) {
        KhSite one = siteRepository.findOne(id);
        if (site.getKhfzrId1()==null){
            site.setKhfzrId1(one.getKhfzrId1());
        }
        if (site.getKhfzrId2()==null){
            site.setKhfzrId2(one.getKhfzrId2());
        }
        if (site.getKhdyId1()==null){
            site.setKhdyId1(one.getKhdyId1());
        }
        if (site.getKhdyId2()==null){
            site.setKhdyId2(one.getKhdyId2());
        }
        this.reposiotry.updateTaskById(id,site.getKhfzrId1(),site.getKhfzrId2(),site.getKhdyId1(),site.getKhdyId2());

    }

    public Page<Map<String,Object>> appKhTask(int dbyb, Pageable pageable, String userId) {
        String result = " id,plan_start_time as startTime,plan_end_time as endTime,task_name as taskName,status ";
        StringBuffer buffer = new StringBuffer();
        if (dbyb == 1) {
            buffer.append("where (status like '未开始' or status like '进行中')");
        } else if (dbyb == 2) {
            buffer.append(" where status like '已完成'");
        }
        String sql = "select " + result + "from kh_task " + buffer.toString() + " and user_id = ?";
        return this.execSqlPage(pageable, sql, userId);
        //WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_start_time) = trunc(sysdate) and cm_user_id = ?1
    }
}

