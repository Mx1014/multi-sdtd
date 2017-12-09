/**    
 * 文件名：RztSysUserService           
 * 版本信息：    
 * 日期：2017/10/10 17:28:27    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.service.CurdService;
import com.rzt.entity.RztSysUser;
import com.rzt.repository.RztSysUserRepository;
import com.rzt.utils.DbUtil;
import com.rzt.utils.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：RztSysUserService    
 * 类描述：InnoDB free: 537600 kB    
 * 创建人：张虎成   
 * 创建时间：2017/10/10 17:28:27 
 * 修改人：张虎成    
 * 修改时间：2017/10/10 17:28:27    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class RztSysUserService extends CurdService<RztSysUser,RztSysUserRepository> {
	@PersistenceContext
	private EntityManager entityManager;

	public Page<RztSysUser> findByName(String name, Pageable pageable){
		if (StringUtils.isEmpty(name))
			return this.reposiotry.findAll(pageable);
		else{
			name = "%" + name + "%";
			return this.reposiotry.findByRealnameLike(name,pageable);
		}
	}

	public List<Map<String,Object>> findUserList(int page,int size){
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT GROUP_CONCAT(r.id) roleid,GROUP_CONCAT(r.roleName) roleName,u.* from " +
				"rztsysuser u LEFT JOIN rztsysuserrole l ON u.id = " +
				"l.userId LEFT JOIN rztsysrole r on l.roleId = r.id GROUP BY u.id");
		builder.append(PageUtil.getLimit(page,size));
		return DbUtil.list(entityManager,builder.toString());
	}

	public RztSysUser findByUsernameAndDeptid(String username,String deptid){
		return this.reposiotry.findByUsernameAndDeptid(username,deptid);
	}

}