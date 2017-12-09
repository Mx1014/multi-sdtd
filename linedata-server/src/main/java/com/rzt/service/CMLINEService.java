/**    
 * 文件名：CMLINEService           
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.entity.CMLINE;
import com.rzt.repository.CMLINERepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**      
 * 类名称：CMLINEService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class CMLINEService extends CurdService<CMLINE,CMLINERepository> {

    public Page<Map<String, Object>> test(){
        Pageable pageable = new PageRequest(0,3,null);
        return this.execSqlPage(pageable, "select * from kh_yh_history");
    }

}