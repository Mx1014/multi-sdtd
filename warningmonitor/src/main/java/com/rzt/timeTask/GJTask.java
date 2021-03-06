package com.rzt.timeTask;

import com.rzt.entity.Monitorcheckyj;
import com.rzt.repository.AlarmOfflineRepository;
import com.rzt.repository.Monitorcheckejrepository;
import com.rzt.repository.Monitorcheckyjrepository;
import com.rzt.service.CurdService;
import com.rzt.service.RedisService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 统一定时使用
 * Created by huyuening on 2018/1/12.
 */
@Component
public class GJTask  extends CurdService<Monitorcheckyj, Monitorcheckyjrepository> {

    @Autowired
    private KHGJ khgj;

    @Autowired
    private Monitorcheckejrepository resp;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    JedisPool pool;

    @Autowired
    private AlarmOfflineRepository offline;


    //定时拉数据  1
    @Scheduled(cron = "0 30 0 * * ? ")
    public void gjTask(){
        khgj.inspectionMissionOverdue();// 巡视超期任务
        khgj.XSWJRW(); //未按时接任务
        khgj.XSWSX();  //人员未上线
        khgj.KHWSX();  //看护未上线
        khgj.KHWKH();  //未按时间接任务

        khgj.JCOutOfTime();  //稽查超期
        //khgj.JCWsx();  //稽查未上线
        khgj.JCWdxc();  //稽查未到达现场

    }

    /**
     * 查询无GPS信号的离线人员
     */
    //十分钟一查
    @Scheduled(fixedDelay = 600000)
    public void lixianTask(){

        ZSetOperations<String, Object> zSet = redisTemplate.opsForZSet();
        long currentTimeMillis = System.currentTimeMillis();
        Set<Object> lixian = zSet.rangeByScore("currentUser", currentTimeMillis - 6000000l, currentTimeMillis - 5400000l);

        //存库、删除redis、置为离线状态
        for (Object userId:lixian){
            String sql = " SELECT * FROM RZTSYSUSER WHERE ID=?1 ";
            try {
                Map<String, Object> map = execSqlSingleResult(sql, userId);
                Integer taskType=Integer.parseInt(map.get("WORKTYPE").toString());
                if(taskType==1){
                    taskType=2;
                }else if(taskType==2){
                    taskType=1;
                }
                //插入告警记录
                int flag = lixian((String) userId, taskType);

                if(flag>0){
                    //置为离线状态
                    userQuit((String) userId);
                }
                zSet.remove("currentUser",userId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 退出操作
     * @param userId
     */
    public void userQuit(String userId){
        String userAccout = "SELECT * FROM USERINFO where id=?1";
        try {
            this.resp.quitUserLOGINSTATUS(userId);
            this.resp.updateMonitorCheckEjUserLoginType(0,userId);
            Map<String, Object> stringObjectMap = this.execSqlSingleResult(userAccout, userId);
            HashOperations hashOperations = redisTemplate.opsForHash();
            hashOperations.put("UserInformation", userId, stringObjectMap);
            hashOperations.delete("USERTOKEN", stringObjectMap.get("ID"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int lixian(String userId,Integer taskType){
        String sql = "";
        if(taskType==2){
            sql = " SELECT kh.ID,d.ID AS  DEPTID,kh.PLAN_START_TIME,kh.PLAN_END_TIME, kh.TASK_NAME,kh.USER_ID FROM  KH_TASK kh  LEFT JOIN RZTSYSDEPARTMENT d " +
                    " ON kh.TDYW_ORG = d.DEPTNAME WHERE trunc(kh.PLAN_START_TIME) = trunc(sysdate) AND kh.USER_ID =?1  AND kh.STATUS !=2 AND kh.STATUS !=3";
        }else if(taskType==1){
            sql = "SELECT ID,TD_ORG as DEPTID,PLAN_START_TIME,TASK_NAME,CM_USER_ID,PLAN_END_TIME,STAUTS  " +
                    "FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND CM_USER_ID=?1  AND STAUTS !=2 AND IS_DELETE=0";
        }else if(taskType==3){
          /*  sql=" SELECT t.ID,t.USER_ID,t.TASK_NAME,t.PLAN_START_TIME,t.PLAN_END_TIME,u.DEPTID FROM CHECK_LIVE_TASK t " +
                    " LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID " +
                    " WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND STATUS!=2 AND USER_ID=?1";*/
            sql = " SELECT t.ID,t.USER_ID,t.TASK_NAME,t.PLAN_START_TIME,t.PLAN_END_TIME,u.DEPTID FROM CHECK_LIVE_TASK t " +
                    "   LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID " +
                    "  WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND STATUS!=2  AND USER_ID=?1" +
                    " UNION ALL " +
                    " SELECT t.ID,t.USER_ID,t.TASK_NAME,t.PLAN_START_TIME,t.PLAN_END_TIME,u.DEPTID FROM CHECK_LIVE_TASKSB t " +
                    "      LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID " +
                    "      WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND STATUS!=2  AND USER_ID=?1" +
                    " UNION ALL  " +
                    " SELECT t.ID,t.USER_ID,t.TASK_NAME,d.PLAN_START_TIME,d.PLAN_END_TIME,u.DEPTID FROM CHECK_LIVE_TASKXS t " +
                    "      LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID LEFT JOIN CHECK_LIVE_TASK_DETAILXS d ON d.TASK_ID=t.ID " +
                    "      WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND t.STATUS!=2  AND USER_ID=?1";
        }
        List<Map<String, Object>> maps = execSql(sql,userId);
        //如果查询结果为0，证明这个人当天没有任务
        if(maps.size()==0){
            return 0;
        }
        String sql2 = " SELECT LOGINSTATUS FROM RZTSYSUSER WHERE ID=?1 ";
        List<Map<String, Object>> maps2 = execSql(sql2, userId);
        if(maps2.size()>0){
            Integer loginstatus = Integer.parseInt(maps2.get(0).get("LOGINSTATUS").toString());
            if(loginstatus==0){
                return 0;
            }
        }
        int flag = 0;
        for (Map<String, Object> map:maps) {
            //开始时间
            Date plan_start_time = (Date) map.get("PLAN_START_TIME");
            //结束时间
            Date plan_end_time = (Date) map.get("PLAN_END_TIME");
            String reason = "无GPS信号";
            try {
                Long startDate = plan_start_time.getTime();
                Long endDate = plan_end_time.getTime();
                Long currentDate = new Date().getTime();
                if((currentDate-startDate)>5400000){

                    if(startDate<currentDate && currentDate<endDate){
                        if(flag==0){
                            String sql1="";
                            if(taskType==2){
                                sql1=" SELECT   ID  " +
                                        " FROM PICTURE_KH  " +
                                        " WHERE TASK_ID =?1 AND CREATE_TIME BETWEEN sysdate-90/(24*60) AND sysdate+10/(24*60)";
                            }else if(taskType==1){
                                sql1 = "SELECT   ID " +
                                        "FROM PICTURE_TOUR " +
                                        "WHERE TASK_ID =?1 AND CREATE_TIME BETWEEN sysdate-90/(24*60) AND sysdate+10/(24*60)";
                            }else{
                                continue;
                            }
                            Object id = map.get("ID");
                            List<Map<String, Object>> maps1 = execSql(sql1, id);
                            if(maps1.size()>0){
                                continue;
                            }
                            flag++;
                            String key = "";
                            if (taskType==2){
                                key = "ONE+"+map.get("ID")+"+2+8+"+map.get("USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME")+"+"+reason;
                                resp.saveCheckEjWdw(SnowflakeIdWorker.getInstance(20,14).nextId(),Long.valueOf(map.get("ID").toString()),2,8,map.get("USER_ID").toString(),map.get("DEPTID").toString(),map.get("TASK_NAME").toString(),reason);
                                lixianRedis(map.get("USER_ID").toString());
                            }else if (taskType==1){
                                key = "ONE+"+map.get("ID")+"+1+2+"+map.get("CM_USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME")+"+"+reason;
                                resp.saveCheckEjWdw(SnowflakeIdWorker.getInstance(20,14).nextId(),Long.valueOf(map.get("ID").toString()),1,2,map.get("CM_USER_ID").toString(),map.get("DEPTID").toString(),map.get("TASK_NAME").toString(),reason);
                                lixianRedis(map.get("CM_USER_ID").toString());
                            }else if(taskType==3){
                                key = "ONE+"+map.get("ID")+"+3+13+"+map.get("USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME")+"+"+reason;
                                resp.saveCheckEjWdw(SnowflakeIdWorker.getInstance(20,14).nextId(),Long.valueOf(map.get("ID").toString()),3,13,map.get("USER_ID").toString(),map.get("DEPTID").toString(),map.get("TASK_NAME").toString(),reason);
                                lixianRedis(map.get("USER_ID").toString());
                            }
                            redisService.setex(key);

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }


    public void lixianRedis(String userId){
        Jedis jedis = null;
        try {
            String sql = "SELECT * FROM ALARM_OFFLINE WHERE USER_ID=?1 AND trunc(CREATE_TIME)=trunc(sysdate)";
            List<Map<String, Object>> maps = execSql(sql, userId);
            Date date = new Date();
            Long timeLong = 5400000l; //延迟之后报的警，所以告警产生时就已经离线90分钟
            if(maps.size()==0){//如果ALARM_OFFLINE表中没有数据，则进行添加
                //向ALARM_OFFLINE中添加数据
                offline.addoffLine(SnowflakeIdWorker.getInstance(10,10).nextId(),userId,timeLong,date);
            }else{ //如果已经存在，则只更细时长和次数

                Integer frequency = Integer.parseInt(maps.get(0).get("OFFLINE_FREQUENCY").toString())+1;
                timeLong = Long.parseLong(maps.get(0).get("OFFLINE_TIME_LONG").toString())+timeLong;
                offline.updateoffLine(Long.parseLong(maps.get(0).get("ID").toString()),frequency,timeLong,date);
            }

            jedis = pool.getResource();
            jedis.select(5);
            jedis.hset("lixian",userId,String.valueOf(date.getTime()));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

}
