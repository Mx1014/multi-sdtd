/**
 * 文件名：XSZCTASK
 * 版本信息：
 * 日期：2017/12/05 10:02:41
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.entity.app;

import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 类名称：XSZCTASK
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/05 10:02:41
 * 修改人：张虎成
 * 修改时间：2017/12/05 10:02:41
 * 修改备注：
 */
@Entity
@Table(name = "XS_ZC_TASK")
public class XSZCTASK implements Serializable {
    //字段描述: 通道单位
    @Column(name = "TD_ORG")
    private String tdOrg;
    //字段描述: 外协单位
    @Column(name = "WX_ORG")
    private String wxOrg;
    //字段描述: 班组
    @Column(name = "CLASS_ID")
    private String classId;
    //字段描述: 正常巡视任务周期id
    @Column(name = "XS_ZC_CYCLE_ID")
    private Long xsZcCycleId;
    //字段描述: 任务状态0  待办 1进行中 2已完成
    @Column(name = "STAUTS")
    private String stauts;
    //字段描述: 巡视频率 每次任务中巡视应该重复的次数
    @Column(name = "PLAN_XS_NUM")
    private Integer planXsNum;
    //字段描述: 本次巡视重复次数
    @Column(name = "REAL_XS_NUM")
    private Integer realXsNum;
    //字段描述: 计划开始时间
    @Column(name = "PLAN_START_TIME")
    private Date planStartTime;
    //字段描述: 计划结束时间
    @Column(name = "PLAN_END_TIME")
    private Date planEndTime;
    //字段描述: 实际开始时间
    @Column(name = "REAL_START_TIME")
    private Date realStartTime;
    //字段描述: 身份确认时间
    @Column(name = "SFQR_TIME")
    private Date sfqrTime;
    //字段描述: 到达现场时间
    @Column(name = "DDXC_TIME")
    private Date ddxcTime;
    //字段描述: 巡视开始时间
    @Column(name = "XSKS_TIME")
    private Date xsksTime;
    //字段描述: 实际结束时间
    @Column(name = "REAL_END_TIME")
    private Date realEndTime;
    //字段描述: 周期内第多少次任务
    @Column(name = "TASK_NUM_IN_CYCLE")
    private String taskNumInCycle;
    //字段描述: id
    @Id
    private Long id;
    //字段描述: 任务名称
    @Column(name = "TASK_NAME")
    private String taskName;
    //字段描述: 任务执行人
    @Column(name = "CM_USER_ID")
    private String cmUserId;

    public void setTdOrg(String tdOrg) {
        this.tdOrg = tdOrg;
    }

    @ExcelResources(title = "通道单位", order = 1)
    public String getTdOrg() {
        return this.tdOrg;
    }

    public void setWxOrg(String wxOrg) {
        this.wxOrg = wxOrg;
    }

    @ExcelResources(title = "外协单位", order = 2)
    public String getWxOrg() {
        return this.wxOrg;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    @ExcelResources(title = "班组", order = 3)
    public String getClassId() {
        return this.classId;
    }

    public void setXsZcCycleId(Long xsZcCycleId) {
        this.xsZcCycleId = xsZcCycleId;
    }

    @ExcelResources(title = "正常巡视任务周期id", order = 4)
    public Long getXsZcCycleId() {
        return this.xsZcCycleId;
    }

    public void setStauts(String stauts) {
        this.stauts = stauts;
    }

    @ExcelResources(title = "任务状态0  待办 1进行中 2已完成 ", order = 5)
    public String getStauts() {
        return this.stauts;
    }

    public void setPlanXsNum(Integer planXsNum) {
        this.planXsNum = planXsNum;
    }

    @ExcelResources(title = "巡视频率 每次任务中巡视应该重复的次数", order = 6)
    public Integer getPlanXsNum() {
        return this.planXsNum;
    }

    public void setRealXsNum(Integer realXsNum) {
        this.realXsNum = realXsNum;
    }

    @ExcelResources(title = "本次巡视重复次数", order = 7)
    public Integer getRealXsNum() {
        return this.realXsNum;
    }

    public void setPlanStartTime(Date planStartTime) {
        this.planStartTime = planStartTime;
    }

    @ExcelResources(title = "计划开始时间", order = 8)
    public Date getPlanStartTime() {
        return this.planStartTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    @ExcelResources(title = "计划结束时间", order = 9)
    public Date getPlanEndTime() {
        return  this.planEndTime;
    }

    public void setRealStartTime(Date realStartTime) {
        this.realStartTime = realStartTime;
    }

    @ExcelResources(title = "实际开始时间", order = 10)
    public Date getRealStartTime() {
        return this.realStartTime;
    }

    public void setSfqrTime(Date sfqrTime) {
        this.sfqrTime = sfqrTime;
    }

    @ExcelResources(title = "身份确认时间", order = 11)
    public Date getSfqrTime() {
        return this.sfqrTime;
    }

    public void setDdxcTime(Date ddxcTime) {
        this.ddxcTime = ddxcTime;
    }

    @ExcelResources(title = "到达现场时间", order = 12)
    public Date getDdxcTime() {
        return this.ddxcTime;
    }

    public void setXsksTime(Date xsksTime) {
        this.xsksTime = xsksTime;
    }

    @ExcelResources(title = "巡视开始时间", order = 13)
    public Date getXsksTime() {
        return this.xsksTime;
    }

    public void setRealEndTime(Date realEndTime) {
        this.realEndTime = realEndTime;
    }

    @ExcelResources(title = "实际结束时间", order = 14)
    public Date getRealEndTime() {
        return this.realEndTime;
    }

    public void setTaskNumInCycle(String taskNumInCycle) {
        this.taskNumInCycle = taskNumInCycle;
    }

    @ExcelResources(title = "周期内第多少次任务", order = 15)
    public String getTaskNumInCycle() {
        return this.taskNumInCycle;
    }

    public void setId() {
        this.id = Long.valueOf(new SnowflakeIdWorker(0,0).nextId());
    }

    @ExcelResources(title = "id", order = 16)
    public Long getId() {
        return this.id;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @ExcelResources(title = "任务名称", order = 17)
    public String getTaskName() {
        return this.taskName;
    }

    public void setCmUserId(String cmUserId) {
        this.cmUserId = cmUserId;
    }

    @ExcelResources(title = "任务执行人", order = 18)
    public String getCmUserId() {
        return this.cmUserId;
    }

}