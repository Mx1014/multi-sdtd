package com.rzt.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * 李成阳
 * 2018/1/20
 */
public class CycleListener implements TaskListener {
    /**
     * 变更巡视周期任务
     * @param delegateTask
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("进入节点监听 ----------------- 变更巡视周期任务");
        System.out.println(delegateTask);
    }
}
