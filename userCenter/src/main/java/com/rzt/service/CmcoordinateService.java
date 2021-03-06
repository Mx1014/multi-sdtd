/**
 * 文件名：CMCOORDINATEService
 * 版本信息：
 * 日期：2017/12/20 15:22:15
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.entity.Cmcoordinate;
import com.rzt.repository.CmcoordinateRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;

/**
 * 类名称：CMCOORDINATEService    
 * 类描述：${table.comment}
 * 创建人：张虎成   
 * 创建时间：2017/12/20 15:22:15 
 * 修改人：张虎成    
 * 修改时间：2017/12/20 15:22:15    
 * 修改备注：    
 * @version
 */
@Service
public class CmcoordinateService extends CurdService<Cmcoordinate, CmcoordinateRepository> {

    /***
     * @Method lineCoordinateList
     * @Description 给你一条线路所有的杆塔和坐标
     * @param [lineId]
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @date 2017/12/25 22:13
     * @author nwz
     */
    public List<Map<String, Object>> lineCoordinateList(Long lineId) {
        String sql = "SELECT\n" +
                "  t.line_name,\n" +
                "  tt.NAME      \"name\",\n" +
                "  tt.LONGITUDE \"longtitude\",\n" +
                "  tt.LATITUDE  \"latitude\",\n" +
                "  ttt.V_LEVEL\n" +
                "FROM (SELECT\n" +
                "        id,\n" +
                "        line_name,\n" +
                "        TOWER_ID,\n" +
                "        sort,\n" +
                "        line_id\n" +
                "      FROM CM_LINE_TOWER\n" +
                "      WHERE LINE_ID = ?) t\n" +
                "  JOIN cm_tower tt ON t.TOWER_ID = tt.ID\n" +
                "  join cm_line ttt on t.LINE_ID = ttt.ID\n" +
                "ORDER BY t.sort";
        List<Map<String, Object>> maps = this.execSql(sql, lineId);
        return maps;
    }


    public List<Map<String, Object>> lineCoordinateList(String[] lineId) {
        List params = new ArrayList<>();
        for (String id : lineId) {
            String sql = "SELECT\n" +
                    "  t.line_name,\n" +
                    "  tt.NAME      \"name\",\n" +
                    "  tt.LONGITUDE \"longtitude\",\n" +
                    "  tt.LATITUDE  \"latitude\",\n" +
                    "  ttt.V_LEVEL,t.LINE_ID\"lineId\",tt.ID as towerId\n" +
                    "FROM (SELECT\n" +
                    "        id,\n" +
                    "        line_name,\n" +
                    "        TOWER_ID,\n" +
                    "        sort,\n" +
                    "        line_id\n" +
                    "      FROM CM_LINE_TOWER\n" +
                    "      WHERE LINE_ID =" + id + ") t\n" +
                    "  JOIN cm_tower tt ON t.TOWER_ID = tt.ID\n" +
                    "  join cm_line ttt on t.LINE_ID = ttt.ID\n" +
                    "ORDER BY t.sort";
            List<Map<String, Object>> maps = this.execSql(sql);
            params.add(maps);
        }

        return params;
    }

    public void lineCoordinateListTwo(String[] lineIdArr) {
        /*for(String lineIdAndTdOrgs :lineIdArr) {
            String[] lineIdAndTdOrg = lineIdAndTdOrgs.split("_");
//            lineIdAndTdOrg[0]

        }*/
    }

    /***
     * @Method towerCoordinate
     * @Description 给你一个杆塔的坐标
     * @param [towerId]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @date 2017/12/25 22:13
     * @author nwz
     */
    public Map<String, Object> towerCoordinate(Long towerId) throws Exception {
        String sql = "select t.NAME \"name\",t.LONGITUDE \"longitude\",t.LATITUDE \"latitude\" from cm_tower t where id = ?";
        Map<String, Object> map = this.execSqlSingleResult(sql);
        return map;
    }

    /***
     * @Method getMenAboutLine
     * @Description 拿到这条线路的人
     * @param { currentUserId, startDate]
     * @param deptId
     * @return void
     * @date 2018/1/17 18:36
     * @author nwz
     */
    public List<Map<String, Object>> getMenAboutLine(String deptId, Long lineId) throws Exception {
        List<Map<String, Object>> maps;
        String sql = "";
        if ("err".equals(deptId)) {
            throw new Exception();
        } else if ("all".equals(deptId)) {
            sql = "SELECT DISTINCT userid from (select CM_USER_ID userid from xs_zc_cycle where is_delete = 0 and LINE_ID = ?\n" +
                    "union all\n" +
                    "select USER_ID userid from KH_SITE where LINE_ID = ?) t where t.userid is not null ";
            maps = this.execSql(sql, lineId, lineId);
        } else {
            sql = "SELECT DISTINCT userid from (select CM_USER_ID userid from xs_zc_cycle is_delete = 0  where LINE_ID = ? and TD_ORG = ?\n" +
                    "union all\n" +
                    "select USER_ID userid from KH_SITE where LINE_ID = ? and TDYW_ORGID = ?) t where t.userid is not null ";
            maps = this.execSql(sql, lineId, deptId, lineId, deptId);

        }
        return maps;
    }

    public List<Map<String, Object>> getMenAboutLines(String lineId, String deptId) throws Exception {
        List<Map<String, Object>> maps = new ArrayList<>();
        String sql = "";
        String[] split = lineId.split(",");
        List<Long> lineIds = new ArrayList<>();
        for (String hehe : split) {
            lineIds.add(Long.parseLong(hehe));
        }
        if ("err".equals(deptId)) {
            throw new Exception();
        } else if ("all".equals(deptId)) {
            sql = "SELECT DISTINCT userid from (select CM_USER_ID userid from xs_zc_cycle where is_delete = 0 and LINE_ID in (?1) \n" +
                    "union all\n" +
                    "select USER_ID userid from KH_SITE where LINE_ID in (?1) ) t where t.userid is not null ";
            maps.addAll(this.execSql(sql, lineIds));
        } else {
            sql = "SELECT DISTINCT userid from (select CM_USER_ID userid from xs_zc_cycle where is_delete = 0 and  LINE_ID in (?1) and TD_ORG = ?2\n" +
                    "union all\n" +
                    "select USER_ID userid from KH_SITE where LINE_ID in (?1) and TDYW_ORGID = ?2) t where t.userid is not null ";
            maps.addAll(this.execSql(sql, lineIds, deptId));

        }
        return maps;
    }


    public List<Map<String, Object>> getMenAboutLineMain(String[] lineIdArr, String s1, String loginType, String workType,String userId) {
        List<Map<String, Object>> maps = new ArrayList<>();
        String sql = "";
        List<Long> lineIds = new ArrayList<>();
        List<Object> params = new ArrayList<Object>();
        StringBuffer sqlBuffer = new StringBuffer("");
        int length = lineIdArr.length;
        sqlBuffer.append(" and (");
        for (int i = 0; i < length; i++) {
            String hehe = lineIdArr[i];
            sqlBuffer.append(" line_id = ?  ");
            if (i < length - 1) {
                sqlBuffer.append(" or ");
            }
            params.add(hehe);
        }
        sqlBuffer.append(") " + s1);
        if (!StringUtils.isEmpty(loginType)) {
            sqlBuffer.append(" and LOGINSTATUS ="+loginType);
        }
        if (!StringUtils.isEmpty(workType)) {
            String[] split = workType.split(",");
            sqlBuffer.append("and (");
            for (int i = 0; i < split.length; i++) {
                String hehe = split[i];
                sqlBuffer.append(" worktype = ?  ");
                if (i <  split.length - 1) {
                    sqlBuffer.append(" or ");
                }
                params.add(hehe);
            }
            sqlBuffer.append(")");

        }
        if (!StringUtils.isEmpty(userId)) {
            sqlBuffer.append(" and userid ='"+userId+"'");
        }
        sql = "SELECT DISTINCT userid from (select CM_USER_ID userid,line_id,td_org,u.WORKTYPE,u.LOGINSTATUS  from xs_zc_cycle c LEFT JOIN RZTSYSUSER u on u.ID=c.CM_USER_ID where is_delete = 0 " +
                "UNION ALL " +
                "select USER_ID userid,line_id,tdyw_orgid td_org,u.WORKTYPE,u.LOGINSTATUS from KH_SITE s LEFT JOIN RZTSYSUSER u on u.id = s.USER_ID) t where t.userid is not null\n " + sqlBuffer.toString();
        maps.addAll(this.execSql(sql, params.toArray()));

        return maps;
    }
}