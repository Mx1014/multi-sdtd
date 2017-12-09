/**    
 * 文件名：RztSysUsergroupRepository           
 * 版本信息：    
 * 日期：2017/10/12 11:27:38    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;

import com.rzt.entity.RztSysUsergroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 类名称：RztSysUsergroupRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/10/12 11:27:38 
 * 修改人：张虎成    
 * 修改时间：2017/10/12 11:27:38    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface RztSysUsergroupRepository extends JpaRepository<RztSysUsergroup,String> {
}
