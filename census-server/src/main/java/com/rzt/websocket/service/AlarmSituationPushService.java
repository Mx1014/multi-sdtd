package com.rzt.websocket.service;

import com.rzt.websocket.serverendpoint.AlarmSituationServerEndpoint;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.websocket
 * @Author: liuze
 * @date: 2017-11-29 15:11
 * 告警情况展示service
 */
@Service
public class AlarmSituationPushService {

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
    @Scheduled(fixedRate = 5000)
    public void sendMsgs() {
        Map<String, HashMap> sendMsg = alarmSituationServerEndpoint.sendMsg();
        /**
         * notstarttime 保电查当天巡视未按时开始任务
         */
        String notstarttime = "select count(id)  from xs_txbd_task   where trunc(plan_start_time)=trunc(sysdate)  and plan_start_time < nvl(real_start_time,sysdate)";
        /**
         * normalinspection 正常巡视当天未按时开始任务的
         */
        String normalinspection = "select count(*) from xs_zc_task where trunc(plan_start_time) = trunc(sysdate) and trunc(plan_start_time) <nvl(real_start_time ,sysdate)";
        /**
         * touroverdue 巡视超期
         */
        String touroverdue = "select count(id) from xs_txbd_task where  (stauts = 0 or stauts = 1) and  trunc(plan_end_time) <trunc(sysdate)";
        //遍历Map取出通道单位id用于数据库查询权限
        sendMsg.forEach((sessionId, session) -> {
            Query q = this.entityManager.createNativeQuery("SELECT ((" + notstarttime + ")+(" + normalinspection + ")) as notstarttime,(" + touroverdue + ") as touroverdue FROM DUAL");
//            if (session.get("orgid") != null) {
//                q.setParameter(1, session.get("orgid"));
//            }
            ((SQLQuery) q.unwrap(SQLQuery.class)).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            try {
                alarmSituationServerEndpoint.sendText((Session) session.get("session"), q.getResultList().toString());
            } catch (Exception e) {
                LOGGER.error("Error: The user closes the browser , Session Does Not Exist", e);
            }
        });
    }
}
