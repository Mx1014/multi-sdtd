/**    
 * 文件名：KhCycleService           
 * 版本信息：    
 * 日期：2017/11/28 14:43:44    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.KhSite;
import com.rzt.entity.KhTask;
import com.rzt.repository.KhSiteRepository;
import com.rzt.repository.KhTaskRepository;
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
 * @version        
 */
@Service
@Transactional
public class KhSiteService extends CurdService<KhSite, KhSiteRepository> {

    @Autowired
    private KhTaskRepository taskRepository;
    public Page listAllTaskNotDo(KhTask task, Pageable pageable, String userName) {
        // task = timeUtil(task);
        String result = " k.id as id,k.task_name as taskName,k.tdyw_org as yworg,y.yhms as ms,y.yhjb as jb,k.create_time as createTime ";
        StringBuffer buffer = new StringBuffer();
        buffer.append(" where k.status = ?");// 0为未派发的任务
        List params = new ArrayList<>();
        params.add(task.getStatus());
        if (task.getPlanStartTime()!=null && !task.getPlanStartTime().equals("")){
            buffer.append(" and k.create_time between to_date(?,'YYYY-MM-DD hh24:mi') and to_date(?,'YYYY-MM-DD hh24:mi') ");
            params.add(task.getPlanStartTime());
            params.add(task.getPlanEndTime());
        }
        if (task.getTaskName()!=null && !task.getTaskName().equals("")){  //线路名查询
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
        String sql = "select * from (select a.*,rownum rn from (select "+result+" from kh_site k left join kh_yh_history y on k.yh_id = y.id" + buffer.toString();
        //List<Map<String, Object>> maps = execSql(sql, params.toArray());
        int count = this.reposiotry.getCount(task.getStatus());
        JSONObject jsonObject = new JSONObject();
        Page<Map<String, Object>> maps1 = this.execSqlPage(pageable, sql, params.toArray());
        /*if(count > 0){
            Map<String, Object> map = new HashMap<>();
            map.put("COUNT",count);
            maps.add(map);
        }*/
        /*jsonObject.put("data",maps);
        jsonObject.put("count",count);*/
       return maps1;
    }

    public void updateQxTask(String id) {
        this.reposiotry.updateQxTask(id,new Date());
        this.reposiotry.updateDoingTask(id,new Date());
        KhSite site = this.findOne(id);
        this.reposiotry.updateYH(site.getYhId(),new Date());
        //将带稽查 已完成稽查的看护任务状态修改
        this.reposiotry.updateCheckTask(id,new Date());
    }
    public List findAll(){
        String sql = "select * from kh_site";
        List<Map<String, Object>> maps = this.execSql(sql);
        List<KhSite> all = this.reposiotry.findAll();
        return all;
    }

    public void paifaTask(String id,KhSite site) {
        //将看护人信息保存到表中
        this.reposiotry.updateSite(id,site.getKhfzrId1(),site.getKhdyId1(),site.getKhfzrId2(),site.getKhdyId2());

    }

    public List listKhtaskByid(String id) {
        String sql ="select k.task_name,y.yhms as ms,y.yhjb as jb,a.name as khfzr1,b.name as khfzr2,c.name as khdy1,d.name as khdy2 from kh_site k left join " +
                " (select u.user_name as name,k1.id from kh_site k1 left join cm_user u on u.id =k1.khfzr_id1) a " +
                " on a.id=k.id left join " +
                " (select u.user_name as name,k1.id from kh_site k1 left join cm_user u on u.id =k1.khfzr_id2) b " +
                " on b.id=k.id left join  " +
                " (select u.user_name as name,k1.id from kh_site k1 left join cm_user u on u.id =k1.khdy_id1) c " +
                " on c.id=k.id left join " +
                " (select u.user_name as name,k1.id from kh_site k1 left join cm_user u on u.id =k1.khdy_id2) d " +
                " on d.id=k.id " +
                " left join kh_yh_history y on k.yh_id = y.id  where k.id=? " ;
        return this.execSql(sql,id);
    }
}