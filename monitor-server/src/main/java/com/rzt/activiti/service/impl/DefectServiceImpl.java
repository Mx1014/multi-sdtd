package com.rzt.activiti.service.impl;

import com.rzt.activiti.service.ActivitiService;
import com.rzt.entity.CheckResult;
import com.rzt.repository.CheckResultRepository;
import com.rzt.service.CurdService;
import com.rzt.util.WebApiResponse;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 李成阳
 * 2018/1/18
 * 缺陷上报实现类
 */
@Service
public class DefectServiceImpl   extends CurdService<CheckResult, CheckResultRepository> implements ActivitiService {


    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private JedisPool jedisPool;


    /**
     * 展开任务
     * @return
     */
    @Override
    public List<ProcessDefinition> checkDeploy() {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.orderByProcessDefinitionVersion().desc();
        return processDefinitionQuery.list();
    }

    /**
     * 查看任务部署状态
     * @return
     */
    @Override
    public List<ProcessInstance> checkStatus() {
        return runtimeService.createProcessInstanceQuery().list();
    }

    @Override
    public WebApiResponse checkTask(String userName, Integer page, Integer size, Object... values) {
        return null;
    }

    /**
     * 查看任务
     * @param userName
     * @return
     */

    public WebApiResponse checkTasks(String userName,Integer page,Integer size) {


        List<Object> result = new ArrayList<>();

        TaskQuery taskQuery = taskService.createTaskQuery();
        //分页查询当前待办任务
        List<Task> list = taskQuery.taskAssignee(userName).processDefinitionKey("defect:2:92504").orderByTaskCreateTime().desc().listPage(page,size);
        if(null == list  || list.size()==0){
            return WebApiResponse.success("");
        }
        try{
            //这个sql可以用工作流提供的id查询到启动流程时传递的参数
            for (Task task : list) {
                HashMap<String, Object> map = new HashMap<>();
                String realname = "";

                Object qxId =  taskService.getVariable(task.getId(), "qxId");
                Object info =  taskService.getVariable(task.getId(), "info");
                map.put("qxId",qxId);
                map.put("info",info);
                map.put("acTaskId",task.getId());
                map.put("acTaskName",task.getName());
                map.put("acCreateTime",task.getCreateTime());
                map.put("acAssignee",task.getAssignee());
                map.put("acProId",task.getProcessInstanceId());
                result.add(map);
            }

        }catch (Exception e){

        }

        return WebApiResponse.success(result);

    }



    /**
     * 通过定义好的流程图文件部署
     */
    @Override
    public void deploy() {
        repositoryService.createDeployment().addClasspathResource("diagrams/DefectActiviti.bpmn")
                .addClasspathResource("diagrams/DefectActiviti.png").deploy();

    }

    @Override
    public void delpd(String deploymentId) {
        repositoryService.deleteDeployment(deploymentId,true);
    }

    /**
     * 开始流程
     * 在开始流程时 需要将当前节点流程存入redis   当redis定时失效回调时 使此流程继续  失效时间为24小时
     * 或当用户手动点击继续时在redis中删除这个键
     * @param key
     * @param map
     */
    @Override
    public ProcessInstance start(String key, Map map) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, map);
        String defectId = processInstance.getProcessInstanceId();
        String taskId = "";
        //查询出流程id
        String sql = "SELECT ID_" +
                "      FROM ACT_RU_TASK WHERE PROC_INST_ID_ = '"+defectId+"'";
        List<Map<String, Object>> maps = this.execSql(sql);
        if(null != maps && maps.size()>0){
            taskId = maps.get(0).get("ID_").toString();
        }
        //将键 defectId 存入redis   失效时间24小时 需要回调
        Jedis jedis = jedisPool.getResource();
        //选择数据库
        jedis.select(5);
        String qxId = map.get("qxId").toString();
        String redisKey = taskId+","+qxId+","+key;
        jedis.set(redisKey, "缺陷审核超时使用");
        //设置超时  时间为24小时
        jedis.expire(redisKey, 24*60*60);
        return processInstance;

    }

    /**
     * 查看任务
     * @param taskId
     * @param variaName
     * @return
     */
    @Override
    public Object checkTask(String taskId, String variaName) {
        return taskService.getVariable(taskId,variaName);
    }

    /**
     * 进入下一节点
     *  流程逻辑
     * @param taskId
     * @param map
     */
    @Override
    public void complete(String taskId, Map<String, Object> map) {
        taskService.complete(taskId,map);

    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public InputStream checkInputStream(String id) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
        String processDefinitionId = pi.getProcessDefinitionId();
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        return repositoryService.getResourceAsStream(
                processDefinition.getDeploymentId(), "diagrams/DefectActiviti.png");
    }



}
