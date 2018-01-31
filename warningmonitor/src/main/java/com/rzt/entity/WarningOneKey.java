package com.rzt.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="WARNING_ONE_KEY")
public class WarningOneKey implements Serializable {

    //字段描述:
    @Id
    private Long id;
    //字段描述: 用户id
    @Column(name = "USER_ID")
    private String userId;
    //字段描述: 创建时间
    @Column(name = "CREATE_TIME")
    private Date createTime;
    //字段描述: 经度
    @Column(name = "LAT")
    private String lat;
    //字段描述: 纬度
    @Column(name = "LON")
    private String lon;
    //字段描述: 告警类型
    @Column(name = "GJLX")
    private String gjlx;
    //字段描述: 告警描述
    @Column(name = "GJMS")
    private String gjms;
    //字段描述: 处理状态 0未处理 1处理中 2已处理
    @Column(name = "STATUS")
    private Integer status;
    //字段描述: 处理中信息
    @Column(name = "CHECK_INFO_Z")
    private String checkInfoZ;
    //字段描述: 已处理信息
    @Column(name = "CHECK_INFO_C")
    private String checkInfoC;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getGjlx() {
        return gjlx;
    }

    public void setGjlx(String gjlx) {
        this.gjlx = gjlx;
    }

    public String getGjms() {
        return gjms;
    }

    public void setGjms(String gjms) {
        this.gjms = gjms;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCheckInfoZ() {
        return checkInfoZ;
    }

    public void setCheckInfoZ(String checkInfoZ) {
        this.checkInfoZ = checkInfoZ;
    }

    public String getCheckInfoC() {
        return checkInfoC;
    }

    public void setCheckInfoC(String checkInfoC) {
        this.checkInfoC = checkInfoC;
    }
}
