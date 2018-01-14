package com.rzt.service;

import com.rzt.entity.Monitorcheckej;
import com.rzt.repository.Monitorcheckejrepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class tourPublicService extends CurdService<Monitorcheckej, Monitorcheckejrepository> {
    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private Monitorcheckejrepository resp;

    @Autowired
    private RedisService redisService;

    /**
     * 巡视人员未到杆塔半径5米范围内
     */
    @Transactional(rollbackFor = Exception.class)
    public WebApiResponse xsTourScope(Long taskid, String orgid, String userid) {
        try {
            String sql = "   SELECT TASK_NAME AS TASKNAME FROM XS_ZC_TASK WHERE ID=? ";
            Map<String, Object> map = this.execSqlSingleResult(sql, taskid);
            //往二级单位插数据
            resp.saveCheckEj(new SnowflakeIdWorker(10, 12).nextId(),taskid,1,3,userid,orgid,map.get("TASKNAME").toString());
            String key = "ONE+" + taskid + "+1+3+" + userid + "+" + orgid + "+" + map.get("TASKNAME").toString();

            redisService.setex(key);
            return WebApiResponse.success("");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }



    //看护脱岗 给脱岗用
    public void KHTG(String userId,Long taskId){
        String sql = "SELECT TASK_NAME,TDYW_ORG FROM KH_TASK WHERE ID =?2 AND USER_ID=?1";
        try {
            Map<String, Object> map = execSqlSingleResult(sql, userId, taskId);
            //直接存到二级单位
            resp.saveCheckEj(new SnowflakeIdWorker(0,0).nextId(),taskId,2,7,userId,map.get("TDYW_ORG").toString(),map.get("TASK_NAME").toString());
            String key = "ONE+"+taskId+"+2+7+"+userId+"+"+map.get("TDYW_ORG").toString()+"+"+map.get("TASK_NAME").toString();
            redisService.setex(key);
        } catch (Exception e) {
            e.getMessage();
            //throw new RuntimeException(e.getMessage()+"看护脱岗");
        }
    }


    //看护/巡视未上线 给下线用
    //下线时如果该用户在任务时间段内，就把该用户放入redis准备往一级推，并且直接往二级查数据
    public void KHXX(String userId,Integer taskType){
        String sql = "";
        if(taskType==2){
            sql = " SELECT kh.ID,kh.TDYW_ORG as DEPTID,kh.PLAN_START_TIME,kh.PLAN_END_TIME, kh.TASK_NAME,kh.USER_ID FROM  KH_TASK kh  " +
                    "WHERE trunc(kh.PLAN_START_TIME) = trunc(sysdate) AND kh.USER_ID =?1 ";
        }else if(taskType==1){
            sql = "SELECT ID,TD_ORG as DEPTID,PLAN_START_TIME,TASK_NAME,CM_USER_ID,PLAN_END_TIME  " +
                    "FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND CM_USER_ID=?1";
        }
        List<Map<String, Object>> maps = execSql(sql,userId);
        //如果查询结果为0，证明这个人当天没有任务
        if(maps.size()==0){
            return;
        }
        for (Map<String, Object> map:maps) {
            //开始时间
            Date plan_start_time = (Date) map.get("PLAN_START_TIME");
            //结束时间
            Date plan_end_time = (Date) map.get("PLAN_END_TIME");

            try {
                Long startDate = plan_start_time.getTime();
                Long endDate = plan_end_time.getTime();
                Long currentDate = new Date().getTime();
                if(startDate<currentDate && currentDate<endDate){
                    //如果用户在任务时间内退出登录，则直接往往二级推，并设置往一级推送时间
                    String key = "";
                    if (taskType==2){
                        key = "ONE+"+map.get("ID")+"+2+8+"+map.get("USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME");
                        resp.saveCheckEj(new SnowflakeIdWorker(0,0).nextId(),Long.valueOf(map.get("ID").toString()),2,8,map.get("USER_ID").toString(),map.get("DEPTID").toString(),map.get("TASK_NAME").toString());
                    }else if (taskType==1){
                        key = "ONE+"+map.get("ID")+"+1+2+"+map.get("CM_USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME");
                        resp.saveCheckEj(new SnowflakeIdWorker(0,0).nextId(),Long.valueOf(map.get("ID").toString()),1,2,map.get("CM_USER_ID").toString(),map.get("DEPTID").toString(),map.get("TASK_NAME").toString());
                    }
                    redisService.setex(key);
                }else if(new Date().getTime()<startDate){
                    String key = "";
                    if (taskType==2){
                        key = "TWO+"+map.get("ID")+"+2+8+"+map.get("USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME");
                        //resp.saveCheckEj(new SnowflakeIdWorker(0,0).nextId(),Long.valueOf(map.get("ID").toString()),2,8,map.get("USER_ID").toString(),map.get("DEPTID").toString(),map.get("TASK_NAME").toString());
                    }else if (taskType==1){
                        key = "TWO+"+map.get("ID")+"+1+2+"+map.get("CM_USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME");
                        //resp.saveCheckEj(new SnowflakeIdWorker(0,0).nextId(),Long.valueOf(map.get("ID").toString()),1,2,map.get("CM_USER_ID").toString(),map.get("DEPTID").toString(),map.get("TASK_NAME").toString());
                    }
                    Long time = plan_start_time.getTime() - new Date().getTime();
                    redisService.psetex(key,time);
                }
            } catch (Exception e) {
                //System.out.println(e.getMessage());
                //throw new RuntimeException(e.getMessage()+"看护/巡视未上线");
            }
        }
    }

    //看护/巡视上线  taskType任务类型，1巡视 2看护
    //上线后就把redis中的值删掉，不删往一级推的键
    public void KHSX(String userId,Integer taskType){
        String sql = "";
        if(taskType==2){
            sql = " SELECT kh.ID,kh.TDYW_ORG as DEPTID,kh.PLAN_START_TIME,kh.PLAN_END_TIME, kh.TASK_NAME,kh.USER_ID FROM  KH_TASK kh  " +
                    "WHERE trunc(kh.PLAN_START_TIME) = trunc(sysdate) AND kh.USER_ID =?1 ";
        }else if(taskType==1){
            sql = "SELECT ID,TD_ORG as DEPTID,PLAN_START_TIME,TASK_NAME,CM_USER_ID,PLAN_END_TIME  " +
                    "FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND CM_USER_ID=?1";
        }

        List<Map<String, Object>> maps = execSql(sql,userId);
        //如果查询结果为0，证明这个人当天没有任务
        if(maps.size()==0){
            return;
        }
        for (Map<String, Object> map:maps) {
            Jedis jedis = jedisPool.getResource();
            jedis.select(1);
            //开始时间
            Date plan_start_time = (Date) map.get("PLAN_START_TIME");
            //结束时间
            Date plan_end_time = (Date) map.get("PLAN_END_TIME");
            try {
                Long startDate = plan_start_time.getTime();
                Long endDate = plan_end_time.getTime();
                Long currentDate = new Date().getTime();
                if(currentDate<endDate){
                    //主要是删除定时拉取数据，存放在redis中的key
                    String key = "";
                    if(taskType==2){
                        key = "TWO+"+map.get("ID")+"+2+8+"+map.get("USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME");
                    }else if(taskType==1){
                        key = "TWO+"+map.get("ID")+"+1+2+"+map.get("CM_USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME");
                    }
                    jedis.del(key);
                }
            } catch (Exception e) {
                //System.out.println(e.getMessage());
                //throw new RuntimeException(e.getMessage()+"看护/巡视上线");
            }finally {
                jedis.close();
            }
        }
    }

    public void takePhoto(Long taskid, String orgid, String userid) {

        try {
            Map<String, Object> map = null;
            String sql = "   SELECT TASK_NAME AS TASKNAME FROM XS_ZC_TASK WHERE ID=? ";
            map = this.execSqlSingleResult(sql, taskid);
            //往二级单位插数据
            resp.saveCheckEj(new SnowflakeIdWorker(10, 12).nextId(),taskid,1,5,userid,orgid,map.get("TASKNAME").toString());
            String key = "ONE+" + taskid + "+1+5+" + userid + "+" + orgid + "+" + map.get("TASKNAME").toString();
            redisService.setex(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
