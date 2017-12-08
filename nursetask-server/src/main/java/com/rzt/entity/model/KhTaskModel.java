package com.rzt.entity.model;

import com.rzt.entity.KhTask;

import java.util.List;

/**
 * Created by admin on 2017/12/2.
 */
public class KhTaskModel {
    private List<KhTask> taskList;

    public void setTaskList(List<KhTask> taskList) {
        this.taskList = taskList;
    }

    public List<KhTask> getTaskList() {
        return taskList;
    }
}
