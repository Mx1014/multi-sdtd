/**    
 * 文件名：PictureWarnRepository           
 * 版本信息：    
 * 日期：2018/01/21 03:31:41    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;

import com.rzt.entity.PictureWarn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名称：PictureWarnRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2018/01/21 03:31:41 
 * 修改人：张虎成    
 * 修改时间：2018/01/21 03:31:41    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface PictureWarnRepository extends JpaRepository<PictureWarn,String> {

 List<PictureWarn> findBytaskId(Long taskId);

 PictureWarn findById(Long id);

 void deleteById(Long id);
}
