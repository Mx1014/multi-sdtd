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

    @Override
    public WebApiResponse checkTask(String userName, Integer page, Integer size, Object... values) {
        return null;
    }


    /**
     * 查看任务
     * @param userId  用户id
     * @param page
     * @param size
     * @param YHLB   隐患类别
     * @param YHJB   隐患级别
     * @param start  开始时间
     * @param end    结束时间
     * @param deptId  部门
     * @return
     */
    public WebApiResponse checkTasks(String userId, Integer page, Integer size,String YHLB, String YHJB,String start,String end,String deptId) {
        Page<Map<String, Object>> maps = null;
       try{
           String td = redisUtil.findTDByUserId(userId);
           userId = redisUtil.findRoleIdByUserId(userId);
           if(null == userId || "".equals(userId)){
                return WebApiResponse.success("问题审核待办查询失败");
           }
           if(null == td || "".equals(td)){
               return WebApiResponse.success("问题审核待办查询失败");
           }
           Pageable pageable = new PageRequest(page, size, null);
           ArrayList<String> strings = new ArrayList<>();
           strings.add(userId);
           String sql = "SELECT y.ID,y.SECTION,y.CREATE_TIME,y.TDYW_ORG,y.TDWX_ORG,y.YHLB,y.YHMS,y.YHJB,y.YHJB1,h.TEXT_,t.ID_ as actaskid,t.PROC_INST_ID_,t.ASSIGNEE_," +
                   "   y.YHTDQX,y.YHTDXZJD,y.YHTDC,y.GKCS,y.LINE_NAME,y.YWORG_ID,  " +
                  "  (SELECT DISTINCT v.TEXT_ FROM ACT_HI_VARINST v WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'info') as info," +
                   "    (SELECT DISTINCT v.TEXT_ FROM ACT_HI_VARINST v WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'khid') as khid,"+
                  "  (SELECT DISTINCT u.REALNAME FROM ACT_HI_VARINST v LEFT JOIN RZTSYSUSER u ON u.ID = v.TEXT_ WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'userName' ) as squsername," +
                  "  (SELECT DISTINCT u.REALNAME FROM RZTSYSUSER u WHERE u.ID = y.TBRID) as tbrName," +
                  "  (SELECT DISTINCT u.PHONE FROM RZTSYSUSER u WHERE u.ID = y.TBRID) as phone" +
                  "   FROM ACT_RU_TASK t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_" +
                  "  LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_" +
                  "   WHERE h.NAME_ = 'YHID' AND t.PROC_DEF_ID_ LIKE 'wtsh%'  AND ASSIGNEE_ = ?"+strings.size();
                    if(null != YHLB && !"".equals(YHLB)){
                        strings.add(YHLB);
                        sql += "  AND y.YHLB = ?"+strings.size();
                    }
                   if(null != YHJB && !"".equals(YHJB)){
                       strings.add(YHJB);
                       sql += "  AND y.YHJB1 = ?"+strings.size();
                   }

           if(null != start && !"".equals(start)){
               sql += "   AND  y.CREATE_TIME >=  to_date('"+start+":00','YYYY-MM-dd HH24:mi:ss')  ";
           }
           if(null != end && !"".equals(end)){
               sql += "   AND  y.CREATE_TIME <=  to_date('"+end+":00','YYYY-MM-dd HH24:mi:ss')  ";
           }


           //判断当前用户所属节点    书否显示所有信息
                   if("sdid".equals(userId) || "sdyjid".equals(userId)){
                        sql += "  AND y.TDYW_ORG = '"+td+"'  ";
                   }else{
                       if(null != deptId && !"".equals(deptId)){
                           strings.add(deptId);
                           sql += "  AND y.YWORG_ID = ?"+strings.size();
                       }
                   }



            maps = this.execSqlPage(pageable, sql, strings);
            LOGGER.info("当前节点待办任务查询成功"+strings);

       }catch (Exception e){
            LOGGER.error("当前节点待办信息查询失败"+e.getMessage());
           return WebApiResponse.success("当前节点待办信息查询失败"+e.getMessage());
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
        LOGGER.info(key+"--流程开始了，携带参数："+map);
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
        LOGGER.info(taskId+"节点进入了下一个节点，携带参数："+map);
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




    /**
     * 查看当前节点历史任务
     * @param userId  用户id
     * @param page
     * @param size
     * @param YHLB   隐患类别
     * @param YHJB   隐患级别
     * @param start  开始时间
     * @param end    结束时间
     * @param deptId  部门
     * @return
     */
    public WebApiResponse historyActInstanceList(String userId, Integer page, Integer size,String YHLB, String YHJB,String start,String end,String deptId) {
        Page<Map<String, Object>> maps = null;
        try{
            String td = redisUtil.findTDByUserId(userId);
            userId = redisUtil.findRoleIdByUserId(userId);
            if(null == userId || "".equals(userId)){
                return WebApiResponse.success("问题审核待办查询失败");
            }
            if(null == td || "".equals(td)){
                return WebApiResponse.success("问题审核待办查询失败");
            }
            Pageable pageable = new PageRequest(page, size, null);
            ArrayList<String> strings = new ArrayList<>();
            String sql = "SELECT  " +
                    "    y.ID,y.LINE_NAME,y.SECTION,y.CREATE_TIME,y.TDYW_ORG,y.TDWX_ORG,y.YHLB,y.YHMS,y.YHJB,y.YHJB1,h.TEXT_,t.ID_ as actaskid,t.PROC_INST_ID_,t.ASSIGNEE_," +
                    "    (SELECT DISTINCT l.LINE_NAME1 FROM CM_LINE_SECTION l WHERE l.LINE_ID = y.LINE_ID) as linename1,t.END_TIME_,t.START_TIME_," +
                    "    (SELECT DISTINCT  v.TEXT_ FROM ACT_HI_VARINST v WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'info') as info," +
                    "    (SELECT DISTINCT  v.TEXT_ FROM ACT_HI_VARINST v WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'flag') as flag," +
                    "    (SELECT DISTINCT  v.TEXT_ FROM ACT_HI_VARINST v WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'khid') as khid," +
                    "    (SELECT DISTINCT  u.REALNAME FROM RZTSYSUSER u WHERE u.ID = y.TBRID) as tbrName," +
                    "    (SELECT DISTINCT  u.PHONE FROM RZTSYSUSER u WHERE u.ID = y.TBRID) as phone" +
                    "     FROM ACT_HI_ACTINST t LEFT JOIN ACT_HI_VARINST h ON t.PROC_INST_ID_ = h.PROC_INST_ID_ AND  h.NAME_ = 'YHID'" +
                    "    LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_" +
                    "     WHERE  t.PROC_DEF_ID_ LIKE 'wtsh%'  AND ASSIGNEE_ = '"+userId+"'  AND t.END_TIME_ IS NOT  NULL ";
            sql += "  AND t.END_TIME_ IS NOT NULL ";
            if(null != YHLB && !"".equals(YHLB)){
                strings.add(YHLB);
                sql += "  AND y.YHLB = ?"+strings.size();
            }
            if(null != YHJB && !"".equals(YHJB)){
                strings.add(YHJB);
                sql += "  AND y.YHJB1 = ?"+strings.size();
            }

            //判断当前用户所属节点    书否显示所有信息
            if("sdid".equals(userId) || "sdyjid".equals(userId)){
                strings.add(td);
                sql += "  AND y.TDYW_ORG = ?"+strings.size();
            }else{
                if(null != deptId && !"".equals(deptId)){
                    strings.add(deptId);
                    sql += "  AND y.YWORG_ID = ?"+strings.size();
                }
            }
            if(null != start && !"".equals(start)){
                sql += "   AND  y.CREATE_TIME >=  to_date('"+start+":00','YYYY-MM-dd HH24:mi:ss')  ";
            }
            if(null != end && !"".equals(end)){
                sql += "   AND  y.CREATE_TIME <=  to_date('"+end+":00','YYYY-MM-dd HH24:mi:ss')  ";
            }
            if(null != sql && sql.length()>0){
                sql += "   ORDER BY t.END_TIME_ DESC   ";
            }

                maps = this.execSqlPage(pageable, sql, strings.toArray());
                LOGGER.info("历史记录查询成功");
            }catch (Exception e){
                LOGGER.error("查询历史记录失败"+e.getMessage());
                return WebApiResponse.success("查询历史记录失败"+e.getMessage());
            }

        return WebApiResponse.success(maps);
    }







/*
    public WebApiResponse historyActInstanceList(String assignee,Integer page,Integer size){
        ArrayList<String> strings = new ArrayList<>();
        Page<Map<String, Object>> maps = null;
        strings.add(assignee);

        String sql = "SELECT y.ID,y.LINE_NAME,y.SECTION,y.CREATE_TIME,y.TDYW_ORG,y.TDWX_ORG,y.YHLB,y.YHMS,y.YHJB,y.YHJB1,h.TEXT_,t.ID_ as actaskid,t.PROC_INST_ID_,t.ASSIGNEE_," +
              "  (SELECT DISTINCT l.LINE_NAME1 FROM CM_LINE_SECTION l WHERE l.LINE_ID = y.LINE_ID) as linename1,t.END_TIME_,t.START_TIME_," +
              "  (SELECT DISTINCT  v.TEXT_ FROM ACT_HI_VARINST v WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'info') as info," +
              "  (SELECT DISTINCT  v.TEXT_ FROM ACT_HI_VARINST v WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'khid') as khid," +
              "  (SELECT DISTINCT  u.REALNAME FROM RZTSYSUSER u WHERE u.ID = y.TBRID) as tbrName," +
              "  (SELECT DISTINCT  u.PHONE FROM RZTSYSUSER u WHERE u.ID = y.TBRID) as phone" +
              "  FROM ACT_HI_ACTINST t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_" +
              "  LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_" +
              "  WHERE h.NAME_ = 'YHID' AND t.PROC_DEF_ID_ LIKE 'wtsh%' AND ASSIGNEE_ = ?"+strings.size()+" AND y.ID IS NOT NULL";
         sql += "  AND t.END_TIME_ IS NOT NULL ";
         // 有结束时间代表已经经过当前节点     没有代表还停留在当前节点
        try {
            Pageable pageable = new PageRequest(page, size, null);
             maps = this.execSqlPage(pageable, sql, strings);
             LOGGER.info("历史记录查询成功");
        }catch (Exception e){
                LOGGER.error("查询历史记录失败"+e.getMessage());
            return WebApiResponse.erro("查询历史记录失败"+e.getMessage());
        }
        return WebApiResponse.success(maps);
    }*/

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

    public WebApiResponse findLB() {
        try{
            HashMap<String, Object> map = new HashMap<>();
            String sql  = "SELECT YHJB1" +
                    "  FROM XS_SB_YH GROUP BY YHJB1" ;

            String sql2 = "SELECT YHLB" +
                    "  FROM XS_SB_YH GROUP BY YHLB";

            String sql3  = "SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL ORDER BY t.DEPTSORT";

            List<Map<String, Object>> maps1 = this.execSql(sql, null);
            List<Map<String, Object>> maps2 = this.execSql(sql2, null);
            List<Map<String, Object>> maps3 = this.execSql(sql3, null);
            map.put("JB",maps1);
            map.put("LB",maps2);
            map.put("TD",maps3);
            return WebApiResponse.success(map);
        }catch (Exception e){
            LOGGER.error("类别查询失败"+e.getMessage());
            return WebApiResponse.erro("类别查询失败"+e.getMessage());
        }

    }
}
