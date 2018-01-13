package com.rzt.timeTask;

import com.rzt.entity.Monitorcheckej;
import com.rzt.repository.Monitorcheckejrepository;
import com.rzt.service.CurdService;
import com.rzt.service.RedisService;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;

@Service
public class AlarmScheduledTask extends CurdService<Monitorcheckej, Monitorcheckejrepository> {
    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private RedisService redisService;

    @Autowired
    private Monitorcheckejrepository repo;

    //超期任务
    public void inspectionMissionOverdue() {
        String sql = " SELECT ID,TASK_NAME,CM_USER_ID,TD_ORG  FROM XS_ZC_TASK  WHERE PLAN_END_TIME BETWEEN trunc(sysdate-1) and trunc(sysdate) AND STAUTS !=2 ";
        List<Map<String, Object>> list = this.execSql(sql);
        for (int i = 0; i < list.size(); i++) {
            /*Jedis jedis = jedisPool.getResource();
            jedis.select(1);*/
            Map<String, Object> map = list.get(i);
            if(repo!=null)
               repo.saveCheckEj(new SnowflakeIdWorker(0,0).nextId(),Long.valueOf(map.get("ID").toString()),1,1,map.get("CM_USER_ID").toString(),map.get("TD_ORG").toString(),map.get("TASK_NAME").toString());
            String key = "ONE+"+map.get("ID").toString()+"+1+1+"+map.get("CM_USER_ID")+"+"+map.get("TD_ORG")+"+"+map.get("TASK_NAME");
            redisService.setex(key);
            //jedisPool.returnResource(jedis);
        }
    }

    //未按规定时间接任务
    //@Scheduled(cron = "0/10 * *  * * ? ")
    /*public void notReceivingATaskAtASpecifiedTime() {
        String sql = " SELECT ID as taskid,TASK_NAME as taskname,CM_USER_ID as userid,TD_ORG as orgid,PLAN_START_TIME as plantime FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) ";
        List<Map<String, Object>> list = this.execSql(sql);
        for (int i = 0; i < list.size(); i++) {
            String key = "TWO+" + list.get(i).get("TASKID") + "+1+4+" + list.get(i).get("USERID") + "+" + list.get(i).get("ORGID") + "+" + list.get(i).get("TASKNAME");
            Jedis jedis = jedisPool.getResource();
            jedis.select(1);
            //try {
                //Date plantime = (Date) list.get(i).get("plantime");
                //Long time = plantime.getTime() - new Date().getTime();
                jedis.setex(key, 1, "未按规定时间接任务");
            //} catch (Exception e) {
              //  throw new RuntimeException(e.getMessage());
            //}
        }
    }*/
}
