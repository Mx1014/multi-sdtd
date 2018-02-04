/**    
 * 文件名：CHECKLIVETASKService           
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.alibaba.fastjson.JSON;
import com.rzt.entity.CheckLiveTask;
import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.repository.CheckLiveTaskDetailRepository;
import com.rzt.repository.CheckLiveTaskRepository;
import com.rzt.repository.CheckLiveTaskXsRepository;
import com.rzt.repository.KhYhHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

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

    protected static Logger LOGGER = LoggerFactory.getLogger(CheckLiveTaskService.class);
    @Autowired
    public CheckLiveTaskDetailRepository checkLiveTaskDetailRepository;

    @Autowired
    private CheckLiveTaskXsRepository checkLiveTaskXsRepository;

    @Autowired
    private KhYhHistoryRepository khYhHistoryRepository;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //看护稽查列表查询展示
    public Page<Map<String,Object>> listKhCheckPage(Pageable pageable, String lineId, String tddwId,String currentUserId) {

        String sql = "select s.id,s.task_id,s.TASK_NAME,h.yhms,h.yhjb,h.XLZYCD,d.DEPTNAME,s.yh_id from CHECK_LIVE_site s " +
                " left JOIN KH_YH_HISTORY h on s.YH_ID=h.id " +
                " LEFT JOIN  RZTSYSDEPARTMENT d on d.ID = s.TDYW_ORGID where s.status=0 ";

        List params = new ArrayList<>();
        //线路查询
        if (!StringUtils.isEmpty(lineId)) {
            params.add(lineId);
            sql += " AND s.LINE_ID =? ";
        }

        if(!StringUtils.isEmpty(currentUserId)){
            Map<String, Object> map = userInfoFromRedis(currentUserId);
            Integer roletype = Integer.parseInt(map.get("ROLETYPE").toString());
            String deptid  = map.get("DEPTID").toString();
            switch (roletype) {
                case 0:
                    break;
                case 1:
                    tddwId = deptid;
                    break;
                case 2:
                    tddwId = deptid;
                    break;
                case 3:
                    //外协角色
                    break;
                case 4:
                    //班组角色
                    break;
                case 5:
                    //个人角色
                    break;
            }

        }
        //通道单位查询
        if (!StringUtils.isEmpty(tddwId)) {
            params.add(tddwId);
            sql += " and trunc(s.CREATE_TIME)=trunc(sysdate) and s.task_type=2 ";
            sql += " AND s.TDYW_ORGID =?";
        }else{
            sql += " and s.task_type=1 and trunc(s.CREATE_TIME)>trunc(sysdate-3) ";
        }
        return execSqlPage(pageable, sql, params.toArray());
    }
    public Map<String, Object> userInfoFromRedis(String userId) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();

        Map<String,Object> jsonObject = null;
        Object userInformation = hashOperations.get("UserInformation", userId);
        if(userInformation == null) {
            String sql = "select * from userinfo where id = ?";
            try {
                jsonObject = this.execSqlSingleResult(sql, userId);
            } catch (Exception e) {
                LOGGER.error("currentUserId未获取到唯一数据!",e);
            }
            hashOperations.put("UserInformation",userId,jsonObject);
        } else {
            jsonObject = JSON.parseObject(userInformation.toString(),Map.class);
        }
        return jsonObject;
    }
    //看护已派发稽查任务
    public Page<Map<String,Object>> listKhCheckTaskPage(Pageable pageable, String userId, String tddwId,String currentUserId,String startTime,String endTime,String status,String queryAll,String loginType) {

        String sql = "select t.id,t.TASK_ID,t.CREATE_TIME,t.TASK_NAME,t.PLAN_START_TIME,t.PLAN_END_TIME,u.REALNAME,d.DEPTNAME, " +
                "  t.status , t.TASK_TYPE " +
                " from CHECK_LIVE_TASK t " +
                "  LEFT JOIN  rztsysuser u on u.id=t.USER_ID " +
                "  LEFT JOIN  RZTSYSDEPARTMENT d on d.ID = u.DEPTID where 1=1 ";

        List params = new ArrayList<>();
        //任务状态人查询
        if (!StringUtils.isEmpty(status)) {
            sql += " AND t.status =" + status;
        }
        //人员在线状态查询
        if (!StringUtils.isEmpty(loginType)) {
            int login = Integer.parseInt(loginType);
            sql += " AND u.LOGINSTATUS =" + login;
        }
        //稽查人查询
        if (!StringUtils.isEmpty(userId)) {
            params.add(userId);
            sql += " AND u.id =?";
        }
        //时间段查询
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)){
            params.add(endTime);
            params.add(startTime);
            sql += " and to_date(?,'yyyy-MM-dd HH24:mi') > t.plan_start_time and to_date(?,'yyyy-MM-dd HH24:mi') < t.plan_end_time ";
        }
        if(!StringUtils.isEmpty(currentUserId)){
            Map<String, Object> map = userInfoFromRedis(currentUserId);
            Integer roletype = Integer.parseInt(map.get("ROLETYPE").toString());
            String deptid  = map.get("DEPTID").toString();
            switch (roletype) {
                case 0:
                    break;
                case 1:
                    tddwId = deptid;
                    break;
                case 2:
                    tddwId = deptid;
                    break;
                case 3:
                    //外协角色
                    break;
                case 4:
                    //班组角色
                    break;
                case 5:
                    //个人角色
                    break;
            }

        }
        if(!"queryAll".equals(queryAll)){
            //通道单位查询
            if (!StringUtils.isEmpty(tddwId)) {
                params.add(tddwId);
                sql += " and t.CHECK_TYPE=2 ";
                sql += " AND d.ID =?";
            }else{
                sql += " and t.CHECK_TYPE=1 ";
            }
        }

        return execSqlPage(pageable, sql, params.toArray());
    }
    public Map<String, Object> khTaskDetail(String taskId) throws Exception {
        String sql = "select s.TASK_NAME,h.yhms,h.yhjb,h.yhjb1,h.YHZRDW,h.YHZRDWLXR,h.YHZRDWDH,h.YHFXSJ,h.gkcs,h.yhxcyy,h.XLZYCD,h.YHLB,d.DEPTNAME from CHECK_LIVE_site s " +
                " left JOIN KH_YH_HISTORY h on s.YH_ID=h.id " +
                " LEFT JOIN  RZTSYSDEPARTMENT d on d.ID = s.TDYW_ORGID where s.id = "+taskId;
        return execSqlSingleResult(sql);
    }

    @Transactional
    public void paifaKhCheckTask(CheckLiveTask task , String username,String currentUserId) throws Exception {

        if(!StringUtils.isEmpty(currentUserId)){
            Map<String, Object> map = userInfoFromRedis(currentUserId);
            Integer roletype = Integer.parseInt(map.get("ROLETYPE").toString());
            String deptid  = map.get("DEPTID").toString();
            switch (roletype) {
                case 0:
                    task.setCheckType(1); //1一级单位 2二级单位
                    break;
                case 1:
                    task.setCheckType(2); //1一级单位 2二级单位
                    break;
                case 2:
                    task.setCheckType(2); //1一级单位 2二级单位
                    break;
                case 3:
                    //外协角色
                    break;
                case 4:
                    //班组角色
                    break;
                case 5:
                    //个人角色
                    break;
            }

        }
        task.setId(null);
        task.setCreateTime(new Date());
        task.setStatus(0);//任务派发状态  0未接单 1进行中 2已完成 3超期
        //task.setCheckType(0); //1一级单位 2二级单位
        //task.setTaskType(0);//（0 正常 1保电 2 特殊）
        task.setCheckCycle(1);
        String taskname = "";
        List<Map<String, Object>> tasknames = execSql("select TDYW_ORG||line_name||section taskname from KH_YH_HISTORY where id in (" + task.getTaskId() + ")");
        for (int i = 0; i <tasknames.size() ; i++) {
            taskname += tasknames.get(i).get("TASKNAME")+",";
        }
        task.setTaskName(taskname);
        CheckLiveTask save = reposiotry.save(task);
        String[] split = save.getTaskId().split(",");//隐患ids
        for (int i = 0; i < split.length; i++) {
            Map<String,Object> map = new HashMap<>();
            try{
                map = execSqlSingleResult("select id,YWORG_ID,WXORG_ID from KH_YH_HISTORY where id = ?1", split[i]);
            }catch (Exception e){
                LOGGER.error("该隐患不存在!----->yhId = "+ split[i]);
                continue;
            }

            CheckLiveTaskDetail taskDetail = new CheckLiveTaskDetail();
            taskDetail.setId(null);
            taskDetail.setCreateTime(new Date());
            taskDetail.setTdywOrgid(String.valueOf(map.get("YWORG_ID")).replace("null",""));
            taskDetail.setTdwxOrgid(String.valueOf(map.get("WXORG_ID")).replace("null",""));
            taskDetail.setPlanStartTime(save.getPlanStartTime());
            taskDetail.setPlanEndTime(save.getPlanEndTime());
            taskDetail.setStatus(0);// 0未开始 1进行中 2已完成 3已超期
            taskDetail.setKhTaskType(save.getTaskType());//（（0 正常 1保电 2 特殊）
            taskDetail.setKhTaskId(Long.valueOf(split[i]));
            taskDetail.setTaskId(save.getId());
            checkLiveTaskDetailRepository.save(taskDetail);
            reposiotry.updateLiveSiteStatus(1,Long.valueOf(split[i]));
        }

    }

    public Page<Map<String,Object>> appCheckList(Pageable pageable, String userId,String taskType) {

        String sql = "";
        //0看护 1巡视 0待稽查 1已稽查
        if("0,0".equals(taskType)){
            sql = "select t.id,t.TASK_ID,t.TASK_NAME,u.REALNAME,t.plan_start_time, " +
                    " t.TASK_TYPE , t.STATUS " +
                    "from CHECK_LIVE_TASK t " +
                    "  LEFT JOIN  rztsysuser u on u.id=t.USER_ID " +
                    " where t.status !=3 and t.status !=2 ";
        }else if("0,1".equals(taskType)){
            sql = "select t.id,t.TASK_ID,t.TASK_NAME,u.REALNAME, t.TASK_TYPE,t.plan_start_time " +
                    " from CHECK_LIVE_TASK t " +
                    "  LEFT JOIN  rztsysuser u on u.id=t.USER_ID " +
                    " where t.status =2 and trunc(t.PLAN_START_TIME) <= trunc(sysdate) and trunc(t.PLAN_END_TIME) >= trunc(sysdate) ";
        }else if("1,0".equals(taskType)){
            sql = "select t.id,t.TASK_ID,t.TASK_NAME,u.REALNAME, " +
                    " t.TASK_TYPE ,  t.STATUS " +
                    " from CHECK_LIVE_TASKXS t " +
                    "  LEFT JOIN  rztsysuser u on u.id=t.USER_ID " +
                    " where t.status !=3 and t.status !=2 ";
        }else if("1,1".equals(taskType)){
            sql = "select t.id,t.TASK_ID,t.TASK_NAME,u.REALNAME, " +
                    " t.TASK_TYPE " +
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
        if("0,0".equals(taskType)||"0,1".equals(taskType)){
            sql = "select d.id detail_id,c.yh_id,c.task_name,c.tdyw_org,wx_org,d.status from CHECK_LIVE_TASK_DETAIL d " +
                    " left join KH_CYCLE c on d.kh_task_id=c.YH_ID " +
                    " where 1=1 ";
            if (!StringUtils.isEmpty(id)) {
                sql += " and d.task_id= "+id;
            }
            if (!StringUtils.isEmpty(taskId)) {
                sql += " and c.YH_ID in ("+taskId+")";
            }
        }else if("1,0".equals(taskType)||"1,1".equals(taskType)){
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
            String s = "TWO+"+id+"+3+14+*";
            RedisConnection connection = null;
            try {
                connection = redisTemplate.getConnectionFactory().getConnection();
                connection.select(1);
                Set<byte[]> keys = connection.keys(s.getBytes());
                byte[][] ts = keys.toArray(new byte[][]{});
                if(ts.length > 0) {
                    connection.del(ts);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.close();
            }
            reposiotry.updateWptsById(id,str);
        }else if("1,0".equals(taskType)){
            checkLiveTaskDetailRepository.updateWptsById(id,str);
        }
    }

    public Object getById(Long id,String taskType) {
        Object obj = new Object();
        //0看护 1巡视 0待稽查 1已稽查
        if("0,0".equals(taskType)||"0,1".equals(taskType)){
            obj = reposiotry.findById(id);
        }else if("1,0".equals(taskType)||"1,1".equals(taskType)){
            obj = checkLiveTaskXsRepository.findById(id);
        }
        return obj;

    }

    public Object checkChildrenDetail(String id, String taskType) throws Exception {
        Object obj = new Object();
        //0看护 1巡视 0待稽查 1已稽查
        if("0,0".equals(taskType)||"0,1".equals(taskType)){
            //此处id是隐患id
            obj = khYhHistoryRepository.findById(Long.valueOf(id));
        }else if("1,0".equals(taskType)||"1,1".equals(taskType)){
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

    @Transactional
    public void taskComplete(String id, String taskType) {
        //0看护 1巡视 0待稽查 1已稽查
        if("0,0".equals(taskType)){
            reposiotry.taskComplete(Long.valueOf(id));
        }else if("1,0".equals(taskType)){
            checkLiveTaskXsRepository.taskComplete(Long.valueOf(id));
        }
    }

    public List<Map<String,Object>> listKhCheckTaskDetail(Long id) {
        String sql = " select d.id detail_id,d.CREATE_TIME,d.PLAN_START_TIME,d.PLAN_END_TIME,replace(h.vtype,'kV')||h.line_name||' '||h.section task_name,t.task_type,d.status,u.REALNAME,h.yhms,h.TDYW_ORG,h.TDWX_ORG,h.yhjb,h.yhjb1,h.YHZRDW,h.YHZRDWLXR,h.YHZRDWDH,h.YHFXSJ,h.gkcs," +
                " h.YHXCYY , h.XLZYCD,h.classname " +
                " from CHECK_LIVE_TASK_DETAIL d " +
                " left join CHECK_LIVE_TASK t on t.id=d.task_id " +
                " left join KH_YH_HISTORY h on h.id=d.kh_task_id " +
                " left join rztsysuser u on u.id = t.user_id " +
                "where t.id = "+id;
        List<Map<String, Object>> list = execSql(sql);
        return list;
    }

    /**
     * 每天根据看护点生成待派发的看护稽查
     */
    //@Scheduled(cron = "0/5 * *  * * ? ")
    @Scheduled(cron = "0 5 0 ? * *")
    @Transactional
    public void generalKhSite(){
        //更新check_live_task,plan_end_time为昨天的没完成的状态为超期
        reposiotry.updateLiveTaskYesterday(3);
        //给二级单位生成新一轮待派发看护稽查(每天生成)
        reposiotry.generalKhSite(2);

        //给一级单位生成新一轮待派发看护稽查(每3天生成)
        try {
            Map<String, Object> map = execSqlSingleResult("select trunc(sysdate)-trunc(max(create_time)) daydiff from check_live_site where task_type=1 and sysdate-5<create_time");
            if(Integer.valueOf(map.get("DAYDIFF").toString())>=3){
                reposiotry.generalKhSite(1);
            }
        } catch (Exception e) {
            reposiotry.generalKhSite(1);
        }
    }


    public Map<String,Object> getKhRange(String yhId) throws Exception {
        String sql = "select to_number(RADIUS) jl,jd,WD from KH_YH_HISTORY where id ="+yhId;
        return execSqlSingleResult(sql);
    }

    //无用TODO
    public Object checkDetailDone(String id, String taskId, String taskType) {
        List<Map<String, Object>> list = new ArrayList<>();
        //0看护 1巡视 0待稽查 1已稽查
        if("0,1".equals(taskType)){
            String sql = "select p.id,p.FILE_PATH,p.FILE_SMALL_PATH,p.PROCESS_NAME,p.CREATE_TIME from check_live_task t,CHECK_LIVE_TASK_DETAIL d,PICTURE_JC p where t.id=d.TASK_ID AND (p.TASK_ID=t.ID OR p.TASK_ID=d.id) and t.id="+id;
            list = execSql(sql);
        }else if("1,1".equals(taskType)){
            String sql = "select p.id,p.FILE_PATH,p.FILE_SMALL_PATH,p.PROCESS_NAME,p.CREATE_TIME from check_live_taskxs t,CHECK_LIVE_TASK_DETAILxs d,PICTURE_JC p where t.id=d.TASK_ID AND (p.TASK_ID=t.ID OR p.TASK_ID=d.id) and t.id="+id;
            list = execSql(sql);
        }
        return list;
    }

    @Transactional
    public void updateKhCheckUser(Long id, String userId, String userName) {
        reposiotry.updateKhCheckUser(id,userId,userName);
    }

    public List<Map<String,Object>> listKhCheckTaskDetailPicture(String id,String detailId) {
        String sql = "select process_name,CREATE_TIME,FILE_SMALL_PATH,FILE_PATH from PICTURE_JC " +
                " where task_id=?  order by CREATE_TIME ASC " ;
        List<Map<String, Object>> listAll = new ArrayList<>();
        List<Map<String, Object>> list1 = new ArrayList<>();
        List<Map<String, Object>> list2 = new ArrayList<>();

        if(!StringUtils.isEmpty(id)){
            String sql1 = sql.replace("?",id);
            list1 = execSql(sql1);
        }
        if(!StringUtils.isEmpty(detailId)){
            String sql2 = sql.replace("?",detailId);
            list2 = execSql(sql2);
        }
        listAll.addAll(list1);
        listAll.addAll(list2);
        return listAll;
    }
}
