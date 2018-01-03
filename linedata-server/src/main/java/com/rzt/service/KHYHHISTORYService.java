/**    
 * 文件名：KHYHHISTORYService           
 * 版本信息：    
 * 日期：2017/12/27 17:23:43    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.entity.KHYHHISTORY;
import com.rzt.repository.KHYHHISTORYRepository;
import com.rzt.utils.DateUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：KHYHHISTORYService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/27 17:23:43 
 * 修改人：张虎成    
 * 修改时间：2017/12/27 17:23:43    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class KHYHHISTORYService extends CurdService<KHYHHISTORY,KHYHHISTORYRepository> {


    public Page<Map<String, Object>> getYHInfo(Pageable pageable, String tdOrg, String wxOrg, String kv, String lineId,String yhjb, String startTime, String endTime) {
        List<Object> list = new ArrayList<>();
        String sql = "select * from KH_YH_HISTORY WHERE 1=1 ";
        if(tdOrg!=null&&!"".equals(tdOrg.trim())){
            list.add(tdOrg);
            sql += " and yworg_id= ?" + list.size();
        }
        if(wxOrg!=null&&!"".equals(wxOrg.trim())){
            list.add(wxOrg);
            sql += " and wxorg_id= ?" + list.size();
        }
        if(kv!=null&&!"".equals(kv.trim())){
            list.add(kv);
            sql += " and vtype= ?" + list.size();
        }
        if(lineId!=null&&!"".equals(lineId.trim())){
            list.add(lineId);
            sql += " and line_id= ?" + list.size();
        }
        if(yhjb!=null&&!"".equals(yhjb.trim())){
            list.add(yhjb);
            sql += " and yhjb= ?" + list.size();
        }
        if(startTime!=null&&!"".equals(startTime.trim())){
            Date date = DateUtil.parse(startTime+":00", "yyyy-MM-dd HH:mm:ss");
            list.add(date);
            sql += " and YHFXSJ > ?" + list.size();
        }
        if(endTime!=null&&!"".equals(endTime.trim())){
            Date date = DateUtil.parse(endTime+":00", "yyyy-MM-dd HH:mm:ss");
            list.add(date);
            sql += " and YHFXSJ < ?" + list.size();
        }
        return execSqlPage(pageable,sql,list.toArray());
    }
}