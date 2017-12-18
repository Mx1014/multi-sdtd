/**
 * 文件名：RztSysRole
 * 版本信息：
 * 日期：2017/10/11 18:51:02
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.entity;

import com.rzt.entity.BaseEntity;
import com.rzt.util.excelUtil.ExcelResources;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 类名称：RztSysRole
 * 类描述：InnoDB free: 537600 kB
 * 创建人：张虎成
 * 创建时间：2017/10/11 18:51:02
 * 修改人：张虎成
 * 修改时间：2017/10/11 18:51:02
 * 修改备注：
 */
@Entity
@Table(name = "RZTSYSROLE")
public class RztSysRole extends BaseEntity implements Serializable {
    //字段描述:
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;
    //字段描述:
    @Column(name = "ROLENAME")
    private String rolename;
    //字段描述:
    @Column(name = "ROLEDESC")
    private String roledesc;
    //字段描述:
    @Column(name = "CREATETIME")
    private Date createtime;
    @Column(name = "ROLENODEID")
    private String rolenodeid;

    @ExcelResources(title = "", order = 5)
    public String getRolenodeid() {
        return rolenodeid;
    }

    public void setRolenodeid(String rolenodeid) {
        this.rolenodeid = rolenodeid;
    }

    public void setId(String id) {
        this.id = UUID.randomUUID().toString();
    }

    @ExcelResources(title = "", order = 1)
    public String getId() {
        return this.id;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    @ExcelResources(title = "", order = 2)
    public String getRolename() {
        return this.rolename;
    }

    public void setRoledesc(String roledesc) {
        this.roledesc = roledesc;
    }

    @ExcelResources(title = "", order = 3)
    public String getRoledesc() {
        return this.roledesc;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    @ExcelResources(title = "", order = 4)
    public Date getCreatetime() {
        return this.createtime;
    }

}