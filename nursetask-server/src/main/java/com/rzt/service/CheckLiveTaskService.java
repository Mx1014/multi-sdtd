/**    
 * 文件名：CHECKLIVETASKService           
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.entity.*;
import com.rzt.entity.model.CheckLiveTaskExecModel;
import com.rzt.repository.*;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CheckLiveTaskExecRepository execRepository;

    @Autowired
    private CheckLiveTaskDetailRepository khDetailRepository;

    @Autowired
    private KhSiteRepository khSiteRepository;

    @Autowired
    private CheckLiveTaskDetailXsRepository checkLiveTaskDetailXsRepository;





     public  Page<Map<String, Object>> listAllCheckTask(String startTime,String endTime,String taskName,Integer status,String userId,String taskType,Pageable pageable) {
        String result = " c.id id,c.task_name taskName,u.realname realName,g.DEPTNAME className,c.tdwh_org tdwxorg, " +
                        " c.create_time createTime,c.plan_start_time startTime,c.plan_end_time endTime,c.task_status taskStatus ";
        StringBuilder sb = new StringBuilder();
        sb.append(" where 1=1 ");
        List params = new ArrayList<>();
         //时间段查询
         if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)){
             startTime =  startTime.substring(0, 10);
             endTime = endTime.substring(0, 10);
             sb.append(" and c.create_time between to_date(?,'YYYY-MM-DD') and to_date(?,'YYYY-MM-DD') ");
             params.add(startTime);
             params.add(endTime);
         }
         //任务名查询
         if (!StringUtils.isEmpty(taskName)) {
             sb.append(" and c.task_name like ? ");
             params.add("%"+taskName+"%");
         }
        //任务状态查询
        if (status!=null) {
            sb.append(" and c.task_status = ? ");
            params.add(status);
        }

         //用户人查询
         if(!StringUtils.isEmpty(userId)){
             sb.append(" and c.user_id = ? ");
             params.add(userId);
         }

         if(taskType.equals("0")){
             //看护主任务
             sb.append(" and c.TASK_TYPE='看护稽查' ");

         }else{
             sb.append(" and c.TASK_TYPE='巡视稽查' ");
         }

        sb.append(" order by c.create_time desc ");

        String sql = "select " + result + " from " +
                     " check_live_task_exec c  left join rztsysuser u " +
                     "on c.user_id = u.id left join RZTSYSDEPARTMENT  g on u.CLASSNAME = g.ID" + sb.toString();
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql, params.toArray());

        return maps;

    }


    public  Page<Map<String, Object>> listpaifaCheckTask(String startTime,String endTime, String userId,String taskName,String taskType,Pageable pageable) {
        String result1 = " k.id id,k.task_name taskName,k.TDYW_ORG tddw,k.WX_ORG tdwxdw,k1.YHMS yhms," +
                        " k1.YHJB yhjb ,k1.create_time createTime, u.realname realname " ;

        String result2 = " c.ID id,c.task_name taskName,r.companyname tddw,d.DEPTNAME tdwxdw , " +
                         " c.plan_start_time startTime,c.plan_end_time endTime,u.realname realname " ;

        StringBuilder sb = new StringBuilder();
        sb.append(" where 1=1 ");
        List params = new ArrayList<>();
        //时间段查询
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)){
            startTime =  startTime.substring(0, 10);
            endTime = endTime.substring(0, 10);
            sb.append(" and c.create_time between to_date(?,'YYYY-MM-DD') and to_date(?,'YYYY-MM-DD') ");
            params.add(startTime);
            params.add(endTime);
        }
        //任务名查询
        if (!StringUtils.isEmpty(taskName)) {
            sb.append(" and c.task_name like ? ");
            params.add("%"+taskName+"%");
        }



        //用户人查询
        if (!StringUtils.isEmpty(userId)) {
            sb.append(" and c.user_id = ? ");
            params.add(userId);
        }

       // sb.append(" order by c.create_time desc  ");

      /*  String sql = "select " + result + " from Check_Live_Task c left join " +
                     "RZTSYSCOMPANY r on c.tdwx_orgid = r.id LEFT JOIN  RZTSYSDEPARTMENT d on c.CHECK_DEPT=d.ID " +
                     "left join KH_YH_HISTORY k " +
                     "on c.yh_id = k.id " + sb.toString();*/

        String sql = null;
        Page<Map<String, Object>> maps=null;

        if(taskType.equals("0")){
            //查询看护任务
            sb.append(" and k.jc_status = 0 ");//未派发定死
            sb.append(" and k1.yhzt=0 ");
            sb.append(" order by k.create_time desc  ");
            sql = "select " + result1 + " from KH_CYCLE k LEFT JOIN  KH_YH_HISTORY k1 ON k.YH_ID=k1.ID " +
                    " LEFT JOIN RZTSYSUSER u ON  k.JC_USER_ID=u.ID  " +sb.toString();



            maps = execSqlPage(pageable, sql, params.toArray());

            List<Map<String,Object>> list= maps.getContent();
            for(Map<String,Object> map : list){
                map.put("TASKTYPE","看护");

            }
            //看一下有没有更改 内存中中maps的值
            return maps;


        }else{
            //查询巡视任务
            sb.append(" and c.jc_status = 0 ");//未派发定死
            sb.append(" and c1.IN_USE=0 ");
            sb.append(" order by c.PLAN_START_TIME desc  ");
            sql = "select " + result2 + " from XS_ZC_TASK c LEFT JOIN  XS_ZC_CYCLE c1 on c.XS_ZC_CYCLE_ID = c1.ID  " +
                    " left join RZTSYSCOMPANY r on r.id=c.TD_ORG  LEFT JOIN  RZTSYSDEPARTMENT d on d.ID = c.WX_ORG  left join rztsysuser u on u.id=c.JC_USER_ID   " +sb.toString();


             maps = execSqlPage(pageable, sql, params.toArray());

            List<Map<String,Object>> list= maps.getContent();
            for(Map<String,Object> map : list){
                map.put("TASKTYPE","稽查");

            }
            //看一下有没有更改 内存中中maps的值
            return maps;
        }



    }


    public List getCheckTaskById(String id,String taskType) {
        String sql = null;
        if(taskType.equals("0")){
            //看护任务详情查看
            sql= " select c.id id,c.task_name taskName,c.CREATE_TIME createtime," +
                    " c.TDYW_ORG tddw,c.WX_ORG wxOrg,c1.yhjb yhjb,u.REALNAME realname " +
                    "from KH_CYCLE c left join kh_yh_history c1 on c.yh_id = c1.id LEFT JOIN  RZTSYSUSER u on c.JC_USER_ID = u.ID where c.id=? ";
        }else{
            //巡视任务详情查看
            sql =" select c.id id,c.task_name taskName, r.COMPANYNAME tddw,u.REALNAME realname " +
                    " from XS_ZC_TASK c LEFT JOIN  RZTSYSUSER u on c.CM_USER_ID = u.ID LEFT JOIN  RZTSYSCOMPANY r on r.ID = c.TD_ORG where c.id=?";
        }
        Long ids = Long.parseLong(id);

        return this.execSql(sql,ids);
    }

    public List getCheckTaskName() {
        String sql = " select DISTINCT task_name   from CHECK_LIVE_TASK where ROWNUM<=15  ";
        return this.execSql(sql);
    }

    public CheckLiveTask findLiveTask(String value) {
        long id = Long.parseLong(value);
        return this.reposiotry.findLiveTask(id);
    }

    public void updateLiveTask(CheckLiveTask tt, String value) {

        long id = Long.parseLong(value);
      //  this.reposiotry.updateLiveTask(tt.getStatus(),tt.getCycleId(),id);
    }

    @Transactional
    public WebApiResponse deleteById(String id) {
        try {
            String[] split = id.split(",");
            if (split.length>0){
                for (int i = 0;i < split.length;i++) {
                    this.reposiotry.deleteById(Long.parseLong(split[i]));
                }
            }else{
                this.reposiotry.deleteById(Long.parseLong(id));
            }
            return WebApiResponse.success("删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return WebApiResponse.erro("删除失败");
        }
    }

    public List listCheckTaskName() {

        String sql = "select distinct task_name from check_live_task where ROWNUM<=15";
        return this.execSql(sql);

    }


    @Transactional
    public void saveCheckLiveTask() {
        //由巡视任务生成稽查任务   userId 只是测试
        String sql = "select c.ID taskId,c.TASK_NAME taskName,c.TD_ORG tdOrg,c.WX_ORG wxOrg ,c.CM_USER_ID userId " +
                     "from XS_ZC_TASK c LEFT JOIN  XS_ZC_CYCLE c1 on c.XS_ZC_CYCLE_ID = c1.ID where c1.IN_USE=0";
        List<Map<String, Object>> result = this.execSql(sql);
        for (Map<String,Object> map:result) {
            CheckLiveTask checkLiveTask = new CheckLiveTask();
            checkLiveTask.setId();
            Object taskId = map.get("TASKID");
            checkLiveTask.setTaskId(Long.parseLong(String.valueOf(taskId)));
            checkLiveTask.setTaskType(0);//新增
            checkLiveTask.setCheckType(1);//巡视
            checkLiveTask.setCreateTime(DateUtil.dateNow());
            checkLiveTask.setTaskName((String)map.get("TASKNAME"));
            checkLiveTask.setStatus(0);
            checkLiveTask.setTdwhOrg((String)map.get("TDORG"));
            checkLiveTask.setTdwxOrgid((String)map.get("WXORG"));

            //测试用
            checkLiveTask.setUserId((String) map.get("USERID"));

            this.reposiotry.save(checkLiveTask);

        }
        // 由看护任务生成稽查任务
        String sql2 = "select k.ID taskId,k.TASK_NAME taskName,k.TDYW_ORG tdOrg,k.WX_ORG wxOrg,k.YH_ID yhid,k.USER_ID userId " +
                      " from KH_TASK k LEFT JOIN  KH_SITE k1 on k.SITE_ID=k1.ID  ";

        List<Map<String, Object>> result2 = this.execSql(sql2);

        for (Map<String,Object> map:result2) {
            CheckLiveTask checkLiveTask = new CheckLiveTask();
            checkLiveTask.setId();
            Object taskId = map.get("TASKID");
            checkLiveTask.setTaskId(Long.parseLong(String.valueOf(taskId)));
            checkLiveTask.setTaskType(0);//新增
            checkLiveTask.setCheckType(1);//巡视
            checkLiveTask.setCreateTime(DateUtil.dateNow());
            checkLiveTask.setTaskName((String)map.get("TASKNAME"));
            checkLiveTask.setStatus(0);
            checkLiveTask.setTdwhOrg((String)map.get("TDORG"));
            checkLiveTask.setTdwxOrgid((String)map.get("WXORG"));
            Object yhid = map.get("YHID");
            checkLiveTask.setYhId(Long.parseLong(String.valueOf(yhid)));

            // //测试用
            checkLiveTask.setUserId((String) map.get("USERID"));

            this.reposiotry.save(checkLiveTask);

        }

    }


    public List<Map<String,Object>> paifaCheckTaskPro(String userId,String task_type) {

        String sql = null;
        List<Map<String,Object>> result=null;
        if(task_type.equals("0")){
            //看护任务推荐
            sql = " select id id,task_name taskName  from kh_cycle where jc_user_id=? ";
            result = this.execSql(sql,userId);
            for (Map<String,Object> map : result ){
                map.put("TASKTYPE","看护");
            }
            return  result;

        }else{
            //巡视任务推荐
            sql = " select id id,task_name taskName  from XS_ZC_TASK where jc_user_id=? ";
            result = this.execSql(sql,userId);
            for (Map<String,Object> map : result ){
                map.put("TASKTYPE","稽查");
            }
            return  result;
        }


    }

    public static void main(String[] args) {
      /*  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, -1);
        Date m = c.getTime();
        System.out.println(m);
        String mon = df.format(m);*/
       // task.setPlanStartTime(mon+" 00:00");
       // task.setPlanEndTime(df.format(new Date())+" 23:59");

        Date date = new Date();
        System.out.println(date);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String d = df.format(date);
        System.out.println(d);

        String s = "";
        s =  d.substring(0,4)+"年"+d.substring(5,7)+"月"+d.substring(8,10)+"日";
        System.out.println(s);





    }


    public List<Map<String,Object>> getUser(String userId) {
        String sql = " select realname from rztsysuser where id=? ";

        return  this.execSql(sql,userId);

    }

    @Transactional
    public void paifaTask(CheckLiveTaskExecModel model,String task_type) {
        CheckLiveTaskExec taskExec = new CheckLiveTaskExec();
        taskExec.setId(0L);
        StringBuilder taskName = new StringBuilder();
        String userId = model.getUserId();
        List<Map<String,Object>> list = this.getUser(userId);
        for (Map<String,Object> map: list) {
            Object realname = map.get("REALNAME");
            taskName.append(realname);
        }
        String time = DateUtil.getDate();
        if(task_type.equals("0")){
            //看护任务派发
            taskName.append(time).append("看护稽查任务");
            taskExec.setTaskName(taskName.toString());
            taskExec.setTaskType("看护稽查");
        }else{
            //巡视稽查
            taskName.append(time).append("巡视稽查任务");
            taskExec.setTaskName(taskName.toString());
            taskExec.setTaskType("巡视稽查");
        }

        taskExec.setStatus(0);//未开始
        taskExec.setCreateTime(model.getCreateTime());
        taskExec.setUserId(model.getUserId());
        taskExec.setTaskStatus(0);//未稽查
        taskExec.setTdwhOrg(model.getTdwhOrg());//通道外协单位id
        taskExec.setPlanStartTime(model.getPlanStartTime());
        taskExec.setPlanEndTime(model.getPlanEndTime());


        this.execRepository.save(taskExec);


        if(task_type.equals("0")){
            String [] values = model.getIds().split(",");
            for (String id:values) {
                CheckLiveTaskDetail khTaskDetail = new CheckLiveTaskDetail();
                khTaskDetail.setId();
                khTaskDetail.setExecId(taskExec.getId());
                khTaskDetail.setPlanStartTime(taskExec.getPlanStartTime());
                khTaskDetail.setPlanEndTime(taskExec.getPlanEndTime());
                khTaskDetail.setStatus(0);//未开始
                khTaskDetail.setCreateTime(DateUtil.dateNow());
                khTaskDetail.setKhTaskId(Long.parseLong(String.valueOf(id)));
                this.khDetailRepository.save(khTaskDetail);
                //更新
                Long khcycleId = Long.parseLong(id);
                String sql = "update kh_cycle set JC_STATUS=?,JC_USER_ID=? where id=?";
                Query q = this.entityManager.createNativeQuery(sql);
                q.setParameter(1,0);
                q.setParameter(2,model.getUserId());
                q.setParameter(3,khcycleId);
                q.executeUpdate();

            }

        }else {
            String xs_id = model.getIds();
            CheckLiveTaskDetailXs xsTask = new CheckLiveTaskDetailXs();
            xsTask.setId();
            xsTask.setExecId(taskExec.getId());
            xsTask.setXsTaskId(Long.parseLong(xs_id));
            xsTask.setXsTaskType(2);//正常
            xsTask.setPlanStartTime(taskExec.getPlanStartTime());
            xsTask.setPlanEndTime(taskExec.getPlanEndTime());
            xsTask.setStatus(0);
            xsTask.setCreateTime(DateUtil.dateNow());
            this.checkLiveTaskDetailXsRepository.save(xsTask);

            //更改巡视任务表
            Long xsid = Long.parseLong(xs_id);
            String sql = "UPDATE  XS_ZC_TASK set JC_STATUS=?,JC_USER_ID=? where ID=? ";
            Query q = this.entityManager.createNativeQuery(sql);
            q.setParameter(1,0);
            q.setParameter(2,model.getUserId());
            q.setParameter(3,xsid);
            q.executeUpdate();



        }

    }

    @Transactional
    public void timeCheckLiveTask() {

     //   this.khSiteRepository.updateKhSiteJcStatus();//更新看护点表 稽查状态为未派发
        String s = "update KH_cycle set JC_STATUS=0";
        Query q = this.entityManager.createNativeQuery(s);
        q.executeUpdate();
        //更改巡视表 稽查状态为未派发
        String sql = "update XS_ZC_TASK set JC_STATUS=0 ";
        Query query = this.entityManager.createNativeQuery(sql);
        query.executeUpdate();

    }
}
