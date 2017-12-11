/**    
 * 文件名：RztSysUserRepository           
 * 版本信息：    
 * 日期：2017/10/10 17:28:27    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;

import com.rzt.entity.RztSysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 类名称：RztSysUserRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/10/10 17:28:27 
 * 修改人：张虎成    
 * 修改时间：2017/10/10 17:28:27    
 * 修改备注：    
 * @version        
 */
@Repository
public interface RztSysUserRepository extends JpaRepository<RztSysUser,String> {

	Page<RztSysUser> findByRealnameLike(String name, Pageable pageable);

	RztSysUser findByUsernameAndDeptid(String username,String deptid);
}
