package com.rzt.security;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysDepartment;
import com.rzt.entity.RztSysMenu;
import com.rzt.entity.RztSysRole;
import com.rzt.entity.RztSysUser;

import java.util.List;

/**
 * Created by 张虎成 on 2016/12/20.
 */
public class Audience {
    private RztSysUser user;

    private RztSysDepartment department;

    private String access_token;

    private List<RztSysRole> roleList;

    private List<RztSysMenu> menuList;

	public RztSysUser getUser() {
		return user;
	}

	public void setUser(RztSysUser user) {
		this.user = user;
	}

	public RztSysDepartment getDepartment() {
		return department;
	}

	public void setDepartment(RztSysDepartment department) {
		this.department = department;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public List<RztSysRole> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<RztSysRole> roleList) {
		this.roleList = roleList;
	}

	public List<RztSysMenu> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<RztSysMenu> menuList) {
		this.menuList = menuList;
	}
}
