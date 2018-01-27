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


    public Object getCheckRecord(String loginUserId, Integer page, Integer size, String startDate, String endDate, Integer taskType, String vLevel, Integer lineId) {
        String deptID = getDeptID(loginUserId);
        if (deptID == null) {
            return "该用户状态为null";
        } else if ("-1".equals(deptID)) {
            return "该用户无此权限";
        }
        Pageable pageable = new PageRequest(page, size);

        String sql = "select * from ( SELECT tas.*,xs.TD_ORG,khh.TDYW_ORGID FROM" +
                "  (SELECT tcr.*,cm.V_LEVEL FROM" +
                "    (SELECT DISTINCT tc.TASKID,tc.TASKNAME,tc.CHECK_USER,tc.CREATE_TIME,tc.ID,tc.TASKTYPE,tc.THREEDAY,cr.LINE_ID FROM" +
                "      (SELECT          t.TASKID,          t.TASKNAME,          c.CHECK_USER,          c.CREATE_TIME,          c.ID,          t.TASKTYPE,            t.THREEDAY" +
                "       FROM TIMED_TASK t        RIGHT JOIN CHECK_DETAIL c ON t.TASKID = c.QUESTION_TASK_ID" +
                "       WHERE t.STATUS = 1) tc         LEFT JOIN CHECK_RESULT cr ON tc.ID = cr.CHECK_DETAIL_ID) tcr" +
                "    LEFT JOIN CM_LINE cm ON tcr.LINE_ID = cm.ID) tas" +
                "  LEFT JOIN XS_ZC_TASK xs ON tas.TASKID = xs.ID" +
                "    LEFT JOIN ( SELECT kh.ID,si.TDYW_ORGID FROM KH_TASK kh JOIN KH_SITE si ON kh.SITE_ID = si.ID ) khh" +
                "      ON khh.ID = tas.TASKID )  ORDER BY CREATE_TIME DESC  ";

        List<Object> list = new ArrayList<>();
        String s = "";
        if (taskType != null) {
            list.add(taskType);
            s += " AND TASKTYPE =?" + list.size();
        }
        if (startDate != null && !"".equals(startDate) && endDate != null && !"".equals(endDate)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            list.add(startDate);
            s += "  AND CREATE_TIME BETWEEN to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss') ";
            list.add(endDate);
            s += "  AND to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss')  ";
        }
        if (vLevel != null && !"".equals(vLevel)) {
            list.add(vLevel);
            s += " AND V_LEVEL = ?" + list.size();
        }
        if (lineId != null) {
            list.add(lineId);
            s += " AND LINE_ID =?" + list.size();
        }

        Page<Map<String, Object>> pageResult = null;
        try {
            String sqll = "";
            //最高权限查询所有
            if ("0".equals(deptID)) {
                sqll = " select * from ( " + sql + "  ) where 1 = 1  " + s ;
            } else {
                sqll = " select * from ( " + sql + " ) where   TD_ORG='" + deptID + "' OR TDYW_ORGID='" + deptID + "'" + s;
            }
            pageResult = this.execSqlPage(pageable, sqll, list.toArray());
            Iterator<Map<String, Object>> iterator = pageResult.iterator();
            HashOperations hashOperations = redisTemplate.opsForHash();

            while (iterator.hasNext()) {
                Map<String, Object> next = iterator.next();

                String userID = (String) next.get("CHECK_USER");
                Object userInformation = hashOperations.get("UserInformation", userID);
                if (userInformation == null) {
                    continue;
                }
                JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
                if (jsonObject != null) {
                    next.put("DEPT", jsonObject.get("DEPT"));
                    next.put("COMPANYNAME", jsonObject.get("COMPANYNAME"));
                    next.put("REALNAME", jsonObject.get("REALNAME"));
                    next.put("PHONE", jsonObject.get("PHONE"));
                }
            }
        } catch (Exception e) {
            return WebApiResponse.erro("查询失败" + e.getMessage());
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
                        String phs = checkResult.getPhotoIds() + photo_ids;
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
