/**    
 * 文件名：RztMenuPrivilegeService           
 * 版本信息：    
 * 日期：2017/10/12 10:30:09    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.entity.RztSysMenu;
import com.rzt.service.CurdService;
import com.rzt.entity.RztMenuPrivilege;
import com.rzt.repository.RztMenuPrivilegeRepository;
import com.rzt.utils.DbUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：RztMenuPrivilegeService    
 * 类描述：InnoDB free: 537600 kB    
 * 创建人：张虎成   
 * 创建时间：2017/10/12 10:30:09 
 * 修改人：张虎成    
 * 修改时间：2017/10/12 10:30:09    
 * 修改备注：    
 * @version        
 */
@Service

public class RztMenuPrivilegeService extends CurdService<RztMenuPrivilege,RztMenuPrivilegeRepository> {
	@PersistenceContext
	private EntityManager entityManager;
	@Transactional
	public void  deleteByroleId(String roleId){
		this.reposiotry.deleteByRoleid(roleId);
	}

	public List<RztMenuPrivilege> findRztMenuPrivilege(String roleid){
		return this.reposiotry.findRztMenuPrivilegeByRoleid(roleid);
	}

	public List<Map<String, Object>> findUserPrivilege(String userid){
		String sql = "SELECT DISTINCT s.* FROM rztmenuprivilege m LEFT JOIN rztsysuserrole r ON m.roleid = r.roleId " +
				"LEFT JOIN rztsysmenu s ON m.menuId = s.id " +
				"where r.userId = '" + userid + "'";
		return DbUtil.list(entityManager,sql);
	}

}