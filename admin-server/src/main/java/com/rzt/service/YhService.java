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
public class YhService extends CurdService<TimedTask,XSZCTASKRepository>{

    protected static Logger LOGGER = LoggerFactory.getLogger(YhService.class);


    public WebApiResponse findYh(){
        Map<String, Object> map = null;
       try {
           //获取所有隐患类别
           String sql = "SELECT (SELECT count(1)" +
                   "        FROM KH_YH_HISTORY WHERE trunc(CREATE_TIME) = trunc(sysdate) ) AS DAYYH," +//上报
                   "  (SELECT count(1)" +
                   "   FROM KH_YH_HISTORY WHERE YHZT = 0 ) AS SUM," +//总数
                   "  (SELECT count(1)" +
                   "   FROM KH_YH_HISTORY WHERE YHJB1 = '其他' AND YHZT = 0) AS  QT," +
                   "  (SELECT count(1)" +
                   "   FROM KH_YH_HISTORY WHERE YHJB1 = '异物隐患' AND YHZT = 0) AS  YW," +
                   "  (SELECT count(1)" +
                   "   FROM KH_YH_HISTORY WHERE YHJB1 = '树木隐患' AND YHZT = 0) AS  SM," +
                   "  (SELECT count(1)" +
                   "   FROM KH_YH_HISTORY WHERE YHJB1 = '建筑隐患' AND YHZT = 0) AS  JZ," +
                   "  (SELECT count(1)" +
                   "   FROM KH_YH_HISTORY WHERE YHJB1 = '施工隐患' AND YHZT = 0) AS  SG," +
                   "  (SELECT count(1)" +
                   "   FROM KH_YH_HISTORY WHERE trunc(YHXQ_TIME) = trunc(sysdate) ) AS XQ," +//消缺
                   "  (SELECT count(1)" +
                   "   FROM KH_YH_HISTORY WHERE trunc(UPDATE_TIME) = trunc(sysdate) ) AS TZ" +//今日调整
                   "       FROM dual";
           map = this.execSqlSingleResult(sql, null);
           LOGGER.info("隐患信息查询成功");
       }catch (Exception e){
           LOGGER.error("隐患信息查询失败"+e.getMessage());
           return WebApiResponse.erro("隐患信息查询失败"+e.getMessage());
       }
        return WebApiResponse.success(map);
    }

    public WebApiResponse findYhTwo(){
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
                //获取所有隐患类别
                String sql = "SELECT (SELECT count(1)" +
                        "        FROM KH_YH_HISTORY WHERE trunc(CREATE_TIME) = trunc(sysdate) AND YWORG_ID = '"+id+"' ) AS DAYYH," +//今日新增
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE YHZT = 0  AND YWORG_ID = '"+id+"' ) AS SUM," +//总数
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE YHJB1 = '其他' AND YHZT = 0  AND YWORG_ID = '"+id+"' ) AS  QT," +
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE YHJB1 = '异物隐患' AND YHZT = 0  AND YWORG_ID = '"+id+"' ) AS  YW," +
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE YHJB1 = '树木隐患' AND YHZT = 0  AND YWORG_ID = '"+id+"' ) AS  SM," +
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE YHJB1 = '建筑隐患' AND YHZT = 0  AND YWORG_ID = '"+id+"' ) AS  JZ," +
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE YHJB1 = '施工隐患' AND YHZT = 0  AND YWORG_ID = '"+id+"' ) AS  SG," +
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE trunc(YHXQ_TIME) = trunc(sysdate)  AND YWORG_ID = '"+id+"'  ) AS XQ," +//消缺
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE trunc(UPDATE_TIME) = trunc(sysdate)  AND YWORG_ID = '"+id+"'  ) AS TZ" +//今日调整
                        "       FROM dual";
                Map<String, Object> map1 = this.execSqlSingleResult(sql, null);
                HashMap<String, Object> obj = new HashMap<>();
                obj.put("name", deptName);
                obj.put("id", id);
                obj.put("value", map1);
                objects.add(obj);
            }

            LOGGER.info("隐患信息查询成功");
        }catch (Exception e){
            LOGGER.error("隐患信息查询失败"+e.getMessage());
            return WebApiResponse.erro("隐患信息查询失败"+e.getMessage());
        }
        return WebApiResponse.success(objects);
    }


    public WebApiResponse findYhThree(String id){
        if(null == id || "".equals(id)){
            return WebApiResponse.erro("参数错误   deptId = "+id);
        }
        HashMap<String, Object> obj = new HashMap<>();
        try {
                //获取所有隐患类别
                String sql = "SELECT (SELECT count(1)" +
                        "        FROM KH_YH_HISTORY WHERE trunc(CREATE_TIME) = trunc(sysdate) AND YWORG_ID = '"+id+"' ) AS DAYYH," +//今日上报
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE YHZT = 0  AND YWORG_ID = '"+id+"' ) AS SUM," +//总数
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE YHJB1 = '其他' AND YHZT = 0  AND YWORG_ID = '"+id+"' ) AS  QT," +
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE YHJB1 = '异物隐患' AND YHZT = 0  AND YWORG_ID = '"+id+"' ) AS  YW," +
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE YHJB1 = '树木隐患' AND YHZT = 0  AND YWORG_ID = '"+id+"' ) AS  SM," +
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE YHJB1 = '建筑隐患' AND YHZT = 0  AND YWORG_ID = '"+id+"' ) AS  JZ," +
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE YHJB1 = '施工隐患' AND YHZT = 0  AND YWORG_ID = '"+id+"' ) AS  SG," +
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE trunc(YHXQ_TIME) = trunc(sysdate)  AND YWORG_ID = '"+id+"'  ) AS XQ," +//消缺
                        "  (SELECT count(1)" +
                        "   FROM KH_YH_HISTORY WHERE trunc(UPDATE_TIME) = trunc(sysdate)  AND YWORG_ID = '"+id+"'  ) AS TZ" +//今日调整
                        "       FROM dual";
                Map<String, Object> map1 = this.execSqlSingleResult(sql, null);

                obj.put("id", id);
                obj.put("value", map1);


            LOGGER.info("隐患信息查询成功");
        }catch (Exception e){
            LOGGER.error("隐患信息查询失败"+e.getMessage());
            return WebApiResponse.erro("隐患信息查询失败"+e.getMessage());
        }
        return WebApiResponse.success(obj);
    }
}
