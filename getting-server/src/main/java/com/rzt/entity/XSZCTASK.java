/**
 * 文件名：XsTxbdTask           
 * 版本信息：    
 * 日期：2017/12/26 16:46:02    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import com.rzt.entity.BaseEntity;
import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "XS_ZC_TASK")
public class XSZCTASK implements Serializable{
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "TASK_NAME")
    private String taskName;
    @Column(name = "CM_USER_ID")
    private String cmUserId;
    @Column(name = "TD_ORG")
    private String tdOrg;
    @Column(name = "WX_ORG")
    private String wxOrg;
    @Column(name = "CLASS_ID")
    private String classId;
    @Column(name = "XS_ZC_CYCLE_ID")
    private Long xsZcCycleId;
    @Column(name = "STAUTS")
    private Integer status;
    @Column(name = "PLAN_XS_NUM")
    private Integer planXsNum;
    @Column(name = "REAL_XS_NUM")
    private Integer realXsNum;
    @Column(name = "PLAN_START_TIME")
    private Date planStartTime;
    @Column(name = "PLAN_END_TIME")
    private Date planEndTime;
    @Column(name = "REAL_START_TIME")
    private Date realStartTime;
    @Column(name = "SFQR_TIME")
    private Date sfqrTime;
    @Column(name = "DDXC_TIME")
    private Date ddxcTime;
    @Column(name = "XSKS_TIME")
    private Date xsksTime;
    @Column(name = "REAL_END_TIME")
    private Date realEndTime;
    @Column(name = "TASK_NUM_IN_CYCLE")
    private Long tasknumInCycle;
    @Column(name = "ZXYS_NUM")
    private Integer zxysNum;
    @Column(name = "XSCS_NUM")
    private Long xscsNum;
    @Column(name = "PD_TIME")
    private Date pdTime;
    @Column(name = "WPTX_TIME")
    private Date wptxTime;
    @Column(name = "JC_USER_ID")
    private String jcUserId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCmUserId() {
        return cmUserId;
    }

    public void setCmUserId(String cmUserId) {
        this.cmUserId = cmUserId;
    }

    public String getTdOrg() {
        return tdOrg;
    }

    public void setTdOrg(String tdOrg) {
        this.tdOrg = tdOrg;
    }

    public String getWxOrg() {
        return wxOrg;
    }

    public void setWxOrg(String wxOrg) {
        this.wxOrg = wxOrg;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Long getXsZcCycleId() {
        return xsZcCycleId;
    }

    public void setXsZcCycleId(Long xsZcCycleId) {
        this.xsZcCycleId = xsZcCycleId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPlanXsNum() {
        return planXsNum;
    }

    public void setPlanXsNum(Integer planXsNum) {
        this.planXsNum = planXsNum;
    }

    public Integer getRealXsNum() {
        return realXsNum;
    }

    public void setRealXsNum(Integer realXsNum) {
        this.realXsNum = realXsNum;
    }

    public Date getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Date getRealStartTime() {
        return realStartTime;
    }

    public void setRealStartTime(Date realStartTime) {
        this.realStartTime = realStartTime;
    }

    public Date getSfqrTime() {
        return sfqrTime;
    }

    public void setSfqrTime(Date sfqrTime) {
        this.sfqrTime = sfqrTime;
    }

    public Date getDdxcTime() {
        return ddxcTime;
    }

    public void setDdxcTime(Date ddxcTime) {
        this.ddxcTime = ddxcTime;
    }

    public Date getXsksTime() {
        return xsksTime;
    }

    public void setXsksTime(Date xsksTime) {
        this.xsksTime = xsksTime;
    }

    public Date getRealEndTime() {
        return realEndTime;
    }

    public void setRealEndTime(Date realEndTime) {
        this.realEndTime = realEndTime;
    }

    public Long getTasknumInCycle() {
        return tasknumInCycle;
    }

    public void setTasknumInCycle(Long tasknumInCycle) {
        this.tasknumInCycle = tasknumInCycle;
    }

    public Integer getZxysNum() {
        return zxysNum;
    }

    public void setZxysNum(Integer zxysNum) {
        this.zxysNum = zxysNum;
    }

    public Long getXscsNum() {
        return xscsNum;
    }

    public void setXscsNum(Long xscsNum) {
        this.xscsNum = xscsNum;
    }

    public Date getPdTime() {
        return pdTime;
    }

    public void setPdTime(Date pdTime) {
        this.pdTime = pdTime;
    }

    public Date getWptxTime() {
        return wptxTime;
    }

    public void setWptxTime(Date wptxTime) {
        this.wptxTime = wptxTime;
    }

    public String getJcUserId() {
        return jcUserId;
    }

    public void setJcUserId(String jcUserId) {
        this.jcUserId = jcUserId;
    }

    public Integer getJcStatus() {
        return jcStatus;
    }

    public void setJcStatus(Integer jcStatus) {
        this.jcStatus = jcStatus;
    }

    @Column(name = "JC_STATUS")
    private Integer jcStatus
            ;


}