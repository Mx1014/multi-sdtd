package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.CheckDetail;
import com.rzt.entity.CheckResult;
import com.rzt.repository.CheckResultRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.SnowflakeIdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

@Service

public class CheckResultService extends CurdService<CheckResult, CheckResultRepository> {

    protected static Logger LOGGER = LoggerFactory.getLogger(CheckResultService.class);
    @Autowired
    private CheckResultRepository checkResultRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /*
     * 添加审核结果
     */
    @Transactional
    public void addResult(CheckResult checkResult) {

        //为checkResult设置id
        checkResult.setId(Long.valueOf(SnowflakeIdWorker.getInstance(0, 0).nextId()));
        //添加创建时间

        checkResult.setCreateTime(new Date());
        checkResultRepository.save(checkResult);
    }

    public Page<Map<String, Object>> getCheckResult(Integer page, Integer size, CheckDetail checkDetail) {
        Pageable pageable = new PageRequest(page, size);
        String sql = "select * from check_result_view t" +
                " where 1=1";
        List<Object> list = new ArrayList<Object>();
        if (checkDetail != null) {
            if (checkDetail.getTdOrg() != null && !"".equals(checkDetail.getTdOrg().trim())) {
                list.add(checkDetail.getTdOrg());
                sql += " and t.td_org = ?" + list.size();
            }
            if (checkDetail.getCheckUser() != null && !"".equals(checkDetail.getCheckUser().trim())) {
                list.add(checkDetail.getCheckUser());
                sql += " and t.check_user = ?" + list.size();
            }
            if (checkDetail.getCheckOrg() != null && !"".equals(checkDetail.getCheckOrg().trim())) {
                list.add(checkDetail.getCheckOrg());
                sql += " and t.check_org = ?" + list.size();
            }
            if (checkDetail.getQuestionTaskId() != null) {
                list.add(checkDetail.getQuestionTaskId());
                sql += " and t.question_task_id = ?" + list.size();
            }
        }
        Page<Map<String, Object>> execSqlPage = this.execSqlPage(pageable, sql, list.toArray());
        return execSqlPage;
    }


    public Object getQuestion(Long questionTaskId) {
        ArrayList<String> longs = new ArrayList<>();
        longs.add(questionTaskId + "");
        String sql = "SELECT r.*,d.CHECK_DETAIL_TYPE,d.QUESTION_TASK_ID  " +
                " FROM CHECK_RESULT r LEFT JOIN CHECK_DETAIL d ON r.CHECK_DETAIL_ID = d.ID  " +
                " WHERE d.QUESTION_TASK_ID = ?" + longs.size();
        List<Map<String, Object>> maps = execSql(sql, longs.toArray());
        return maps;
    }


    public Object getCheckRecord(Integer page,Integer size, String taskType ,String userId
            ,String userName,String TD,String targetType,String TaskName,String startDate,String endDate) {
        /**
         *   所有权限	    0
         公司本部权限	1
         属地单位权限	2
         外协队伍权限	3
         组织权限	    4
         个人权限	    5

         */
        List<Object> list = new ArrayList<>();
        Pageable pageable = new PageRequest(page, size);
        //sql 中 拉取数据为刷新时间至刷新时间前10分钟
        String sql = "";
        Page<Map<String, Object>> pageResult = null;
        try {
            if(null == userId || "".equals(userId)){
                return WebApiResponse.success("");
            }

            Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", userId);
            if(null != userInformation1 && !"".equals(userInformation1)){
                JSONObject jsonObject1 = JSONObject.parseObject(userInformation1.toString());
                String roletype = (String) jsonObject1.get("ROLETYPE");//用户权限信息  0 为1级单位  1为二级单位 2为单位 只展示当前单位的任务
                String deptid = (String) jsonObject1.get("DEPTID");//当角色权限为3时需要只显示本单位的任务信息
                if(null != roletype && !"".equals(roletype)){//证明当前用户信息正常
                    int i = Integer.parseInt(roletype);
                    switch (i){
                        case 0 :{

                            sql = "    SELECT * FROM (  SELECT DISTINCT t.TASKID," +
                                    "   t.ID," +
                                    "   t.USER_ID,t.THREEDAY," +
                                    "   t.TASKNAME,cd.CHECK_USER,d.ID AS TDYW_ORGID," +
                                    "    t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,dd.ID as did,t.CREATETIME," +
                                    "  dd.DEPTNAME as DEPT,d.DEPTNAME,u.REALNAME as REALNAME,u.PHONE,(SELECT COMPANYNAME FROM RZTSYSCOMPANY WHERE ID = xs.WX_ORG) AS COMPANYNAME" +
                                    "    FROM TIMED_TASK t  LEFT JOIN CHECK_DETAIL cd ON t.TASKID = cd.QUESTION_TASK_ID" +
                                    "        LEFT JOIN RZTSYSUSER u ON u.ID = cd.CHECK_USER    LEFT JOIN RZTSYSUSER uu ON uu.ID = t.USER_ID" +
                                    "           LEFT JOIN RZTSYSDEPARTMENT dd ON dd.ID = uu.DEPTID  " +
                                    "   LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = cd.CHECK_ORG LEFT JOIN XS_ZC_TASK xs ON xs.ID = t.TASKID" +
                                    "    WHERE  t.STATUS = 1 AND t.THREEDAY = 1 AND t.TASKTYPE = 1" +
                                    "  UNION ALL" +
                                    "   SELECT DISTINCT t.TASKID," +
                                    "     t.ID," +
                                    "     t.USER_ID,t.THREEDAY," +
                                    "     t.TASKNAME,cd.CHECK_USER,d.ID AS TDYW_ORGID," +
                                    "     t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,dd.ID as did,t.CREATETIME," +
                                    "     dd.DEPTNAME as DEPT,d.DEPTNAME,u.REALNAME as REALNAME,u.PHONE" +
                                    "  ,kh.WX_ORG  AS COMPANYNAME" +
                                    "   FROM TIMED_TASK t  LEFT JOIN CHECK_DETAIL cd ON t.TASKID = cd.QUESTION_TASK_ID" +
                                    "     LEFT JOIN RZTSYSUSER u ON u.ID = cd.CHECK_USER   LEFT JOIN RZTSYSUSER uu ON uu.ID = t.USER_ID" +
                                    "           LEFT JOIN RZTSYSDEPARTMENT dd ON dd.ID = uu.DEPTID " +
                                    "     LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = cd.CHECK_ORG LEFT JOIN KH_TASK kh ON kh.ID = t.TASKID" +
                                    "   WHERE  t.STATUS = 1 AND t.THREEDAY = 1 AND t.TASKTYPE = 2" +
                                    "    UNION  ALL" +
                                    "                 SELECT DISTINCT t.TASKID," +
                                    "                   t.ID," +
                                    "                   t.USER_ID,t.THREEDAY," +
                                    "                   t.TASKNAME,cd.CHECK_USER,d.ID AS TDYW_ORGID," +
                                    "                   t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,dd.ID as did,t.CREATETIME," +
                                    "                  dd.DEPTNAME as DEPT,d.DEPTNAME,u.REALNAME as REALNAME,u.PHONE,com.COMPANYNAME AS COMPANYNAME" +
                                    "                 FROM TIMED_TASK t LEFT JOIN CHECK_LIVE_TASK c ON  t.TASKID = c.ID" +
                                    "                   LEFT JOIN CHECK_DETAIL cd ON t.TASKID = cd.CHECK_USER" +
                                    "                   LEFT JOIN RZTSYSUSER u ON u.ID = cd.QUESTION_USER_ID    LEFT JOIN RZTSYSUSER uu ON uu.ID = t.USER_ID" +
                                    "           LEFT JOIN RZTSYSDEPARTMENT dd ON dd.ID = uu.DEPTID  " +
                                    "                   LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = cd.CHECK_ORG LEFT JOIN RZTSYSCOMPANY com ON com.ID = u.COMPANYID" +
                                    "                 WHERE" +
                                    "                         t.STATUS = 1 AND t.THREEDAY = 1 AND t.TASKTYPE = 3" +
                                    "    ) WHERE 1=1";
                            break;
                        }case 1 :{//公司本部单位   显示全部周期为三天的任务

                            sql = "    SELECT * FROM (  SELECT DISTINCT t.TASKID," +
                                    "   t.ID," +
                                    "   t.USER_ID,t.THREEDAY," +
                                    "   t.TASKNAME,cd.CHECK_USER,d.ID AS TDYW_ORGID," +
                                    "    t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,dd.ID as did,t.CREATETIME," +
                                    "  dd.DEPTNAME as DEPT,d.DEPTNAME,u.REALNAME as REALNAME,u.PHONE,(SELECT COMPANYNAME FROM RZTSYSCOMPANY WHERE ID = xs.WX_ORG) AS COMPANYNAME" +
                                    "    FROM TIMED_TASK t  LEFT JOIN CHECK_DETAIL cd ON t.TASKID = cd.QUESTION_TASK_ID" +
                                    "        LEFT JOIN RZTSYSUSER u ON u.ID = cd.CHECK_USER    LEFT JOIN RZTSYSUSER uu ON uu.ID = t.USER_ID" +
                                    "           LEFT JOIN RZTSYSDEPARTMENT dd ON dd.ID = uu.DEPTID  " +
                                    "   LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = cd.CHECK_ORG LEFT JOIN XS_ZC_TASK xs ON xs.ID = t.TASKID" +
                                    "    WHERE  t.STATUS = 1 AND t.THREEDAY = 1 AND t.TASKTYPE = 1" +
                                    "  UNION ALL" +
                                    "   SELECT DISTINCT t.TASKID," +
                                    "     t.ID," +
                                    "     t.USER_ID,t.THREEDAY," +
                                    "     t.TASKNAME,cd.CHECK_USER,d.ID AS TDYW_ORGID," +
                                    "     t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,dd.ID as did,t.CREATETIME," +
                                    "     dd.DEPTNAME as DEPT,d.DEPTNAME,u.REALNAME as REALNAME,u.PHONE" +
                                    "  ,kh.WX_ORG  AS COMPANYNAME" +
                                    "   FROM TIMED_TASK t  LEFT JOIN CHECK_DETAIL cd ON t.TASKID = cd.QUESTION_TASK_ID" +
                                    "     LEFT JOIN RZTSYSUSER u ON u.ID = cd.CHECK_USER   LEFT JOIN RZTSYSUSER uu ON uu.ID = t.USER_ID" +
                                    "           LEFT JOIN RZTSYSDEPARTMENT dd ON dd.ID = uu.DEPTID " +
                                    "     LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = cd.CHECK_ORG LEFT JOIN KH_TASK kh ON kh.ID = t.TASKID" +
                                    "   WHERE  t.STATUS = 1 AND t.THREEDAY = 1 AND t.TASKTYPE = 2" +
                                    "    UNION  ALL" +
                                    "                 SELECT DISTINCT t.TASKID," +
                                    "                   t.ID," +
                                    "                   t.USER_ID,t.THREEDAY," +
                                    "                   t.TASKNAME,cd.CHECK_USER,d.ID AS TDYW_ORGID," +
                                    "                   t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,dd.ID as did,t.CREATETIME," +
                                    "                  dd.DEPTNAME as DEPT,d.DEPTNAME,u.REALNAME as REALNAME,u.PHONE,com.COMPANYNAME AS COMPANYNAME" +
                                    "                 FROM TIMED_TASK t LEFT JOIN CHECK_LIVE_TASK c ON  t.TASKID = c.ID" +
                                    "                   LEFT JOIN CHECK_DETAIL cd ON t.TASKID = cd.CHECK_USER" +
                                    "                   LEFT JOIN RZTSYSUSER u ON u.ID = cd.QUESTION_USER_ID    LEFT JOIN RZTSYSUSER uu ON uu.ID = t.USER_ID" +
                                    "           LEFT JOIN RZTSYSDEPARTMENT dd ON dd.ID = uu.DEPTID  " +
                                    "                   LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = cd.CHECK_ORG LEFT JOIN RZTSYSCOMPANY com ON com.ID = u.COMPANYID" +
                                    "                 WHERE" +
                                    "                         t.STATUS = 1 AND t.THREEDAY = 1 AND t.TASKTYPE = 3" +
                                    "    ) WHERE 1=1";
                            break;
                        }case 2 :{//属地单位   只显示本单位的任务

                            if(null != deptid && !"".equals(deptid)){//当前用户单位信息获取成功，进入流程
                                list.add(deptid);

                                sql = "          SELECT * FROM (  SELECT DISTINCT t.TASKID," +
                                        "         t.ID," +
                                        "         t.USER_ID,t.THREEDAY," +
                                        "         t.TASKNAME,cd.CHECK_USER,d.ID AS TDYW_ORGID," +
                                        "          t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,d.ID as did,t.CREATETIME," +
                                        "        d.DEPTNAME as DEPT,u.REALNAME as REALNAME,u.PHONE,(SELECT COMPANYNAME FROM RZTSYSCOMPANY WHERE ID = xs.WX_ORG) AS COMPANYNAME" +
                                        "          FROM TIMED_TASK t  LEFT JOIN CHECK_DETAIL cd ON t.TASKID = cd.QUESTION_TASK_ID" +
                                        "              LEFT JOIN RZTSYSUSER u ON u.ID = cd.CHECK_USER" +
                                        "         LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = cd.CHECK_ORG LEFT JOIN XS_ZC_TASK xs ON xs.ID = t.TASKID" +
                                        "          WHERE  t.STATUS = 1 AND t.THREEDAY = 0 AND t.TASKTYPE = 1" +
                                        "        UNION ALL" +
                                        "         SELECT DISTINCT t.TASKID," +
                                        "           t.ID," +
                                        "           t.USER_ID,t.THREEDAY," +
                                        "           t.TASKNAME,cd.CHECK_USER,d.ID AS TDYW_ORGID," +
                                        "           t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,d.ID as did,t.CREATETIME," +
                                        "           d.DEPTNAME as DEPT,u.REALNAME as REALNAME,u.PHONE" +
                                        "        ,kh.WX_ORG  AS COMPANYNAME" +
                                        "         FROM TIMED_TASK t  LEFT JOIN CHECK_DETAIL cd ON t.TASKID = cd.QUESTION_TASK_ID" +
                                        "           LEFT JOIN RZTSYSUSER u ON u.ID = cd.CHECK_USER" +
                                        "           LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = cd.CHECK_ORG LEFT JOIN KH_TASK kh ON kh.ID = t.TASKID" +
                                        "         WHERE  t.STATUS = 1 AND t.THREEDAY = 0 AND t.TASKTYPE = 2" +
                                        "          UNION  ALL" +
                                        "                       SELECT DISTINCT t.TASKID," +
                                        "                         t.ID," +
                                        "                         t.USER_ID,t.THREEDAY," +
                                        "                         t.TASKNAME,cd.CHECK_USER,d.ID AS TDYW_ORGID," +
                                        "                         t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,d.ID as did,t.CREATETIME," +
                                        "                        d.DEPTNAME as DEPT,u.REALNAME as REALNAME,u.PHONE,com.COMPANYNAME AS COMPANYNAME" +
                                        "                       FROM TIMED_TASK t LEFT JOIN CHECK_LIVE_TASK c ON  t.TASKID = c.ID" +
                                        "                         LEFT JOIN CHECK_DETAIL cd ON t.TASKID = cd.CHECK_USER" +
                                        "                         LEFT JOIN RZTSYSUSER u ON u.ID = cd.QUESTION_USER_ID" +
                                        "                         LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = cd.CHECK_ORG LEFT JOIN RZTSYSCOMPANY com ON com.ID = u.COMPANYID" +
                                        "                       WHERE" +
                                        "                               t.STATUS = 1 AND t.THREEDAY = 0 AND t.TASKTYPE = 3" +
                                        "          ) WHERE   DID  = ?"+list.size();
                            }else {
                                LOGGER.error("获取当前用户单位信息失败");
                                return WebApiResponse.erro("获取当前用户单位信息失败");
                            }
                            break;
                        }default:{
                            LOGGER.error("获取登录人权限失败,当前权限未知");
                            return WebApiResponse.erro("获取登录人权限失败,当前权限未知");
                        }
                    }
                }
            }
            if(taskType!=null && !"".equals(taskType.trim())){// 判断当前任务类型  巡视1   看护2  看护稽查3  巡视稽查4
                sql+= "  AND TASKTYPE = "+taskType;
            }

            //查询责任人
            if(null != userName && !"".equals(userName)){
                sql += "  AND  REALNAME LIKE '%"+userName+"%'";
            }
            //通道单位
            if(null != TD && !"".equals(TD)){
                sql += "  AND  DID = '"+TD+"'";
            }
            //任务状态
            if(null != targetType && !"".equals(targetType)){
                sql += "  AND  TARGETSTATUS =  "+targetType;
            }
            //任务名称模糊查询
            if(null != TaskName && !"".equals(TaskName)){
                sql += "  AND  TASKNAME like '%"+TaskName+"%'  ";
            }
            //按时间查询 startDate,String endDate
            if((null != startDate  &&  !"".equals(startDate)) && (null != endDate && !"".equals(endDate))){
                sql += "  AND  CREATETIME >= to_date('"+startDate+"','YYYY-MM-dd HH24:mi:ss')  AND" +
                        "    CREATETIME <= to_date('"+endDate+"','YYYY-MM-dd HH24:mi:ss')      ";
            }
            if(null != sql && !"".equals(sql)){
                sql +="   ORDER BY CREATETIME DESC     ";
            }

            pageResult = this.execSqlPage(pageable, sql, list.toArray());

        }catch (Exception e){
            LOGGER.error("抽查任务查询失败"+e.getMessage());
            return WebApiResponse.erro("抽查任务查询失败"+e.getMessage());
        }
        return WebApiResponse.success(pageResult);
    }

    /**
     * 判断当前问题是否重复
     *
     * @param checkResult
     * @param checkDetail
     * @return
     */
    public List<Map<String, Object>> getCheckResultInfo(CheckResult checkResult, CheckDetail checkDetail) {
        if ((null != checkDetail.getQuestionTaskId() && checkDetail.getQuestionTaskId() > 0) && (null != checkResult.getQuestionType() && checkResult.getQuestionType() > 0)) {
            ArrayList<Object> strings = new ArrayList<>();
            strings.add(checkDetail.getQuestionTaskId());
            strings.add(checkResult.getQuestionType());
            String sql = "SELECT r.ID FROM CHECK_RESULT r LEFT JOIN CHECK_DETAIL d ON r.CHECK_DETAIL_ID = d.ID" +
                    "    WHERE QUESTION_TASK_ID = ?1 AND QUESTION_TYPE = ?2";
            List<Map<String, Object>> maps = this.execSql(sql, strings.toArray());
            return maps;

        }
        return null;


    }

    /**
     * 如果当前添加的问题已存在 需要将问题中的图片id合并  并去重
     *
     * @param checkResult
     * @param checkDetail
     * @param id
     * @return
     */
    @Transactional
    public WebApiResponse updateByCheckId(CheckResult checkResult, CheckDetail checkDetail, String id) {
        try {
            if ((null != checkDetail.getQuestionTaskId() && checkDetail.getQuestionTaskId() > 0) && (null != checkResult.getQuestionType() && checkResult.getQuestionType() > 0) && (null != checkResult.getPhotoIds() && !"".equals(checkResult.getPhotoIds()))) {
                ArrayList<Object> strings = new ArrayList<>();
                strings.add(checkDetail.getQuestionTaskId());
                strings.add(checkResult.getQuestionType());
                String sql = "SELECT r.PHOTO_IDS FROM CHECK_RESULT r LEFT JOIN CHECK_DETAIL d ON r.CHECK_DETAIL_ID = d.ID" +
                        "    WHERE QUESTION_TASK_ID = ?1 AND QUESTION_TYPE = ?2";
                List<Map<String, Object>> maps = this.execSql(sql, strings.toArray());
                if (null != maps.get(0)) {
                    String photo_ids = (String) maps.get(0).get("PHOTO_IDS");
                    if (null != photo_ids && !"".equals(photo_ids)) {
                        String phs = checkResult.getPhotoIds()+"," + photo_ids;
                        String[] split = phs.split(",");
                        HashSet<String> set = new HashSet<>();
                        for (String s : split) {
                            if (null != s && !"".equals(s)) {
                                set.add(s);
                            }
                        }
                        String ids = "";
                        ArrayList<String> strings1 = new ArrayList<>(set);
                        for (String s : strings1) {
                            if (null != s && !"".equals(s)) {
                                ids += s + ",";
                            }
                        }


                        checkResult.setPhotoIds(ids);
                        checkResultRepository.updateByCheckId(id, checkResult.getPhotoIds(), checkResult.getQuestionInfo());
                    } else {
                        checkResultRepository.updateByCheckId(id, checkResult.getPhotoIds(), checkResult.getQuestionInfo());
                    }
                    return WebApiResponse.success("添加完成");
                }

            }
        } catch (Exception e) {
            LOGGER.error("参数错误" + e.getMessage());
            return WebApiResponse.success("参数错误" + e.getMessage());
        }
        return WebApiResponse.success("添加成功");
    }

    public WebApiResponse getQuestionInfo(String id) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(id);
        String sql = "SELECT QUESTION_INFO from CHECK_RESULT WHERE ID = ?1";
        List<Map<String, Object>> maps = this.execSql(sql, strings);
        if (null != strings && strings.size() > 0) {
            if (null != maps.get(0) && !"".equals(maps.get(0))) {
                String question_info = (String) maps.get(0).get("QUESTION_INFO");
                if (null != question_info && !"".equals(question_info)) {
                    return WebApiResponse.success(question_info);
                }
            }
        }
        return WebApiResponse.success("");
    }

    //获取当前登录用户的deptId，如果是全部查询则返回0  权限使用
    public String getDeptID(String userId) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        Object userInformation = hash.get("UserInformation", userId);
        if (userInformation == null || "".equals(userInformation)) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
        String roletype = (String) jsonObject.get("ROLETYPE");
        if (roletype == null || "".equals(roletype)) {
            return null;
        }
        if ("0".equals(roletype)) {
            //0是查询全部
            return "0";
        } else if ("1".equals(roletype) || "2".equals(roletype)) {
            return (String) jsonObject.get("DEPTID");
        } else {
            return "-1";
        }
    }
}
