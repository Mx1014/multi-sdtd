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
public class KhTaskService extends CurdService<KhTask,KhTaskRepository> {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private KhSiteRepository siteRepository;
    
    public Object listAllKhTask(KhTaskModel task, Pageable pageable) {
        task = timeUtil(task);
        String result = "k.id as id, k.task_name as taskName,k.tdyw_org as yworg,k.CREATE_TIME as createTime,k.plan_start_time as startTime,k.plan_end_time as endTime,k.status as status,u.realname as userName,u.classname as class";
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
             buffer.append(" and u.realname like ?");
             params.add(task.getUserId());
         }
         //此处加分页 人员表换成真实表
         buffer.append(" order by k.create_time desc ");
        String sql = "select "+result+" from kh_task k " +
               " left join rztsysuser u on u.id = k.user_id "+ buffer.toString();
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql, params.toArray());
        List<Map<String, Object>> content1 = maps.getContent();
        for (Map map:content1) {
            map.put("ID",map.get("ID")+"");
        }
        return maps;
    }

    public KhTaskModel timeUtil(KhTaskModel task){
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

    public List<Map<String,Object>> getKhTaskById(long id) {
        String result=" k.task_name as taskName,y.yhms as yhms,y.yhjb as yhjb,u.realname as userName,u.phone as phone ";
        String sql = "select "+result+" from kh_task k left join kh_yh_history y on k.yh_id=y.id left join rztsysuser u on u.id=k.user_id  where k.id=?";
        return   this.execSql(sql,id);
    }

    public int getCount(long id, String userId) {
         return this.reposiotry.getCount(id,userId);
    }

    /*public void updateDDTime(Date time, long id) {
        this.reposiotry.updateDDTime(time,id);
    }

    public void updateSFQRTime(Date time, long id) {
        this.reposiotry.updateSFQRTime(time,id);
    }*/

    public void updateWPQRTime(Date time, long id) {
        this.reposiotry.updateWPQRTime(time,id);
    }

    public void updateRealStartTime(Date time, long id) {
        this.reposiotry.updateRealStartTime(time,id);
    }

    public void updateTaskById(KhSite site, String id) {
        KhSite one = siteRepository.find(Long.parseLong(id));
        if (site.getKhfzrId1()== null){
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
        this.reposiotry.updateTaskById(Long.parseLong(id),site.getKhfzrId1(),site.getKhfzrId2(),site.getKhdyId1(),site.getKhdyId2());

    }


}

