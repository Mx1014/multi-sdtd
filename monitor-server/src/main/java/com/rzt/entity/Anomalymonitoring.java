/**
 * 文件名：ANOMALYMONITORING
 * 版本信息：
 * 日期：2017/12/31 16:25:17
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 类名称：ANOMALYMONITORING
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/31 16:25:17
 * 修改人：张虎成
 * 修改时间：2017/12/31 16:25:17
 * 修改备注：
 */
@Entity
@Table(name = "ANOMALY_MONITORING")
public class Anomalymonitoring implements Serializable {
    //字段描述:
    @Id
    private Long id;
    //字段描述: 任务ID
    @Column(name = "TASKID")
    private String taskid;
    //字段描述: 一级审核状态   0 已完成
    @Column(name = "ONECHECK_STATUS")
    private String onecheckStatus;
    //字段描述: 二级审核说明
    @Column(name = "TWOCHECK_EXPLAIN")
    private String twocheckExplain;
    //字段描述: 一级审核说明
    @Column(name = "ONECHECK_EXPLAIN")
    private String onecheckExplain;
    //字段描述: 二级审核状态
    @Column(name = "TWOCHECK_STATUS")
    private String twocheckStatus;
    //字段描述: 任务类行 1 看护 2 巡视 3 前台稽查 4 后台稽查
    @Column(name = "TASK_TYPE")
    private String taskType;
    //字段描述: 告警类型
    @Column(name = "ANOMALY_TYPE")
    private String anomalyType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getTaskid() {
        return this.taskid;
    }


    public void setOnecheckStatus(String onecheckStatus) {
        this.onecheckStatus = onecheckStatus;
    }

    public String getOnecheckStatus() {
        return this.onecheckStatus;
    }


    public void setTwocheckExplain(String twocheckExplain) {
        this.twocheckExplain = twocheckExplain;
    }

    public String getTwocheckExplain() {
        return this.twocheckExplain;
    }


    public void setOnecheckExplain(String onecheckExplain) {
        this.onecheckExplain = onecheckExplain;
    }

    public String getOnecheckExplain() {
        return this.onecheckExplain;
    }


    public void setTwocheckStatus(String twocheckStatus) {
        this.twocheckStatus = twocheckStatus;
    }

    public String getTwocheckStatus() {
        return this.twocheckStatus;
    }


    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskType() {
        return this.taskType;
    }


    public void setAnomalyType(String anomalyType) {
        this.anomalyType = anomalyType;
    }

    public String getAnomalyType() {
        return this.anomalyType;
    }


}
