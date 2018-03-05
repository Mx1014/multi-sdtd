package com.rzt.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * 李成阳
 * 2018/3/2
 * 缺陷审核  公司本部通报监听器
 */
@Component
public class DefGSListener  implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("进入公司本部通报监听器");
        System.out.println(delegateTask);
    }
}
