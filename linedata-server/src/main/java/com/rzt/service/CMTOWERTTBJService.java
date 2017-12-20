/**    
 * 文件名：CMTOWERTTBJService           
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.entity.CMTOWERTTBJ;
import com.rzt.repository.CMTOWERTTBJRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**      
 * 类名称：CMTOWERTTBJService    
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
public class CMTOWERTTBJService extends CurdService<CMTOWERTTBJ,CMTOWERTTBJRepository> {


    public List<Object> getIdsByTdorg(String tdOrg) {
        return reposiotry.getIdsByTdorg(tdOrg);
    }
}