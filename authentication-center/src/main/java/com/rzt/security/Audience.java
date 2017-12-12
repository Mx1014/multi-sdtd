package com.rzt.security;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.Department;
import com.rzt.entity.Menu;
import com.rzt.entity.Role;
import com.rzt.entity.User;

import java.util.List;

/**
 * Created by 张虎成 on 2016/12/20.
 */
public class Audience {
    private User user;

    private Department department;

    private String access_token;

    private List<Role> roleList;

    private List<Menu> menuList;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }

    public String toString(){
       return JSONObject.toJSONString(this);
    }
}
