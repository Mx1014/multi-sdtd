package com.rzt.activiti.service.impl;

import com.rzt.activiti.service.ActivitiService;
import com.rzt.entity.CheckResult;
import com.rzt.repository.CheckResultRepository;
import com.rzt.service.CurdService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.RedisUtil;
import org.activiti.engine.*;
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
import java.util.HashMap;
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
    @Autowired
    private RedisUtil redisUtil;
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




        Page<Map<String, Object>> maps = null;

       try{
           Pageable pageable = new PageRequest(page, size, null);
           ArrayList<String> strings = new ArrayList<>();
           strings.add(userName);
           String sql = "SELECT y.ID,y.SECTION,y.CREATE_TIME,y.TDYW_ORG,y.TDWX_ORG,y.YHLB,y.YHMS,y.YHJB,y.YHJB1,h.TEXT_,t.ID_ as actaskid,t.PROC_INST_ID_,t.ASSIGNEE_," +
                   "   y.YHTDQX,y.YHTDXZJD,y.YHTDC,y.GKCS,y.XCP,y.LINE_NAME,y.YWORG_ID,  " +
                   " (SELECT DISTINCT l.LINE_NAME1 FROM CM_LINE_SECTION l WHERE l.LINE_ID = y.LINE_ID) as linename1,"+
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

    public WebApiResponse tree(){
        List<Map<String, Object>> list2 = null;
        List list1 =  null;
        ArrayList<Object> objects = new ArrayList<>();
        try {
             LOGGER.info("省市查询成功");
            String sql = "SELECT ID,NAME as \"label\",PID , NAME as \"value\" FROM  SD_YX_LINEAREA ";
            list2 = this.execSql(sql, null);
            for (Map<String, Object> map : list2) {
                String pid =  map.get("PID").toString();
                if(null != pid && "0".equals(pid)){
                    Map<String, Object> stringObjectHashMap = new HashMap<>();
                     list1 = treeOrgList(list2, pid);

                }
            }



        }catch (Exception e){
            LOGGER.error("省市查询失败"+e.getMessage());
            return WebApiResponse.erro("省市查询失败"+e.getMessage());
        }
        return WebApiResponse.success(list1);
    }

    public List treeOrgList(List<Map<String, Object>> orgList, String parentId) {
        List childOrg = new ArrayList<>();
        for (Map<String, Object> map : orgList) {
            String menuId = map.get("ID").toString();
            String pid = map.get("PID").toString();
            if (parentId.equals(pid)) {
                List c_node = treeOrgList(orgList, menuId);
                map.put("children", c_node);
                childOrg.add(map);
            }
        }
        return childOrg;
    }

}
