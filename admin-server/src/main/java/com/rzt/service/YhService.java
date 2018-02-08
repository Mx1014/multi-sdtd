package com.rzt.service;

import com.rzt.entity.TimedTask;
import com.rzt.repository.XSZCTASKRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 李成阳
 * 2018/1/31
 */
@Service
public class YhService extends CurdService<TimedTask,XSZCTASKRepository>{

    protected static Logger LOGGER = LoggerFactory.getLogger(YhService.class);


    public WebApiResponse findYh(){
        List list = new ArrayList();
        String s = "";
        String sql = " SELECT * " +
                "FROM (SELECT " +
                "        sum(decode(YHJB1, '树木隐患', 1, 0)) AS shu, " +
                "        sum(decode(YHJB1, '建筑隐患', 1, 0)) AS jian, " +
                "        sum(decode(YHJB1, '异物隐患', 1, 0)) AS yi, " +
                "        sum(decode(YHJB1, '施工隐患', 1, 0)) AS shi, " +
                "        YWORG_ID  FROM KH_YH_HISTORY WHERE 1 = 1 AND YHZT = 0 " +
                "      GROUP BY YWORG_ID) a RIGHT JOIN (SELECT t.ID, t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL  " + s +
                "                                       ORDER BY t.DEPTSORT) r  ON a.YWORG_ID = r.ID ";
        try {
            return WebApiResponse.success(this.execSql(sql));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    public WebApiResponse findYh1(){
        List list = new ArrayList();
        String sql = " SELECT * " +
                "FROM (SELECT " +
                "        sum(decode(YHJB1, '树木隐患', 1, 0)) AS shu, " +
                "        sum(decode(YHJB1, '建筑隐患', 1, 0)) AS jian, " +
                "        sum(decode(YHJB1, '异物隐患', 1, 0)) AS yi, " +
                "        sum(decode(YHJB1, '施工隐患', 1, 0)) AS shi, " +
                "        YWORG_ID  FROM KH_YH_HISTORY WHERE 1 = 1 AND YHZT = 0 " +
                "      GROUP BY YWORG_ID) a RIGHT JOIN (SELECT t.ID, t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL  " +
                "                                       ORDER BY t.DEPTSORT) r  ON a.YWORG_ID = r.ID ";
        try {
            List<Map<String, Object>> maps = this.execSql(sql);
            int SHU = 0;
            int JIAN = 0;
            int YI = 0;
            int SHI = 0;
            for (Map<String, Object> map : maps) {
                SHU += Integer.parseInt(map.get("SHU")==null?"0":map.get("SHU").toString());
                JIAN += Integer.parseInt(map.get("JIAN")==null?"0":map.get("JIAN").toString());
                YI+= Integer.parseInt(map.get("YI") ==null ? "0" : map.get("YI").toString());
                SHI += Integer.parseInt(map.get("SHI")==null ? "0": map.get("SHI").toString());
            }
            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            stringObjectHashMap.put("SHU",SHU);
            stringObjectHashMap.put("JIAN",JIAN);
            stringObjectHashMap.put("YI",YI);
            stringObjectHashMap.put("SHI",SHI);

            String sql1 = "SELECT" +
                    "  (SELECT count(1) FROM KH_YH_HISTORY k WHERE trunc(k.CREATE_TIME) = trunc(sysdate)) AS JRCJ," +
                    "  (SELECT count(1) FROM KH_YH_HISTORY k WHERE trunc(k.YHXQ_TIME) = trunc(sysdate)) AS YHXQ," +
                    "  (SELECT count(1) FROM KH_YH_HISTORY k WHERE trunc(k.UPDATE_TIME) = trunc(sysdate)) AS YHUPDATE" +
                    "   FROM dual";

            Map<String, Object> map = this.execSqlSingleResult(sql1);
            stringObjectHashMap.put("YHCJ",Integer.parseInt(map.get("JRCJ") == null ? "0" : map.get("JRCJ").toString()));
            stringObjectHashMap.put("YHXQ",Integer.parseInt(map.get("YHXQ") == null ? "0" : map.get("YHXQ").toString()));
            stringObjectHashMap.put("YHUPDATE",Integer.parseInt(map.get("YHUPDATE") == null ? "0" : map.get("YHUPDATE").toString()));
            return WebApiResponse.success(stringObjectHashMap);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
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

    public Page<Map<String,Object>> getYHInfo(Pageable pageable, String kv, String lineId, String yhjb,  String deptId) {
        List<Object> list = new ArrayList<>();
        String sql = "select * from KH_YH_HISTORY WHERE yhzt=0 ";

        if (deptId != null && !"".equals(deptId.trim())) {
            list.add(deptId);
            sql += " and yworg_id= ?" + list.size();
        }
        if (kv != null && !"".equals(kv.trim())) {
            list.add(kv);
            sql += " and vtype= ?" + list.size();
        }
        if (lineId != null && !"".equals(lineId.trim())) {
            list.add(lineId);
            sql += " and line_id= ?" + list.size();
        }
        if (yhjb != null && !"".equals(yhjb.trim())) {
            list.add("%"+yhjb+"%");
            sql += " and yhjb1 like ?" + list.size();
        }

        sql +=" order by create_time desc";
        return execSqlPage(pageable, sql, list.toArray());


    }
}
