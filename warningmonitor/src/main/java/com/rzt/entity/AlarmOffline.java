package com.rzt.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "ALARM_OFFLINE")
public class AlarmOffline implements Serializable {
    @Id
    private Long id;
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "OFFLINE_FREQUENCY")
    private Integer offLineFrequency;
    @Column(name = "OFFLINE_TIME_LONG")
    private Integer offLineTimeLong;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "OFFLINE_END_TIME")
    private Date offLineEndTime;
    @Column(name = "LAST_FLUSH_TIME")
    private Date lastflushtime;

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

    public Integer getOffLineFrequency() {
        return offLineFrequency;
    }

    public void setOffLineFrequency(Integer offLineFrequency) {
        this.offLineFrequency = offLineFrequency;
    }

    public Integer getOffLineTimeLong() {
        return offLineTimeLong;
    }

    public void setOffLineTimeLong(Integer offLineTimeLong) {
        this.offLineTimeLong = offLineTimeLong;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getOffLineEndTime() {
        return offLineEndTime;
    }

    public void setOffLineEndTime(Date offLineEndTime) {
        this.offLineEndTime = offLineEndTime;
    }

    public Date getLastflushtime() {
        return lastflushtime;
    }

    public void setLastflushtime(Date lastflushtime) {
        this.lastflushtime = lastflushtime;
    }
}
