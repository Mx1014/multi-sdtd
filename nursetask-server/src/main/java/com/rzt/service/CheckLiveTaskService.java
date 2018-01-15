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
import com.rzt.repository.CheckLiveTaskXsRepository;
import com.rzt.repository.KhYhHistoryRepository;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：CHECKLIVETASKService    
 * 类描述：${table.comment}    
 * 创建人：李泽州
 * 创建时间：2018/01/03 15:13:15
 * 修改备注：
 * @version        
 */
@Service
public class CheckLiveTaskService extends CurdService<CheckLiveTask, CheckLiveTaskRepository> {

    @Autowired
    private CheckLiveTaskDetailRepository checkLiveTaskDetailRepository;

    @Autowired
    private CheckLiveTaskXsRepository checkLiveTaskXsRepository;

    @Autowired
    private KhYhHistoryRepository khYhHistoryRepository;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //看护稽查列表查询展示
    public Page<Map<String,Object>> listKhCheckPage(Pageable pageable, String lineId, String tddwId) {

        String sql = "select s.task_id,s.TASK_NAME,h.yhms,h.yhjb,h.XLZYCD,d.DEPTNAME,s.yh_id from CHECK_LIVE_site s " +
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

    //看护已派发稽查任务
    public Page<Map<String,Object>> listKhCheckTaskPage(Pageable pageable, String userId, String tddwId) {

        String sql = "select t.id,t.TASK_ID,t.CREATE_TIME,t.TASK_NAME,t.PLAN_START_TIME,t.PLAN_END_TIME,u.REALNAME,d.DEPTNAME,CASE t.TASK_TYPE WHEN 0 THEN '正常' WHEN 1 THEN '保电' WHEN 2 THEN '特殊' END aaa from CHECK_LIVE_TASK t " +
                "  LEFT JOIN  rztsysuser u on u.id=t.USER_ID " +
                "  LEFT JOIN  RZTSYSDEPARTMENT d on d.ID = u.DEPTID where 1=1 ";

        List params = new ArrayList<>();
        //稽查人查询
        if (!StringUtils.isEmpty(userId)) {
            params.add(userId);
            sql += " AND u.id =?";
        }
        //通道单位查询
        if (!StringUtils.isEmpty(tddwId)) {
            params.add(tddwId);
            sql += " AND d.ID =?";
        }
        return execSqlPage(pageable, sql, params.toArray());
    }

    public Map<String, Object> khTaskDetail(String taskId) throws Exception {
        String sql = "select s.TASK_NAME,h.yhms,h.yhjb,h.XLZYCD,d.DEPTNAME from CHECK_LIVE_site s " +
                "  left JOIN KH_YH_HISTORY h on s.YH_ID=h.id " +
                "  LEFT JOIN  RZTSYSDEPARTMENT d on d.ID = s.TDYW_ORGID where s.id =?1 ";
        Map<String, Object> map = execSqlSingleResult(sql, taskId);
        return map;
    }

    @Transactional
    public void paifaKhCheckTask(CheckLiveTask task , String username) throws Exception {

        task.setId();
        task.setCreateTime(new Date());
        task.setStatus(0);//任务派发状态  0未接单 1进行中 2已完成 3超期
        task.setCheckType(0); //0 看护  1巡视
        //task.setTaskType(0);//（0 正常 1保电 2 特殊）
        task.setCheckCycle(1);
        task.setTaskName(username+DateUtil.getCurrentDate()+"稽查任务");
        CheckLiveTask save = reposiotry.save(task);

        String[] split = save.getTaskId().split(",");
        for (int i = 0; i < split.length; i++) {
            Map<String,Object> map = execSqlSingleResult("select id,TDYW_ORGID,TDWX_ORGID from CHECK_LIVE_SITE where YH_ID = ?1", split[i]);
            CheckLiveTaskDetail taskDetail = new CheckLiveTaskDetail();
            taskDetail.setId(null);
            taskDetail.setCreateTime(new Date());
            taskDetail.setTdywOrgid(String.valueOf(map.get("TDYW_ORGID")).replace("null",""));
            taskDetail.setTdwxOrgid(String.valueOf(map.get("TDWX_ORGID")).replace("null",""));
            taskDetail.setPlanStartTime(save.getPlanStartTime());
            taskDetail.setPlanEndTime(save.getPlanEndTime());
            taskDetail.setStatus(0);// 0未开始 1进行中 2已完成 3已超期
            taskDetail.setKhTaskType(save.getTaskType());//（（0 正常 1保电 2 特殊）
            taskDetail.setKhTaskId(Long.valueOf(split[i]));
            taskDetail.setTaskId(save.getId());
            checkLiveTaskDetailRepository.save(taskDetail);
        }

    }

    public Page<Map<String,Object>> appCheckList(Pageable pageable, String userId,String taskType) {

        String sql = "";
        //0看护 1巡视 0待稽查 1已稽查
        if("0,0".equals(taskType)){
            sql = "select t.id,t.TASK_ID,t.TASK_NAME,u.REALNAME, " +
                    "  CASE t.TASK_TYPE WHEN 0 THEN '正常' WHEN 1 THEN '保电' WHEN 2 THEN '特殊' END task_type , " +
                    "  CASE t.STATUS WHEN 0 THEN '未接单' WHEN 1 THEN '进行中' WHEN 2 THEN '已完成' WHEN 3 THEN '已超期' END task_status " +
                    "from CHECK_LIVE_TASK t " +
                    "  LEFT JOIN  rztsysuser u on u.id=t.USER_ID " +
                    " where t.status !=3 and t.status !=2 ";
        }else if("0,1".equals(taskType)){
            sql = "select t.id,t.TASK_ID,t.TASK_NAME,u.REALNAME, " +
                    "  CASE t.TASK_TYPE WHEN 0 THEN '正常' WHEN 1 THEN '保电' WHEN 2 THEN '特殊' END task_type  " +
                    " from CHECK_LIVE_TASK t " +
                    "  LEFT JOIN  rztsysuser u on u.id=t.USER_ID " +
                    " where t.status =2 and trunc(t.PLAN_START_TIME) <= trunc(sysdate) and trunc(t.PLAN_END_TIME) >= trunc(sysdate) ";
        }else if("1,0".equals(taskType)){
            sql = "select t.id,t.TASK_ID,t.TASK_NAME,u.REALNAME, " +
                    "  CASE t.TASK_TYPE WHEN 0 THEN '正常' WHEN 1 THEN '保电' WHEN 2 THEN '特殊' END task_type , " +
                    "  CASE t.STATUS WHEN 0 THEN '未接单' WHEN 1 THEN '进行中' WHEN 2 THEN '已完成' WHEN 3 THEN '已超期' END task_status " +
                    " from CHECK_LIVE_TASKXS t " +
                    "  LEFT JOIN  rztsysuser u on u.id=t.USER_ID " +
                    " where t.status !=3 and t.status !=2 ";
        }else if("1,1".equals(taskType)){
            sql = "select t.id,t.TASK_ID,t.TASK_NAME,u.REALNAME, " +
                    "  CASE t.TASK_TYPE WHEN 0 THEN '正常' WHEN 1 THEN '保电' WHEN 2 THEN '特殊' END task_type  " +
                    " from CHECK_LIVE_TASKXS t " +
                    "  LEFT JOIN  rztsysuser u on u.id=t.USER_ID " +
                    " where t.status =2 ";
        }

        //status 0未接单 1进行中 2已完成 3超期

        List params = new ArrayList<>();
        //稽查人查询
        if (!StringUtils.isEmpty(userId)) {
            params.add(userId);
            sql += " AND u.id =?"+params.size();
        }
        //通道单位查询
/*        if (!StringUtils.isEmpty(tddwId)) {
            params.add(tddwId);
            sql += " AND d.ID =?";
        }*/
        return execSqlPage(pageable, sql, params.toArray());
    }

    public Map<String,Object> userInfo(String userId) throws Exception {
        String sql = "select u.REALNAME,u.PHONE,d.DEPTNAME from RZTSYSUSER u LEFT JOIN RZTSYSDEPARTMENT d on u.CLASSNAME=d.id where u.id=?1 ";
        return execSqlSingleResult(sql, userId);
    }

    public Page<Map<String,Object>> checkChildrenList(Pageable pageable,String id, String taskId,String taskType) {
        String sql = "";
        //0看护 1巡视 0待稽查 1已稽查
        if("0,0".equals(taskType)){
            sql = "select d.id detail_id,c.yh_id,c.task_name,c.tdyw_org,wx_org,d.status from CHECK_LIVE_TASK_DETAIL d " +
                    " left join KH_CYCLE c on d.kh_task_id=c.YH_ID " +
                    " where 1=1 ";
            if (!StringUtils.isEmpty(id)) {
                sql += " and d.task_id= "+id;
            }
            if (!StringUtils.isEmpty(taskId)) {
                sql += " and c.YH_ID in ("+taskId+")";
            }
        }else if("1,0".equals(taskType)){
            sql = "select d.id detail_id,t.id xs_zc_task_id,t.task_name,d.status,t.PLAN_START_TIME,u.realname from CHECK_LIVE_TASK_DETAILXS d left join XS_ZC_TASK t on d.xs_task_id=t.id " +
                    "left join rztsysuser u on u.id = t.cm_user_id " +
                    "where 1=1 ";
            if (!StringUtils.isEmpty(id)) {
                sql += " and d.task_id= "+id;
            }
            if (!StringUtils.isEmpty(taskId)) {
                sql += " and t.id in ("+taskId+")";
            }
        }
        return execSqlPage(pageable, sql);
    }

    @Transactional
    public void updateGoodsInfo(Long id, String taskType,String str) {
        //0看护 1巡视 0待稽查 1已稽查
        if("0,0".equals(taskType)){
            reposiotry.updateWptsById(id,str);
        }else if("1,0".equals(taskType)){
            checkLiveTaskDetailRepository.updateWptsById(id,str);
        }
    }

    public Object getById(Long id,String taskType) {
        Object obj = new Object();
        //0看护 1巡视 0待稽查 1已稽查
        if("0,0".equals(taskType)){
            obj = reposiotry.findById(id);
        }else if("1,0".equals(taskType)){
            obj = checkLiveTaskXsRepository.findById(id);
        }
        return obj;

    }

    public Object checkChildrenDetail(String id, String taskType) throws Exception {
        Object obj = new Object();
        //0看护 1巡视 0待稽查 1已稽查
        if("0,0".equals(taskType)){
            //此处id是隐患id
            obj = khYhHistoryRepository.findById(Long.valueOf(id));
        }else if("1,0".equals(taskType)){
            //此处id是xs_zc_taskk的id
            String sql = "select TASK_NAME,PLAN_START_TIME,PLAN_END_TIME,CM_USER_ID from xs_zc_task where id= "+id;
            Map<String, Object> map = execSqlSingleResult(sql);
            HashOperations<String, Object, Object> opt = redisTemplate.opsForHash();
            Object o = opt.get("UserInformation", String.valueOf(map.get("CM_USER_ID")));
            map.put("userInfo", o);
            String sqlImg = "select FILE_PATH,FILE_SMALL_PATH from PICTURE_TOUR where TASK_ID="+id+" and PROCESS_ID=1";
            List<Map<String, Object>> list = execSql(sqlImg);
            if(list.size()>0){
                map.put("FILE_PATH",list.get(0).get("FILE_PATH").toString());
                map.put("FILE_SMALL_PATH",list.get(0).get("FILE_SMALL_PATH").toString());
            }else {
                map.put("FILE_PATH","");
                map.put("FILE_SMALL_PATH","");
            }
            obj = map;
        }
        return obj;
    }

    /**
     * 每天根据看护点生成待派发的看护稽查
     */
    //@Scheduled(cron = "0/5 * *  * * ? ")
    @Scheduled(cron = "0 5 0 ? * *")
    @Transactional
    public void generalKhSite(){
        //reposiotry.generalKhSite();
    }



}
