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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
     * 查看所有待办任务
     * @param userId 登录人
     * @param page
     * @param size
     * @param tdId   通道公司
     * @param lineName  线路名称
     * @param vLevel    电压等级
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return
     */

    public WebApiResponse checkTasks(String userId,Integer page,Integer size
            ,String tdId,String lineName,String vLevel,String startTime,String endTime) {
        Page<Map<String, Object>> maps = null;
        try{
            String td = redisUtil.findTDByUserId(userId);
            userId = redisUtil.findRoleIdByUserId(userId);
            if(null == userId || "".equals(userId)){
                return WebApiResponse.success("巡视审核历史查询失败  登录人节点 = "+userId);
            }
            if(null == td || "".equals(td)){
                return WebApiResponse.success("巡视审核历史查询失败 通道公司="+td);
            }
            Pageable pageable = new PageRequest(page, size, null);
            String sql = "SELECT *" +
                    "FROM (SELECT x.ID,x.XS_ZC_CYCLE_ID as cycleid,x.XS_ZC_CYCLE,x.PLAN_XS_NUM,x.APPROVER_TIME,t.ID_ as actaskid,t.PROC_INST_ID_,t.ASSIGNEE_,x.PROPOSER_TYPE," +
                    "        x.PLAN_START_TIME,x.PLAN_END_TIME,x.CM_USER_ID,x.CHANGE_REASON,x.DESCRIPTION,x.PROPOSER_ID," +
                    "                  (SELECT u.REALNAME FROM RZTSYSUSER u WHERE u.ID = x.CM_USER_ID ) as username," +
                    "                   (SELECT u.DEPTID FROM RZTSYSUSER u WHERE u.ID = x.CM_USER_ID ) as DID," +
                    "                  (SELECT d.DEPTNAME FROM RZTSYSUSER u LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID WHERE u.ID = x.CM_USER_ID ) as dept," +
                    "                  (SELECT d.COMPANYNAME FROM RZTSYSUSER u LEFT JOIN RZTSYSCOMPANY d ON d.ID = u.COMPANYID WHERE u.ID = x.CM_USER_ID ) as wx," +
                    "                  (SELECT DISTINCT v.TEXT_ FROM ACT_HI_VARINST v WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'info') as info," +
                    "                   (SELECT DISTINCT u.REALNAME FROM ACT_HI_VARINST v LEFT JOIN RZTSYSUSER u  ON  u.ID = v.TEXT_ WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'userName') as v_user," +
                    "                    (SELECT DISTINCT u.PHONE FROM ACT_HI_VARINST v LEFT JOIN RZTSYSUSER u  ON  u.ID = v.TEXT_ WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'userName') as v_phone," +
                    "                  (SELECT DISTINCT u.PHONE FROM RZTSYSUSER u WHERE u.ID = x.CM_USER_ID) as phone," +
                    "                  (SELECT l.LINE_NAME FROM XS_ZC_CYCLE cy LEFT JOIN CM_LINE l ON l.ID = cy.LINE_ID WHERE cy.ID = x.XS_ZC_CYCLE_ID ) as LINE_NAME," +
                    "                  (SELECT l.V_LEVEL FROM XS_ZC_CYCLE cy LEFT JOIN CM_LINE l ON l.ID = cy.LINE_ID WHERE cy.ID = x.XS_ZC_CYCLE_ID ) as V_LEVEL," +
                    "                  (SELECT l.SECTION FROM XS_ZC_CYCLE cy LEFT JOIN CM_LINE l ON l.ID = cy.LINE_ID WHERE cy.ID = x.XS_ZC_CYCLE_ID ) as SECTION," +
                    "                   (SELECT  c.PLAN_XS_NUM from XS_ZC_CYCLE  c WHERE c.ID = x.XS_ZC_CYCLE_ID ) as xsnum," +
                    "                   (SELECT  c.TASK_NAME from XS_ZC_CYCLE  c WHERE c.ID = x.XS_ZC_CYCLE_ID ) as taskname," +
                    "                (SELECT  c.CYCLE from XS_ZC_CYCLE  c WHERE c.ID = x.XS_ZC_CYCLE_ID ) as xscycle" +
                    "      FROM ACT_RU_TASK t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_" +
                    "        LEFT JOIN XS_ZC_CYCLE_RECORD x ON x.XS_ZC_CYCLE_ID = h.TEXT_" +
                    "      WHERE h.NAME_ = 'XSID' AND t.PROC_DEF_ID_ LIKE 'xssh%'  AND t.ASSIGNEE_ = '"+userId+"') tt WHERE 1=1 ";

            if(!"公司本部".equals(td)){//各属地单位登录账号

            }
            if(null != lineName && !"".equals(lineName) ){
                sql += "  AND  tt.LINE_NAME LIKE '%"+lineName+"%' ";
            }
            if(null != vLevel && !"".equals(vLevel) ){
                sql += "  AND tt.V_LEVEL =  '"+vLevel+"'";
            }
            if(null != startTime && !"".equals(startTime) ){
                sql += "  AND   APPROVER_TIME >=  to_date('"+startTime+"','YYYY-MM-dd HH24:mi') ";
            }
            if(null != startTime && !"".equals(startTime) ){
                sql += "  AND   APPROVER_TIME <=  to_date('"+endTime+"','YYYY-MM-dd HH24:mi') ";
            }
            //判断当前用户所属节点    书否显示所有信息
           /* if("sdid".equals(userId) || "sdyjid".equals(userId)){
                sql += "  AND  tt.DID =   '"+td+"'";
            }else{
                if(null != tdId && !"".equals(tdId)){
                    sql += "  AND  tt.DID =   '"+tdId+"'";
                }
            }*/
            //判断当前用户所属节点    书否显示所有信息
            if("sdid".equals(userId) || "sdyjid".equals(userId)){
                sql += "  AND tt.DEPT = '"+td+"'  ";
            }else{
                if(null != tdId && !"".equals(tdId)){
                    sql += "  AND  tt.DID =   '"+tdId+"'";
                }
            }


            maps = this.execSqlPage(pageable, sql, null);
            LOGGER.info("当前节点待办任务查询成功"+userId);

        }catch (Exception e){
            LOGGER.error(userId+"当前节点待办信息查询失败"+e.getMessage());
            return WebApiResponse.success(userId+"当前节点待办信息查询失败"+e.getMessage());
        }

        return WebApiResponse.success(maps);
    }




    /**
     * 查看所有历史任务
     * @param userId 登录人
     * @param page
     * @param size
     * @param tdId   通道公司
     * @param lineName  线路名称
     * @param vLevel    电压等级
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return
     */
    public WebApiResponse historyActInstanceList(String userId,Integer page,Integer size
            ,String tdId,String lineName,String vLevel,String startTime,String endTime) {
        Page<Map<String, Object>> maps = null;
        try{
            String td = redisUtil.findTDByUserId(userId);
            userId = redisUtil.findRoleIdByUserId(userId);
            if(null == userId || "".equals(userId)){
                return WebApiResponse.success("巡视审核历史查询失败  登录人节点 = "+userId);
            }
            if(null == td || "".equals(td)){
                return WebApiResponse.success("巡视审核历史查询失败 通道公司="+td);
            }
            Pageable pageable = new PageRequest(page, size, null);
            String sql = "SELECT *" +
                    "FROM (SELECT" +
                    "  x.ID,x.XS_ZC_CYCLE_ID as cycleid,x.XS_ZC_CYCLE,x.PLAN_XS_NUM,x.APPROVER_TIME," +
                    "  x.PLAN_START_TIME,x.PLAN_END_TIME,x.CM_USER_ID,x.CHANGE_REASON,x.DESCRIPTION,x.PROPOSER_ID,t.START_TIME_,t.END_TIME_," +
                    "       (SELECT u.REALNAME FROM RZTSYSUSER u WHERE u.ID = x.CM_USER_ID ) as username," +
                    "       (SELECT u.DEPTID FROM RZTSYSUSER u WHERE u.ID = x.CM_USER_ID ) as DID," +
                    "       (SELECT d.DEPTNAME FROM RZTSYSUSER u LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID WHERE u.ID = x.CM_USER_ID ) as dept," +
                    "       (SELECT DISTINCT u.REALNAME FROM ACT_HI_VARINST v LEFT JOIN RZTSYSUSER u  ON  u.ID = v.TEXT_ WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'userName') as v_user," +
                    "       (SELECT DISTINCT u.PHONE FROM ACT_HI_VARINST v LEFT JOIN RZTSYSUSER u  ON  u.ID = v.TEXT_ WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'userName') as v_phone," +
                    "       (SELECT d.COMPANYNAME FROM RZTSYSUSER u LEFT JOIN RZTSYSCOMPANY d ON d.ID = u.COMPANYID WHERE u.ID = x.CM_USER_ID ) as wx," +
                    "       (SELECT DISTINCT v.TEXT_ FROM ACT_HI_VARINST v WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'info') as info," +
                    "       (SELECT DISTINCT v.TEXT_ FROM ACT_HI_VARINST v WHERE v.PROC_INST_ID_ = h.PROC_INST_ID_ AND v.NAME_ = 'flag') as flag," +
                    "       (SELECT DISTINCT u.PHONE FROM RZTSYSUSER u WHERE u.ID = x.CM_USER_ID) as phone," +
                    "       (SELECT l.LINE_NAME FROM XS_ZC_CYCLE cy LEFT JOIN CM_LINE l ON l.ID = cy.LINE_ID WHERE cy.ID = x.XS_ZC_CYCLE_ID ) as LINE_NAME," +
                    "       (SELECT  c.TASK_NAME from XS_ZC_CYCLE  c WHERE c.ID = x.XS_ZC_CYCLE_ID ) as taskname," +
                    "       (SELECT l.V_LEVEL FROM XS_ZC_CYCLE cy LEFT JOIN CM_LINE l ON l.ID = cy.LINE_ID WHERE cy.ID = x.XS_ZC_CYCLE_ID ) as V_LEVEL," +
                    "       (SELECT l.SECTION FROM XS_ZC_CYCLE cy LEFT JOIN CM_LINE l ON l.ID = cy.LINE_ID WHERE cy.ID = x.XS_ZC_CYCLE_ID ) as SECTION" +
                    "            FROM ACT_HI_ACTINST t" +
                    "            LEFT JOIN ACT_HI_VARINST h ON t.PROC_INST_ID_ = h.PROC_INST_ID_ AND h.NAME_ = 'XSID'" +
                    "            LEFT JOIN XS_ZC_CYCLE_RECORD x ON x.XS_ZC_CYCLE_ID = h.TEXT_" +
                    "            WHERE  t.PROC_DEF_ID_ LIKE 'xssh%' AND t.ASSIGNEE_ = '"+userId+"'  AND t.END_TIME_ IS NOT NULL  ORDER BY t.END_TIME_ DESC  ) tt WHERE  ID IS NOT NULL ";


            if(null != lineName && !"".equals(lineName) ){
                sql += "  AND  tt.LINE_NAME LIKE '%"+lineName+"%' ";
            }
            if(null != vLevel && !"".equals(vLevel) ){
                sql += "  AND tt.V_LEVEL =  '"+vLevel+"'";
            }
            if(null != startTime && !"".equals(startTime) ){
                sql += "  AND   APPROVER_TIME >=  to_date('"+startTime+"','YYYY-MM-dd HH24:mi') ";
            }
            if(null != startTime && !"".equals(startTime) ){
                sql += "  AND   APPROVER_TIME <=  to_date('"+endTime+"','YYYY-MM-dd HH24:mi') ";
            }
            //判断当前用户所属节点    书否显示所有信息
           /* if("sdid".equals(userId) || "sdyjid".equals(userId)){
                sql += "  AND  tt.DID =   '"+td+"'";
            }else{
                if(null != tdId && !"".equals(tdId)){
                    sql += "  AND  tt.DID =   '"+tdId+"'";
                }
            }*/
            //判断当前用户所属节点    书否显示所有信息
            if("sdid".equals(userId) || "sdyjid".equals(userId)){
                sql += "  AND tt.DEPT = '"+td+"'  ";
            }else{
                if(null != tdId && !"".equals(tdId)){
                    sql += "  AND  tt.DID =   '"+tdId+"'";
                }
            }
            maps = this.execSqlPage(pageable, sql, null);
            LOGGER.info("当前节点历史任务查询成功"+userId);

        }catch (Exception e){
            LOGGER.error(userId+"当前节点历史信息查询失败"+e.getMessage());
            return WebApiResponse.success(userId+"当前节点历史信息查询失败"+e.getMessage());
        }

        return WebApiResponse.success(maps);
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
        //yHrepository.updateAppId(map.get("XSID").toString(),userId,new Date(),"0");
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

    /**
     * 巡视周期变更   在巡视周期审核最后通过的监听器中调用
     * @param xsid
     */
    @Transactional
    public void updateXSCycle(String xsid,String actId){
        //查询变更信息
        String sql = "SELECT XS_ZC_CYCLE_ID,XS_ZC_CYCLE,PLAN_START_TIME,PLAN_END_TIME,PLAN_XS_NUM,CM_USER_ID,ID" +
                "       FROM XS_ZC_CYCLE_RECORD WHERE XS_ZC_CYCLE_ID = '"+xsid+"' AND PROPOSER_STATUS = 0 ";
        List<Map<String, Object>> maps = this.execSql(sql);
        try {
            if(null != maps && maps.size()>0){
                SimpleDateFormat sim = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                Map<String, Object> map = maps.get(0);
                //原周期id
                String xscycleId = map.get("XS_ZC_CYCLE_ID") == null ? "":map.get("XS_ZC_CYCLE_ID").toString();
                //巡视周期
                String XSCycle = map.get("XS_ZC_CYCLE") == null ? "":map.get("XS_ZC_CYCLE").toString();
                //计划开始时间
                String planStartTime = map.get("PLAN_START_TIME") == null ? "" : map.get("PLAN_START_TIME").toString();
                //计划结束时间
                String planEndTime = map.get("PLAN_END_TIME") == null ? "" : map.get("PLAN_END_TIME").toString();
                //巡视频率
                String xsNum = map.get("PLAN_XS_NUM") == null ? "" : map.get("PLAN_XS_NUM").toString();
                //执行人
                String userId = map.get("CM_USER_ID") == null ? "":map.get("CM_USER_ID").toString();
                //变更记录id   更改过原周期后需要改变记录状态
                String id = map.get("ID") == null ? "":map.get("ID").toString();
                //  按照原周期id查询
                if(null == xscycleId || "".equals(xscycleId)){//原id为空   证明新增

                }else {//修改
                    yHrepository.updateCycle(xscycleId,XSCycle,planStartTime,planEndTime,xsNum,userId);
                }
                //变更记录中审批状态和审批时间
                yHrepository.updateCycleRecord(id,new Date());
                //结束流程
                taskService.complete(actId,null);
            }
        } catch (Exception e) {
           LOGGER.error(e.getMessage());
        }

    }




}
