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
public class TasksService extends CurdService<TimedTask,XSZCTASKRepository>{

    protected static Logger LOGGER = LoggerFactory.getLogger(TasksService.class);


    /**
     * 查询任务详情   总任务数  已完成数  未开始数
     * @return
     */
    public WebApiResponse findTaskInfo(){
        Map<String, Object> map = null;
  try {
      String sql = "SELECT ((SELECT count(1)" +
              "         FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)) + (SELECT count(1)" +
              "      FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)) +" +
              "        (SELECT count(1)" +
              "         FROM CHECK_LIVE_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate))) AS SUM," +
              "     ((SELECT count(1)" +
              "     FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 2)" +
              "     + (SELECT count(1)" +
              "      FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 2) +" +
              "    (SELECT count(1)" +
              "    FROM CHECK_LIVE_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 2)) AS COMPlETE," +
              "    ((SELECT count(1)" +
              "    FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 0)" +
              "    + (SELECT count(1)" +
              "      FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 0) +" +
              "    (SELECT count(1)" +
              "    FROM CHECK_LIVE_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 0)) AS WKS" +
              "    FROM dual";
      map = this.execSqlSingleResult(sql, null);
      LOGGER.info("任务详细查询成功");
  }catch (Exception e){
      LOGGER.error("任务详细查询失败"+e.getMessage());
      return WebApiResponse.erro("任务详细查询失败"+e.getMessage());
  }
  return WebApiResponse.success(map);
    }



    /**
     * 查看所有单位的任务详情  以单位分组
     * @return
     */
    public WebApiResponse findTasksGroupDept(){
        ArrayList<Object> list = new ArrayList<>();
        try {

            String deptSql = "SELECT ID,DEPTNAME" +
                    "           FROM RZTSYSDEPARTMENT WHERE DEPTPID = '402881e6603a69b801603a6ab1d70000'";
            //所有的部门
            List<Map<String, Object>> maps = this.execSql(deptSql, null);
            for (Map<String, Object> map : maps) {
                //按部门查询并封装
                String id = map.get("ID").toString();
                String deptName = map.get("DEPTNAME").toString();
                String sql = "SELECT ((SELECT count(1)" +
                        "         FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)  AND TD_ORG = '"+id+"' )+  (SELECT count(1)" +
                        "     FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)  AND YWORG_ID = '"+id+"' )+" +
                        "        (SELECT count(1)" +
                        "         FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "         WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND d.TDYW_ORGID = '"+id+"')) AS SUM," +
                        "    ((SELECT count(1)" +
                        "    FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 2   AND TD_ORG = '"+id+"' )+" +
                        "    (SELECT count(1)" +
                        "      FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 2  AND YWORG_ID = '"+id+"')+" +
                        "    (SELECT count(1)" +
                        "    FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "    WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND t.STATUS = 2 AND d.TDYW_ORGID = '"+id+"')) AS COMPlETE," +
                        "    ((SELECT count(1)" +
                        "    FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 0   AND TD_ORG = '"+id+"' )+" +
                        "    (SELECT count(1)" +
                        "      FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 0  AND YWORG_ID = '"+id+"')+" +
                        "    (SELECT count(1)" +
                        "    FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "    WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND t.STATUS = 0 AND d.TDYW_ORGID = '"+id+"')) AS WKS" +
                        "    FROM dual";
                Map<String, Object> map1 = this.execSqlSingleResult(sql, null);
                HashMap<String, Object> obj = new HashMap<>();
                obj.put("name",deptName);
                obj.put("id",id);
                obj.put("value",map1);
                list.add(obj);
            }
        }catch (Exception e){
            LOGGER.error("部门分组查询任务信息失败"+e.getMessage());
            return WebApiResponse.erro("部门分组查询任务信息失败"+e.getMessage());
        }
        LOGGER.info("部门分组查询任务信息成功");
        return WebApiResponse.success(list);
    }



    public WebApiResponse threeTasks(String deptId){
        HashMap<String, Object> obj = new HashMap<>();
        if(null == deptId || "".equals(deptId)){
            return WebApiResponse.erro("参数错误   deptId = "+deptId);
        }
        try {

            if(null !=  deptId && !"".equals(deptId)){
                //按部门查询并封装
                 //任务总数 饼图sql
                String sql = "SELECT ((SELECT count(1)" +
                        "         FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)  AND TD_ORG = '"+deptId+"' )+  (SELECT count(1)" +
                        "     FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)  AND YWORG_ID = '"+deptId+"' )+" +
                        "        (SELECT count(1)" +
                        "         FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "         WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND d.TDYW_ORGID = '"+deptId+"')) AS SUM," +
                        "  ((SELECT count(1)" +
                        "    FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 2   AND TD_ORG = '"+deptId+"' )+" +
                        "    (SELECT count(1)" +
                        "      FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 2  AND YWORG_ID = '"+deptId+"')+" +
                        "   (SELECT count(1)" +
                        "    FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "    WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND t.STATUS = 2 AND d.TDYW_ORGID = '"+deptId+"')) AS COMPlETE," +
                        "  ((SELECT count(1)" +
                        "    FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 0   AND TD_ORG = '"+deptId+"' )+" +
                        "    (SELECT count(1)" +
                        "      FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 0  AND YWORG_ID = '"+deptId+"')+" +
                        "   (SELECT count(1)" +
                        "    FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "    WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND t.STATUS = 0 AND d.TDYW_ORGID = '"+deptId+"')) AS WKS" +
                        "    FROM dual";
                Map<String, Object> map1 = this.execSqlSingleResult(sql, null);
                //饼图
                obj.put("values",map1);
                //巡视统计
                String xsSql = "SELECT (SELECT count(1)" +
                        "        FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)" +
                        "          AND TD_ORG = '"+deptId+"' ) AS SUM," +
                        "  (SELECT count(1)" +
                        "   FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)" +
                        "      AND STAUTS = 2   AND TD_ORG = '"+deptId+"' ) AS COMPlETE," +
                        "  (SELECT count(1)" +
                        "   FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)" +
                        "      AND STAUTS = 0   AND TD_ORG = '"+deptId+"' ) AS WKS" +
                        "       FROM dual";
                Map<String, Object> xsMap = this.execSqlSingleResult(xsSql, null);
                obj.put("xsMap",xsMap);
                //看护统计
                String khSql = "SELECT (SELECT count(1)" +
                        "        FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)" +
                        "    AND YWORG_ID = '"+deptId+"' ) AS SUM," +
                        "  (SELECT count(1)" +
                        "   FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)" +
                        "    AND STATUS = 2  AND YWORG_ID = '"+deptId+"') AS COMPlETE," +
                        "  (SELECT count(1)" +
                        "   FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)" +
                        "    AND STATUS = 0  AND YWORG_ID = '"+deptId+"') AS WKS" +
                        "   FROM dual";
                Map<String, Object> khMap = this.execSqlSingleResult(khSql, null);
                obj.put("khMap",khMap);
                //前台稽查统计
                String qtjcSql = "SELECT (SELECT count(1)" +
                        "        FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "        WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate)" +
                        "              AND d.TDYW_ORGID = '"+deptId+"') AS SUM," +
                        "  (SELECT count(1)" +
                        "   FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "   WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND t.STATUS = 2" +
                        "         AND d.TDYW_ORGID = '"+deptId+"') AS COMPlETE," +
                        "  (SELECT count(1)" +
                        "   FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "   WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND t.STATUS = 0" +
                        "         AND d.TDYW_ORGID = '"+deptId+"') AS WKS" +
                        "   FROM dual";
                Map<String, Object> qtjcMap = this.execSqlSingleResult(qtjcSql, null);
                obj.put("qtjcMap",qtjcMap);
                LOGGER.info("三级页面任务详情查询成功");
                return WebApiResponse.success(obj);
            }
        }catch (Exception e){
            LOGGER.error("三级页面任务详情查询失败"+e.getMessage());
            return WebApiResponse.erro("三级页面任务详情查询失败"+e.getMessage());
        }
        return WebApiResponse.success("");
    }




}
