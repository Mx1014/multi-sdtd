/**    
 * 文件名：WarningOffPostUserService           
 * 版本信息：    
 * 日期：2017/12/27 03:58:05    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.entity.OffPostUser;
import com.rzt.repository.OffPostUserRepository;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：WarningOffPostUserService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/27 03:58:05 
 * 修改人：张虎成    
 * 修改时间：2017/12/27 03:58:05    
 * 修改备注：    
 * @version        
 */
@Service
public class WarningOffPostUserService extends CurdService<OffPostUser, OffPostUserRepository> {

    private static int faixTime = 2400;
    @Autowired
    private OffPostUserRepository warning;

    public OffPostUser findByUserIdAndTaskId(String userId, Long taskId) {
        return warning.findByUserIdAndTaskId(userId,taskId);
    }

    @Transactional
    public void updateOffUser(OffPostUser offUser) {
        warning.saveAndFlush(offUser);
    }
    @Transactional
    public void addUser(OffPostUser offPostUser){
        offPostUser.setId(SnowflakeIdWorker.getInstance(0,0).nextId());
        warning.save(offPostUser);
    }

    /**
     * 根据userIds，查询看护脱岗人员信息
     * @param userIds
     * @return
     */
    public List<Map<String, Object>> KHPostUserInfo(String userIds) {
        String[] arrUserId = null;
        if(!StringUtils.isEmpty(userIds)){
            arrUserId = userIds.split(",");
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (String userId:arrUserId) {
            //时长和脱岗次数
            String sql = "SELECT count(kh_time.id) times, " +
                    "sum(ROUND(TO_NUMBER(nvl(kh_time.END_TIME,sysdate) - kh_time.START_TIME) * 24 * 60 * 60)) timeLong " +
                    "FROM WARNING_OFF_POST_USER_TIME kh_time " +
                    "WHERE kh_time.FK_USER_ID = ?1";
            List<Map<String, Object>> maps = execSql(sql, userId);
            result.addAll(maps);
            //当天看护脱岗任务信息
            String sql2 = "SELECT " +
                    "  kh.TASK_NAME, " +
                    "  kh.WX_ORG, " +
                    "  kh.TDYW_ORG, " +
                    "  kh.STATUS " +
                    "FROM KH_TASK kh RIGHT JOIN WARNING_OFF_POST_USER u ON u.TASK_ID=kh.ID " +
                    " WHERE kh.USER_ID = ?1 and trunc(kh.PLAN_START_TIME) = trunc(sysdate)";
            List<Map<String, Object>> maps1 = execSql(sql2, userId);
            if(result.size()>0){
                result.get(0).putAll(maps1.get(0));
            }else{
                result.addAll(maps1);
            }
            //看护脱岗人员信息、部门
            String sql3 = "SELECT u.USERNAME,u.PHONE,d.DEPTNAME,c.COMPANYNAME,c.ORGNAME " +
                    "FROM RZTSYSUSER u LEFT JOIN RZTSYSDEPARTMENT d on d.DEPTPID = u.DEPTID and d.ORGTYPE = 5 " +
                    "LEFT JOIN RZTSYSCOMPANY c ON c.ID = u.CLASSNAME " +
                    "WHERE u.ID = ?1 AND u.USERDELETE = 1 AND u.WORKTYPE = 1";
            List<Map<String, Object>> maps2 = execSql(sql3, userId);
            if(result.size()>0){
                result.get(0).putAll(maps2.get(0));
            }else{
                result.addAll(maps2);
            }
        }
        return result;
    }


    public int updateTimeStatus(Object fk_task_id, Object fk_user_id,Long id) {
       return reposiotry.updateTimeStatus(fk_task_id,fk_user_id,id);
    }


    public void KHTG(String userId,Long taskId){
        String sql = "SELECT kh.TASK_NAME,kh.YWORG_ID AS TDYW_ORG FROM KH_TASK kh WHERE kh.ID =?1";
        try {
            Map<String, Object> map = execSqlSingleResult(sql, taskId);
            //直接存到二级单位
            reposiotry.saveCheckEj(SnowflakeIdWorker.getInstance(0,0).nextId(),taskId,2,7,userId,map.get("TDYW_ORG").toString(),map.get("TASK_NAME").toString());
            String key = "ONE+"+taskId+"+2+7+"+userId+"+"+map.get("TDYW_ORG").toString()+"+"+map.get("TASK_NAME").toString();
            setex(key);

        } catch (Exception e) {
            e.getMessage();
            //throw new RuntimeException(e.getMessage()+"看护脱岗");
        }
    }

    @Autowired
    JedisPool jedisPool;

    public void setex(String key){
        //redisTemplate.
        Jedis jedis = jedisPool.getResource();
        jedis.select(1);//这里应该是1
        try {
            jedis.setex(key, faixTime, " ");
        } catch (Exception e) {
        }finally {
            jedis.close();
        }
    }

    public void tuoGangRedis(String userId,Long taskId,Date offWorkTime){ //offWorkTime为脱岗开始时间
        String sql = "SELECT * FROM ALARM_OFFWORK WHERE USER_ID=?1 AND TASK_ID=?2 AND trunc(ALARM_TIME)=trunc(sysdate)";
        List<Map<String, Object>> maps = execSql(sql, userId,taskId);

        Date current = new Date();
        Long timeLong = current.getTime()-offWorkTime.getTime();

        if(maps.size()==0){//如果ALARM_OFFWORK表中没有数据，则进行添加

            //向ALARM_OFFWORK中添加数据
            warning.addoffWork(SnowflakeIdWorker.getInstance(10,10).nextId(),userId,timeLong,offWorkTime,taskId);

        }else{ //如果已经存在，则只更新时长和次数 和最新一次的脱岗时间

            Integer frequency = Integer.parseInt(maps.get(0).get("OFFWORK_FREQUENCY").toString())+1; //脱岗次数
            timeLong = Long.parseLong(maps.get(0).get("OFFWORK_TIME_LONG").toString())+timeLong; //脱岗时长
            warning.updateoffWork(Long.parseLong(maps.get(0).get("ID").toString()),frequency,timeLong,offWorkTime);
        }
        String key = userId+"#"+taskId;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(5);
            jedis.hset("tuogang",key,String.valueOf(offWorkTime.getTime()));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    //人员回岗后，将ALARM_OFFWORK中的状态置为回岗
    public void updateAlarmOffWorkStatus(String userId, Long taskId) {
        reposiotry.updateAlarmOffWorkStatus(userId,taskId);
    }
}