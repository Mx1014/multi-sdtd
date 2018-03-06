package com.rzt.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * 李成阳
 * 2018/3/2
 * 缺陷审核  入台账节点监听
 */
@Component
public class DefTZListener  implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("进入   入台账节点监听");
        System.out.println(delegateTask);
    }
}
