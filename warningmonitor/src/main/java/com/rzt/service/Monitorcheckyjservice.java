/**
 * 文件名：MONITORCHECKYJService
 * 版本信息：
 * 日期：2018/01/08 11:06:23
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.Monitorcheckyj;
import com.rzt.repository.Monitorcheckyjrepository;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 类名称：MONITORCHECKYJService
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2018/01/08 11:06:23
 * 修改人：张虎成
 * 修改时间：2018/01/08 11:06:23
 * 修改备注：
 */
@Service
public class Monitorcheckyjservice extends CurdService<Monitorcheckyj, Monitorcheckyjrepository> {

    @Autowired
    private Monitorcheckyjrepository repo;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    public void saveCheckYj(String[] messages) {
        //保存到一级
        repo.saveCheckYj(SnowflakeIdWorker.getInstance(20,14).nextId(),Long.valueOf(messages[1]),Integer.valueOf(messages[2]),Integer.valueOf(messages[3]),messages[4],messages[5],messages[6]);
    }
    //一级单位天加未到位
    public void saveCheckYjWdw(String[] messages) {
        repo.saveCheckYjWdw(SnowflakeIdWorker.getInstance(20,14).nextId(),Long.valueOf(messages[1]),Integer.valueOf(messages[2]),Integer.valueOf(messages[3]),messages[4],messages[5],messages[6],messages[7]);
    }

    /**
     * 查询任务详情
     */
    public Object taskInfo(Long taskId, Integer warningType,Integer taskType) {
        //巡视
        String sql = "";
        if(taskType==1){
            sql = "SELECT ej.CREATE_TIME,ej.TASK_NAME,ej.WARNING_TYPE,xs.PLAN_START_TIME,xs.PLAN_END_TIME,xs.REAL_START_TIME,xs.PLAN_END_TIME,xs.CM_USER_ID AS user_id,xs.STAUTS  " +
                    " FROM MONITOR_CHECK_EJ ej LEFT JOIN XS_ZC_TASK xs ON ej.TASK_ID = xs.ID WHERE ej.TASK_ID=?1";
        }else if(taskType==2){
            sql = "SELECT ej.CREATE_TIME,ej.TASK_NAME,kh.REAL_START_TIME,kh.PLAN_END_TIME,kh.PLAN_START_TIME,kh.REAL_END_TIME,kh.USER_ID,kh.STATUS  " +
                    "FROM MONITOR_CHECK_EJ ej LEFT JOIN KH_TASK kh ON ej.TASK_ID = kh.ID";
        }
        try {
            Map<String, Object> map = execSqlSingleResult(sql, taskId);
            return map;
        } catch (Exception e) {
            e.getMessage();
            return "error";
        }

    }

    /**
     * 查看人员详情
     */
    public Object userInfo(String userId,Integer warningType,Long taskId) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        Object userInformation = hash.get("UserInformation", userId);
        JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
        if(warningType==7){
            String sql1 = "SELECT nvl(REAL_END_TIME,sysdate) AS REAL_END_TIME FROM KH_TASK WHERE USER_ID=?1 AND ID=?2 ";
            List<Map<String, Object>> maps1 = execSql(sql1, userId, taskId);
            Date endTime = new Date();
            if(maps1.size()>0){
               Date end = (Date)maps1.get(0).get("REAL_END_TIME");
                if(end!=null && end.getTime()<endTime.getTime()){
                    endTime = end;
                }
            }

            /*String sql = "SELECT count(kh_time.id) times," +
                    "     sum(ROUND(TO_NUMBER(nvl(kh_time.END_TIME,?2) - kh_time.START_TIME) * 24 * 60 * 60)) timeLong " +
                    "     FROM WARNING_OFF_POST_USER_TIME kh_time " +
                    "     WHERE kh_time.FK_USER_ID = ?1";*/
            String sql = "SELECT count(kh_time.id) times,  " +
                    "   sum(ROUND(TO_NUMBER(nvl(kh_time.END_TIME,?2) - kh_time.START_TIME) * 24 * 60 * 60)) timeLong  " +
                    "   FROM WARNING_OFF_POST_USER_TIME kh_time  " +
                    "   WHERE kh_time.FK_TASK_ID=?3 AND kh_time.FK_USER_ID =?1";
            List<Map<String, Object>> maps = execSql(sql, userId,endTime);
            if(maps.size()>0){
                Map<String, Object> map = maps.get(0);
                String time = map.get("TIMELONG").toString();
                Long timelong = Long.valueOf(time);
                Long h=timelong/3600;
                Long m=(timelong%3600)/60;
                Long s=(timelong%3600)%60;
                jsonObject.put("TIMES",map.get("TIMES").toString());
                jsonObject.put("TIMELONG",h+"小时"+m+"分"+s+"秒");
            }
        }
        return jsonObject;
    }

    /**
     * 柱状图查询
     * @param type
     * @return
     */
    public Object sumInfo(String userId,Integer type) {
        String deptID = getDeptID(userId);
        if("-1".equals(deptID)){
            return "该用户无此权限";
        }
        List<Map<String, Object>> result = new ArrayList<>();
        if("0".equals(deptID)){

                //查询未出理
                String sql1 = "SELECT count(1) AS sum,yj.DEPTID FROM MONITOR_CHECK_YJ yj LEFT JOIN RZTSYSUSER u ON yj.USER_ID=u.ID " +
                        "WHERE TASK_TYPE=?1 AND u.USERDELETE=1 AND trunc(yj.CREATE_TIME)=trunc(sysdate) AND STATUS =0 AND yj.USER_ID IS NOT NULL GROUP BY yj.DEPTID ";
                //查询处理中
                String sql2 = "SELECT count(1) AS sum,DEPTID FROM MONITOR_CHECK_YJ WHERE TASK_TYPE=?1 AND trunc(CREATE_TIME)=trunc(sysdate) AND STATUS =1 AND USER_ID IS NOT NULL GROUP BY DEPTID  ";
                //查询已处理
                String sql3 = "SELECT count(1) AS sum,DEPTID FROM MONITOR_CHECK_YJ WHERE TASK_TYPE=?1 AND trunc(CREATE_TIME)=trunc(sysdate) AND STATUS =2  AND USER_ID IS NOT NULL  GROUP BY DEPTID  ";
                List<Map<String, Object>> maps = execSql(sql1,type);
                List<Map<String, Object>> maps1 = execSql(sql2,type);
                List<Map<String, Object>> maps2 = execSql(sql3,type);
                String deptnameSql = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL ORDER BY t.DEPTSORT ";
                List<Map<String, Object>> dept = execSql(deptnameSql);
                for (Map<String,Object> map:dept){
                    Map<String,Object> sumMap = new HashMap<String,Object>();
                    String id = (String) map.get("ID");
                    String deptName = (String) map.get("DEPTNAME");
                    deptName=deptName.substring(0,deptName.length()-2);
                    sumMap.put("ID",id);
                    sumMap.put("DEPTNAME",deptName);
                    sumMap.put("WCL",0);
                    sumMap.put("CLZ",0);
                    sumMap.put("YCL",0);
                    //添加未处理
                    for(Map<String,Object> m1:maps){
                        if(id.equals(m1.get("DEPTID").toString())){
                            sumMap.put("WCL",m1.get("SUM"));
                        }
                    }
                    //添加处理中
                    for(Map<String,Object> m1:maps1){
                        if(id.equals(m1.get("DEPTID").toString())){
                            sumMap.put("CLZ",m1.get("SUM"));
                        }
                    }
                    //添加已处理
                    for(Map<String,Object> m1:maps2){
                        if(id.equals(m1.get("DEPTID").toString())){
                            sumMap.put("YCL",m1.get("SUM"));
                        }
                    }
                    result.add(sumMap);
                }


        }else{
            String sql = "SELECT count(1) AS sum,u.CLASSNAME FROM MONITOR_CHECK_EJ ej LEFT JOIN RZTSYSUSER u ON ej.USER_ID=u.ID  " +
                    "WHERE ej.TASK_TYPE=?2  AND u.USERDELETE=1  AND trunc(ej.CREATE_TIME)=trunc(sysdate) AND ej.STATUS =0 AND ej.DEPTID=?1 AND ej.USER_ID IS NOT NULL " +
                    "GROUP BY u.CLASSNAME";
            String sql2 = "SELECT count(1) AS sum,u.CLASSNAME FROM MONITOR_CHECK_EJ ej LEFT JOIN RZTSYSUSER u ON ej.USER_ID=u.ID  " +
                    "  WHERE ej.TASK_TYPE=?2  AND trunc(ej.CREATE_TIME)=trunc(sysdate) AND ej.STATUS =1 AND ej.DEPTID=?1  AND ej.USER_ID IS NOT NULL " +
                    " GROUP BY u.CLASSNAME";
            String sql3 = "SELECT count(1) AS sum,u.CLASSNAME FROM MONITOR_CHECK_EJ ej LEFT JOIN RZTSYSUSER u ON ej.USER_ID=u.ID  " +
                    "  WHERE ej.TASK_TYPE=?2  AND trunc(ej.CREATE_TIME)=trunc(sysdate) AND ej.STATUS =2 AND ej.DEPTID=?1  AND ej.USER_ID IS NOT NULL " +
                    " GROUP BY u.CLASSNAME";

            List<Map<String, Object>> maps = execSql(sql, deptID, type);
            List<Map<String, Object>> maps1 = execSql(sql2, deptID, type);
            List<Map<String, Object>> maps2 = execSql(sql3, deptID, type);

            String sqll = "SELECT ID,  DEPTNAME FROM (SELECT ID, DEPTNAME, LASTNODE  FROM RZTSYSDEPARTMENT  " +
                    "  START WITH ID = ?1 CONNECT BY PRIOR ID = DEPTPID) WHERE LASTNODE = 0";
            List<Map<String, Object>> maps3 = execSql(sqll, deptID);
            for (Map<String,Object> map:maps3){
                Map<String,Object> sumMap = new HashMap<String,Object>();
                String id = (String) map.get("ID");
                String deptName = (String) map.get("DEPTNAME");
                if(deptName.length()>6){
                    deptName = deptName.substring(0,6);
                }
                sumMap.put("ID",id);
                sumMap.put("DEPTNAME",deptName);
                sumMap.put("WCL",0);
                sumMap.put("CLZ",0);
                sumMap.put("YCL",0);
                //添加未处理
                for(Map<String,Object> m1:maps){
                    if(m1.get("CLASSNAME")!=null && id.equals(m1.get("CLASSNAME").toString())){
                        sumMap.put("WCL",m1.get("SUM"));
                    }
                }
                //添加处理中
                for(Map<String,Object> m1:maps1){
                    if(m1.get("CLASSNAME")!=null && id.equals(m1.get("CLASSNAME").toString())){
                        sumMap.put("CLZ",m1.get("SUM"));
                    }
                }
                //添加已处理
                for(Map<String,Object> m1:maps2){
                    if(m1.get("CLASSNAME")!=null && id.equals(m1.get("CLASSNAME").toString())){
                        sumMap.put("YCL",m1.get("SUM"));
                    }
                }
                result.add(sumMap);

            }

        }
        return result;
    }

    /**
     * 查询所有柱状图
     * @param userId
     * @param type
     * @return
     */
    public Object totalSumInfo(String userId, Integer type) {
        String deptID = getDeptID(userId);
        if("-1".equals(deptID)){
            return "该用户无此权限";
        }
        List<Map<String, Object>> result = new ArrayList<>();
        if("0".equals(deptID)){
            //查询未出理
            String sql1 = "SELECT count(1) AS sum,yj.DEPTID FROM MONITOR_CHECK_YJ yj LEFT JOIN RZTSYSUSER u ON yj.USER_ID=u.ID " +
                    " WHERE trunc(yj.CREATE_TIME)=trunc(sysdate) AND u.USERDELETE=1 " +
                    "    AND yj.STATUS =0  AND yj.USER_ID IS NOT NULL GROUP BY yj.DEPTID";
            //查询处理中
            String sql2 = "SELECT count(1) AS sum,DEPTID FROM MONITOR_CHECK_YJ WHERE trunc(CREATE_TIME)=trunc(sysdate) AND STATUS =1  AND USER_ID IS NOT NULL GROUP BY DEPTID  ";
            //查询已处理
            String sql3 = "SELECT count(1) AS sum,DEPTID FROM MONITOR_CHECK_YJ WHERE trunc(CREATE_TIME)=trunc(sysdate) AND STATUS =2  AND USER_ID IS NOT NULL GROUP BY DEPTID  ";
            List<Map<String, Object>> maps = execSql(sql1);
            List<Map<String, Object>> maps1 = execSql(sql2);
            List<Map<String, Object>> maps2 = execSql(sql3);
            String deptnameSql = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL ORDER BY t.DEPTSORT ";
            List<Map<String, Object>> dept = execSql(deptnameSql);

            for (Map<String,Object> map:dept){
                Map<String,Object> sumMap = new HashMap<String,Object>();
                String id = (String) map.get("ID");
                String deptName = (String) map.get("DEPTNAME");
                deptName=deptName.substring(0,deptName.length()-2);
                sumMap.put("ID",id);
                sumMap.put("DEPTNAME",deptName);
                sumMap.put("WCL",0);
                sumMap.put("CLZ",0);
                sumMap.put("YCL",0);

                //添加未处理
                for(Map<String,Object> m1:maps){
                    if(id.equals(m1.get("DEPTID").toString())){
                        sumMap.put("WCL",m1.get("SUM"));
                    }
                }
                //添加处理中
                for(Map<String,Object> m1:maps1){
                    if(id.equals(m1.get("DEPTID").toString())){
                        sumMap.put("CLZ",m1.get("SUM"));
                    }
                }
                //添加已处理
                for(Map<String,Object> m1:maps2){
                    if(id.equals(m1.get("DEPTID").toString())){
                        sumMap.put("YCL",m1.get("SUM"));
                    }
                }
                result.add(sumMap);
            }

        }else{
            String sql = "SELECT count(1) AS sum,u.CLASSNAME FROM MONITOR_CHECK_EJ ej LEFT JOIN RZTSYSUSER u ON ej.USER_ID=u.ID  " +
                    "WHERE trunc(ej.CREATE_TIME)=trunc(sysdate)  AND u.USERDELETE=1  AND ej.STATUS =0 AND ej.DEPTID=?1  AND ej.USER_ID IS NOT NULL " +
                    "GROUP BY u.CLASSNAME";
            String sql2 = "SELECT count(1) AS sum,u.CLASSNAME FROM MONITOR_CHECK_EJ ej LEFT JOIN RZTSYSUSER u ON ej.USER_ID=u.ID  " +
                    "  WHERE  trunc(ej.CREATE_TIME)=trunc(sysdate) AND ej.STATUS =1 AND ej.DEPTID=?1  AND ej.USER_ID IS NOT NULL " +
                    " GROUP BY u.CLASSNAME";
            String sql3 = "SELECT count(1) AS sum,u.CLASSNAME FROM MONITOR_CHECK_EJ ej LEFT JOIN RZTSYSUSER u ON ej.USER_ID=u.ID  " +
                    "  WHERE trunc(ej.CREATE_TIME)=trunc(sysdate) AND ej.STATUS =2 AND ej.DEPTID=?1  AND ej.USER_ID IS NOT NULL " +
                    " GROUP BY u.CLASSNAME";

            List<Map<String, Object>> maps = execSql(sql, deptID);
            List<Map<String, Object>> maps1 = execSql(sql2, deptID);
            List<Map<String, Object>> maps2 = execSql(sql3, deptID);

            String sqll = "SELECT ID,  DEPTNAME FROM (SELECT ID, DEPTNAME, LASTNODE  FROM RZTSYSDEPARTMENT  " +
                    "  START WITH ID = ?1 CONNECT BY PRIOR ID = DEPTPID) WHERE LASTNODE = 0";
            List<Map<String, Object>> maps3 = execSql(sqll, deptID);
            for (Map<String,Object> map:maps3){
                Map<String,Object> sumMap = new HashMap<String,Object>();
                String id = (String) map.get("ID");
                String deptName = (String) map.get("DEPTNAME");
                if(deptName.length()>6){
                    deptName = deptName.substring(0,5);
                }
                sumMap.put("ID",id);
                sumMap.put("DEPTNAME",deptName);
                sumMap.put("WCL",0);
                sumMap.put("CLZ",0);
                sumMap.put("YCL",0);
                //添加未处理
                for(Map<String,Object> m1:maps){

                    if(m1.get("CLASSNAME")!=null && id.equals(m1.get("CLASSNAME").toString())){
                        sumMap.put("WCL",m1.get("SUM"));
                    }
                }
                //添加处理中
                for(Map<String,Object> m1:maps1){
                    if(m1.get("CLASSNAME")!=null && id.equals(m1.get("CLASSNAME").toString())){
                        sumMap.put("CLZ",m1.get("SUM"));
                    }
                }
                //添加已处理
                for(Map<String,Object> m1:maps2){
                    if(m1.get("CLASSNAME")!=null && id.equals(m1.get("CLASSNAME").toString())){
                        sumMap.put("YCL",m1.get("SUM"));
                    }
                }
                result.add(sumMap);

            }
        }
        return result;

    }

    //判断权限，获取当前登录用户的deptId，如果是全部查询则返回0
    public String getDeptID(String userId){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        Object userInformation = hash.get("UserInformation", userId);
        if(userInformation==null){
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
        String roletype = (String) jsonObject.get("ROLETYPE");
        if(roletype==null){
            return null;
        }
        if("0".equals(roletype)){
            //0是查询全部
            return "0";
        }else if("1".equals(roletype)||"2".equals(roletype)){
            return (String) jsonObject.get("DEPTID");
        }else{
            return "-1";
        }
    }

    /**
     * 查询照片
     */
    public Object pictureInfo(String taskId, Integer type,Integer warningType) {
        List<Map<String, Object>> result = new ArrayList<>();
        if(type==1){
            if(warningType==5){
                String sql = "select YCDATA from XS_ZC_EXCEPTION WHERE TASK_ID=?1";
                List<Map<String, Object>> maps = execSql(sql, taskId);
                /*maps.forEach(map ->{
                    Object ycdata = map.get("YCDATA");
                    JSONArray objects = JSONObject.parseArray(ycdata.toString());
                    for (int i=0;i<objects.size();i++){
                        Map<String,Object> m = (Map<String, Object>) objects.get(i);
                        String id = (String) m.get("ID");
                        String sql1 = "select FILE_PATH,PROCESS_NAME AS OPERATE_NAME ,CREATE_TIME from PICTURE_TOUR where PROCESS_ID =?1";
                        List<Map<String, Object>> maps1 = execSql(sql1, id);
                        result.addAll(maps1);
                    }
                });*/
                StringBuffer ids = new StringBuffer();
                for(int j = 0;j<maps.size();j++){
                    Map<String, Object> map = maps.get(j);
                    Object ycdata = map.get("YCDATA");
                    JSONArray objects = JSONObject.parseArray(ycdata.toString());
                    for (int i=0;i<objects.size();i++){
                        Map<String,Object> m = (Map<String, Object>) objects.get(i);
                        String id = (String) m.get("ID");
                        if(i!=objects.size()-1){
                            ids.append(id+", ");
                        }else{
                            ids.append(id);
                        }
                    }
                    if(j!=maps.size()-1){
                        ids.append(",");
                    }
                }
                String sql1 = "select FILE_PATH,PROCESS_NAME AS OPERATE_NAME ,CREATE_TIME from PICTURE_TOUR where PROCESS_ID in ("+ids.toString()+")";
                List<Map<String, Object>> maps1 = execSql(sql1);
                result.addAll(maps1);

            }
        }
        return result;
    }



}