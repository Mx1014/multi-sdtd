package com.rzt.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 李成阳
 * 2018/1/3
 */
@Entity
@Table(name = "TIMED_TASK")
public class TimedTask implements Serializable {
    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "TASKID")
    private Long taskId;
    @Column(name = "CREATETIME")
    private Date createTime;
    @Column(name = "TARGETSTATUS")
    private String targetStart;
    @Column(name = "STATUS")
    private Integer status;


    @Override
    public String toString() {
        return "TimedTask{" +
                "id='" + id + '\'' +
                ", TaskId=" + taskId +
                ", createTime=" + createTime +
                ", targetStart='" + targetStart + '\'' +
                ", status=" + status +
                '}';
    }

    public TimedTask() {
    }

    public TimedTask(String id, Long taskId, Date createTime, String targetStart, Integer status) {
        this.id = id;
        this.taskId = taskId;
        this.createTime = createTime;
        this.targetStart = targetStart;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        taskId = taskId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTargetStart() {
        return targetStart;
    }

    public void setTargetStart(String targetStart) {
        this.targetStart = targetStart;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
