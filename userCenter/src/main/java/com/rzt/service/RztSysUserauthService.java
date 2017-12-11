/**    
 * 文件名：RztSysUserauthService           
 * 版本信息：    
 * 日期：2017/10/10 17:28:27    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.service.CurdService;
import com.rzt.entity.RztSysUser;
import com.rzt.entity.RztSysUserauth;
import com.rzt.repository.RztSysUserauthRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

/**      
 * 类名称：RztSysUserauthService    
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
public class RztSysUserauthService extends CurdService<RztSysUserauth,RztSysUserauthRepository> {

	public String findByUserName(RztSysUser user){
		String flag = "1";
		RztSysUserauth userauth = this.reposiotry.findByIdentitytype(user.getEmail());
		if (userauth != null)
			flag = "该邮箱已存在";
		RztSysUserauth userauth1 = this.reposiotry.findByIdentitytype(user.getUsername());
		if (userauth1 != null)
			flag = "该账号已存在";
		RztSysUserauth userauth2 = this.reposiotry.findByIdentitytype(user.getPhone());
		if (userauth2 != null)
			flag = "该手机号已存在";
		return flag;
	}

	public void addUserAuth(RztSysUser user,String password){
		RztSysUserauth userauth = new RztSysUserauth();
		userauth.setCreatetime(new Date());
		userauth.setIdentifier(0);
		userauth.setIdentitytype(user.getUsername());
		userauth.setPassword(password);
		userauth.setUserid(user.getId());
		this.reposiotry.save(userauth);
		if (!StringUtils.isEmpty(user.getPhone())){
			RztSysUserauth userauth1 = new RztSysUserauth();
			userauth1.setCreatetime(new Date());
			userauth1.setIdentifier(1);
			userauth1.setIdentitytype(user.getPhone());
			userauth1.setPassword(password);
			userauth1.setUserid(user.getId());
			this.reposiotry.save(userauth1);
		}
		if (!StringUtils.isEmpty(user.getEmail())){
			RztSysUserauth userauth2 = new RztSysUserauth();
			userauth2.setCreatetime(new Date());
			userauth2.setIdentifier(2);
			userauth2.setIdentitytype(user.getEmail());
			userauth2.setPassword(password);
			userauth2.setUserid(user.getId());
			this.reposiotry.save(userauth2);
		}
	}
	public void updateLoginIp(String loginId, String userid,int identifier){
		this.reposiotry.updateLoginIp(loginId,userid,identifier);
	}

	public void updateUserAuth(String userid,String password){
		this.reposiotry.updatePasswordByUserid(userid,password);
	}

	public void deleteAuthByUserId(String userId){
		this.reposiotry.deleteAuthByUserid(userId);
	}

	public void deleteBatchAuthByUserId(String ids){
		this.reposiotry.deleteBatchAuthByUseiId(ids);
	}

	public RztSysUserauth findByIdentifierAndIdentityTypeAndPassword(int identifier, String identityType, String password){
		return this.reposiotry.findByIdentifierAndIdentitytypeAndPassword(identifier,identityType,password);
	}
}