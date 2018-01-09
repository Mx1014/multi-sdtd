/**    
 * 文件名：MONITORCHECKEJService           
 * 版本信息：    
 * 日期：2018/01/08 11:06:23    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.entity.Monitorcheckej;
import com.rzt.repository.Monitorcheckejrepository;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**      
 * 类名称：MONITORCHECKEJService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2018/01/08 11:06:23 
 * 修改人：张虎成    
 * 修改时间：2018/01/08 11:06:23    
 * 修改备注：    
 * @version        
 */
@Service
public class Monitorcheckejservice extends CurdService<Monitorcheckej,Monitorcheckejrepository> {

    @Autowired
    private Monitorcheckejrepository resp;


    @Transactional
    public void saveCheckEj(String[] messages) {
        Monitorcheckej ej = new Monitorcheckej();
        ej.setId(Long.valueOf(new SnowflakeIdWorker(0,0).nextId()));
        ej.setCreateTime(new Date());
        ej.setTaskType(messages[1]);
        ej.setTaskId(messages[2]);
        resp.save(ej);
    }
}