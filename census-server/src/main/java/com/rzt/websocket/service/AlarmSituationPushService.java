package com.rzt.websocket.service;

import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.websocket.serverendpoint.AlarmSituationServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.websocket
 * @Author: liuze
 * @date: 2017-11-29 15:11
 * 告警情况展示service
 */
@Service
public class AlarmSituationPushService extends CurdService<websocket, websocketRepository> {

    private static Logger LOGGER = LoggerFactory.getLogger(AlarmSituationPushService.class);

    @Autowired
    AlarmSituationServerEndpoint alarmSituationServerEndpoint;
    @PersistenceContext
    EntityManager entityManager;

    /**
     * 定时查询数据推送消息
     * The WebSocket session [0] has been closed
     * and no method (apart from close()) may be called on a closed session
     */
    @Scheduled(fixedRate = 3000)
    public void sendMsgs() {
        Map<String, HashMap> sendMsg = alarmSituationServerEndpoint.sendMsg();
        /**
         * notstarttime 保电查当天巡视未按时开始任务
         */
        String notstarttime = "select count(id)  from xs_txbd_task   where trunc(plan_start_time)=trunc(sysdate)  and plan_start_time < nvl(real_start_time,sysdate)";
        /**
         * normalinspection 正常巡视当天未按时开始任务的
         */
        String normalinspection = "select count(id) from xs_zc_task where trunc(plan_start_time) = trunc(sysdate) and plan_start_time <nvl(real_start_time ,sysdate)";
        String notKhstarttime = " SELECT count(*) FROM KH_TASK WHERE PLAN_START_TIME<nvl(REAL_START_TIME,sysdate) AND trunc(PLAN_START_TIME)=trunc(sysdate) ";
        /**
         * touroverdue 巡视超期
         */
        String touroverdue = " select count(1) from XS_ZC_TASK where PLAN_END_TIME BETWEEN trunc(sysdate-1) and trunc(sysdate) and STAUTS != 2  ";
        /**
         * 巡视不合格
         */
//        String xsbhg = "SELECT count(1) as xsbhg FROM XS_ZC_TASK_EXEC_DETAIL WHERE is_dw = 1";
        String yhtg = " SELECT count(1) AS sum FROM WARNING_OFF_POST_USER WHERE STATUS=1 AND trunc(CREATE_TIME) = trunc(sysdate) ";
        String xsbhg = " SELECT count(1) FROM XS_ZC_EXCEPTION WHERE trunc(CREATE_TIME) = trunc(sysdate) ";
        //遍历Map取出通道单位id用于数据库查询权限
        sendMsg.forEach((sessionId, session) -> {
            String sql = "SELECT ((" + notstarttime + ")+(" + normalinspection + ")+(" + notKhstarttime + ")) as notstarttime,(" + touroverdue + ") as touroverdue,(" + xsbhg + ") as xsbhg,(" + yhtg + ") as yhtg  FROM DUAL";
            List<Map<String, Object>> execSql = this.execSql(sql);
            try {
                alarmSituationServerEndpoint.sendText((Session) session.get("session"), execSql);
            } catch (Exception e) {
                LOGGER.error("Error: The user closes the browser , Session Does Not Exist", e);
            }
        });
    }
}
