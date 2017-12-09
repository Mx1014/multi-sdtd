/**    
 * 文件名：RztSysRoleRepository           
 * 版本信息：    
 * 日期：2017/10/11 18:51:02    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;

import com.rzt.entity.RztSysRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名称：RztSysRoleRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/10/11 18:51:02 
 * 修改人：张虎成    
 * 修改时间：2017/10/11 18:51:02    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface RztSysRoleRepository extends JpaRepository<RztSysRole,String> {
 	Page<RztSysRole> findByRolenameLike(String name, Pageable pageable);

}
