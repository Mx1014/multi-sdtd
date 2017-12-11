/**
 * 文件名：CHECKLIVETASKDETAILService
 * 版本信息：
 * 日期：2017/12/05 10:24:09
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;
import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.repository.CheckLiveTaskDetailRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名称：CHECKLIVETASKDETAILService
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/05 10:24:09
 * 修改人：张虎成
 * 修改时间：2017/12/05 10:24:09
 * 修改备注：
 * @version
 */
@Service
@Transactional
public class CheckLiveTaskDetailService extends CurdService<CheckLiveTaskDetail,CheckLiveTaskDetailRepository> {


    public List listCheckDoingById(String id) {
        String result = " c.task_name as taskName,u.user_name as userName,u.phone_num as phoneNum,c.check_type as checkType ";
//        List params = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        String sql = "select "+result+" from check_live_task as c left join user u as u.id = c.user_id where c.id=?";
        return this.execSql(sql,id);
    }
    public List listAllCheckDoing(CheckLiveTaskDetail task, Pageable pageable) {
        String result = " c.task_name as taskName,c.check_type as type,u.user_name as useName,c.plan_start_time as startTime,c.plan_end_time as endTime,c.create_time as createTime,c.status";
        StringBuffer buffer = new StringBuffer();
        buffer.append(" where c.status = 0");
        List params = new ArrayList<>();
        if (task.getPlanStartTime()!=null){
            buffer.append(" and c.create_time between to_date(?,'YYYY-MM-DD') and to_date(?,'YYYY-MM-DD') ");
            params.add(task.getPlanStartTime());
            params.add(task.getPlanEndTime());
        }
        if (task.getTaskName() != null){  //线路名查询
            task.setTaskName("%"+task.getTaskName()+"%");
            buffer.append(" and c.task_name like ? ");
            params.add(task.getTaskName());
        }
       /* if (task.getStatus() != null){
       少根据稽查人筛选的条件
            buffer.append(" and c.status = ? ");
            params.add(task.getStatus());
        }*/
        params.add(pageable.getPageNumber()*pageable.getPageSize());
        params.add((pageable.getPageNumber()+1)*pageable.getPageSize());
        buffer.append(" order by k.create_time desc ) a) b ");
        buffer.append(" where b.rn>? and b.rn <=?");
        String sql = "select * from (select a.*,rownum rn from (select "+result+" from check_live_task_detail c left join user u on c.user_id = u.id " + buffer.toString();
        return this.execSql(sql,params.toArray());
    }
}