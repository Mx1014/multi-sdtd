package com.rzt.timeTask;

import com.rzt.entity.AlarmOffline;
import com.rzt.entity.Monitorcheckyj;
import com.rzt.repository.AlarmOfflineRepository;
import com.rzt.repository.Monitorcheckyjrepository;
import com.rzt.service.CurdService;
import com.rzt.utils.SnowflakeIdWorker;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Component
public class TimeTask  extends CurdService<AlarmOffline, AlarmOfflineRepository> {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private AlarmOfflineRepository offline;

    @Autowired
    private JedisPool pool;

    //定时1分钟
    @Scheduled(fixedDelay = 30000)
    public void lixiantask(){
        Jedis jedis = pool.getResource();
        jedis.select(5);
        Map<String, String> lixian = jedis.hgetAll("lixian");
        Set<String> strings = lixian.keySet();
        for (String userId:strings) {
            String value = lixian.get(userId);
            String[] split = value.split("#");
            long start = Long.parseLong(split[0]);
            long current = new Date().getTime();

            String sql = "SELECT * FROM ALARM_OFFLINE WHERE USER_ID=?1 AND trunc(CREATE_TIME)=trunc(sysdate)";
            List<Map<String, Object>> maps = execSql(sql, userId);

            if(Integer.parseInt(split[1])==0){ //如果是0，则证明是第一次添加进来
                lixianRedis(userId,String.valueOf(start));
                Long timeLong = current-start+5400000l;
                Date date = new Date(start);
                if(maps.size()>0){
                    Integer frequency = Integer.parseInt(maps.get(0).get("OFFLINE_FREQUENCY").toString())+1; //离线次数
                    timeLong = Long.parseLong(maps.get(0).get("OFFLINE_TIME_LONG").toString())+timeLong; //离线时长
                    offline.updateoffLine(Long.parseLong(maps.get(0).get("ID").toString()),frequency,timeLong,date);
                }else{
                    offline.addoffLine(SnowflakeIdWorker.getInstance(10,10).nextId(),userId,timeLong,date);
                }
            }else{//如果是1，则证明是已经存在的，不增加次数，只更新时长
                Date last_flush_time = (Date) maps.get(0).get("LAST_FLUSH_TIME");
                Long timeLong = current-last_flush_time.getTime()+Long.parseLong(maps.get(0).get("OFFLINE_TIME_LONG").toString());
                offline.updateoffLineTime(userId,timeLong);
            }

        }
        jedis.close();

    }

    //定时1分钟
    @Scheduled(fixedDelay = 30000)
    public void budaoweitask(){
        Jedis jedis = pool.getResource();
        jedis.select(5);
        Map<String, String> budaowei = jedis.hgetAll("budaowei");
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
                offline.addBuDaoWei(SnowflakeIdWorker.getInstance(10,10).nextId(),warningTime,Long.parseLong(taskId),split[1], Integer.parseInt(split[2]),split[3]);
            }
        }
    }

    private void lixianRedis(String userId,String value){
        Jedis jedis = pool.getResource();
        jedis.select(5);
        jedis.hset("lixian",userId,value+"#1");
        jedis.close();
    }

}
