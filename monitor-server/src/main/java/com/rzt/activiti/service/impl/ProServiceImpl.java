package com.rzt.activiti.service.impl;

import com.rzt.activiti.service.ActivitiService;
import com.rzt.entity.CheckResult;
import com.rzt.repository.CheckResultRepository;
import com.rzt.service.CurdService;
import com.rzt.util.WebApiResponse;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 隐患上报流程
 * 李成阳
 * 2018/1/15
 */
@Service
public class ProServiceImpl  extends CurdService<CheckResult, CheckResultRepository> implements ActivitiService {


    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;
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



        //分页数据容错
      /*  if(null == page || 0 == page){
            page = 1;
        }
        if(null == size || 0 == size){
            size = 10;
        }*/
        Page<Map<String, Object>> maps = null;
        //List<Object> result = new ArrayList<>();
/*
        //流程定义key（流程定义的标识）
        String processDefinitionKey = "wtsh";
        //创建查询对象
        TaskQuery taskQuery = taskService.createTaskQuery();
        //设置查询条件
        taskQuery.taskAssignee(userName);
        //指定流程定义key，只查询某个流程的任务
        taskQuery.processDefinitionKey(processDefinitionKey);
        //获取查询列表
        List<Task> list = taskQuery.list();

        list.stream().forEach(a-> System.out.println(a));*/


   /*     TaskQuery taskQuery = taskService.createTaskQuery();
        //分页查询当前待办任务
        List<Task> list = taskQuery.taskAssignee(userName).orderByTaskCreateTime().desc().listPage(page,size);*/
    /*    if(null == list  || list.size()==0){
            return WebApiResponse.success("");
        }*/
       try{
           //这个sql可以用工作流提供的id查询到启动流程时传递的参数
          /* for (Task task : list) {
               String realname = "";
               System.out.println("当前任务  "+task);
               System.out.println(task.getId());

               Object yhid =  taskService.getVariable(task.getId(), "YHID");
               Object info =  taskService.getVariable(task.getId(), "info");
               Object khid =  taskService.getVariable(task.getId(), "khid");
               Object isKH =  taskService.getVariable(task.getId(), "isKH");
               System.out.println("########################################################");
               System.out.println("任务ID:"+task.getId());
               System.out.println("任务名称:"+task.getName());
               System.out.println("任务的创建时间:"+task.getCreateTime());
               System.out.println("任务的办理人:"+task.getAssignee());
               System.out.println("流程实例ID："+task.getProcessInstanceId());
               System.out.println("########################################################");
               if(null == yhid || "".equals(yhid)){
                   //拿不到隐患id的跳过
                    continue;
               }
               ArrayList<Object> strings = new ArrayList<>();
               strings.add(yhid);

               String sql = "SELECT ID as YHID, TBRID,YHJB1,WXORG_ID,XSTASK_ID,LINE_ID,CREATE_TIME,DXDYHSPJL," +
                       "    YHJB,LINE_NAME,TDWX_ORG,YHMS,YHLB,YWORG_ID,TDYW_ORG,SECTION,VTYPE " +
                       "   FROM XS_SB_YH WHERE ID = ?"+strings.size();
               Map<String, Object> map = this.execSqlSingleResult(sql, strings);
               String tbrid = (String) map.get("TBRID");
               if(null != tbrid  && !"".equals(tbrid)){
                   ArrayList<String> strings1 = new ArrayList<>();
                   strings1.add(tbrid);
                   String tbrsql = "SELECT REALNAME from RZTSYSUSER WHERE ID  = ?"+strings1.size();
                   Map<String, Object> map1 = this.execSqlSingleResult(tbrsql, strings1);
                    realname = (String) map1.get("REALNAME");
               }
               map.put("acTaskId",task.getId());
               map.put("createTime",task.getCreateTime());
               map.put("assignee",task.getAssignee());
               map.put("name",task.getName());
               map.put("isKH",isKH);
               map.put("realname",realname);
               map.put("info",info);
               map.put("proId",task.getProcessInstanceId());
               result.add(map);

           }
*/
           Pageable pageable = new PageRequest(page, size, null);
           ArrayList<String> strings = new ArrayList<>();
           strings.add(userName);
           String sql = "SELECT y.ID,y.SECTION,y.CREATE_TIME,y.TDYW_ORG,y.TDWX_ORG,y.YHLB,y.YHMS,y.YHJB,y.YHJB1,h.TEXT_,t.ID_ as actaskid,t.PROC_INST_ID_,t.ASSIGNEE_," +
                   "    (SELECT DISTINCT l.LINE_NAME1 FROM CM_LINE_SECTION l WHERE l.LINE_ID = y.LINE_ID) as linename1,"+
                  "  (SELECT DISTINCT v.TEXT_ FROM ACT_HI_VARINST v WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'info') as info," +
                   "    (SELECT DISTINCT v.TEXT_ FROM ACT_HI_VARINST v WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'khid') as khid,"+
                  "  (SELECT DISTINCT u.REALNAME FROM ACT_HI_VARINST v LEFT JOIN RZTSYSUSER u ON u.ID = v.TEXT_ WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'userName' ) as squsername," +
                  "  (SELECT DISTINCT u.REALNAME FROM RZTSYSUSER u WHERE u.ID = y.TBRID) as tbrName," +
                  "  (SELECT DISTINCT u.PHONE FROM RZTSYSUSER u WHERE u.ID = y.TBRID) as phone" +
                  "   FROM ACT_RU_TASK t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_" +
                  "  LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_" +
                  "   WHERE h.NAME_ = 'YHID' AND t.PROC_DEF_ID_ LIKE 'wtsh%' AND ASSIGNEE_ = ?"+strings.size();

            maps = this.execSqlPage(pageable, sql, strings);
            LOGGER.info("当前节点待办任务查询成功"+strings);

       }catch (Exception e){
            LOGGER.error("当前节点待办信息查询失败"+e.getMessage());
           return WebApiResponse.erro("当前节点待办信息查询失败"+e.getMessage());
       }

        return WebApiResponse.success(maps);
    }



    /**
     * 通过定义好的流程图文件部署
     */
    @Override
    public void deploy() {
        repositoryService.createDeployment().addClasspathResource("diagrams/ProActiviti.bpmn")
                .addClasspathResource("diagrams/ProActiviti.png").deploy();

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

    /**
     * 进入下一节点
     *  流程逻辑
     * @param taskId
     * @param map
     */
    @Override
    public void complete(String taskId, Map<String, Object> map) {
        System.out.println(taskId);
        System.out.println(map);

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
                processDefinition.getDeploymentId(), "diagrams/ProActiviti.png");
    }

    public String findIdByProId(String proId)  {

       try {
           if(null != proId && !"".equals(proId)){
               ArrayList<String> strings = new ArrayList<>();
               strings.add(proId);
               String sql  = "SELECT ID_ " +
                       "  FROM ACT_RU_TASK WHERE PROC_INST_ID_ = ?"+strings.size();
               Map<String, Object> map = this.execSqlSingleResult(sql, strings);
               String id_ = (String) map.get("ID_");
               if(null != id_ && !"".equals(id_)){
                   LOGGER.info("实例id查询节点id成功");
                   return id_;
               }
           }
       }catch (Exception e){
           LOGGER.error("实例id查询节点id失败"+e.getMessage());
            return "";
       }



        return "";
    }


    public WebApiResponse historyActInstanceList(String assignee,Integer page,Integer size){
        ArrayList<String> strings = new ArrayList<>();
        Page<Map<String, Object>> maps = null;
        strings.add(assignee);
        String sql = "SELECT y.ID,y.SECTION,y.CREATE_TIME,y.TDYW_ORG,y.TDWX_ORG,y.YHLB,y.YHMS,y.YHJB,y.YHJB1,h.TEXT_,t.ID_ as actaskid,t.PROC_INST_ID_,t.ASSIGNEE_," +
              "  (SELECT DISTINCT l.LINE_NAME1 FROM CM_LINE_SECTION l WHERE l.LINE_ID = y.LINE_ID) as linename1,t.END_TIME_,t.START_TIME_," +
              "  (SELECT DISTINCT  v.TEXT_ FROM ACT_HI_VARINST v WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'info') as info," +
              "  (SELECT DISTINCT  v.TEXT_ FROM ACT_HI_VARINST v WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'khid') as khid," +
              "  (SELECT DISTINCT  u.REALNAME FROM ACT_HI_VARINST v LEFT JOIN RZTSYSUSER u ON u.ID = v.TEXT_ WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'userName' ) as squsername," +
              "  (SELECT DISTINCT  u.REALNAME FROM RZTSYSUSER u WHERE u.ID = y.TBRID) as tbrName," +
              "  (SELECT DISTINCT  u.PHONE FROM RZTSYSUSER u WHERE u.ID = y.TBRID) as phone" +
              "  FROM ACT_HI_ACTINST t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_" +
              "  LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_" +
              "  WHERE h.NAME_ = 'YHID' AND t.PROC_DEF_ID_ LIKE 'wtsh%' AND ASSIGNEE_ = ?"+strings.size()+" AND t.END_TIME_ IS NOT NULL ";
        try {
            Pageable pageable = new PageRequest(page, size, null);
             maps = this.execSqlPage(pageable, sql, strings);
             LOGGER.info("历史记录查询成功");
        }catch (Exception e){
                LOGGER.error("查询历史记录失败"+e.getMessage());
            return WebApiResponse.erro("查询历史记录失败"+e.getMessage());
        }
        return WebApiResponse.success(maps);
    }

}
