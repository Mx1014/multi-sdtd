/**    
 * 文件名：RztMenuPrivilegeRepository           
 * 版本信息：    
 * 日期：2017/10/12 10:30:09    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;

import com.rzt.entity.RztMenuPrivilege;
import com.rzt.entity.RztSysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 类名称：RztMenuPrivilegeRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/10/12 10:30:09 
 * 修改人：张虎成    
 * 修改时间：2017/10/12 10:30:09    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface RztMenuPrivilegeRepository extends JpaRepository<RztMenuPrivilege,String> {

	public void  deleteByRoleid(String roleId);

	public List<RztMenuPrivilege> findRztMenuPrivilegeByRoleid(String roleid);

}
