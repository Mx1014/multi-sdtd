/**
 * 文件名：Cmcoordinate
 * 版本信息：
 * 日期：2017/12/20 15:22:15
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.entity;

import com.rzt.utils.SnowflakeIdWorker;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 类名称：Cmcoordinate
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/20 15:22:15
 * 修改人：张虎成
 * 修改时间：2017/12/20 15:22:15
 * 修改备注：
 */
@Entity
@Table(name = "CM_COORDINATE")
public class Cmcoordinate {
    //字段描述:
    @Id
    private String id;
    //字段描述: 用户id
    @Column(name = "USERID")
    private String userid;
    //字段描述: 经度
    @Column(name = "LONGITUDE")
    private float longitude;
    //字段描述: 纬度
    @Column(name = "LATITUDE")
    private float latitude;
    //字段描述: 创建日期
    @Column(name = "CREATETIME")
    private Date createtime;
    //字段描述: 手机串号
    @Column(name = "IMEI2")
    private String imei2;
    //字段描述: 工作类型
    @Column(name = "gzlx")
    private Integer gzlx;
    //字段描述: 用户名
    @Column(name = "user_name")
    private Integer userName;
    //字段描述: 在线 离线
    @Column(name = "on_line")
    private boolean onLine;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserid() {
        return this.userid;
    }


    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLongitude() {
        return this.longitude;
    }


    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLatitude() {
        return this.latitude;
    }


    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getCreatetime() {
        return this.createtime;
    }

    public String getImei2() {
        return imei2;
    }

    public void setImei2(String imei2) {
        this.imei2 = imei2;
    }

    public Integer getGzlx() {
        return gzlx;
    }

    public void setGzlx(Integer gzlx) {
        this.gzlx = gzlx;
    }

    public Integer getUserName() {
        return userName;
    }

    public void setUserName(Integer userName) {
        this.userName = userName;
    }

    public boolean isOnLine() {
        return onLine;
    }

    public void setOnLine(boolean onLine) {
        this.onLine = onLine;
    }
}
