/**    
 * 文件名：XsZcCycleService           
 * 版本信息：    
 * 日期：2017/12/07 07:50:10    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service.pc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.app.XSZCTASK;
import com.rzt.entity.pc.XsZcCycle;
import com.rzt.entity.pc.XsZcCycleLineTower;
import com.rzt.entity.sch.XsTaskSCh;
import com.rzt.repository.pc.XsZcCycleRepository;
import com.rzt.service.CurdService;
import com.rzt.service.app.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**      
 * 类名称：XsZcCycleService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 07:50:10 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 07:50:10    
 * 修改备注：    
 **-0* @version
 */
@Service
public class XsZcCycleService extends CurdService<XsZcCycle,XsZcCycleRepository> {

    @Autowired
    private XSZCTASKService xszctaskService;
    @Autowired
    private XsZcCycleLineTowerService xsZcCycleLineTowerService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /***
    * @Method
     * addCycle
    * @Description 新增周期
    * @param xsZcCycle
    * @param userId
     * @return java.lang.Object
    * @date 2017/12/8 9:18
    * @author nwz
    */

//

    @Modifying
    @Transactional
    public void addCycle(XsZcCycle xsZcCycle, String userId) throws Exception {
//        Map<String,Object> userInfo = userInfoFromRedis(userId);
//        String deptid = userInfo.get("DEPTID").toString();
        //添加周期
        xsZcCycle.setId();
        xsZcCycle.setCreateTime(DateUtil.dateNow());
//        xsZcCycle.setTdywOrg(deptid);
        this.add(xsZcCycle);
        //添加周期表关联的线路杆塔
        Long xsZcCycleId = xsZcCycle.getId();
        Long lineId = xsZcCycle.getLineId();
        Integer xsStartSort = xsZcCycle.getXsStartSort();
        Integer xsEndSort = xsZcCycle.getXsEndSort();
        String sql = "select * from cm_line_tower where line_id = ?1 and tower_id between ?2 and ?3";
        List<Map<String, Object>> lineTowerList = this.execSql(sql,lineId,xsStartSort,xsEndSort);
        for (Map<String,Object> lineTower: lineTowerList) {
            long towerId = Long.parseLong(lineTower.get("TOWER_ID").toString());
            XsZcCycleLineTower xsZcCycleLineTower = new XsZcCycleLineTower();
            xsZcCycleLineTower.setId();
            xsZcCycleLineTower.setCmLineTowerId(towerId);
            xsZcCycleLineTower.setXsZcCycleId(xsZcCycleId);
            xsZcCycleLineTowerService.add(xsZcCycleLineTower);
        }
    }

    @Modifying
    @Transactional
    public Object addPlan(XSZCTASK xszctask) {
        try {
            String userId = xszctask.getCmUserId();
            Map<String,Object> jsonObject = userInfoFromRedis(userId);
            String deptid = jsonObject.get("DEPTID").toString();
            String classid = jsonObject.get("CLASSID").toString();
            String companyid = jsonObject.get("COMPANYID").toString();

            //拿到周期相关的信息
            Long xsZcCycleId = xszctask.getXsZcCycleId();
            String sql = "select task_name,section,plan_xs_num,cycle,total_task_num from xs_zc_cycle where id = ?1";
            List<Map<String, Object>> xsZcCycles = this.execSql(sql, xsZcCycleId);
            if(xsZcCycles.isEmpty()) {
                return WebApiResponse.erro("没有这个周期");
            } else {
                Map<String, Object> xsZcCycle = xsZcCycles.get(0);
                String taskName = xsZcCycle.get("TASK_NAME").toString();
                Integer planXsNum = Integer.parseInt(xsZcCycle.get("PLAN_XS_NUM").toString());

                xszctask.setId();
                xszctask.setTaskName(taskName);
                xszctask.setPlanXsNum(planXsNum);
                xszctask.setTaskNumInCycle(0);
                xszctask.setRealXsNum(0);
                xszctask.setTaskNumInCycle(0);
                xszctask.setStauts(0);
                xszctask.setPdTime(DateUtil.dateNow());
                xszctask.setTdOrg(deptid);
                xszctask.setWxOrg(companyid);
                xszctask.setClassId(classid);

                xszctaskService.add(xszctask);
                this.reposiotry.updateTotalTaskNum(xsZcCycleId,classid,companyid,userId);
            }


            return WebApiResponse.success("数据保存成功!");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据保存失败" + var3.getStackTrace());
        }
    }


    /***
    * @Method cycleList
    * @Description 巡视任务周期列表
    * @param [pageable, xsTaskSch]
    * @param userId
     * @return java.lang.Object
    * @date 2017/12/14 10:34
    * @author nwz
    */
    public Object cycleList(Pageable pageable, XsTaskSCh xsTaskSch, String userId) throws Exception {
        String authoritySql = userAuthority(userId);//把权限的sql给我

        StringBuffer sqlBuffer = new StringBuffer();
        ArrayList arrList = new ArrayList();
        sqlBuffer.append("SELECT id,plan_xs_num xspl,plan_start_time,plan_end_time,v_level \"vLevel\",task_name \"taskName\",section,cycle,td_org \"tdywOrg\",in_use \"inUse\",total_task_num \"totalTaskNum\",create_time \"createTime\" FROM xs_zc_cycle where 1 = 1 and is_delete = 0");
        sqlBuffer.append(authoritySql);//我是权限
        //开始日期  结束日期
        Date startDate = xsTaskSch.getStartDate();
        Date endDate = xsTaskSch.getEndDate();
        if(startDate != null && endDate != null) {
            sqlBuffer.append(" and create_time between ? and ?");
            arrList.add(startDate);
            arrList.add(endDate);
        }

        //0 在用 1 停用
        Integer status = xsTaskSch.getStatus();
        if(status != null) {
            sqlBuffer.append(" and in_use = ?");
            arrList.add(status);

        }

        //区分周期维护 和 任务派发的请求
        Integer ispf = xsTaskSch.getIspf();
        if(ispf == 1) {
            sqlBuffer.append(" and total_task_num = 0");
        }

        //电压等级
        Integer v_type = xsTaskSch.getV_type();
        if(v_type != null) {
            sqlBuffer.append(" and V_LEVEL = ?");
            arrList.add(v_type);
        }

        //线路id
        Long lineId = xsTaskSch.getLineId();
        if(lineId != null) {
            sqlBuffer.append(" and line_id = ?");
            arrList.add(lineId);
        }
        
        //通道单位
        xsTaskSch.getTdOrg();

        sqlBuffer.append(" order by id desc");


        Page<Map<String, Object>> maps = this.execSqlPage(pageable,sqlBuffer.toString(), arrList.toArray());
        return maps;
    }


    /***
    * @Method userAuthority
    * @Description  权限模块
    * @param [userId]
    * @return void
    * @date 2018/1/3 10:12
    * @author nwz
    */
    public String userAuthority(String userId) throws Exception {
        String authoritySql = "";
        if(userId != null) {
            //从reids中拿userInfo
            Map<String,Object> jsonObject = userInfoFromRedis(userId);
            try {
                Integer roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
                String deptid = jsonObject.get("DEPTID").toString();
                String classid = jsonObject.get("CLASSID").toString();
                String companyid = jsonObject.get("COMPANYID").toString();
                switch (roletype) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        authoritySql = " and td_org = '" + deptid + "'";
                        break;
                    case 3:
                        authoritySql = " and wx_org = '" + companyid + "'";
                        break;
                    case 4:
                        authoritySql = " and class_id = '" + classid + "'";
                        break;
                    case 5:
                        authoritySql = " and cm_user_id = '" + userId + "'";
                        break;
                }
                return authoritySql;
            } catch (Exception e) {
                e.printStackTrace();
                return authoritySql;
            }

        }
        //拼权限的sql
        return authoritySql;

    }

    public Map<String, Object> userInfoFromRedis(String userId) throws Exception {
        HashOperations hashOperations = redisTemplate.opsForHash();

        Map<String,Object> jsonObject = null;
        Object userInformation = hashOperations.get("UserInformation", userId);
        if(userInformation == null) {
            String sql = "select * from userinfo where id = ?";
            jsonObject = this.execSqlSingleResult(sql, userId);
            hashOperations.put("UserInformation",userId,jsonObject);
        } else {
            jsonObject = JSON.parseObject(userInformation.toString(),Map.class);
        }
        return jsonObject;
    }

    public Object judgeFromRedis() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Object judge = stringRedisTemplate.opsForValue().get("judge");
        return judge;
    }



    /**
    * @Method logicalDelete
    * @Description 逻辑删除
    * @param [ids]
    * @return java.lang.Object
    * @date 2017/12/14 14:09
    * @author nwz
    */
    public void logicalDelete(Long[] ids) {
        List<Long> longs = Arrays.asList(ids);
        this.reposiotry.logicalDelete(longs);
    }
    /**
    * @Method listPlan
    * @Description 任务列表
    * @param [pageable, xsTaskSch]
    * @param userId
     * @return java.lang.Object
    * @date 2017/12/14 14:17
    * @author nwz
    */
    public Object listPlan(Pageable pageable, XsTaskSCh xsTaskSch, String userId1) throws Exception {
        String authoritySql = userAuthority(userId1);//把权限的sql给我

        StringBuffer sqlBuffer = new StringBuffer();
        ArrayList arrList = new ArrayList();
        sqlBuffer.append("SELECT * FROM xs_zc_task where 1 = 1 and is_delete = 0 ");
        sqlBuffer.append(authoritySql);//拼上权限的sql
        //开始日期 结束日期
        Date startDate = xsTaskSch.getStartDate();
        Date endDate = xsTaskSch.getEndDate();
        if (startDate != null && endDate != null) {
            sqlBuffer.append("and plan_start_time between ? and ? ");
            arrList.add(startDate);
            arrList.add(endDate);
        }

        //状态 0 未开始 1 巡视中 2 已完成
        Integer status = xsTaskSch.getStatus();
        if (status != null) {
            sqlBuffer.append("and stauts = ? ");
            arrList.add(status);

        }

        //人员id
        String userId = xsTaskSch.getUserId();
        if (userId != null) {
            sqlBuffer.append("and cm_user_id = ? ");
            arrList.add(userId);
        }

        //线路id
        Long lineId = xsTaskSch.getLineId();
        if (lineId != null) {
            sqlBuffer.append("and line_id = ? ");
            arrList.add(userId);
        }

        //通道单位
        

        Page<Map<String, Object>> maps = this.execSqlPage(pageable, sqlBuffer.toString(), arrList.toArray());
        return maps;
    }

    /***
    * @Method getCycle
    * @Description 查看周期
    * @param [id]
    * @return java.lang.Object
    * @date 2017/12/15 16:56
    * @author nwz
    */
//    @Cacheable(value = "xsZcCycles" , key = "#id")
    public Object getCycle(Long id) throws Exception{
        String sql = "select * from xs_zc_cycle where id = ?";
        Map<String, Object> cycle = this.execSqlSingleResult(sql, id);
        return cycle;
    }

//    @CacheEvict(value = "xsZcCycles" , key = "#id")
    public void updateCycle(Long id, Integer cycle, Integer inUse, Integer planXsNum, String planStartTime, String planEndTime) {
        this.reposiotry.updateCycle(id,cycle,inUse,planXsNum,planStartTime,planEndTime);
    }

    public Object listPictureById(Long taskId, Integer zj) {
        String sql = "select PROCESS_NAME \"name\",FILE_SMALL_PATH \"smallFilePath\",FILE_PATH \"filePath\",CREATE_TIME \"createTime\",lon,lat from PICTURE_TOUR WHERE TASK_ID = ? order by id";
        if(zj == 1) {
            sql += " desc";
        }
        List<Map<String, Object>> maps = this.execSql(sql, taskId);
        return maps;
    }

    public Object listExecByTaskid(Long taskId) {
        String sql = "select id,XS_CREATE_TIME,XS_END_TIME,XS_STATUS,XS_REPEAT_NUM from XS_ZC_TASK_EXEC where XS_ZC_TASK_ID = ? order by ID";
        List<Map<String, Object>> maps = this.execSql(sql, taskId);
        return maps;
    }

    public Object listExecDetail(Long execId) {
        String sql = "select t.ID,t.IS_DW,t.OPERATE_NAME,t.END_TIME,t.REASON,t.REALLONGITUDE,t.REALLATITUDE,tt.LONGITUDE,tt.LATITUDE from (select * from XS_ZC_TASK_EXEC_DETAIL where XS_ZC_TASK_EXEC_ID = ? and end_tower_id = '0') t join CM_TOWER tt on t.START_TOWER_ID = tt.ID";
        List<Map<String, Object>> maps = this.execSql(sql, execId);
        return maps;
    }

    public void logicalDeletePlan(Long[] ids) {
        this.reposiotry.logicalDeletePlan(ids);
    }

}