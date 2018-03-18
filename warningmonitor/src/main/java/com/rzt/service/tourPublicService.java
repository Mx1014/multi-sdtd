package com.rzt.service;

import com.rzt.entity.Monitorcheckej;
import com.rzt.repository.Monitorcheckejrepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

@Service
public class tourPublicService extends CurdService<Monitorcheckej, Monitorcheckejrepository> {
    @Autowired
    JedisPool jedisPool;

    @Autowired
    private Monitorcheckejrepository resp;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 巡视人员未到杆塔半径5米范围内
     */
    @Transactional(rollbackFor = Exception.class)
    public WebApiResponse xsTourScope(Long taskid, String userid,String reason,Long execDetailId) {
        try {
            //查询任务的所有塔的个数
            /*String sql1 = "SELECT c.SECTION FROM XS_ZC_TASK x RIGHT JOIN  XS_ZC_CYCLE c ON x.XS_ZC_CYCLE_ID=c.ID WHERE x.ID=?1";
            Map<String, Object> map1 = execSqlSingleResult(sql1, taskid);
            String section = (String) map1.get("SECTION");
            String[] split = section.split("-");
            Double sum = 0.0;
            if(split.length>1){
                sum = Double.parseDouble(split[1])-Double.parseDouble(split[0])+1;
            }
            Double d = 0.0;
            if(sum>0){
                //查询该任务不到位的个数
                String sql2 = "SELECT count(1) AS count FROM XS_ZC_TASK_EXEC_DETAIL d " +
                        "  LEFT JOIN XS_ZC_TASK_EXEC e ON d.XS_ZC_TASK_EXEC_ID=e.ID WHERE IS_DW=1  AND e.XS_ZC_TASK_ID=?1";
                List<Map<String, Object>> maps = execSql(sql2, taskid);
                if(maps.size()>0){
                    Double isDWNum =  Double.parseDouble(maps.get(0).get("COUNT").toString());
                    d = isDWNum / sum;
                }
            }*/
            String sql1 = "SELECT\n" +
                    "  nvl(sum(decode(IS_DW, 1, 1, 0)),0) wdw,\n" +
                    "  count(1)                    total,\n" +
                    "  t.XS_ZC_TASK_EXEC_ID\n" +
                    "FROM XS_ZC_TASK_EXEC_DETAIL t\n" +
                    "WHERE exists(SELECT id\n" +
                    "             FROM XS_ZC_TASK_EXEC_DETAIL tt\n" +
                    "             WHERE t.XS_ZC_TASK_EXEC_ID = tt.XS_ZC_TASK_EXEC_ID AND tt.id\n" +
                    "=?1  AND t.END_TOWER_ID = 0)\n" +
                    "GROUP BY t.XS_ZC_TASK_EXEC_ID";
            List<Map<String, Object>> maps1 = execSql(sql1, execDetailId);
            Double d = 0.0;
            if(maps1.size()>0){
               Double wdw = Double.parseDouble(maps1.get(0).get("WDW").toString());
               Double total = Double.parseDouble(maps1.get(0).get("TOTAL").toString());
               d = wdw/total;
            }

            Long xsZcTaskExecId = Long.parseLong(maps1.get(0).get("XS_ZC_TASK_EXEC_ID").toString());
            //如果大于等于0.3则插入告警
            if(d>=0.3){
                //查询是否已经插入告警
                String sql3 = " SELECT ID FROM MONITOR_CHECK_EJ WHERE TASK_ID=?1 AND WARNING_TYPE=3 AND trunc(CREATE_TIME)=trunc(sysdate) ";
                List<Map<String, Object>> maps = execSql(sql3, taskid);
                if(maps.size()==0){
                    String sql = "   SELECT TASK_NAME AS TASKNAME,TD_ORG FROM XS_ZC_TASK WHERE ID=?1 ";
                    Map<String, Object> map = this.execSqlSingleResult(sql, taskid);
                    //往二级单位插数据
                    resp.saveCheckEjWdwExec(SnowflakeIdWorker.getInstance(10, 12).nextId(),taskid,1,3,userid,map.get("TD_ORG").toString(),map.get("TASKNAME").toString(),reason,xsZcTaskExecId);
                    String key = "ONE+" + taskid + "+1+3+" + userid + "+" + map.get("TD_ORG").toString() + "+" + map.get("TASKNAME").toString()+"+"+reason+"+"+execDetailId;
                    redisService.setex(key);
                }

                String value = new Date().getTime()+"#"+userid+"#"+Integer.parseInt(maps1.get(0).get("WDW").toString())+"#"+reason+"#"+xsZcTaskExecId+"#"+0;
                budaoweiRedis(taskid,value);
            }
            return WebApiResponse.success("");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro"+e.getMessage());
        }
    }
    //往redis中扔不到位数据
    private void budaoweiRedis(Long taskId,String value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(5);
            jedis.hset("budaowei",String.valueOf(taskId),value);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }



    //看护脱岗 给脱岗用
    public void KHTG(String userId,Long taskId){
        String sql = "SELECT kh.TASK_NAME,kh.YWORG_ID AS TDYW_ORG FROM KH_TASK kh WHERE kh.ID =?1";
        try {
            Map<String, Object> map = execSqlSingleResult(sql, taskId);
            //直接存到二级单位
            resp.saveCheckEj(SnowflakeIdWorker.getInstance(0,0).nextId(),taskId,2,7,userId,map.get("TDYW_ORG").toString(),map.get("TASK_NAME").toString());
            String key = "ONE+"+taskId+"+2+7+"+userId+"+"+map.get("TDYW_ORG").toString()+"+"+map.get("TASK_NAME").toString();
            redisService.setex(key);
        } catch (Exception e) {
            e.getMessage();
            //throw new RuntimeException(e.getMessage()+"看护脱岗");
        }
    }

    //看护未到位
    public void khWFDW(Long taskid, String userid) {
        try {
            String sql = "   SELECT  kh.TASK_NAME AS TASKNAME,kh.YWORG_ID AS TDYW_ORG,nvl(kh.REASON,'未填写') AS REASON  FROM KH_TASK kh WHERE kh.ID=?1 ";
            Map<String, Object> map = this.execSqlSingleResult(sql, taskid);
            //往二级单位插入未到位
            resp.saveCheckEjWdw(SnowflakeIdWorker.getInstance(10, 12).nextId(),taskid,2,11,userid,map.get("TDYW_ORG").toString(),map.get("TASKNAME").toString(),map.get("REASON"));
            String key = "ONE+" + taskid + "+2+11+" + userid + "+" + map.get("TDYW_ORG").toString() + "+" + map.get("TASKNAME").toString()+"+"+map.get("REASON").toString();

            redisService.setex(key);
        } catch (Exception e) {
        }
    }

    //看护/巡视未上线 给下线用
    //下线时如果该用户在任务时间段内，就把该用户放入redis准备往一级推，并且直接往二级查数据
    public void KHXX(String userId,Integer taskType,Integer typeReason){
        String sql = "";

        if(taskType==1){
            //看护
            sql = " SELECT kh.ID,d.ID AS  DEPTID,kh.PLAN_START_TIME,kh.PLAN_END_TIME, kh.TASK_NAME,kh.USER_ID,kh.STATUS AS STATUS FROM  KH_TASK kh  LEFT JOIN RZTSYSDEPARTMENT d " +
                    " ON kh.TDYW_ORG = d.DEPTNAME WHERE trunc(kh.PLAN_START_TIME) = trunc(sysdate) AND kh.USER_ID =?1  AND kh.STATUS !=2 AND kh.STATUS !=3";
        }else if(taskType==2){
            //巡视
            sql = "SELECT ID,TD_ORG as DEPTID,PLAN_START_TIME,TASK_NAME,CM_USER_ID,PLAN_END_TIME,STAUTS AS STATUS  " +
                    "FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND CM_USER_ID=?1  AND STAUTS !=2";
        }else if(taskType==3){
            //现场稽查
         /*   sql=" SELECT t.ID,t.USER_ID,t.TASK_NAME,t.PLAN_START_TIME,t.PLAN_END_TIME,u.DEPTID FROM CHECK_LIVE_TASK t " +
                    " LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID " +
                    " WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND STATUS!=2  AND USER_ID=?1";*/

            sql=" SELECT t.ID,t.USER_ID,t.TASK_NAME,t.PLAN_START_TIME,u.DEPTID,t.STATUS FROM CHECK_LIVE_TASK t " +
                    "   LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID " +
                    "  WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND t.STATUS!=2  AND t.USER_ID=?1" +
                    "UNION ALL " +
                    " SELECT t.ID,t.USER_ID,t.TASK_NAME,t.PLAN_START_TIME,u.DEPTID,t.STATUS FROM CHECK_LIVE_TASKSB t " +
                    "      LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID " +
                    "      WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND t.STATUS!=2  AND t.USER_ID=?1" +
                    "UNION ALL  " +
                    " SELECT t.ID,t.USER_ID,t.TASK_NAME,d.PLAN_START_TIME,u.DEPTID,t.STATUS FROM CHECK_LIVE_TASKXS t " +
                    "      LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID LEFT JOIN CHECK_LIVE_TASK_DETAILXS d ON d.TASK_ID=t.ID " +
                    "      WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND t.STATUS!=2  AND t.USER_ID=?1";
        }
        List<Map<String, Object>> maps = execSql(sql,userId);
        //如果查询结果为0，证明这个人当天没有任务
        if(maps.size()==0){
            return;
        }
        Integer falg=0;
        for (Map<String, Object> map:maps) {
            //开始时间
            Date plan_start_time = (Date) map.get("PLAN_START_TIME");
            //结束时间
            Date plan_end_time = (Date) map.get("PLAN_END_TIME");
            Object status = map.get("STATUS");

            try {
                Long startDate = plan_start_time.getTime();
                Long endDate = plan_end_time.getTime();
                Long currentDate = new Date().getTime();
                String reason="";
                if(typeReason==0){
                    //手动退出
                    reason="手动退出";
                }else if(typeReason==1){
                    reason="90分钟无操作";
                }else{
                    reason="90分钟";
                }

                if(startDate<currentDate && currentDate<endDate){
                    //如果用户在任务时间内退出登录，则直接往往二级推，并设置往一级推送时间
                    if(falg==0){
                        String key = "";
                        if (taskType==1){
                            key = "ONE+"+map.get("ID")+"+2+8+"+map.get("USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME")+"+"+reason;
                            resp.saveCheckEjWdw(SnowflakeIdWorker.getInstance(20,14).nextId(),Long.valueOf(map.get("ID").toString()),2,8,map.get("USER_ID").toString(),map.get("DEPTID").toString(),map.get("TASK_NAME").toString(),reason);
                        }else if (taskType==2){
                            key = "ONE+"+map.get("ID")+"+1+2+"+map.get("CM_USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME")+"+"+reason;
                            resp.saveCheckEjWdw(SnowflakeIdWorker.getInstance(20,14).nextId(),Long.valueOf(map.get("ID").toString()),1,2,map.get("CM_USER_ID").toString(),map.get("DEPTID").toString(),map.get("TASK_NAME").toString(),reason);
                        }else if(taskType==3){
                            key = "ONE+"+map.get("ID")+"+3+13+"+map.get("USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME")+"+"+reason;
                            resp.saveCheckEjWdw(SnowflakeIdWorker.getInstance(20,14).nextId(),Long.valueOf(map.get("ID").toString()),3,13,map.get("USER_ID").toString(),map.get("DEPTID").toString(),map.get("TASK_NAME").toString(),reason);
                        }
                        redisService.setex(key);
                        falg++;
                    }
                }else if(new Date().getTime()<startDate && "0".equals(status.toString())){
                    //如果还未接单，则重新设置redis
                    String r = "未上线";
                    String key = "";
                    if (taskType==1){
                        key = "TWO+"+map.get("ID")+"+2+8+"+map.get("USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME")+"+"+r;
                    }else if (taskType==2){
                        key = "TWO+"+map.get("ID")+"+1+2+"+map.get("CM_USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME")+"+"+r;
                    }else if(taskType==3){
                        key = "TWO+"+map.get("ID")+"+3+13+"+map.get("USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME")+"+"+r;
                    }
                    Long time = plan_start_time.getTime() - new Date().getTime();
                    time = time+5400000L;
                    redisService.psetex(key,time);
                }
            } catch (Exception e) {
                //System.out.println(e.getMessage());
                //throw new RuntimeException(e.getMessage()+"看护/巡视未上线");
            }
        }
    }

    //看护/巡视上线  taskType任务类型，1巡视 2看护 3现场稽查
    //上线后就把redis中的值删掉，不删往一级推的键
    public void KHSX(String userId,Integer taskType){
        String sql = "";
        if(taskType==2){
            sql = " SELECT kh.ID,d.ID as DEPTID,kh.PLAN_START_TIME,kh.PLAN_END_TIME, kh.TASK_NAME,kh.USER_ID FROM  KH_TASK kh   LEFT JOIN RZTSYSDEPARTMENT d " +
                    " ON kh.TDYW_ORG = d.DEPTNAME  WHERE trunc(kh.PLAN_START_TIME) = trunc(sysdate) AND kh.USER_ID =?1 AND kh.STATUS !=2 AND kh.STATUS !=3";
        }else if(taskType==1){
            sql = "SELECT ID,TD_ORG as DEPTID,PLAN_START_TIME,TASK_NAME,CM_USER_ID,PLAN_END_TIME  " +
                    "FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND CM_USER_ID=?1 AND STAUTS !=2";
        }else if(taskType==3){
            /*sql=" SELECT t.ID,t.USER_ID,t.TASK_NAME,t.PLAN_START_TIME,t.PLAN_END_TIME,u.DEPTID FROM CHECK_LIVE_TASK t " +
                    " LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID " +
                    " WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND STATUS!=2";*/
            sql = " SELECT t.ID,t.USER_ID,t.TASK_NAME,t.PLAN_START_TIME,t.PLAN_END_TIME,u.DEPTID FROM CHECK_LIVE_TASK t " +
                    "   LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID " +
                    "  WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND STATUS!=2 " +
                    " UNION ALL " +
                    " SELECT t.ID,t.USER_ID,t.TASK_NAME,t.PLAN_START_TIME,t.PLAN_END_TIME,u.DEPTID FROM CHECK_LIVE_TASKSB t " +
                    "      LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID " +
                    "      WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND STATUS!=2 " +
                    " UNION ALL  " +
                    " SELECT t.ID,t.USER_ID,t.TASK_NAME,d.PLAN_START_TIME,d.PLAN_END_TIME,u.DEPTID FROM CHECK_LIVE_TASKXS t " +
                    "      LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID LEFT JOIN CHECK_LIVE_TASK_DETAILXS d ON d.TASK_ID=t.ID " +
                    "      WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND t.STATUS!=2";
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
                    /*if(taskType==2){
                        key = "TWO+"+map.get("ID")+"+2+8+"+map.get("USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME");
                    }else if(taskType==1){
                        key = "TWO+"+map.get("ID")+"+1+2+"+map.get("CM_USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME");
                    }else if(taskType==3){
                        key = "TWO+"+map.get("ID")+"+3+13+"+map.get("USER_ID")+"+"+map.get("DEPTID")+"+"+map.get("TASK_NAME");
                    }*/
                    if(taskType==2){
                        key = "TWO+*+2+8+"+map.get("USER_ID")+"+"+map.get("DEPTID")+"+*";
                    }else if(taskType==1){
                        key = "TWO+*+1+2+"+map.get("CM_USER_ID")+"+"+map.get("DEPTID")+"+*";
                    }else if(taskType==3){
                        key = "TWO+*+3+13+"+map.get("USER_ID")+"+"+map.get("DEPTID")+"+*";
                    }
                    removeKey(key);
                   resp.updateOnlineTime(userId,Long.parseLong(map.get("ID").toString()));
                }
            } catch (Exception e) {
                //System.out.println(e.getMessage());
                //throw new RuntimeException(e.getMessage()+"看护/巡视上线");
            }finally {
                jedis.close();
            }
        }
    }
    public void removeKey(String s){
        //String s = "TWO+*+2+8+*";
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
    }

    //未按标准拍照
    public void takePhoto(Long taskid, String userid) {

        try {
            Map<String, Object> map = null;
            String sql = "   SELECT TASK_NAME AS TASKNAME,TD_ORG FROM XS_ZC_TASK WHERE ID=? ";
            map = this.execSqlSingleResult(sql, taskid);
            //往二级单位插数据
            resp.saveCheckEj(SnowflakeIdWorker.getInstance(10, 12).nextId(),taskid,1,5,userid,map.get("TD_ORG").toString(),map.get("TASKNAME").toString());
            String key = "ONE+" + taskid + "+1+5+" + userid + "+" + map.get("TD_ORG").toString() + "+" + map.get("TASKNAME").toString();
            redisService.setex(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除键  巡视是1 看护是2
    public void delKey(String userId, Long taskId, Integer taskType) {
        String key = "";
        if(1==taskType){
            String sql = "SELECT TASK_NAME,TD_ORG FROM XS_ZC_TASK WHERE ID=?1";
            List<Map<String, Object>> maps = execSql(sql, taskId);
            if(maps.size()>0){
                key = "TWO+"+taskId+"+"+taskType+"+4+"+userId+maps.get(0).get("TD_ORG").toString()+maps.get(0).get("TASK_NAME").toString();
            }
        }else if(2==taskType){
            String sql = "SELECT kh.TASK_NAME,d.ID as TDYW_ORG FROM KH_TASK  kh LEFT JOIN RZTSYSDEPARTMENT d " +
                    " ON kh.TDYW_ORG = d.DEPTNAME  WHERE kh.ID=?1";
            List<Map<String, Object>> maps = execSql(sql, taskId);
            if(maps.size()>0){
                key = "TWO+"+taskId+"+"+taskType+"+10+"+userId+maps.get(0).get("TDYW_ORG").toString()+maps.get(0).get("TASK_NAME").toString();
            }
        }
        redisService.delKey(key);
    }


    public Object khtgang(Long taskId) {
        String sql="SELECT * FROM WARNING_OFF_POST_USER_TIME WHERE FK_TASK_ID=?1";
        List<Map<String, Object>> maps = execSql(sql, taskId);

        return maps;
    }


    /**
     * ri
     * @param
     * @param fileType
     * @return
     */
    public Map<String,Object> getDocBytaskId(Integer page,Integer size, String startDate,String endDate, Integer fileType) {
        Map<String, Object> result = new HashMap<>();
        Pageable pageable = new PageRequest(page, size);
        List<Object> list = new ArrayList();
        String s = "";

        if(fileType!=null){
            list.add(fileType);
            s+=" AND　FILE_TYPE=?"+list.size();
        }else{
            s+=" AND  (FILE_TYPE=4 OR FILE_TYPE=5)";
        }
        if(!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)){
            /*list.add(date);
            s+=" AND trunc(to_date(?"+list.size()+",'yyyy-mm-dd hh24:mi:ss')) =trunc(CREATE_TIME) ";*/
            list.add(startDate);
            s += " AND  trunc(CREATE_TIME) BETWEEN trunc(to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss')) ";
            list.add(endDate);
            s += "  AND trunc(to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss')) ";
        }else{
            s+=" AND trunc(CREATE_TIME) BETWEEN trunc(sysdate-15) and trunc(sysdate)";
        }
        String sql = " SELECT * FROM PICTURE_JC WHERE 1=1"+s+" ORDER BY CREATE_TIME DESC ";
        //List<Map<String, Object>> maps = execSql(sql, list.toArray());
        Page<Map<String, Object>> maps1 = execSqlPage(pageable, sql, list.toArray());
        result.put("success",true);
        result.put("object",maps1);
        return result;
    }
}
