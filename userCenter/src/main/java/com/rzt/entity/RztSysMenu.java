/**
 * 文件名：RztSysMenu
 * 版本信息：
 * 日期：2017/09/25 09:58:09
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
 * 类名称：RztSysMenu    
 * 类描述：InnoDB free: 537600 kB    
 * 创建人：张虎成   
 * 创建时间：2017/09/25 09:58:09 
 * 修改人：张虎成    
 * 修改时间：2017/09/25 09:58:09    
 * 修改备注：    
 * @version
 */
@Entity
@Table(name = "RZTSYSMENU")
public class RztSysMenu extends BaseEntity implements Serializable {

    public RztSysMenu() {
    }

    public int getMenutype() {
        return menutype;
    }

    public void setMenutype(int menutype) {
        this.menutype = menutype;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public RztSysMenu(String menuname, String menuurl, String menupid, int lft, int rgt, String id) {
        this.id = id;
        this.menuname = menuname;
        this.menuurl = menuurl;
        this.menuicon = menuicon;
        this.menupid = menupid;
        this.lft = lft;

        this.rgt = rgt;
    }

    //字段描述:
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;
    //字段描述:
    @Column(name = "MENUNAME")
    private String menuname;
    //字段描述:
    @Column(name = "MENUURL")
    private String menuurl;
    //字段描述:
    @Column(name = "MENUICON")
    private String menuicon;
    //字段描述:
    @Column(name = "MENUPID")
    private String menupid;
    //字段描述:
    @Column(name = "lft")
    private int lft;
    //字段描述: 菜单类型 1 列表类型 2 页内tab 3 按钮
    @Column(name = "MENUTYPE")
    private int menutype;
    //字段描述: 类别 1 PC 2 APP
    @Column(name = "type")
    private int type;
    //字段描述:
    @Column(name = "rgt")
    private int rgt;
    //字段描述:
    @Column(name = "MENUDESC")
    private String menudesc;

    @Column(name = "CREATETIME")
    private Date createtime;

    public void setId(String id) {
        this.id = UUID.randomUUID().toString();
    }

    @ExcelResources(title = "", order = 1)
    public String getId() {
        return this.id;
    }

    public void setMenuname(String menuname) {
        this.menuname = menuname;
    }

    @ExcelResources(title = "", order = 2)
    public String getMenuname() {
        return this.menuname;
    }

    public void setMenuurl(String menuurl) {
        this.menuurl = menuurl;
    }

    @ExcelResources(title = "", order = 3)
    public String getMenuurl() {
        return this.menuurl;
    }

    public void setMenuicon(String menuicon) {
        this.menuicon = menuicon;
    }

    @ExcelResources(title = "", order = 4)
    public String getMenuicon() {
        return this.menuicon;
    }

    public void setMenupid(String menupid) {
        this.menupid = menupid;
    }

    @ExcelResources(title = "", order = 5)
    public String getMenupid() {
        return this.menupid;
    }

    public void setLft(int lft) {
        this.lft = lft;
    }

    @ExcelResources(title = "", order = 6)
    public int getLft() {
        return this.lft;
    }

    public void setRgt(int rgt) {
        this.rgt = rgt;
    }

    @ExcelResources(title = "", order = 7)
    public int getRgt() {
        return this.rgt;
    }

    public void setMenudesc(String menudesc) {
        this.menudesc = menudesc;
    }

    @ExcelResources(title = "", order = 8)
    public String getMenudesc() {
        return this.menudesc;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}