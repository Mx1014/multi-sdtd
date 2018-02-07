package com.rzt.service;

import com.rzt.entity.TimedTask;
import com.rzt.repository.XSZCTASKRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 李成阳
 * 2018/1/31
 */
@Service
public class GJService extends CurdService<TimedTask,XSZCTASKRepository>{

    protected static Logger LOGGER = LoggerFactory.getLogger(GJService.class);



    /**
     * 告警查询
     * @return
     */
    public WebApiResponse GJ(){
        //告警数sql
        Map<String, Object> map = null;
        try {
            String gjSql = " SELECT (SELECT count(1)" +
                    "        FROM (SELECT * FROM(SELECT x.TASK_NAME,nvl(x.PLAN_START_TIME,sysdate-2) as PLAN_START_TIME,x.REAL_START_TIME,u.REALNAME,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STATUS as STAUTS" +
                    "       FROM (SELECT TASK_ID,USER_ID" +
                    "             FROM MONITOR_CHECK_EJ" +
                    "             WHERE WARNING_TYPE = 10   AND trunc(CREATE_TIME)=trunc(sysdate)   ) e LEFT JOIN KH_TASK x ON e.TASK_ID = x.ID" +
                    "         LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1" +
                    "                                                        AND trunc(x.PLAN_START_TIME) = trunc(sysdate)) UNION ALL" +
                    "              SELECT * FROM ( SELECT x.TASK_NAME,nvl(x.PLAN_START_TIME,sysdate-2) as PLAN_START_TIME," +
                    "          x.REAL_START_TIME,u.REALNAME,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STAUTS" +
                    "        FROM (SELECT TASK_ID,USER_ID" +
                    "              FROM MONITOR_CHECK_EJ" +
                    "              WHERE WARNING_TYPE = 4   AND trunc(CREATE_TIME)=trunc(sysdate)   )" +
                    "             e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID = x.ID" +
                    "          LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1" +
                    "        AND trunc(x.PLAN_START_TIME) = trunc(sysdate)))) as WAS," +
                    "        (SELECT count(1)  FROM(" +
                    "    SELECT" +
                    "         e.USER_ID,u.REALNAME,u.CLASSNAME,u.DEPT, u.COMPANYNAME," +
                    "         CASE u.WORKTYPE" +
                    "         WHEN 1" +
                    "           THEN '看护'" +
                    "         WHEN 2" +
                    "           THEN '巡视'" +
                    "         WHEN 3" +
                    "           THEN '现场稽查' END AS WORKTYPE," +
                    "         e.a          AS MORE," +
                    "         u.DEPTID," +
                    "         e.CREATE_TIME," +
                    "         e.ONLINE_TIME" +
                    "       FROM (SELECT" +
                    "                        count(1) as a," +
                    "               USER_ID, MAX (CREATE_TIME) AS CREATE_TIME," +
                    "                        nvl(to_char( MAX (ONLINE_TIME), 'yyyy-MM-dd hh24:mi:ss'), '人员未上线') as ONLINE_TIME" +
                    "             FROM MONITOR_CHECK_EJ" +
                    "             WHERE (WARNING_TYPE = 8 OR WARNING_TYPE = 2)  AND trunc(CREATE_TIME) = trunc(sysdate)  GROUP BY USER_ID) e LEFT JOIN USERINFO u" +
                    "           ON e.USER_ID = u.ID) ch LEFT JOIN MONITOR_CHECK_EJ ce" +
                    "      ON ch.USER_ID=ce.USER_ID AND ch.CREATE_TIME=ce.CREATE_TIME  WHERE ch.ONLINE_TIME = '人员未上线') AS OFF_LINE," +
                    "  (SELECT" +
                    "     count(1)" +
                    "   FROM (SELECT TASK_ID" +
                    "         FROM MONITOR_CHECK_EJ" +
                    "         WHERE WARNING_TYPE = 1   AND trunc(CREATE_TIME) = trunc(sysdate)" +
                    "        ) e LEFT JOIN XS_ZC_TASK k ON e.TASK_ID = k.ID LEFT JOIN USERINFO u ON k.CM_USER_ID = u.ID) AS CQ," +
                    "  (SELECT count(1)" +
                    "   FROM (SELECT *" +
                    "         FROM (SELECT x.TASK_NAME,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE, '巡视超速' as  type,e.REASON" +
                    "               FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID=x.ID LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID" +
                    "               WHERE WARNING_TYPE = 5  AND  trunc(CREATE_TIME) = trunc(sysdate))" +
                    "         UNION ALL" +
                    "         SELECT * FROM (SELECT x.TASK_NAME,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE, '未到位' as  type,e.REASON" +
                    "                        FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID=x.ID LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID" +
                    "                        WHERE WARNING_TYPE = 3  AND  trunc(CREATE_TIME) = trunc(sysdate)))) AS XSBHG," +
                    "  (SELECT count(1) FROM" +
                    "    ( SELECT u.TASK_ID, u.STATUS, t.START_TIME, t.END_TIME FROM WARNING_OFF_POST_USER u LEFT JOIN WARNING_OFF_POST_USER_TIME t ON u.USER_ID=t.FK_USER_ID AND u.TASK_ID=t.FK_TASK_ID WHERE 1=1" +
                    "    AND  trunc(CREATE_TIME) = trunc(sysdate)" +
                    "    )t LEFT JOIN KH_TASK k ON t.TASK_ID = k.ID JOIN USERINFO u ON k.USER_ID = u.ID) AS KHTG,10 AS JCBDW" +
                    "       FROM dual";
            map = this.execSqlSingleResult(gjSql, null);
            LOGGER.info("告警信息查询成功");
        }catch (Exception e){
            LOGGER.error("告警信息查询失败"+e.getMessage());
            return WebApiResponse.erro("告警信息查询失败"+e.getMessage());
        }

        return WebApiResponse.success(map);
    }



    /**
     * 二级页面使用
     * 告警查询
     * @return
     */
    public WebApiResponse GJTwo(){
        ArrayList<Object> objects = new ArrayList<>();
        try {
            String deptSql = "SELECT ID,DEPTNAME" +
                    "           FROM RZTSYSDEPARTMENT WHERE DEPTPID = '402881e6603a69b801603a6ab1d70000'";
            //所有的部门
            List<Map<String, Object>> maps = this.execSql(deptSql, null);
            for (Map<String, Object> map : maps) {
                //按部门查询并封装
                String id = map.get("ID").toString();
                String deptName = map.get("DEPTNAME").toString();
                String gjSql = " SELECT (SELECT count(1)" +
                        "        FROM (SELECT * FROM(SELECT x.TASK_NAME,nvl(x.PLAN_START_TIME,sysdate-2) as PLAN_START_TIME,x.REAL_START_TIME,u.REALNAME,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STATUS as STAUTS" +
                        "       FROM (SELECT TASK_ID,USER_ID" +
                        "             FROM MONITOR_CHECK_EJ" +
                        "             WHERE WARNING_TYPE = 10   AND trunc(CREATE_TIME)=trunc(sysdate) AND DEPTID = '"+id+"' ) e LEFT JOIN KH_TASK x ON e.TASK_ID = x.ID" +
                        "         LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1" +
                        "                                                        AND trunc(x.PLAN_START_TIME) = trunc(sysdate)) UNION ALL" +
                        "              SELECT * FROM ( SELECT x.TASK_NAME,nvl(x.PLAN_START_TIME,sysdate-2) as PLAN_START_TIME," +
                        "          x.REAL_START_TIME,u.REALNAME,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STAUTS" +
                        "        FROM (SELECT TASK_ID,USER_ID" +
                        "              FROM MONITOR_CHECK_EJ" +
                        "              WHERE WARNING_TYPE = 4   AND trunc(CREATE_TIME)=trunc(sysdate)  AND DEPTID = '"+id+"')" +
                        "             e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID = x.ID" +
                        "          LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1" +
                        "        AND trunc(x.PLAN_START_TIME) = trunc(sysdate))) ) as WAS," +
                        "        (SELECT count(1)  FROM(" +
                        "    SELECT" +
                        "         e.USER_ID,u.REALNAME,u.CLASSNAME,u.DEPT, u.COMPANYNAME," +
                        "         CASE u.WORKTYPE" +
                        "         WHEN 1" +
                        "           THEN '看护'" +
                        "         WHEN 2" +
                        "           THEN '巡视'" +
                        "         WHEN 3" +
                        "           THEN '现场稽查' END AS WORKTYPE," +
                        "         e.a          AS MORE," +
                        "         u.DEPTID," +
                        "         e.CREATE_TIME," +
                        "         e.ONLINE_TIME" +
                        "       FROM (SELECT" +
                        "                        count(1) as a," +
                        "               USER_ID, MAX (CREATE_TIME) AS CREATE_TIME," +
                        "                        nvl(to_char( MAX (ONLINE_TIME), 'yyyy-MM-dd hh24:mi:ss'), '人员未上线') as ONLINE_TIME" +
                        "             FROM MONITOR_CHECK_EJ" +
                        "             WHERE (WARNING_TYPE = 8 OR WARNING_TYPE = 2)  AND trunc(CREATE_TIME) = trunc(sysdate)  GROUP BY USER_ID) e LEFT JOIN USERINFO u" +
                        "           ON e.USER_ID = u.ID) ch LEFT JOIN MONITOR_CHECK_EJ ce" +
                        "      ON ch.USER_ID=ce.USER_ID AND ch.CREATE_TIME=ce.CREATE_TIME  WHERE ch.ONLINE_TIME = '人员未上线'   AND ce.DEPTID = '"+id+"') AS OFF_LINE," +
                        "  (SELECT" +
                        "     count(1)" +
                        "   FROM (SELECT TASK_ID" +
                        "         FROM MONITOR_CHECK_EJ" +
                        "         WHERE WARNING_TYPE = 1   AND trunc(CREATE_TIME) = trunc(sysdate)   AND DEPTID = '"+id+"'" +
                        "        ) e LEFT JOIN XS_ZC_TASK k ON e.TASK_ID = k.ID LEFT JOIN USERINFO u ON k.CM_USER_ID = u.ID) AS CQ," +
                        "  (SELECT count(1)" +
                        "   FROM (SELECT *" +
                        "         FROM (SELECT x.TASK_NAME,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE, '巡视超速' as  type,e.REASON" +
                        "               FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID=x.ID LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID" +
                        "               WHERE WARNING_TYPE = 5  AND  trunc(CREATE_TIME) = trunc(sysdate)   AND e.DEPTID = '"+id+"')" +
                        "         UNION ALL" +
                        "         SELECT * FROM (SELECT x.TASK_NAME,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE, '未到位' as  type,e.REASON" +
                        "                        FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID=x.ID LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID" +
                        "                        WHERE WARNING_TYPE = 3  AND  trunc(CREATE_TIME) = trunc(sysdate)   AND e.DEPTID = '"+id+"'))) AS XSBHG," +
                        "  (SELECT count(1) FROM" +
                        "    ( SELECT u.TASK_ID, u.STATUS, t.START_TIME, t.END_TIME FROM WARNING_OFF_POST_USER u LEFT JOIN WARNING_OFF_POST_USER_TIME t ON u.USER_ID=t.FK_USER_ID AND u.TASK_ID=t.FK_TASK_ID WHERE 1=1" +
                        "    AND  trunc(CREATE_TIME) = trunc(sysdate)" +
                        "    )t LEFT JOIN KH_TASK k ON t.TASK_ID = k.ID AND k.YWORG_ID = '"+id+"' JOIN USERINFO u ON k.USER_ID = u.ID) AS KHTG" +
                        "       FROM dual";
                map = this.execSqlSingleResult(gjSql, null);
                HashMap<String, Object> obj = new HashMap<>();
                obj.put("name", deptName);
                obj.put("id", id);
                obj.put("value", map);
                objects.add(obj);
            }
            LOGGER.info("告警信息查询成功");
        }catch (Exception e){
            LOGGER.error("告警信息查询失败"+e.getMessage());
            return WebApiResponse.erro("告警信息查询失败"+e.getMessage());
        }

        return WebApiResponse.success(objects);
    }

    /**
     * 三级页面使用
     * 告警查询
     * @return
     */
    public WebApiResponse GJThree(String id) {
        if(null == id || "".equals(id)){
            return WebApiResponse.erro("参数错误   deptId = "+id);
        }
        HashMap<String, Object> obj = new HashMap<>();
                try{
                String gjSql = " SELECT (SELECT count(1)" +
                        "        FROM (SELECT * FROM(SELECT x.TASK_NAME,nvl(x.PLAN_START_TIME,sysdate-2) as PLAN_START_TIME,x.REAL_START_TIME,u.REALNAME,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STATUS as STAUTS" +
                        "       FROM (SELECT TASK_ID,USER_ID" +
                        "             FROM MONITOR_CHECK_EJ" +
                        "             WHERE WARNING_TYPE = 10   AND trunc(CREATE_TIME)=trunc(sysdate) AND DEPTID = '"+id+"' ) e LEFT JOIN KH_TASK x ON e.TASK_ID = x.ID" +
                        "         LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1" +
                        "                                                        AND trunc(x.PLAN_START_TIME) = trunc(sysdate)) UNION ALL" +
                        "              SELECT * FROM ( SELECT x.TASK_NAME,nvl(x.PLAN_START_TIME,sysdate-2) as PLAN_START_TIME," +
                        "          x.REAL_START_TIME,u.REALNAME,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STAUTS" +
                        "        FROM (SELECT TASK_ID,USER_ID" +
                        "              FROM MONITOR_CHECK_EJ" +
                        "              WHERE WARNING_TYPE = 4   AND trunc(CREATE_TIME)=trunc(sysdate)  AND DEPTID = '"+id+"')" +
                        "             e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID = x.ID" +
                        "          LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1" +
                        "        AND trunc(x.PLAN_START_TIME) = trunc(sysdate))) ) as WAS," +
                        "        (SELECT count(1)  FROM(" +
                        "    SELECT" +
                        "         e.USER_ID,u.REALNAME,u.CLASSNAME,u.DEPT, u.COMPANYNAME," +
                        "         CASE u.WORKTYPE" +
                        "         WHEN 1" +
                        "           THEN '看护'" +
                        "         WHEN 2" +
                        "           THEN '巡视'" +
                        "         WHEN 3" +
                        "           THEN '现场稽查' END AS WORKTYPE," +
                        "         e.a          AS MORE," +
                        "         u.DEPTID," +
                        "         e.CREATE_TIME," +
                        "         e.ONLINE_TIME" +
                        "       FROM (SELECT" +
                        "                        count(1) as a," +
                        "               USER_ID, MAX (CREATE_TIME) AS CREATE_TIME," +
                        "                        nvl(to_char( MAX (ONLINE_TIME), 'yyyy-MM-dd hh24:mi:ss'), '人员未上线') as ONLINE_TIME" +
                        "             FROM MONITOR_CHECK_EJ" +
                        "             WHERE (WARNING_TYPE = 8 OR WARNING_TYPE = 2)  AND trunc(CREATE_TIME) = trunc(sysdate)  GROUP BY USER_ID) e LEFT JOIN USERINFO u" +
                        "           ON e.USER_ID = u.ID) ch LEFT JOIN MONITOR_CHECK_EJ ce" +
                        "      ON ch.USER_ID=ce.USER_ID AND ch.CREATE_TIME=ce.CREATE_TIME  WHERE ch.ONLINE_TIME = '人员未上线'   AND ce.DEPTID = '"+id+"') AS OFF_LINE," +
                        "  (SELECT" +
                        "     count(1)" +
                        "   FROM (SELECT TASK_ID" +
                        "         FROM MONITOR_CHECK_EJ" +
                        "         WHERE WARNING_TYPE = 1   AND trunc(CREATE_TIME) = trunc(sysdate)   AND DEPTID = '"+id+"'" +
                        "        ) e LEFT JOIN XS_ZC_TASK k ON e.TASK_ID = k.ID LEFT JOIN USERINFO u ON k.CM_USER_ID = u.ID) AS CQ," +
                        "  (SELECT count(1)" +
                        "   FROM (SELECT *" +
                        "         FROM (SELECT x.TASK_NAME,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE, '巡视超速' as  type,e.REASON" +
                        "               FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID=x.ID LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID" +
                        "               WHERE WARNING_TYPE = 5  AND  trunc(CREATE_TIME) = trunc(sysdate)   AND e.DEPTID = '"+id+"')" +
                        "         UNION ALL" +
                        "         SELECT * FROM (SELECT x.TASK_NAME,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE, '未到位' as  type,e.REASON" +
                        "                        FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID=x.ID LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID" +
                        "                        WHERE WARNING_TYPE = 3  AND  trunc(CREATE_TIME) = trunc(sysdate)   AND e.DEPTID = '"+id+"'))) AS XSBHG," +
                        "  (SELECT count(1) FROM" +
                        "    ( SELECT u.TASK_ID, u.STATUS, t.START_TIME, t.END_TIME FROM WARNING_OFF_POST_USER u LEFT JOIN WARNING_OFF_POST_USER_TIME t ON u.USER_ID=t.FK_USER_ID AND u.TASK_ID=t.FK_TASK_ID WHERE 1=1" +
                        "    AND  trunc(CREATE_TIME) = trunc(sysdate)" +
                        "    )t LEFT JOIN KH_TASK k ON t.TASK_ID = k.ID AND k.YWORG_ID = '"+id+"' JOIN USERINFO u ON k.USER_ID = u.ID) AS KHTG" +
                        "       FROM dual";
                Map<String, Object> map = this.execSqlSingleResult(gjSql, null);

                obj.put("id", id);
                obj.put("value", map);
            LOGGER.info("告警信息查询成功");
        }catch (Exception e){
            LOGGER.error("告警信息查询失败"+e.getMessage());
            return WebApiResponse.erro("告警信息查询失败"+e.getMessage());
        }

        return WebApiResponse.success(obj);
    }


}
