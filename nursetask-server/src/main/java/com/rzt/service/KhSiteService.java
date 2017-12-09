/**    
 * 文件名：KhCycleService           
 * 版本信息：    
 * 日期：2017/11/28 14:43:44    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.entity.KhSite;
import com.rzt.entity.KhTask;
import com.rzt.repository.KhSiteRepository;
import com.rzt.repository.KhTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public List listAllTaskNotDo(KhTask task, Pageable pageable, String userName) {
        // task = timeUtil(task);
        String result = " k.task_name as taskName,k.tdyw_org as yworg,y.yhms as des,y.yhjb as jb,k.create_time as createTime ";
        StringBuffer buffer = new StringBuffer();
        buffer.append(" where k.in_use = 0");// 0为未消缺的任务
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
        String sql = "select * from (select a.*,rownum rn from (select "+result+" from kh_site k left join kh_yh_history y on k.yh_id = y.id" + buffer.toString();
        return execSql(sql, params.toArray());
    }

    public void updateQxTask(String id) {
        String status="1";
        this.reposiotry.updateQxTask(id, status, new Date());
        this.reposiotry.updateDoingTask(id,new Date());
        this.reposiotry.updateYH(id,new Date());
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
}