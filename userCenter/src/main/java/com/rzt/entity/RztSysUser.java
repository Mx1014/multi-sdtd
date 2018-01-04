/**
 * 文件名：RztSysUser
 * 版本信息：
 * 日期：2017/10/10 17:28:27
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.rzt.entity.BaseEntity;
import com.rzt.util.excelUtil.ExcelResources;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 类名称：RztSysUser
 * 类描述：InnoDB free: 537600 kB
 * 创建人：张虎成
 * 创建时间：2017/10/10 17:28:27
 * 修改人：张虎成
 * 修改时间：2017/10/10 17:28:27
 * 修改备注：
 */
@Entity
@Table(name = "RZTSYSUSER")
public class RztSysUser extends BaseEntity implements Serializable {
    //字段描述:
    @Id
    private String id;
    //字段描述: 登录账号
    @Column(name = "USERNAME")
    private String username;

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public int getWorkyear() {
        return workyear;
    }

    public void setWorkyear(int workyear) {
        this.workyear = workyear;
    }

    public int getWorktype() {
        return worktype;
    }

    public void setWorktype(int worktype) {
        this.worktype = worktype;
    }

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    //外协单位ID
    @Column(name = "COMPANYID")
    private String companyid;
    //字段描述: 真实姓名
    @Column(name = "REALNAME")
    private String realname;
    //字段描述:
    @Column(name = "EMAIL")
    private String email;
    //字段描述:
    @Column(name = "PHONE")
    private String phone;
    //字段描述: 头像路径
    @Column(name = "AVATAR")
    private String avatar;
    //字段描述: 部门id
    @Column(name = "DEPTID")
    private String deptid;
    //字段描述: 用户是否删除
    @Column(name = "USERDELETE")
    private int userdelete;
    //字段描述: 身份证
    @Column(name = "CERTIFICATE")
    private String certificate;
    //字段描述: 从业年限
    @Column(name = "WORKYEAR")
    private int workyear;
    //字段描述: 工作性质
    @Column(name = "WORKTYPE")
    private int worktype;
    //字段描述: 编号
    @Column(name = "SERIALNUMBER")
    private String serialnumber;
    //字段描述: 年龄
    @Column(name = "AGE")
    private int age;
    //字段描述: 登录状态 0 未登录 1 登录
    @Column(name = "LOGINSTATUS")
    private int loginstatus;
    //字段描述:创建时间
    @Column(name = "CREATETIME")
    private Date createtime;
    //用户类型
    @Column(name = "USERTYPE")
    private int userType;
    //字段描述: 所属班组
    @Column(name = "CLASSNAME")
    private String className;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @ExcelResources(title = "", order = 2)
    public String getUsername() {
        return this.username;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    @ExcelResources(title = "", order = 3)
    public String getRealname() {
        return this.realname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ExcelResources(title = "", order = 4)
    public String getEmail() {
        return this.email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @ExcelResources(title = "", order = 5)
    public String getPhone() {
        return this.phone;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @ExcelResources(title = "头像路径", order = 6)
    public String getAvatar() {
        return this.avatar;
    }

    public void setDeptid(String deptid) {
        this.deptid = deptid;
    }

    @ExcelResources(title = "", order = 7)
    public String getDeptid() {
        return this.deptid;
    }

    public void setUserdelete(int userdelete) {
        this.userdelete = userdelete;
    }

    @ExcelResources(title = "用户是否删除", order = 8)
    public int getUserdelete() {
        return this.userdelete;
    }

    public void setLoginstatus(int loginstatus) {
        this.loginstatus = loginstatus;
    }

    @ExcelResources(title = "登录状态 0 未登录 1 登录", order = 9)
    public int getLoginstatus() {
        return this.loginstatus;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    @ExcelResources(title = "", order = 10)
    public Date getCreatetime() {
        return this.createtime;
    }

    @ExcelResources(title = "", order = 11)
    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    @ExcelResources(title = "", order = 12)
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @ExcelResources(title = "", order = 13)
    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }
}