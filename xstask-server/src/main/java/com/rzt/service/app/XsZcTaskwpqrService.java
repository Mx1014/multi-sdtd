package com.rzt.service.app;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.app.XsZcTaskwpqr;
import com.rzt.eureka.WarningmonitorServerService;
import com.rzt.repository.app.XsZcTaskwpqrRepository;
import com.rzt.service.CurdService;
import com.rzt.utils.DateUtil;
import com.rzt.utils.SnowflakeIdWorker;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.service.app
 * @Author: liuze
 * @date: 2017-12-7 19:43
 */
@Service
public class XsZcTaskwpqrService extends CurdService<XsZcTaskwpqr, XsZcTaskwpqrRepository> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    WarningmonitorServerService warningmonitorServerService;

    /***
     * @Method updateJdTime
     * @Description\
     * @param [id, xslx]
     * @return void
     * @date 2017/12/17 15:18
     * @author nwz
     */
    @Transactional
    public void updateJdTime(Long id, Integer xslx, String userId) {
        if (xslx == 0 || xslx == 1) {
            this.reposiotry.bdtxJiedan(id);//更新接单时间
            this.reposiotry.bdtxUpdateTaskStatus(id);//更新任务状态
            this.reposiotry.updateMonitorCheckEjXsJxz(id);//更新告警任务状态
            this.reposiotry.updateTxbdTaskZxys(1, id);//更新执行页数
        } else {
            this.reposiotry.zcXsJiedan(id);//更新接单时间
            this.reposiotry.zcXsUpdateTaskStatus(id);//更新任务状态
            this.reposiotry.updateMonitorCheckEjXsJxz(id);//更新告警任务状态
            this.reposiotry.updateXszcTaskZxys(1, id);//更新执行页数
            //更新redis中的人员 任务的状态
            updateXsMenInfoInRedis(userId);
            //remove掉月宁那边的key
            removeSomeKey(id);
        }
    }

    public void removeSomeKey(Long id) {
        String s = "TWO+" + id + "+1+4*";
        RedisConnection connection = null;
        try {
            connection = redisTemplate.getConnectionFactory().getConnection();
            connection.select(1);
            Set<byte[]> keys = connection.keys(s.getBytes());
            byte[][] ts = keys.toArray(new byte[][]{});
            if (ts.length > 0) {
                connection.del(ts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    public void updateXsMenInfoInRedis(String userId) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String key = "xsMenAll:" + DateUtil.dateToString(new Date()).split(" ")[0];
        Object xsMenAllString = valueOperations.get(key);
        if (xsMenAllString == null) {
            return;
        } else {
            JSONObject xsMenAll = JSONObject.parseObject(xsMenAllString.toString());
            String sql = "SELECT cm_user_id,min(stauts) status from XS_ZC_TASK where PLAN_END_TIME >= trunc(sysdate) and  PLAN_START_TIME <= trunc(sysdate+1) and cm_user_id = ? group by cm_user_id";
            try {
                Map<String, Object> map = this.execSqlSingleResult(sql, userId);
                Object status = map.get("STATUS");
                xsMenAll.put(userId, status);
                valueOperations.set(key, xsMenAll);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视
     * 实际开始时间 ,巡视开始时间 ,身份确认时间 更改时间
     *
     * @param id
     * @param xslx
     * @return
     */
    public int updateSfqrTime(Long id, Integer xslx) {
        if (xslx == 0 || xslx == 1) {
            this.reposiotry.updateTxbdTaskZxys(2, id);//更新执行页数
            return this.reposiotry.bdXsSfqrTime(id);//更新身份确认时间
        } else {
            this.reposiotry.updateXszcTaskZxys(2, id);//更新执行页数
            return this.reposiotry.zxXsSfqrTime(id);//更新身份确认时间
        }
    }

    /**
     * 物品提醒 先查询如果有就修改没有就存一条 返回int
     *
     * @param taskID   任务ID
     * @param rwZt     物品存档
     * @param xslx     0 特巡 1 保电 2 巡视
     * @param wptxTime
     * @return
     */
    public void articlesReminding(Long taskID, String rwZt, Integer xslx, Date wptxTime) {
        int zero = 0, one = 1, two = 2;
        try {
            if (xslx == 0 || xslx == 1) {
                String sql = "select id,taskid,wp_zt from xs_zc_txbdwpqr where taskid = ?";
                this.execSqlSingleResult(sql, taskID);
                this.reposiotry.updateBdtxArticles(rwZt, taskID);
            } else if (xslx == 2) {
                String sql = "select id,taskid,wp_zt from xs_zc_taskwpqr where taskid = ?";
                this.execSqlSingleResult(sql, taskID);
                this.reposiotry.updateZcxsArticles(rwZt, taskID);
            }
        } catch (Exception e) {
            if (xslx == 0 || xslx == 1) {
                this.reposiotry.insertBdtxArticles(new SnowflakeIdWorker(12, 20).nextId(), taskID, rwZt);
                this.reposiotry.bdxsWptsTimeUpdate(taskID);
                this.reposiotry.updateTxbdTaskZxys(3, taskID);//更新执行页数
            } else if (xslx == 2) {
                this.reposiotry.insertZcxsArticles(new SnowflakeIdWorker(13, 20).nextId(), taskID, rwZt);
                this.reposiotry.zcxsWptsTimeUpdate(taskID);
                this.reposiotry.updateXszcTaskZxys(3, taskID);//更新执行页数
            }
        }
    }


    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视 id 任务ID
     * 到达现场更改时间
     *
     * @param xslx
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    public Object reachSpot(Integer xslx, Long id) throws Exception {
        long nextId = new SnowflakeIdWorker(18, 20).nextId();
        String bdtxSql = "SELECT ID,ddxc_time AS ddxc FROM xs_txbd_task WHERE id = ?1";
        String zcxsSql = "SELECT ID, ddxc_time AS ddxc FROM xs_zc_task   WHERE id = ?1";
        Map<String, Object> task = null;
        if (xslx == 0 || xslx == 1) {
            task = this.execSqlSingleResult(bdtxSql, id);
            if (task.get("DDXC") != null) {
                return "已经又第一轮的数据了";
            }
            this.reposiotry.bdXsDdxcTime(id, 4);
            this.reposiotry.insertdtxTaskExec(nextId, id, 1);
            addExecDetail(id, xslx, nextId);
        } else if (xslx == 2) {
            task = this.execSqlSingleResult(zcxsSql, id);
            if (task.get("DDXC") != null) {
                return "已经又第一轮的数据了";
            }
            this.reposiotry.zcXsDdxcTime(id, 4);
            this.reposiotry.insertZcXsTaskExec(nextId, id, 1);
            addExecDetail(id, xslx, nextId);

        }
        return nextId;
    }

    /***
     * @Method addExecDetail
     * @Description 添加每一轮所有的轮详情数据
     * @param [taskId, xslx, execId]
     * @return void
     * @date 2017/12/18 19:05
     * @author nwz
     */
    public void addExecDetail(Long taskId, Integer xslx, Long execId) throws Exception {
        Map<String, Object> map = null;
        List<Map<String, Object>> xsTowers = null;
        if (xslx == 0 || xslx == 1) {
            String xszcCycleIdSql = "select XS_txbd_CYCLE_ID cycel FROM xs_txbd_task where id = ?";
            map = this.execSqlSingleResult(xszcCycleIdSql, taskId);
            Long cycleId = Long.parseLong(map.get("CYCEL").toString());
            String xsTowerListSql = "SELECT id,tower_name\n" +
                    "FROM CM_LINE_TOWER\n" +
                    "WHERE tower_id IN (SELECT cm_line_tower_id\n" +
                    "             FROM XS_txbd_CYCLE_LINE_TOWER\n" +
                    "             WHERE XS_txbd_CYCLE_ID = ?)\n" +
                    "ORDER BY sort";
            xsTowers = this.execSql(xsTowerListSql, cycleId);
            Long jiandandewo = Long.valueOf(0);
            Long preTdId = null;
            String preTdName = null;
            for (Map<String, Object> xsTower : xsTowers) {
                String tower_name = xsTower.get("TOWER_NAME").toString();
                long towerId = Long.parseLong(xsTower.get("ID").toString());
                if (preTdId != null) {
                    long nextId1 = new SnowflakeIdWorker(18, 25).nextId();
                    this.reposiotry.insertTxbdExecDetail(nextId1, execId, preTdName + "-" + tower_name, preTdId, towerId);
                }
                long nextId = new SnowflakeIdWorker(18, 25).nextId();
                this.reposiotry.insertTxbdExecDetail(nextId, execId, tower_name, towerId, jiandandewo);
                preTdId = towerId;
                preTdName = tower_name;
            }

        } else {
            String xszcCycleIdSql = "select xs_zc_CYCLE_ID cycel FROM xs_zc_task where id = ?";
            map = this.execSqlSingleResult(xszcCycleIdSql, taskId);
            Long cycleId = Long.parseLong(map.get("CYCEL").toString());

            String xsTowerListSql = "SELECT t.tower_id id,t.LINE_NAME || '-' ||t.tower_name tower_name\n" +
                    "FROM (select * from  CM_LINE_TOWER t WHERE t.tower_id IN (SELECT cm_line_tower_id\n" +
                    "             FROM XS_zc_CYCLE_LINE_TOWER\n" +
                    "             WHERE XS_zc_CYCLE_ID = ?) ) t \n" +
                    "ORDER BY sort";
            xsTowers = this.execSql(xsTowerListSql, cycleId);
            Long jiandandewo = Long.valueOf(0);//我只是一个简单的long类型的0
            Long preTdId = null;
            String preTdName = null;
            for (Map<String, Object> xsTower : xsTowers) {
                String tower_name = xsTower.get("TOWER_NAME").toString();
                long towerId = Long.parseLong(xsTower.get("ID").toString());
                if (preTdId != null) {
                    long nextId1 = new SnowflakeIdWorker(18, 25).nextId();
                    this.reposiotry.insertZcxsExecDetail(nextId1, execId, preTdName + "-" + tower_name, preTdId, towerId);
                }
                long nextId = new SnowflakeIdWorker(18, 25).nextId();
                this.reposiotry.insertZcxsExecDetail(nextId, execId, tower_name, towerId, jiandandewo);
                preTdId = towerId;
                preTdName = tower_name;
            }
        }
    }

    /**
     * @param [xslx,       execId]
     * @param execId
     * @param gznr
     * @param startTowerId
     * @return java.lang.Object
     * @Method insertExecDetail
     * @Description 插入每轮巡视的每一步的数据
     * @date 2017/12/18 11:18
     * @author nwz
     */
    public Object insertExecDetail(Integer xslx, Long execId, String gznr, Long startTowerId, Long endTowerId) {
        long nextId = new SnowflakeIdWorker(18, 20).nextId();
        if (xslx == 0 || xslx == 1) {
            this.reposiotry.insertTxbdExecDetail(nextId, execId, gznr, startTowerId, endTowerId);
        } else {
            this.reposiotry.insertZcxsExecDetail(nextId, execId, gznr, startTowerId, endTowerId);
        }
        return nextId;

    }

    /***
     * @Method updateExecDetail
     * @Description 更新轮详情的数据
     * @param [xslx, sfdw, reason, execDetailId]
     * @return void
     * @date 2017/12/18 19:08
     * @author nwz
     */
    public void updateExecDetail(Integer xslx, Integer sfdw, String reason, Long execDetailId, String longtitude, String latitude, Long taskid, String userid) throws Exception {
        if (xslx == 0 || xslx == 1) {
            this.reposiotry.updateTxbdExecDetail(sfdw, reason, execDetailId);
        } else {
            //计算十分钟五基塔
            jiSuanGanTa(execDetailId, taskid, userid);

            //修改状态
            this.reposiotry.updateZcxsExecDetail(sfdw, reason, execDetailId, longtitude, latitude);
            
            try {
                if (sfdw == 1) {
                    warningmonitorServerService.xsTourScope(taskid, userid, reason,execDetailId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void jiSuanGanTa(Long execDetailId, Long taskid, String userid) {
        try {
            String sql = " select * from xs_zc_task_exec_detail where id = ?";
            Map<String, Object> map = this.execSqlSingleResult(sql, execDetailId);
            Long end_tower_id = Long.parseLong(map.get("END_TOWER_ID").toString());
            if (end_tower_id == 0) {
                HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
                String tourGanta = hashOperations.get("tourGanta", taskid + "");
                Map<String, Object> ganta = new HashMap<String, Object>();
                ganta.put("ID", execDetailId);
                ganta.put("END_TIME", DateUtil.stringNow());
                ganta.put("OPERATE_NAME", map.get("OPERATE_NAME"));
                if (StringUtils.isEmpty(tourGanta)) {
                    ArrayList<Map<String, Object>> gantas = new ArrayList<>();
                    gantas.add(ganta);
                    hashOperations.put("tourGanta", taskid + "", JSONObject.toJSONString(gantas));
                } else {
                    List<Map> gantas = JSONArray.parseArray(tourGanta, Map.class);
                    if (gantas.size() == 9) {
                        hashOperations.delete("tourGanta", taskid + "");
                        Map ganta9th = gantas.get(0);
                        Object end_time = ganta9th.get("END_TIME");
                        Date date = DateUtil.stringToDate(end_time.toString());
                        if (new Date().getTime() - date.getTime() < 10 * 60 * 1000) {
                            gantas.add(ganta);
                            long nextId = new SnowflakeIdWorker(18, 21).nextId();
                            this.reposiotry.insertException(nextId, taskid, "十分钟五基塔", JSONObject.toJSONString(gantas));
                            try {
                                warningmonitorServerService.takePhoto(taskid, userid,nextId);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        gantas.add(ganta);
                        hashOperations.put("tourGanta", taskid + "", JSONObject.toJSONString(gantas));

                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * @Method insertExec
     * @Description 插入新的巡视轮数据
     * @param [xslx, taskId, repeatNum]
     * @return void
     * @date 2017/12/18 19:09
     * @author nwz
     */
    @Modifying
    @Transactional
    public void insertExec(Integer xslx, Long taskId, Integer repeatNum) throws Exception {
        long nextId = new SnowflakeIdWorker(18, 20).nextId();
        if (xslx == 0 || xslx == 1) {
            this.reposiotry.insertdtxTaskExec(nextId, taskId, repeatNum);
            this.reposiotry.updateTxbdTaskXsRepeatNum(taskId, repeatNum);
            addExecDetail(taskId, xslx, nextId);
        } else {
            this.reposiotry.insertZcXsTaskExec(nextId, taskId, repeatNum);
            this.reposiotry.updateZcxsTaskXsRepeatNum(taskId);
            addExecDetail(taskId, xslx, nextId);
        }
    }

    /***
     * @Method updateExec
     * @Description 更新轮数据
     * @param [xslx, execId]
     * @return void
     * @date 2017/12/19 10:09
     * @author nwz
     */
    public void updateExec(Integer xslx, Long execId, Integer status) {

        if (xslx == 0 || xslx == 1) {
            if (status == 1) {
                this.reposiotry.updateTxbdExecOn(execId);
            } else {
                this.reposiotry.updateTxbdExecOff(execId);
            }
        } else {
            if (status == 1) {
                this.reposiotry.updateZcxsExecOn(execId);
            } else {
                this.reposiotry.updateZcxsExecOff(execId);
            }
        }
    }

    /***
     * @Method updateTaskStatus
     * @Description
     * @param [id]
     * @return void
     * @date 2017/12/19 15:03
     * @author nwz
     */
    @Transactional
    public void updateTaskStatus(Integer xslx, Long id, String userId) {
        if (xslx == 0 || xslx == 1) {
            this.reposiotry.updateTxbdTaskToOff(id);
            this.reposiotry.updateMonitorCheckEjXs(id);
        } else {
            this.reposiotry.updateZcxsTaskToOff(id);
            this.reposiotry.updateMonitorCheckEjXs(id);
            updateXsMenInfoInRedis(userId);
        }


    }

    public void lsyhInXs(Integer xslx, Long id, Long execId, Long execDetailId, Long yhId, String yhInfo) {
        long nextId = new SnowflakeIdWorker(18, 21).nextId();
        this.reposiotry.addXsZcTaskLsyh(nextId, id, execId, execDetailId, yhId, yhInfo);
    }

    /***
     * @Method insertException
     * @Description 插入十分钟五基塔的数据
     * @param [taskId, ycms, ycdata]
     * @return void
     * @date 2018/1/17 16:55
     * @author nwz
     */
    public void insertException(Long taskid, String ycms, String ycdata, String userid) {
//        long nextId = new SnowflakeIdWorker(18, 21).nextId();
//        this.reposiotry.insertException(nextId,taskid,ycms,ycdata);
//        try {
////            warningmonitorServerService.takePhoto(taskid,userid);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}