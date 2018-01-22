/**    
 * 文件名：CMLINETOWERService           
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.entity.CMLINETOWER;
import com.rzt.repository.CMLINETOWERRepository;
import com.rzt.util.WebApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：CMLINETOWERService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
@Service
public class CMLINETOWERService extends CurdService<CMLINETOWER,CMLINETOWERRepository> {
    
    public WebApiResponse getLineTowerPosition(Pageable pageable, String tdOrg, String kv, String lineId) {
        List<String> list = new ArrayList<>();
        Object[] objects = list.toArray();
        String sql = "select l.v_level,l.line_name,l.section,LT.tower_name,t.longitude,t.latitude from cm_tower t " +
                "                left join cm_line_tower lt on LT.tower_id=t.id" +
                "                left join cm_line l on l.id=LT.line_id where 1=1 ";
        if(tdOrg!=null&&!"".equals(tdOrg.trim())){
            list.add(tdOrg);
            sql += " and l.td_org= ?" + list.size();
        }
        if(kv!=null&&!"".equals(kv.trim())){
            list.add(kv);
            sql += " and l.v_level= ?" + list.size();
        }
        if(lineId!=null&&!"".equals(lineId.trim())){
            list.add(lineId);
            sql += " and l.id = ?" + list.size();
        }
        sql += " order by lt.sort";
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql,list.toArray());
        return WebApiResponse.success(maps);
    }
}