package com.rzt.timeTask;

import com.rzt.entity.Monitorcheckyj;
import com.rzt.repository.Monitorcheckejrepository;
import com.rzt.repository.Monitorcheckyjrepository;
import com.rzt.service.CurdService;
import com.rzt.service.Monitorcheckyjservice;
import com.rzt.service.RedisService;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 定时拉取数据使用
 * Created by huyuening on 2018/1/10.
 */
@Service
public class KHGJ extends CurdService<Monitorcheckyj, Monitorcheckyjrepository> {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private RedisService redisService;

    @Autowired
    private Monitorcheckejrepository resp;

    //超期任务
    public void inspectionMissionOverdue() {
        String sql = " SELECT ID,TASK_NAME,CM_USER_ID,TD_ORG  FROM XS_ZC_TASK  WHERE PLAN_END_TIME BETWEEN trunc(sysdate-1) and trunc(sysdate) AND STAUTS !=2 ";
        List<Map<String, Object>> list = this.execSql(sql);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            if(resp!=null)
                resp.saveCheckEj(new SnowflakeIdWorker(0,0).nextId(),Long.valueOf(map.get("ID").toString()),1,1,map.get("CM_USER_ID").toString(),map.get("TD_ORG").toString(),map.get("TASK_NAME").toString());
            String key = "ONE+"+map.get("ID").toString()+"+1+1+"+map.get("CM_USER_ID")+"+"+map.get("TD_ORG")+"+"+map.get("TASK_NAME");
            redisService.setex(key);
        }
    }

    //看护未上线  给定时拉取数据用
    public void KHWSX(){
        String sql = " SELECT kh.ID,d.ID AS TDYW_ORG,kh.PLAN_START_TIME,kh.TASK_NAME,kh.USER_ID FROM  KH_TASK kh LEFT JOIN RZTSYSDEPARTMENT d " +
                " ON kh.TDYW_ORG = d.DEPTNAME WHERE trunc(kh.PLAN_START_TIME) = trunc(sysdate)";
        List<Map<String, Object>> maps = execSql(sql);
        for (Map<String, Object> map:maps) {
            Jedis jedis = jedisPool.getResource();
            String key = "TWO+"+map.get("ID")+"+2+8+"+map.get("USER_ID")+"+"+map.get("TDYW_ORG")+"+"+map.get("TASK_NAME");
            jedis.select(1);
            Date plan_start_time = (Date) map.get("PLAN_START_TIME");
            try {
                Long time = plan_start_time.getTime() - new Date().getTime();
                jedis.psetex(key,time,"看护未上线");
            } catch (Exception e) {
                //System.out.println(e.getMessage());
                //throw new RuntimeException(e.getMessage()+"看护未上线");
            }finally {
                jedis.close();
            }
        }
    }
    //巡视未上线 给定时拉取数据用
    public void XSWSX(){
        String sql="SELECT ID,PLAN_START_TIME,TASK_NAME,CM_USER_ID,PLAN_END_TIME,TD_ORG FROM XS_ZC_TASK " +
                "WHERE  trunc(PLAN_START_TIME) = trunc(sysdate)  ";
        List<Map<String, Object>> maps = execSql(sql);
        maps.forEach(map ->{
            Jedis jedis = jedisPool.getResource();
            String key = "TWO+"+map.get("ID")+"+1+2+"+map.get("CM_USER_ID")+"+"+map.get("TD_ORG")+"+"+map.get("TASK_NAME");
            jedis.select(1);
            Date plan_start_time = (Date) map.get("PLAN_START_TIME");
            try {
                Long time = plan_start_time.getTime() - new Date().getTime();
                jedis.psetex(key,time,"巡视未上线");
            } catch (Exception e) {
                //System.out.println(e.getMessage());
                //throw new RuntimeException(e.getMessage()+"巡视未上线");
            }finally {
                jedis.close();
            }
        });
    }
    /**
     * 看护人员未按规定时间看护任务 定时拉取数据用
     */
    public void KHWKH() {
        String sql = " SELECT kh.ID,d.ID AS TDYW_ORG,kh.PLAN_START_TIME,kh.TASK_NAME,kh.USER_ID FROM  KH_TASK kh LEFT JOIN RZTSYSDEPARTMENT  d" +
                "    ON kh.TDYW_ORG = d.DEPTNAME WHERE trunc(kh.PLAN_START_TIME) = trunc(sysdate) AND REAL_START_TIME IS NULL";
        List<Object> list = new ArrayList<>();
        List<Map<String, Object>> maps = execSql(sql);
        for (Map<String, Object> map : maps) {
            Jedis jedis = jedisPool.getResource();
            String key = "TWO+" + map.get("ID") + "+2+10+" + map.get("USER_ID") + "+" + map.get("TDYW_ORG") + "+" + map.get("TASK_NAME");
            jedis.select(1);
            Date plan_start_time = (Date) map.get("PLAN_START_TIME");
            try {
                Long time = plan_start_time.getTime() - new Date().getTime();
                if(time<0){
                    list.add(map.get("ID"));
                    continue;
                }
                jedis.psetex(key, time, "看护人员未按规定时间看护任务");
            } catch (Exception e) {
                //System.out.println(e.getMessage());
                //throw new RuntimeException(e.getMessage()+"看护人员未按规定时间接任务");
            }finally {
                jedis.close();
            }
        }

        //判断在0点到拉数据时间段内有无未按时接任务的
        if(list.size()>0)
            for(Object obj:list){
                String sql1 = "SELECT kh.ID,d.ID AS TDYW_ORG,kh.PLAN_START_TIME,kh.TASK_NAME,kh.USER_ID FROM  KH_TASK kh LEFT JOIN RZTSYSDEPARTMENT d  " +
                        " ON kh.TDYW_ORG = d.DEPTNAME WHERE kh.PLAN_START_TIME<nvl(kh.REAL_START_TIME,sysdate) AND kh.ID=?1";
                List<Map<String, Object>> maps1 = execSql(sql1, obj);
                if(maps1.size()>0){
                    Map<String, Object> map = maps1.get(0);
                    resp.saveCheckEj(new SnowflakeIdWorker(0,0).nextId(),Long.valueOf(map.get("ID").toString()),2,10,map.get("USER_ID").toString(),map.get("TDYW_ORG").toString(),map.get("TASK_NAME").toString());
                    String key = "ONE+"+map.get("ID").toString()+"+2+10+"+map.get("USER_ID")+"+"+map.get("TDYW_ORG")+"+"+map.get("TASK_NAME");
                    redisService.setex(key);
                }
            }
    }
    @Autowired
    private Monitorcheckyjservice monitorcheckyj;
    //巡视未按规定时间接任务 定时拉去数据用
    public void XSWJRW(){
        String sql = "SELECT ID,TD_ORG,PLAN_START_TIME,CM_USER_ID,TASK_NAME FROM XS_ZC_TASK WHERE PLAN_END_TIME >= trunc(SYSDATE) AND PLAN_START_TIME <= trunc(sysdate + 1)  AND REAL_START_TIME IS NULL";
        List<Map<String, Object>> maps = execSql(sql);
        List<Object> list = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            Jedis jedis = jedisPool.getResource();
            String key = "TWO+" + map.get("ID") + "+1+4+" + map.get("CM_USER_ID") + "+" + map.get("TD_ORG") + "+" + map.get("TASK_NAME");
            jedis.select(1);
            Date plan_start_time = (Date) map.get("PLAN_START_TIME");
            try {
                Long time = plan_start_time.getTime() - new Date().getTime();
                if(time<0){
                    list.add(map.get("ID"));
                    continue;
                }
                jedis.psetex(key, time, "巡视未按规定时间接任务");
            } catch (Exception e) {
                //System.out.println(e.getMessage());
                //throw new RuntimeException(e.getMessage()+"巡视人员未按规定时间接任务");
            }finally {
                jedis.close();
            }
        }

        //判断在0点到拉数据时间段内有无未按时接任务的
        if(list.size()>0) {
            for (Object obj : list) {
                String sql1 = "SELECT ID,TD_ORG,PLAN_START_TIME,CM_USER_ID,TASK_NAME FROM XS_ZC_TASK WHERE PLAN_START_TIME<nvl(REAL_START_TIME,sysdate) AND ID=?1";
                List<Map<String, Object>> maps1 = execSql(sql1, obj);
                if (maps1.size() > 0) {
                    Map<String, Object> map = maps1.get(0);
                    resp.saveCheckEj(new SnowflakeIdWorker(0, 0).nextId(), Long.valueOf(map.get("ID").toString()), 1, 4, map.get("CM_USER_ID").toString(), map.get("TD_ORG").toString(), map.get("TASK_NAME").toString());
                    String key = "ONE+"+map.get("ID").toString()+"+1+4+"+map.get("CM_USER_ID")+"+"+map.get("TD_ORG")+"+"+map.get("TASK_NAME");
                    redisService.setex(key);
                    /*String[] message = new String[7];
                    message[0] = "ONE";
                    message[1] = map.get("ID").toString();
                    message[2] = "1";
                    message[3] = "4";
                    message[4] = map.get("CM_USER_ID").toString();
                    message[5] = map.get("TD_ORG").toString();
                    message[6] = map.get("TASK_NAME").toString();

                    monitorcheckyj.saveCheckYj(message);*/
                }
            }
        }
        //System.out.println("结束------------------------");

    }







}
