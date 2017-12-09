/**    
 * 文件名：PICTURETOURRepository           
 * 版本信息：    
 * 日期：2017/11/29 09:35:42    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rzt.entity.PICTURETOUR;

import java.util.List;

/**
 * 类名称：PICTURETOURRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/29 09:35:42 
 * 修改人：张虎成    
 * 修改时间：2017/11/29 09:35:42    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface PICTURETOURRepository extends JpaRepository<PICTURETOUR,String> {

    List<PICTURETOUR> findBytaskId(String taskId);
}
