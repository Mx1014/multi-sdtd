package com.rzt.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 李成阳
 * 2018/1/17
 */
public class ProListener implements TaskListener {


    /**
     * 监听当前看护取消节点   当监听到取消看护任务创建时   写入逻辑取消看护   当前节点继续前进 进入结束节点
     * @param delegateTask
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("进入节点监听");
        System.out.println(delegateTask);
    }
}
