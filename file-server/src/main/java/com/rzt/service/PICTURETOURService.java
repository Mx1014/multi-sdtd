/**    
 * 文件名：PICTURETOURService           
 * 版本信息：    
 * 日期：2017/11/29 09:35:42    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.entity.PICTURETOUR;
import com.rzt.repository.PICTURETOURRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**      
 * 类名称：PICTURETOURService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/11/29 09:35:42 
 * 修改人：张虎成    
 * 修改时间：2017/11/29 09:35:42    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class PICTURETOURService extends CurdService<PICTURETOUR,PICTURETOURRepository> {


    public List<PICTURETOUR> findBytaskId(String taskId) {
        return reposiotry.findBytaskId(taskId);
    }
}