package com.rzt.entity;

import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 类名称：WARNINGONEKEY
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2018/01/06 15:10:39
 * 修改人：张虎成
 * 修改时间：2018/01/06 15:10:39
 * 修改备注：
 */
@Entity
@Table(name = "WARNING_ONE_KEY")
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

    public void setId() {
        if (id == null || id == 0) {
            this.id = new SnowflakeIdWorker(0, 0).nextId();
        } else {
            this.id = id;
        }
    }

    public Long getId() {
        return this.id;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }


    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTime() {
        return this.createTime;
    }


    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLat() {
        return this.lat;
    }


    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLon() {
        return this.lon;
    }


    public void setGjlx(String gjlx) {
        this.gjlx = gjlx;
    }

    public String getGjlx() {
        return this.gjlx;
    }


    public void setGjms(String gjms) {
        this.gjms = gjms;
    }

    public String getGjms() {
        return this.gjms;
    }


}

