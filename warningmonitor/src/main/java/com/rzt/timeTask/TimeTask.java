package com.rzt.timeTask;

import com.rzt.entity.AlarmOffline;
import com.rzt.entity.Monitorcheckyj;
import com.rzt.repository.AlarmOfflineRepository;
import com.rzt.repository.Monitorcheckyjrepository;
import com.rzt.service.CurdService;
import com.rzt.utils.SnowflakeIdWorker;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.SimpleDateFormat;
import java.util.*;


@Component
public class TimeTask  extends CurdService<AlarmOffline, AlarmOfflineRepository> {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private AlarmOfflineRepository offline;

    @Autowired
    JedisPool pool;

    //定时1分钟
    @Scheduled(fixedDelay = 60000)
    public void lixiantask(){
        Jedis jedis = null;
        Map<String, String> lixian = new HashMap<>();
        try {
            jedis = pool.getResource();
            jedis.select(5);
            lixian = jedis.hgetAll("lixian");

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        Set<String> strings = lixian.keySet();
        for (String userId:strings) {
            String value = lixian.get(userId); //value 是离线时间
            long current = new Date().getTime();
            Date offLineTime = new Date(Long.parseLong(value));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String offLine = format.format(offLineTime);
            String sql = "SELECT * FROM ALARM_OFFLINE WHERE USER_ID=?1 AND OFFLINE_END_TIME=to_date( ?2,'yyyy-MM-dd hh24:mi:ss')";
            List<Map<String, Object>> maps = execSql(sql, userId,offLine);

            if(maps.size()>0){
                //只更新时长
                Date last_flush_time = (Date) maps.get(0).get("LAST_FLUSH_TIME");
                Long timeLong = current-last_flush_time.getTime()+Long.parseLong(maps.get(0).get("OFFLINE_TIME_LONG").toString());
                offline.updateoffLineTime(userId,timeLong);
            }else{
                Long timeLong = current-offLineTime.getTime()+5400000l;
                offline.addoffLine(SnowflakeIdWorker.getInstance(10,10).nextId(),userId,timeLong,offLineTime);
                System.out.println("ALARM_OFFLINE添加时没添加进去"+userId);
            }

        }


    }

    //定时1分钟
    //@Scheduled(fixedDelay = 60000)
  /*  public void budaoweitask(){
        Jedis jedis = null;
        Map<String, String> budaowei = new HashMap<>();
        try {
            jedis = pool.getResource();
            jedis.select(5);
            budaowei = jedis.hgetAll("budaowei");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        Set<String> strings = budaowei.keySet();
        for (String taskId:strings) {
            String sql = "SELECT * FROM ALARM_UNQUALIFIEDPATROL WHERE TASK_ID=?1 AND trunc(CREATE_TIME)=trunc(sysdate)";
            List<Map<String, Object>> maps = execSql(sql, taskId);
            String value = budaowei.get(taskId);
            String[] split = value.split("#");

            if(maps.size()>0){
                //如果表中有了，则只更新不到位的塔的个数
                Integer tour = Integer.parseInt(split[2]);
                Integer isDwTour = Integer.parseInt(maps.get(0).get("IS_DW_TOUR").toString());
                if(isDwTour!=tour){
                    offline.updateBuDaoWeiTour(taskId,tour);
                }
            }else{
                Date warningTime = new Date(Long.parseLong(split[0]));
                offline.addBuDaoWei(SnowflakeIdWorker.getInstance(10,10).nextId(),warningTime,Long.parseLong(taskId),split[1], Integer.parseInt(split[2]),split[3],Long.parseLong(split[4]),0);
            }
        }
    }*/

    public void lixianRedis(String userId,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.select(5);
            jedis.hset("lixian",userId,value+"#1");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }
    //脱岗标示
    public void tuoGangRedis(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.select(5);
            jedis.hset("tuogang",key,value+"#1");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    //脱岗定时
    @Scheduled(fixedDelay = 60000)
    public void tuogangTask(){
        Jedis jedis = null;
        Map<String, String> tuogang = new HashMap<>();
        try {
            jedis = pool.getResource();
            jedis.select(5);
            tuogang = jedis.hgetAll("tuogang");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        Set<String> strings = tuogang.keySet();
        for (String key:strings) {
            String value = tuogang.get(key);
            String[] keys = key.split("#");
            String userId = keys[0];
            Long taskId = Long.parseLong(keys[1]);

            long start = Long.parseLong(value); //脱岗时间
            Date offWorkTime = new Date(start);
            long current = new Date().getTime();


            String sql = "SELECT o.*,t.STATUS FROM ALARM_OFFWORK o LEFT JOIN KH_TASK t ON o.TASK_ID=t.ID WHERE o.USER_ID=?1 " +
                    "    AND TASK_ID=?2 AND OFFWORK_TIME=?3";
            List<Map<String, Object>> maps = execSql(sql, userId,taskId,offWorkTime);

            if(maps.size()>0){
                if("2".equals(maps.get(0).get("STATUS").toString())){
                    //如果任务完成了，则将redis删除掉
                    removeTuoGangRedis(key);
                }else{
                    Date last_flush_time = (Date) maps.get(0).get("LAST_FLUSH_TIME");
                    Long timeLong = current-last_flush_time.getTime()+Long.parseLong(maps.get(0).get("OFFWORK_TIME_LONG").toString());
                    offline.updateoffWorkTime(userId,timeLong,taskId);
                }
            }else{
                System.out.println("ALARM_OFFWORK添加时没添加进去"+userId+"==="+taskId);
            }


        }

    }

    public void removeTuoGangRedis(String s) {
        RedisConnection connection = null;
        try {
            connection = redisTemplate.getConnectionFactory().getConnection();
            connection.select(5);
            byte[] bytes = "tuogang".getBytes();
            connection.hDel(bytes,s.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

}
