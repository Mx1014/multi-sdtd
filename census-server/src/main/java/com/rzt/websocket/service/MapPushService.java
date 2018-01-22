package com.rzt.websocket.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.websocket.serverendpoint.MapServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MapPushService extends CurdService<websocket, websocketRepository> {
    @Autowired
    MapServerEndpoint mapserverendpoint;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Scheduled(fixedRate = 3000)
    public void sendMsgs() {
        HashOperations hashOperations = redisTemplate.opsForHash();
        Map<String, HashMap> sendMsg = mapserverendpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            String userId = String.valueOf(session.get("userId"));
            Object userInformation = hashOperations.get("UserInformation", userId);
            JSONObject jsonObject = JSONObject.parseObject(String.valueOf(userInformation));
            Integer roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
            String s = "";
            List lists = new ArrayList();
            if (roletype == 1 || roletype == 0) {
                String DEPTID = jsonObject.get("DEPTID").toString();
                lists.add(DEPTID);
                s += " and id=?" + lists.size();
            }
            if (Integer.valueOf(session.get("type").toString()) == 2) {
                String wks = " SELECT nvl(xswks,0) + nvl(khwks,0) AS wks, a.TD_ORG FROM (SELECT rr.ID AS TD_ORG, xswks FROM (SELECT count(1) AS xswks, TD_ORG FROM XS_ZC_TASK k WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 0 GROUP BY TD_ORG) cae RIGHT JOIN RZTSYSDEPARTMENT rr ON cae.TD_ORG = rr.ID WHERE rr.DEPTSORT IS NOT NULL ORDER BY rr.DEPTSORT) a LEFT JOIN (SELECT khwks,  ppp.ID as TD_ORG FROM (SELECT count(1)   AS khwks, k.TDYW_ORG AS TD_ORG FROM KH_TASK k WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 0 GROUP BY TDYW_ORG) bb RIGHT JOIN RZTSYSDEPARTMENT ppp ON bb.TD_ORG = ppp.DEPTNAME WHERE ppp.DEPTSORT IS NOT NULL) b ON a.TD_ORG = b.TD_ORG";
                String jxz = "SELECT nvl(xswks,0) + nvl(khwks,0) AS wks, a.TD_ORG FROM (SELECT rr.ID AS TD_ORG, xswks FROM (SELECT count(1) AS xswks, TD_ORG FROM XS_ZC_TASK k WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 1 GROUP BY TD_ORG) cae RIGHT JOIN RZTSYSDEPARTMENT rr ON cae.TD_ORG = rr.ID WHERE rr.DEPTSORT IS NOT NULL ORDER BY rr.DEPTSORT) a LEFT JOIN (SELECT khwks,  ppp.ID as TD_ORG FROM (SELECT count(1)   AS khwks, k.TDYW_ORG AS TD_ORG FROM KH_TASK k WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 1 GROUP BY TDYW_ORG) bb RIGHT JOIN RZTSYSDEPARTMENT ppp ON bb.TD_ORG = ppp.DEPTNAME WHERE ppp.DEPTSORT IS NOT NULL) b ON a.TD_ORG = b.TD_ORG ";
                String ywc = " SELECT nvl(xswks,0) + nvl(khwks,0) AS wks, a.TD_ORG FROM (SELECT rr.ID AS TD_ORG, xswks FROM (SELECT count(1) AS xswks, TD_ORG FROM XS_ZC_TASK k WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 2 GROUP BY TD_ORG) cae RIGHT JOIN RZTSYSDEPARTMENT rr ON cae.TD_ORG = rr.ID WHERE rr.DEPTSORT IS NOT NULL ORDER BY rr.DEPTSORT) a LEFT JOIN (SELECT khwks,  ppp.ID as TD_ORG FROM (SELECT count(1)   AS khwks, k.TDYW_ORG AS TD_ORG FROM KH_TASK k WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 2 GROUP BY TDYW_ORG) bb RIGHT JOIN RZTSYSDEPARTMENT ppp ON bb.TD_ORG = ppp.DEPTNAME WHERE ppp.DEPTSORT IS NOT NULL) b ON a.TD_ORG = b.TD_ORG";
                String deptnameSql = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL  " + s + "  ORDER BY t.DEPTSORT ";
                List<Map<String, Object>> deptname = this.execSql(deptnameSql, lists.toArray());
                List<Map<String, Object>> list = this.execSql(wks);
                List<Map<String, Object>> list1 = this.execSql(jxz);
                List<Map<String, Object>> list2 = this.execSql(ywc);
                Map wks1 = new HashMap();
                Map jxz1 = new HashMap();
                Map ywc2 = new HashMap();
                for (Map<String, Object> singleXs : list) {
                    wks1.put(singleXs.get("TD_ORG"), singleXs.get("WKS"));
                }
                for (Map<String, Object> singleKh : list1) {
                    jxz1.put(singleKh.get("TD_ORG"), singleKh.get("WKS"));
                }
                for (Map<String, Object> singleKh : list2) {
                    ywc2.put(singleKh.get("TD_ORG"), singleKh.get("WKS"));
                }
                for (Map<String, Object> dept : deptname) {
                    dept.put("wks", wks1.get(dept.get("ID")));
                    dept.put("jxz", jxz1.get(dept.get("ID")));
                    dept.put("ywc", ywc2.get(dept.get("ID")));
                }
                mapserverendpoint.sendText((Session) session.get("session"), deptname);
            } else if (Integer.valueOf(session.get("type").toString()) == 0) {
                String DEPTID = jsonObject.get("DEPTID").toString();
                List list5 = new ArrayList();
                String s1 = "";
                if (roletype == 0) {
                    list5.add("402881e6603a69b801603a6ab1d70000");
                    s1 = " d.DEPTPID=?" + list5.size();
                } else if (roletype == 1 || roletype == 2) {
                    list5.add(DEPTID);
                    s1 = " d.DEPTPID=?" + list5.size();
                }
                String sql = " SELECT deptname,(select count(h.id) from KH_YH_HISTORY h where h.TDYW_ORG=d.deptname) all_count,(select count(h.id) from KH_YH_HISTORY h where h.TDYW_ORG=d.deptname and trunc(h.CREATE_TIME)=trunc(sysdate)) new_count FROM RZTSYSDEPARTMENT d WHERE 1=1 " + s1 + "  ORDER BY  d.DEPTSORT ";
                List<Map<String, Object>> list = this.execSql(sql, list5.toArray());
                mapserverendpoint.sendText((Session) session.get("session"), list);
            } else if (Integer.valueOf(session.get("type").toString()) == 1) {
                String khzx = " SELECT rr.ID,count(a.ID) as khzx FROM (SELECT u.ID,u.DEPTID FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID WHERE LOGINSTATUS = 1 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= sysdate GROUP BY u.ID,u.DEPTID) a RIGHT JOIN RZTSYSDEPARTMENT rr ON a.DEPTID = rr.ID WHERE rr.DEPTSORT IS NOT NULL  GROUP BY rr.ID ";
                String khlx = " SELECT rr.ID,count(a.ID) as khlx FROM (SELECT u.ID,u.DEPTID FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID WHERE LOGINSTATUS = 0 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= sysdate GROUP BY u.ID,u.DEPTID) a RIGHT JOIN RZTSYSDEPARTMENT rr ON a.DEPTID = rr.ID WHERE rr.DEPTSORT IS NOT NULL GROUP BY rr.ID ";
                String xszx = " SELECT rr.ID,count(a.ID) as xszx FROM (SELECT r.ID,r.DEPTID FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID WHERE LOGINSTATUS = 1 AND USERDELETE = 1 AND z.PLAN_START_TIME< = sysdate AND z.PLAN_END_TIME >= sysdate GROUP BY r.ID,r.DEPTID) a RIGHT JOIN RZTSYSDEPARTMENT rr ON a.DEPTID = rr.ID WHERE rr.DEPTSORT IS NOT NULL GROUP BY rr.ID ";
                String xslx = " SELECT rr.ID,count(a.ID) as xslx FROM (SELECT r.ID,r.DEPTID FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID WHERE LOGINSTATUS = 0 AND USERDELETE = 1 AND z.PLAN_START_TIME< = sysdate AND z.PLAN_END_TIME >= sysdate GROUP BY r.ID,r.DEPTID) a RIGHT JOIN RZTSYSDEPARTMENT rr ON a.DEPTID = rr.ID WHERE rr.DEPTSORT IS NOT NULL GROUP BY rr.ID ";
                String deptnameSql = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL  " + s + "  ORDER BY t.DEPTSORT ";
                List<Map<String, Object>> deptname = this.execSql(deptnameSql, lists.toArray());
                List<Map<String, Object>> list1 = this.execSql(khzx);
                List<Map<String, Object>> list2 = this.execSql(khlx);
                List<Map<String, Object>> list3 = this.execSql(xszx);
                List<Map<String, Object>> list4 = this.execSql(xslx);
                Map map1 = new HashMap();
                Map map2 = new HashMap();
                Map map3 = new HashMap();
                Map map4 = new HashMap();
                for (Map<String, Object> singleXs : list1) {
                    map1.put(singleXs.get("ID"), singleXs.get("KHZX"));
                }
                for (Map<String, Object> singleXs : list2) {
                    map2.put(singleXs.get("ID"), singleXs.get("KHLX"));
                }
                for (Map<String, Object> singleXs : list3) {
                    map3.put(singleXs.get("ID"), singleXs.get("XSZX"));
                }
                for (Map<String, Object> singleXs : list4) {
                    map4.put(singleXs.get("ID"), singleXs.get("XSLX"));
                }
                for (Map<String, Object> dept : deptname) {
                    Integer khzx1 = Integer.valueOf(map1.get(dept.get("ID")).toString());
                    Integer xszx1 = Integer.valueOf(map3.get(dept.get("ID")).toString());
                    dept.put("zx", khzx1 + xszx1);
                    Integer khlx1 = Integer.valueOf(map2.get(dept.get("ID")).toString());
                    Integer xslx1 = Integer.valueOf(map4.get(dept.get("ID")).toString());
                    dept.put("lx", khlx1 + xslx1);
                }
                mapserverendpoint.sendText((Session) session.get("session"), deptname);
            }
        });
    }
}
