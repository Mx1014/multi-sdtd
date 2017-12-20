/**    
 * 文件名：RztSysRoleService           
 * 版本信息：    
 * 日期：2017/10/11 18:51:02    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.service.CurdService;
import com.rzt.entity.RztSysRole;
import com.rzt.repository.RztSysRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**      
 * 类名称：RztSysRoleService    
 * 类描述：InnoDB free: 537600 kB    
 * 创建人：张虎成   
 * 创建时间：2017/10/11 18:51:02 
 * 修改人：张虎成    
 * 修改时间：2017/10/11 18:51:02    
 * 修改备注：    
 * @version        
 */
@Service
public class RztSysRoleService extends CurdService<RztSysRole,RztSysRoleRepository> {

	public List<RztSysRole> findAllRole(){
		return this.reposiotry.findAll();
	}

	public Page<RztSysRole> findByName(String name, Pageable pageable){
		if (StringUtils.isEmpty(name))
			return this.reposiotry.findAll(pageable);
		else{
			name = "%" + name + "%";
			return this.reposiotry.findByRolenameLike(name,pageable);
		}
	}

}