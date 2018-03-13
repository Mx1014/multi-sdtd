package com.rzt.activiti.listener;

import com.rzt.activiti.service.impl.ProServiceImpl;
import com.rzt.activiti.service.impl.XSCycleServiceImpl;
import com.rzt.utils.SpringUtil;
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
        XSCycleServiceImpl xsCycleService = (XSCycleServiceImpl) SpringUtil.getObject("XSCycleServiceImpl");
        //查询当前流程是否由看护任务
        //通过当前节点id获取到这条流程中存储的周期id
        String xsid = (String) xsCycleService.checkTask(delegateTask.getId(), "XSID");
        //变更周期
        xsCycleService.updateXSCycle(xsid,delegateTask.getId());

    }
}
