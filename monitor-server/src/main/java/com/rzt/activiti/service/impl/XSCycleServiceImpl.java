package com.rzt.activiti.service.impl;

import com.rzt.activiti.service.ActivitiService;
import com.rzt.entity.CheckResult;
import com.rzt.repository.CheckResultRepository;
import com.rzt.repository.YHrepository;
import com.rzt.service.CurdService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.RedisUtil;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 李成阳
 * 2018/1/19
 */
@Service
public class XSCycleServiceImpl  extends CurdService<CheckResult, CheckResultRepository> implements ActivitiService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private YHrepository yHrepository;
 /*   @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisUtil redisUtil;*/
    protected static Logger LOGGER = LoggerFactory.getLogger(ProServiceImpl.class);

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

    /**
     * 查看任务
     * @param userName
     * @return
     */
    @Override
    public WebApiResponse checkTask(String userName,Integer page,Integer size) {
      /*  try {
            String roleIdByUserId = redisUtil.findRoleIdByUserId(redisTemplate, userName);
            System.out.println(roleIdByUserId+"******************************");
        }catch (Exception e){

        }*/



        //分页数据容错
        if(null == page || 0 == page){
            page = 1;
        }
        if(null == size || 0 == size){
            size = 10;
        }
        List<Object> result = new ArrayList<>();


        //流程定义key（流程定义的标识）
        String processDefinitionKey = "xssh";
        //创建查询对象
        TaskQuery taskQuery = taskService.createTaskQuery();
        //设置查询条件
        taskQuery.taskAssignee(userName);
        //指定流程定义key，只查询某个流程的任务
        taskQuery.processDefinitionKey(processDefinitionKey);
        //获取查询列表
        List<Task> list = taskQuery.list();
        //完整sql
        /*SELECT x.CREATE_TIME,x.V_LEVEL,x.SECTION,x.TASK_NAME,x.WX_ORG,x.TD_ORG,(SELECT d.DEPTNAME
        FROM RZTSYSDEPARTMENT d WHERE ID = x.TD_ORG) as tdorg,(SELECT d.DEPTNAME
        FROM RZTSYSDEPARTMENT d WHERE ID = x.WX_ORG) as wxorg,x.ID,r.PROPOSER_ID,r.PROPOSER_TIME,r.CHANGE_REASON

        FROM XS_ZC_CYCLE x
        LEFT JOIN XS_ZC_CYCLE_RECORD r ON r.XS_ZC_CYCLE_ID = x.ID
        WHERE r.PROPOSER_STATUS = 0*/



        //分页查询当前待办任务
        //List<Task> list = taskQuery.taskAssignee(userName).orderByTaskCreateTime().desc().listPage(page, size);
        if(null == list  || list.size()==0){
            return WebApiResponse.success("");
        }
        try{
           /* String ids = "";
            for (Task task : list) {
                ids += ","+task.getId();
            }
            System.out.println(ids+"****************");
            if(ids.length()>0){
                ids = ids.substring(1,ids.length());
            }*/
            //这个sql可以用工作流提供的id查询到启动流程时传递的参数
            for (Task task : list) {
                String realname = "";
                System.out.println("当前任务  "+task);
                System.out.println(task.getId());

                Object info =  taskService.getVariable(task.getId(), "info");
                Object XSID =  taskService.getVariable(task.getId(), "XSID");
                Object name =  taskService.getVariable(task.getId(), "userName");

                if(null == XSID || "".equals(XSID)){
                    //拿不到隐患id的跳过
                    continue;
                }
                ArrayList<Object> strings = new ArrayList<>();
                strings.add(XSID);
                Map<String, Object> map = null;
                String sql = " SELECT r.PROPOSER_TIME,x.WX_ORG,x.TD_ORG,(SELECT d.DEPTNAME" +
                        "  FROM RZTSYSDEPARTMENT d WHERE d.ID = x.TD_ORG) as tdorg,(SELECT d.COMPANYNAME" +
                        "  FROM RZTSYSCOMPANY d WHERE d.ID = x.WX_ORG) as wxorg,x.ID," +
                        "  (SELECT l.V_LEVEL" +
                        "   FROM CM_LINE l WHERE l.ID = x.LINE_ID) as vlevel," +
                        "  (SELECT l.SECTION" +
                        "   FROM CM_LINE l WHERE l.ID = x.LINE_ID) as SECTION," +
                        "  (SELECT l.LINE_NAME" +
                        "   FROM CM_LINE l WHERE l.ID = x.LINE_ID) as lineName" +
                        "   , r.CHANGE_REASON," +
                        "  (SELECT u.REALNAME FROM RZTSYSUSER u WHERE  u.id = r.PROPOSER_ID) as name," +
                        "  (SELECT u.PHONE FROM RZTSYSUSER u WHERE  u.id = r.PROPOSER_ID) as PHONE" +
                        "    ,x.IS_KT,x.CYCLE,x.PLAN_XS_NUM,r.XS_ZC_CYCLE cycle1,r.PLAN_XS_NUM as plan_xs_num1,r.PROPOSER_TYPE" +
                        "   FROM XS_ZC_CYCLE x" +
                        "    LEFT JOIN XS_ZC_CYCLE_RECORD r ON r.XS_ZC_CYCLE_ID = x.ID" +
                        "    WHERE r.PROPOSER_STATUS = 0  AND  x.ID = ?"+strings.size();
                List<Map<String, Object>> maps = this.execSql(sql, strings);
                if(null != maps && maps.size()>0){
                     map = maps.get(0);
                }else {
                    continue;
                }
                map.put("acTaskId",task.getId());
                map.put("assignee",task.getAssignee());
                map.put("info",info);
                map.put("proId",task.getProcessInstanceId());
                result.add(map);

            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return WebApiResponse.success(result);
    }



    /**
     * 通过定义好的流程图文件部署
     */
    @Override
    public void deploy() {
        repositoryService.createDeployment().addClasspathResource("diagrams/CycleActiviti.bpmn")
                .addClasspathResource("diagrams/CycleActiviti.png").deploy();

    }

    @Override
    public void delpd(String deploymentId) {
        repositoryService.deleteDeployment(deploymentId,true);
    }

    /**
     * 开始流程
     * @param key
     * @param map
     */
    @Override
    public ProcessInstance start(String key, Map map) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key, map);
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

    @Override
    public void complete(String taskId, Map<String, Object> map) {

    }

    /**
     * 进入下一节点
     *  流程逻辑
     * @param taskId
     * @param map
     */
    @Transactional
    public void complete1(String taskId, Map<String, Object> map,String userId) {
        //此处更改当前任务的审核人id
        yHrepository.updateAppId(map.get("XSID").toString(),userId,new Date(),"0");
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
                processDefinition.getDeploymentId(), "diagrams/CycleActiviti.png");
    }




}
