package com.rzt.activiti.listener;

import com.rzt.activiti.service.impl.ProServiceImpl;
import com.rzt.utils.SpringUtil;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 李成阳
 * 2018/1/17
 * 看护取消节点监听类
 */
@Component
public class ProListener implements TaskListener {



    /**
     * 监听当前看护取消节点   当监听到取消看护任务创建时   写入逻辑取消看护   当前节点继续前进 进入结束节点
     * @param delegateTask
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        try {
            ProServiceImpl proServiceImpl = (ProServiceImpl) SpringUtil.getObject("proServiceImpl");
            //查询当前流程是否由看护任务
            //通过当前节点id获取到这条流程中存储的看护id
            //不需要取消看护任务  到期自动取消
            //String khid = (String) proServiceImpl.checkTask(delegateTask.getId(), "khid");
            //结束流程
            proServiceImpl.complete(delegateTask.getId(),null);
        }catch (Exception e){
            e.getMessage();
        }
    }


}
