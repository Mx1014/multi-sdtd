/**    
 * 文件名：CHECKLIVETASKService           
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.entity.CheckLiveTask;
import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.repository.CheckLiveTaskDetailRepository;
import com.rzt.repository.CheckLiveTaskRepository;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**      
 * 类名称：CHECKLIVETASKService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/04 15:13:15 
 * 修改人：张虎成    
 * 修改时间：2017/12/04 15:13:15    
 * 修改备注：    
 * @version        
 */
@Service
public class CheckLiveTaskService extends CurdService<CheckLiveTask, CheckLiveTaskRepository> {

    @Autowired
    private CheckLiveTaskDetailRepository checkLiveTaskDetailRepository;

    //巡视稽查列表查询展示
    public Page<Map<String,Object>> listKhCheckPage(Pageable pageable, String lineId, String tddwId) {

        String sql = "select s.task_id,s.TASK_NAME,h.yhms,h.yhjb,h.XLZYCD,d.DEPTNAME from CHECK_LIVE_site s " +
                " left JOIN KH_YH_HISTORY h on s.YH_ID=h.id " +
                " LEFT JOIN  RZTSYSDEPARTMENT d on d.ID = s.TDYW_ORGID where 1=1 ";

        List params = new ArrayList<>();
        //线路查询
        if (!StringUtils.isEmpty(lineId)) {
            params.add(lineId);
            sql += " AND s.LINE_ID =?";
        }
        //通道单位查询
        if (!StringUtils.isEmpty(tddwId)) {
            params.add(tddwId);
            sql += " AND s.TDYW_ORGID =?";
        }
        return execSqlPage(pageable, sql, params.toArray());
    }

    public Map<String, Object> xsTaskDetail(String taskId) throws Exception {
        String sql = "select s.TASK_NAME,h.yhms,h.yhjb,h.XLZYCD,d.DEPTNAME from CHECK_LIVE_site s " +
                "  left JOIN KH_YH_HISTORY h on s.YH_ID=h.id " +
                "  LEFT JOIN  RZTSYSDEPARTMENT d on d.ID = s.TDYW_ORGID where s.id =?1 ";
        Map<String, Object> map = execSqlSingleResult(sql, taskId);
        return map;
    }

    @Transactional
    public void paifaXsCheckTask(CheckLiveTask task ,String planStartTime,String planEndTime, String username) throws Exception {

        task.setId();
        task.setCreateTime(new Date());
        task.setStatus(1);//0未派发  1已派发  2已消缺
        task.setCheckType(0); //0 看护  1巡视
        //task.setTaskType(0);//（0 正常 1保电 2 特殊）
        task.setCheckCycle(1);
        task.setTaskName(username+DateUtil.getCurrentDate()+"稽查任务");
        CheckLiveTask save = reposiotry.save(task);

        String[] split = save.getTaskId().split(",");
        for (int i = 0; i < split.length; i++) {
            Map<String,Object> map = execSqlSingleResult("select id,TDYW_ORGID,TDWX_ORGID from CHECK_LIVE_SITE where id = to_number(?1)", split[i]);
            CheckLiveTaskDetail taskDetail = new CheckLiveTaskDetail();
            taskDetail.setId();
            taskDetail.setTdywOrgid(String.valueOf(map.get("TDYW_ORGID")).replace("null",""));
            taskDetail.setTdwxOrgid(String.valueOf(map.get("TDWX_ORGID")).replace("null",""));
            taskDetail.setPlanStartTime(DateUtil.parseDate(planStartTime));
            taskDetail.setPlanEndTime(DateUtil.parseDate(planEndTime));
            taskDetail.setStatus(0);// 0未开始 1进行中 2已完成 3已超期
            taskDetail.setKhTaskType(save.getTaskType());//（（0 正常 1保电 2 特殊）
            taskDetail.setKhTaskId(Long.valueOf(split[i]));
            taskDetail.setTaskId(save.getId());
            checkLiveTaskDetailRepository.save(taskDetail);
        }

    }

/*    @Scheduled(cron = "0/5 * *  * * ? ")
    @Transactional
    public void kh(){

insert into CHECK_LIVE_SITE (id,TASK_ID,TASK_TYPE,CREATE_TIME,TASK_NAME,STATUS,line_id,TDYW_ORGID,TDWX_ORGID,yh_id)
select id,id as taskid,0,sysdate,TASK_NAME,0,LINE_ID,WX_ORGID,TDYW_ORGID,YH_ID from KH_CYCLE;
    }*/



}
