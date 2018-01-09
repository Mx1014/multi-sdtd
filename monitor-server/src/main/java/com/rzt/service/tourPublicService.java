package com.rzt.service;

import com.rzt.entity.CheckResult;
import com.rzt.repository.CheckResultRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@Service
public class tourPublicService extends CurdService<CheckResult, CheckResultRepository> {
    @Autowired
    JedisPool jedisPool;

    /**
     * 巡视人员未到杆塔半径5米范围内
     *
     * @param taskid
     * @param orgid
     * @param userid
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public WebApiResponse xsTourScope(Long taskid, Integer warningtype, String orgid, String userid) {
//        RedisService redisService = new RedisService();
        try {
            String sql = "   SELECT TASK_NAME AS TASKNAME FROM XS_ZC_TASK WHERE ID=? ";
            Map<String, Object> map = this.execSqlSingleResult(sql, taskid);
            this.reposiotry.xsTourScope(new SnowflakeIdWorker(10, 12).nextId(), taskid, warningtype, orgid, userid);
//            redisService.setex("ONE+" + taskid + "+2+" + warningtype + "+" + userid + "+" + orgid + "+" + map.get("TASKNAME"));
            String key = "ONE+" + taskid + "+2+" + warningtype + "+" + userid + "+" + orgid + "+" + map.get("TASKNAME");
            Jedis jedis = jedisPool.getResource();
            jedis.select(1);
            try {
                jedis.setex(key, 1, " ");
            } catch (Exception e) {
//                LOGGER.error("redis定时失败：" + e.getMessage());
//                System.out.println("redis定时失败：" + e.getMessage());
            }
            return WebApiResponse.success("");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }
}
