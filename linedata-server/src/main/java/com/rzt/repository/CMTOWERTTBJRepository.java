/**    
 * 文件名：CMTOWERTTBJRepository           
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.rzt.entity.CMTOWERTTBJ;

import java.util.List;

/**
 * 类名称：CMTOWERTTBJRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface CMTOWERTTBJRepository extends JpaRepository<CMTOWERTTBJ,String> {
     @Query(value = "select distinct line_id from cm_line_section where is_del=0 and td_org = ?1",nativeQuery = true)
    List<Object> getIdsByTdorg(String tdOrg);
}
