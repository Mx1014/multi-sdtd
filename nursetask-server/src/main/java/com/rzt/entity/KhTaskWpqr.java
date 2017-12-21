package com.rzt.entity;

import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by admin on 2017/12/17.
 */
@Entity
public class KhTaskWpqr implements Serializable {
    @Id
    private long id;
    //字段说明  任务id
    @Column(name = "TASKID")
    private long taskId;
    //字段说明 物品状态（0未选中 1选中）
    @Column(name = "WP_ZT")
    private String wpzt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = Long.valueOf(new SnowflakeIdWorker(0,0).nextId());;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getWpzt() {
        return wpzt;
    }

    public void setWpzt(String wpzt) {
        this.wpzt = wpzt;
    }
}
