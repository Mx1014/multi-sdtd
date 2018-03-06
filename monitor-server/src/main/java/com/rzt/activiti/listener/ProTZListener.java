package com.rzt.activiti.listener;

import com.rzt.activiti.Eureka.nurseTaskService;
import com.rzt.activiti.service.impl.ProServiceImpl;
import com.rzt.utils.SpringUtil;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 李成阳
 * 2018/1/21
 * 添加隐患台账节点监听类
 */
public class ProTZListener  implements TaskListener {


    /**
     * 监听当前台账节点     写入逻辑变更看护任务 调用添加隐患台账接口    当前节点继续前进 进入结束节点
     * @param delegateTask
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        ProServiceImpl proService = (ProServiceImpl) SpringUtil.getObject("proServiceImpl");
        nurseTaskService nurseTaskService = (com.rzt.activiti.Eureka.nurseTaskService) SpringUtil.getObject("nurseTaskService");
        //查询当前流程是否由看护任务
        String YHID = (String) proService.checkTask(delegateTask.getId(), "YHID");
        //添加隐患台账
        if(null != YHID && !"".equals(YHID)){

            //变更看护任务和生成看护任务为一个接口  添加隐患台账
            nurseTaskService.reviewYh(new Long(YHID));
        }
        System.out.println("进入节点监听  隐患台账节点"+YHID);
        //结束流程
        proService.complete(delegateTask.getId(),null);
    }
}
