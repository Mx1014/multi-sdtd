/**    
 * 文件名：CMCOORDINATEService           
 * 版本信息：    
 * 日期：2017/12/20 15:22:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.entity.Cmcoordinate;
import com.rzt.repository.CmcoordinateRepository;
import org.springframework.stereotype.Service;

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
    * @Description   给你一条线路所有的杆塔和坐标
    * @param [lineId]
    * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
    * @date 2017/12/25 22:13
    * @author nwz
    */
    public List<Map<String,Object>> lineCoordinateList(Long lineId) {
        String sql = "select tt.NAME \"name\",tt.LONGITUDE \"longtitude\",tt.LATITUDE \"latitude\" from (select id,TOWER_ID from CM_LINE_TOWER WHERE LINE_ID = ?) t join cm_tower tt on t.TOWER_ID = tt.ID";
        List<Map<String, Object>> maps = this.execSql(sql, lineId);
        return maps;
    }

    /***
    * @Method towerCoordinate
    * @Description   给你一个杆塔的坐标
    * @param [towerId]
    * @return java.util.Map<java.lang.String,java.lang.Object>
    * @date 2017/12/25 22:13
    * @author nwz
    */
    public Map<String,Object> towerCoordinate(Long towerId) throws Exception {
            String sql = "select t.NAME \"name\",t.LONGITUDE \"longitude\",t.LATITUDE \"latitude\" from cm_tower t where id = ?";
            Map<String, Object> map = this.execSqlSingleResult(sql);
            return map;
    }
    /***
    * @Method getMenAboutLine
    * @Description 拿到这条线路的人
    * @param [lineId, currentUserId, startDate]
    * @param deptId
     * @return void
    * @date 2018/1/17 18:36
    * @author nwz
    */
    public List<Map<String, Object>> getMenAboutLine(String deptId, Long lineId) throws Exception {
        List<Map<String, Object>> maps;
        String sql = "";
        if("err".equals(deptId)) {
            throw new Exception();
        } else if("all".equals(deptId)) {
            sql = "SELECT DISTINCT userid from (select CM_USER_ID userid from xs_zc_cycle where is_delete = 0 and LINE_ID = ?\n" +
                    "union all\n" +
                    "select USER_ID userid from KH_SITE where LINE_ID = ?) t where t.userid is not null ";
            maps = this.execSql(sql, lineId, lineId);
        } else {
            sql = "SELECT DISTINCT userid from (select CM_USER_ID userid from xs_zc_cycle is_delete = 0  where LINE_ID = ? and TD_ORG = ?\n" +
                    "union all\n" +
                    "select USER_ID userid from KH_SITE where LINE_ID = ? and TDYW_ORGID = ?) t where t.userid is not null ";
            maps = this.execSql(sql,lineId,deptId,lineId,deptId);

        }
        return maps;
    }

    public List<Map<String, Object>> getMenAboutLines(String lineId,String deptId) throws Exception {
        List<Map<String, Object>> maps = new ArrayList<>();
        String sql = "";
        String[] split = lineId.split(",");
        List<Long> lineIds = new ArrayList<>();
        for (String hehe:split) {
            lineIds.add(Long.parseLong(hehe));
        }
        if("err".equals(deptId)) {
            throw new Exception();
        } else if("all".equals(deptId)) {
            sql = "SELECT DISTINCT userid from (select CM_USER_ID userid from xs_zc_cycle where is_delete = 0 and LINE_ID in (?1) \n" +
                    "union all\n" +
                    "select USER_ID userid from KH_SITE where LINE_ID in (?1) ) t where t.userid is not null ";
            maps.addAll(this.execSql(sql, lineIds));
        } else {
            sql = "SELECT DISTINCT userid from (select CM_USER_ID userid from xs_zc_cycle where is_delete = 0 and  LINE_ID in (?1) and TD_ORG = ?2\n" +
                    "union all\n" +
                    "select USER_ID userid from KH_SITE where LINE_ID in (?1) and TDYW_ORGID = ?2) t where t.userid is not null ";
            maps.addAll(this.execSql(sql,lineIds,deptId));

        }
        return maps;
    }
}