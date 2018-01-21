package com.rzt.activiti.listener;

import com.rzt.activiti.service.impl.ProServiceImpl;
import com.rzt.utils.SpringUtil;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 李成阳
 * 2018/1/21
 */
public class ProTZListener  implements TaskListener {


    /**
     * 监听当前看护取消节点   当监听到取消看护任务创建时   写入逻辑取消看护   当前节点继续前进 进入结束节点
     * @param delegateTask
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        ProServiceImpl proService = (ProServiceImpl) SpringUtil.getObject("proServiceImpl");
        String idByProId = proService.findIdByProId(delegateTask.getProcessInstanceId());
        //查询当前流程是否由看护任务
        String YHID = (String) proService.checkTask(idByProId, "YHID");
        String khid = (String) proService.checkTask(idByProId, "khid");
        //变更看护任务
        if(null != khid && !"".equals(khid)){
            System.out.println("变更看护任务"+khid);
        }
        //添加隐患台账
        if(null != YHID && !"".equals(YHID)){
            System.out.println("添加隐患台账"+YHID);
        }
        System.out.println("进入节点监听  隐患台账节点"+YHID);
        //结束流程
        proService.complete(idByProId,null);
        System.out.println(delegateTask);
    }
}
