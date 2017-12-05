package com.rzt.websocket;

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
 */
@Service
public class PushService {

    private static Logger LOGGER = LoggerFactory.getLogger(PushService.class);

    @Autowired
    PushServerEndpoint pushServerEndpoint;
    @PersistenceContext
    EntityManager entityManager;

    /**
     * 定时查询数据推送消息
     * The WebSocket session [0] has been closed
     * and no method (apart from close()) may be called on a closed session
     */
    @Scheduled(fixedRate = 5000)
    public void sendMsgs() {
        Map<String, HashMap> sendMsg = pushServerEndpoint.sendMsg();
        //遍历Map取出通道单位id用于数据库查询权限
        sendMsg.forEach((sessionId, session) -> {
            Query q = this.entityManager.createNativeQuery("select id, line_name, v_level, line_jb from cm_line WHERE ID = ? ");
            if (session.get("orgid") != null) {
                q.setParameter(1, session.get("orgid"));
            }
            ((SQLQuery) q.unwrap(SQLQuery.class)).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            try {
                pushServerEndpoint.sendText((Session) session.get("session"), q.getResultList().toString());
            } catch (Exception e) {
                LOGGER.error("Error: The user closes the browser , Session Does Not Exist", e);
            }
        });
    }

    @Scheduled(fixedRate = 5000)
    public void sendMsgss() {
        Map<String, HashMap> sendMsg = pushServerEndpoint.sendMsg();
        //遍历Map取出通道单位id用于数据库查询权限
        sendMsg.forEach((sessionId, session) -> {
            Query q = this.entityManager.createNativeQuery("select 1 from dual where 1=? ");
            if (session.get("orgid") != null) {
                q.setParameter(1, session.get("orgid"));
            }
            ((SQLQuery) q.unwrap(SQLQuery.class)).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            try {
                pushServerEndpoint.sendText((Session) session.get("session"), q.getResultList().toString());
            } catch (Exception e) {
                LOGGER.error("Error: The user closes the browser , Session Does Not Exist", e);
            }
        });
    }
}
