package com.rzt.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 李成阳
 * 2018/1/4
 */
@Entity
@Table(name = "TIMED_CONFIG")
public class TimedConfig implements Serializable {

    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "DAY_TIME")
    private String dayTime;
    @Column(name = "NIGHT_TIME")
    private String nightTime;
    @Column(name = "DAY_ZQ")
    private String dayZQ;
    @Column(name = "NIGHT_ZQ")
    private String nightZQ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }

    public String getNightTime() {
        return nightTime;
    }

    public void setNightTime(String nightTime) {
        this.nightTime = nightTime;
    }

    public String getDayZQ() {
        return dayZQ;
    }

    public void setDayZQ(String dayZQ) {
        this.dayZQ = dayZQ;
    }

    public String getNightZQ() {
        return nightZQ;
    }

    public void setNightZQ(String nightZQ) {
        this.nightZQ = nightZQ;
    }
}
