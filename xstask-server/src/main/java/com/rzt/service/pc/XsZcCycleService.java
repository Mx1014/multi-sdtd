/**    
 * 文件名：XsZcCycleService           
 * 版本信息：    
 * 日期：2017/12/07 07:50:10    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service.pc;

import com.alibaba.fastjson.JSON;
import com.rzt.entity.app.XSZCTASK;
import com.rzt.entity.pc.XsZcCycle;
import com.rzt.entity.pc.XsZcCycleLineTower;
import com.rzt.entity.sch.XsTaskSCh;
import com.rzt.repository.pc.XsZcCycleRepository;
import com.rzt.service.CurdService;
import com.rzt.service.app.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import com.rzt.utils.SnowflakeIdWorker;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
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
     * @param towerIds
     * @return java.lang.Object
     * @date 2017/12/8 9:18
     * @author nwz
     */

//
    @Modifying
    @Transactional
    public void addCycle(XsZcCycle xsZcCycle, String userId, Long[] towerIds) throws Exception {
        Map<String, Object> userInfo = userInfoFromRedis(userId);
        String deptid = userInfo.get("DEPTID").toString();
        //添加周期
        xsZcCycle.setId();
        xsZcCycle.setCreateTime(DateUtil.dateNow());
        xsZcCycle.setTdywOrg(deptid);
        this.add(xsZcCycle);
        //添加周期表关联的线路杆塔
        Long xsZcCycleId = xsZcCycle.getId();
        Long lineId = xsZcCycle.getLineId();
        Long xsStartSort = xsZcCycle.getXsStartSort();
        Long xsEndSort = xsZcCycle.getXsEndSort();
//        String sql = "select * from cm_line_tower where line_id = ?1 and tower_id between ?2 and ?3";
        String sql = "select * from cm_line_tower where line_id = ?1 and tower_id in (?2)";
        List<Long> ids = Arrays.asList(towerIds);
        List<Map<String, Object>> lineTowerList = this.execSql(sql, lineId, ids);
        insertCycleTowersMethod(lineTowerList,xsZcCycleId);
    }


    @Modifying
    @Transactional
    public void insertCycleTower(String sql,Long xsZcCycleId) {
        List<Map<String, Object>> lineTowerList = this.execSql(sql);
        insertCycleTowersMethod(lineTowerList,xsZcCycleId);
    }

    private void insertCycleTowersMethod(List<Map<String, Object>> lineTowerList, Long xsZcCycleId) {

        for (Map<String, Object> lineTower : lineTowerList) {
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
            Map<String, Object> jsonObject = userInfoFromRedis(userId);
            String deptid = jsonObject.get("DEPTID") == null? null:jsonObject.get("DEPTID").toString();
            String classid = jsonObject.get("CLASSID") == null? null:jsonObject.get("CLASSID").toString();
            String groupid = jsonObject.get("GROUPID") == null? null:jsonObject.get("GROUPID").toString();
            String companyid = jsonObject.get("COMPANYID") == null? null:jsonObject.get("COMPANYID").toString();

            //拿到周期相关的信息
            Long xsZcCycleId = xszctask.getXsZcCycleId();
            String sql = "select task_name,section,plan_xs_num,cycle,total_task_num from xs_zc_cycle where id = ?1";
            List<Map<String, Object>> xsZcCycles = this.execSql(sql, xsZcCycleId);
            if (xsZcCycles.isEmpty()) {
                return WebApiResponse.erro("没有这个周期");
            } else {
                Map<String, Object> xsZcCycle = xsZcCycles.get(0);
                String taskName = xsZcCycle.get("TASK_NAME").toString();
                Integer planXsNum = Integer.parseInt(xsZcCycle.get("PLAN_XS_NUM").toString());

                xszctask.setId();
                xszctask.setTaskName(taskName);
                xszctask.setPlanXsNum(planXsNum);
                xszctask.setTaskNumInCycle(1);
                xszctask.setRealXsNum(0);
                xszctask.setTaskNumInCycle(0);
                xszctask.setStauts(0);
                xszctask.setPdTime(DateUtil.dateNow());
                xszctask.setTdOrg(deptid);
                xszctask.setWxOrg(companyid);
                xszctask.setGroupId(groupid);
                xszctask.setClassId(classid);

                xszctaskService.add(xszctask);
                this.reposiotry.updateTotalTaskNum(xsZcCycleId,classid, companyid, userId,deptid);
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
        sqlBuffer.append("SELECT id,is_kt,cm_user_id,plan_xs_num xspl,plan_start_time,plan_end_time,v_level \"vLevel\",task_name \"taskName\",section,cycle,td_org \"tdywOrg\",in_use \"inUse\",total_task_num \"totalTaskNum\",create_time \"createTime\" FROM xs_zc_cycle where 1 = 1 and is_delete = 0");
        sqlBuffer.append(authoritySql);//我是权限
        //开始日期  结束日期
        Date startDate = xsTaskSch.getStartDate();
        Date endDate = xsTaskSch.getEndDate();
        if (startDate != null && endDate != null) {
            int i = arrList.size() + 1;
            sqlBuffer.append(" and create_time between ?" + i+
                    " and ?"+ (i+1));
            arrList.add(startDate);
            arrList.add(endDate);
        }

        //0 在用 1 停用
        Integer status = xsTaskSch.getStatus();
        if (!StringUtils.isEmpty(status )) {
            int i = arrList.size() + 1;
            sqlBuffer.append(" and in_use = ?" + i);
            arrList.add(status);

        }

        //区分周期维护 和 任务派发的请求
        Integer ispf = xsTaskSch.getIspf();
        if (ispf == 1) {
            sqlBuffer.append(" and total_task_num = 0");
        }

        //电压等级
        Integer v_type = xsTaskSch.getV_type();
        if (!StringUtils.isEmpty(v_type )) {
            int i = arrList.size() + 1;
            sqlBuffer.append(" and V_LEVEL = ?" + i);
            arrList.add(v_type);
        }

        //线路id
        Long lineId = xsTaskSch.getLineId();
        if (!StringUtils.isEmpty(lineId)) {
            int i = arrList.size() + 1;
            sqlBuffer.append(" and line_id = ?" + i);
            arrList.add(lineId);
        }

        //任务名
        String taskName = xsTaskSch.getTaskName();
        if (!StringUtils.isEmpty(taskName)) {
            int i = arrList.size() + 1;
            sqlBuffer.append(" and task_name like ?" + i +
                    "");
            arrList.add("%" + taskName + "%");
        }

        //通道单位
        String tdOrg = xsTaskSch.getTdOrg();
        if (!StringUtils.isEmpty(tdOrg)) {
            int i = arrList.size() + 1;
            sqlBuffer.append(" and (td_org = ?" + i +
                    " or wx_org = ?" + i +
                    " or group_id = ?" + i +
                    " or class_id = ?" + i +
                    " )");
            arrList.add(tdOrg);
        }

        //人员id
        String userId1 = xsTaskSch.getUserId();
        if (!StringUtils.isEmpty(userId1 )) {
            sqlBuffer.append("and cm_user_id = ? ");
            arrList.add(userId1);
        }

        sqlBuffer.append(" order by id desc");


        Page<Map<String, Object>> maps = this.execSqlPage(pageable, sqlBuffer.toString(), arrList.toArray());
        List<Map<String, Object>> content = maps.getContent();
        for(Map<String,Object> con:content) {
            Object cm_user_id = con.get("CM_USER_ID");
            if(cm_user_id != null) {
                Map<String, Object> map = userInfoFromRedis(cm_user_id.toString());
                Object realname = map.get("REALNAME");
                con.put("realName",realname);
            } else {
                con.put("realName",null);
            }
        }
        return maps;
    }


    /***
     * @Method userAuthority
     * @Description 权限模块
     * @param [userId]
     * @return void
     * @date 2018/1/3 10:12
     * @author nwz
     */
    public String userAuthority(String userId) throws Exception {
        String authoritySql = "";
        if (userId != null) {
            //从reids中拿userInfo
            Map<String, Object> jsonObject = userInfoFromRedis(userId);
            try {
                Integer roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
                Object tdId = jsonObject.get("DEPTID");
                Object classid = jsonObject.get("CLASSID");
                Object companyid = jsonObject.get("COMPANYID");
                switch (roletype) {
                    case 0:
                        break;
                    case 1:
                        authoritySql = " and td_org = '" + tdId.toString() + "'";
                        break;
                    case 2:
                        authoritySql = " and td_org = '" + tdId.toString() + "'";
                        break;
                    case 3:
                        authoritySql = " and wx_org = '" + companyid.toString() + "'";
                        break;
                    case 4:
                        authoritySql = " and class_id = '" + classid.toString() + "'";
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

        Map<String, Object> jsonObject = null;
        Object userInformation = hashOperations.get("UserInformation", userId);
        if (userInformation == null) {
            String sql = "select * from userinfo where id = ?";
            jsonObject = this.execSqlSingleResult(sql, userId);
            hashOperations.put("UserInformation", userId, jsonObject);
        } else {
            jsonObject = JSON.parseObject(userInformation.toString(), Map.class);
        }
        return jsonObject;
    }

    public Object judgeFromRedis() {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Object judge = stringRedisTemplate.opsForValue().get("judge");
        return judge;
    }


    /**
     * @param [ids]
     * @return java.lang.Object
     * @Method logicalDelete
     * @Description 逻辑删除
     * @date 2017/12/14 14:09
     * @author nwz
     */
    public void logicalDelete(Long[] ids) {
        List<Long> longs = Arrays.asList(ids);
        this.reposiotry.logicalDelete(longs);
    }

    /**
     * @param [pageable, xsTaskSch]
     * @param userId
     * @return java.lang.Object
     * @Method listPlan
     * @Description 任务列表
     * @date 2017/12/14 14:17
     * @author nwz
     */
    public Object listPlan(Pageable pageable, XsTaskSCh xsTaskSch, String currentUserId,String home) throws Exception {
        String authoritySql = userAuthority(currentUserId);//把权限的sql给我

        StringBuffer sqlBuffer = new StringBuffer();
        ArrayList arrList = new ArrayList();
        sqlBuffer.append("select t.*,tt.phone,tt.realname,tt.loginstatus,c.companyname,d.deptname from  (SELECT * FROM xs_zc_task where 1 = 1 and is_delete = 0 ");
        sqlBuffer.append(authoritySql);//拼上权限的sql
        //开始日期 结束日期
        Date startDate = xsTaskSch.getStartDate();
        Date endDate = xsTaskSch.getEndDate();
        if (startDate != null) {
            sqlBuffer.append(" and PLAN_END_TIME >= trunc(?) and  PLAN_START_TIME <= trunc(?+1)");
            arrList.add(startDate);
            arrList.add(startDate);
        } else {
            if (null != home && home.equals("1")){
                sqlBuffer.append(" and PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= trunc(sysdate)");
            }else {
                sqlBuffer.append(" and PLAN_END_TIME >= trunc(sysdate) and  PLAN_START_TIME <= trunc(sysdate+1)");
            }
        }


        //状态 0 未开始 1 巡视中 2 已完成
        Integer status = xsTaskSch.getStatus();
        if (!StringUtils.isEmpty(status )) {
            sqlBuffer.append("and stauts = ? ");
            arrList.add(status);

        }

        //人员id
        String userId = xsTaskSch.getUserId();
        if (!StringUtils.isEmpty(userId )) {
            sqlBuffer.append("and cm_user_id = ? ");
            arrList.add(userId);
        }

        //电压等级
        Integer v_type = xsTaskSch.getV_type();
        if (!StringUtils.isEmpty(v_type )) {
            String kv = "" ;
            switch (v_type) {
                case 0:
                    kv = "35-%";
                    break;
                case 1:
                    kv = "110-%";
                    break;
                case 2:
                    kv = "220-%";
                    break;
                case 3:
                    kv = "500-%";
                    break;
            };
            sqlBuffer.append("and task_name like ? ");
            arrList.add(kv);
        }

        //线路名称
        String lineName = xsTaskSch.getLineName();
        if (!StringUtils.isEmpty(lineName )) {
            sqlBuffer.append("and task_name like ? ");
            arrList.add("%" + lineName + "%");
        }

        //任务名
        String taskName = xsTaskSch.getTaskName();
        if (!StringUtils.isEmpty(taskName)) {
            sqlBuffer.append(" and task_name like ?");
            arrList.add("%" + taskName + "%");
        }

        //通道单位
        String tdOrg = xsTaskSch.getTdOrg();
        if (!StringUtils.isEmpty(tdOrg)) {
            sqlBuffer.append(" and (td_org = ? or wx_org = ? or group_id = ? or class_id = ? )");
            arrList.add(tdOrg);
            arrList.add(tdOrg);
            arrList.add(tdOrg);
            arrList.add(tdOrg);
        }
        //排个序
        sqlBuffer.append(" order by pd_time desc ) t left join rztsysuser tt on t.cm_user_id = tt.id left join rztsysdepartment d on d.id=tt.deptid left join rztsyscompany c on c.id= tt.companyid ");
        if (!StringUtils.isEmpty(xsTaskSch.getLoginType())){
            sqlBuffer.append(" where tt.loginstatus=?");
            arrList.add(Integer.parseInt(xsTaskSch.getLoginType()));
        }
        Page<Map<String, Object>> maps = this.execSqlPage(pageable, sqlBuffer.toString(), arrList.toArray());
        List<Map<String, Object>> content = maps.getContent();
        for(Map<String,Object> con:content) {
            Object cm_user_id = con.get("CM_USER_ID");
            if(cm_user_id != null) {
                Map<String, Object> map = userInfoFromRedis(cm_user_id.toString());
                Object realname = map.get("REALNAME");
            } else {
                con.put("realName",null);
            }
        }
        return maps;
    }

    /***
     * @Method getCycle
     * @Description 查看周期
     * @param [id]
     * @return java.lang.Objecttime
     * @date 2017/12/15 16:56
     * @author nwz
     */
//    @Cacheable(value = "xsZcCycles" , key = "#id")
    public Object getCycle(Long id) throws Exception {
        String sql = "select * from xs_zc_cycle where id = ?";
        Map<String, Object> cycle = this.execSqlSingleResult(sql, id);
        Object cm_user_id = cycle.get("CM_USER_ID");
        if(!StringUtils.isEmpty(cm_user_id)) {
            Map<String, Object> map = userInfoFromRedis(cm_user_id.toString());
            map.putAll(cycle);
            return map;
        }
        return cycle;
    }

    //    @CacheEvict(value = "xsZcCycles" , key = "#id")
    public void updateCycle(Long id, Integer cycle, Integer inUse, Integer planXsNum, String planStartTime, String planEndTime, Integer isKt, String cm_user_id) throws Exception {
        if (!StringUtils.isEmpty(cm_user_id)) {
            Map<String, Object> map = userInfoFromRedis(cm_user_id);
            Object deptid = map.get("DEPTID");
            Object companyid = map.get("COMPANYID");
            Object groupid = map.get("GROUPID");
            Object classid = map.get("CLASSID");

            this.reposiotry.updateCycle(id, cycle, inUse, planXsNum,planStartTime, planEndTime,isKt,cm_user_id,deptid,companyid,groupid,classid);
        } else {
            this.reposiotry.updateCycleTwo(id, cycle, inUse, planXsNum,planStartTime, planEndTime,isKt);
        }
    }

    public Object listPictureById(Long taskId, Integer zj) {
        String sql = "select PROCESS_NAME \"name\",FILE_SMALL_PATH \"smallFilePath\",FILE_PATH \"filePath\",CREATE_TIME \"createTime\",lon,lat from PICTURE_TOUR WHERE TASK_ID = ? order by id";
        if (zj == 1) {
            sql += " desc";
        }
        List<Map<String, Object>> maps = this.execSql(sql, taskId);
        return maps;
    }

    public Object listExecByTaskid(Long taskId) throws Exception {
        String sql = "select id,XS_CREATE_TIME,XS_END_TIME,XS_STATUS,XS_REPEAT_NUM from XS_ZC_TASK_EXEC where XS_ZC_TASK_ID = ? order by ID";
        String vLevel = "select t.V_LEVEL from xs_zc_cycle t join XS_ZC_TASK tt on t.id = tt.XS_ZC_CYCLE_ID and tt.id = ?";
        Map<String, Object> map = this.execSqlSingleResult(vLevel,taskId);
        List<Map<String, Object>> maps = this.execSql(sql, taskId);
        Map<String,Object> wodemap = new HashMap<String,Object>();
        wodemap.put("gantas",maps);
        wodemap.put("vLevel",map.get("V_LEVEL"));
        return wodemap;
    }

    public Object listExecDetail(Long execId) {
        String sql = "select t.ID,t.IS_DW,t.OPERATE_NAME,t.END_TIME,t.REASON,t.REALLONGITUDE,t.REALLATITUDE,tt.LONGITUDE,tt.LATITUDE from (select * from XS_ZC_TASK_EXEC_DETAIL where XS_ZC_TASK_EXEC_ID = ? and end_tower_id = '0') t join CM_TOWER tt on t.START_TOWER_ID = tt.ID";
        List<Map<String, Object>> maps = this.execSql(sql, execId);
        return maps;
    }

    public void logicalDeletePlan(Long[] ids) {
        this.reposiotry.logicalDeletePlan(ids);
    }

    /***
     * @Method importCycle
     * @Description 导入周期
     * @param [execlPath]
     * @return java.lang.Object
     * @date 2018/1/9 15:38
     * @author nwz
     */
    @Transactional
    public void importCycle(String execlPath) {
        try {
            List<Integer> failIndex = new ArrayList<Integer>();
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            HSSFDataFormatter dataFormatter = new HSSFDataFormatter();
            XSSFWorkbook wb = null;
            try {
                //fail1
//                FileInputStream fileInputStream = new FileInputStream("C:\\Users\\nwz\\Desktop\\新建 Microsoft Excel 工作表.xlsx");
                //fail
//                                FileInputStream fileInputStream = new FileInputStream("F:\\default\\qq_cache\\907695276\\FileRecv\\石景山巡视周期导入模板.xlsx");
                //fail2
                FileInputStream fileInputStream = new FileInputStream("C:\\Users\\nwz\\Desktop\\二次录入\\"  + execlPath + ".xlsx");

                wb = new XSSFWorkbook(fileInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            XSSFSheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            XSSFRow row = null;
            Set<Object> keys = hashOperations.keys("fail" + execlPath);
//            for (Object key:keys) {
            for (int i = 1; i <= lastRowNum; i++) {
//                int i = Integer.parseInt(key.toString());
                try {
                    System.err.println(i + "行");
                    XsZcCycle xsZcCycle = new XsZcCycle();
                    xsZcCycle.setId();
                    row = sheet.getRow(i);
                    String deptName = row.getCell(0).getStringCellValue();

                    XSSFCell cell = row.getCell(1);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    String kv = cell.getStringCellValue().toLowerCase().replace("kv","");
                    Integer vType = null;
                    switch (kv) {
                        case "35":
                            vType = 0;
                            break;
                        case "110":
                            vType = 1;
                            break;
                        case "220":
                            vType = 2;
                            break;
                        case "500":
                            vType = 3;
                            break;

                    }
                    if (vType == null) {
                        hashOperations.put("failTwice" + execlPath, i + "", "电压");
                        continue;
                    }

                    xsZcCycle.setVLevel(vType);

                    String lineName = row.getCell(2).getStringCellValue();
//                String lineNamesql = "select * from cm_line where LINE_NAME = ?";
                    String lineNamesql = "select line_id id from cm_line_section where LINE_NAME = ?1 and td_org_name = ?2";
                    Map<String, Object> map;
                    try {
                        map = execSqlSingleResult(lineNamesql, lineName,deptName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        hashOperations.put("failTwice" + execlPath, i + "", "线路");
                        continue;
                    }
                    Long lineID = Long.parseLong(map.get("ID").toString());
                    xsZcCycle.setLineId(lineID);

                    String lineSection = row.getCell(3).getStringCellValue().replace("#","");
                    String[] split = new String[2];
                    if(lineSection.contains("-")){
                        String s = lineSection.replaceAll("-", ",");
                        split = s.split(",");
                    }else{
                        split = new String[]{lineSection,lineSection};
                    }
                    if (split.length != 2) {
                        hashOperations.put("failTwice" + execlPath, i + "", "区段");
                        continue;
                    } else {
                        String sectionSql = "select tower_id,tower_name from cm_line_tower where line_id = ? and tower_name = ?";
                        try {
                            Map<String, Object> map1 = this.execSqlSingleResult(sectionSql, lineID, split[0]);
                            Map<String, Object> map2 = this.execSqlSingleResult(sectionSql, lineID, split[1]);
                            Object tower_id1 = map1.get("TOWER_ID");
                            if (tower_id1 == null) {
                                hashOperations.put("failTwice" + execlPath, i + "", "首杆没有");
                                continue;
                            }
                            Long start = Long.parseLong(tower_id1.toString());
                            Object tower_id2 = map2.get("TOWER_ID");
                            if (tower_id2 == null) {
                                hashOperations.put("failTwice" + execlPath, i + "", "末杆没有");
                                continue;
                            }
                            Long end = Long.parseLong(tower_id2.toString());
                            xsZcCycle.setXsStartSort(start);
                            xsZcCycle.setXsEndSort(end);
                        } catch (Exception e) {
                            System.err.println(lineID + " - " + split[0] + " -- " + split[1]);
                            hashOperations.put("failTwice" + execlPath, i + "", "区段2");
                            e.printStackTrace();
                            continue;
                        }
                    }

                    xsZcCycle.setSection(lineSection);
                    xsZcCycle.setTaskName(kv + "-" + lineName + "-" + lineSection);
                    Integer planXsNum = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(4)));
                    xsZcCycle.setPlanXsNum(planXsNum);
                    Integer cycle = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(5)));
                    xsZcCycle.setCycle(cycle);

                    String planStarttime = row.getCell(6).getStringCellValue();
                    String planEndTime = row.getCell(7).getStringCellValue();
                    Date planStartDate = DateUtil.StringToDateForImport(planStarttime);
                    Date planEndDate = DateUtil.StringToDateForImport(planEndTime);
                    if (planStartDate.before(planEndDate)) {
                        xsZcCycle.setIsKt(1);
                    } else {
                        xsZcCycle.setIsKt(0);
                    }
                    xsZcCycle.setPlanStartTime(planStarttime);
                    xsZcCycle.setPlanEndTime(planEndTime);
                    xsZcCycle.setCreateTime(DateUtil.dateNow());

                    String account = row.getCell(8).getStringCellValue();

                    try {
                        String userSql = "select id,DEPTID,COMPANYID,GROUPID,CLASSNAME from rztsysuser where username = ?";
                        Map<String, Object> map1 = this.execSqlSingleResult(userSql, account);
                        if (map1.size() > 0) {
                            String id = map1.get("ID").toString();
                            String deptid = map1.get("DEPTID") == null ? null : map1.get("DEPTID").toString();
                            String companyid = map1.get("COMPANYID") == null ? null : map1.get("COMPANYID").toString();
                            String groupid = map1.get("GROUPID") == null ? null : map1.get("GROUPID").toString();
                            String classname = map1.get("CLASSNAME") == null ? null : map1.get("CLASSNAME").toString();
                            xsZcCycle.setCmUserId(id);
                            xsZcCycle.setTdywOrg(deptid);
                            xsZcCycle.setWxOrg(companyid);
                            xsZcCycle.setGroupId(groupid);
                            xsZcCycle.setClassId(classname);
                        } else {
                            hashOperations.put("failTwice" + execlPath, i + "", "account没有");
                            continue;
                        }
                    } catch (Exception e) {
                        hashOperations.put("failTwice" + execlPath, i + "", "account多条");
                        continue;
                    }


                    String sql = "select * from cm_line_tower where line_id = ?1 and tower_id between ?2 and ?3";
                    List<Map<String, Object>> lineTowerList = this.execSql(sql, xsZcCycle.getLineId(), xsZcCycle.getXsStartSort(), xsZcCycle.getXsEndSort());
                    if (lineTowerList.size() == 0) {
                        hashOperations.put("failTwice" + execlPath, i + "", "没有杆塔呢");
                        continue;
                    }
                    for (Map<String, Object> lineTower : lineTowerList) {
                        long towerId = Long.parseLong(lineTower.get("TOWER_ID").toString());
                        XsZcCycleLineTower xsZcCycleLineTower = new XsZcCycleLineTower();
                        xsZcCycleLineTower.setId();
                        xsZcCycleLineTower.setCmLineTowerId(towerId);
                        xsZcCycleLineTower.setXsZcCycleId(xsZcCycle.getId());
                        xsZcCycleLineTowerService.add(xsZcCycleLineTower);
                    }
                    this.add(xsZcCycle);

                } catch (Exception e) {
                    hashOperations.put("failTwice" + execlPath, i + "", "大范围");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Transactional
    public void importCycleErr(String execlPath) {
        try {
            List<Integer> failIndex = new ArrayList<Integer>();
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            HSSFDataFormatter dataFormatter = new HSSFDataFormatter();
            XSSFWorkbook wb = null;
            try {
                //fail1
//                FileInputStream fileInputStream = new FileInputStream("C:\\Users\\nwz\\Desktop\\新建 Microsoft Excel 工作表.xlsx");
                //fail
//                                FileInputStream fileInputStream = new FileInputStream("F:\\default\\qq_cache\\907695276\\FileRecv\\石景山巡视周期导入模板.xlsx");
                //fail2
                FileInputStream fileInputStream = new FileInputStream("C:\\Users\\nwz\\Desktop\\转拼音.xlsx");

                wb = new XSSFWorkbook(fileInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            XSSFSheet sheet = wb.getSheetAt(1);
            int lastRowNum = sheet.getLastRowNum();
            XSSFRow row = null;
            for (int i = 1; i <= lastRowNum; i++) {
                try {
                    System.err.println(i + "行");
                    XsZcCycle xsZcCycle = new XsZcCycle();
                    xsZcCycle.setId();
                    row = sheet.getRow(i);
                    String deptName = "%"+row.getCell(0).getStringCellValue().substring(0,2)+"%";

                    XSSFCell cell = row.getCell(1);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    String kv = cell.getStringCellValue().toLowerCase().replace("kv","");
                    Integer vType = null;
                    switch (kv) {
                        case "35":
                            vType = 0;
                            break;
                        case "110":
                            vType = 1;
                            break;
                        case "220":
                            vType = 2;
                            break;
                        case "500":
                            vType = 3;
                            break;

                    }
                    if (vType == null) {
                        hashOperations.put("failTwice" + execlPath, i + "", "电压");
                        continue;
                    }

                    xsZcCycle.setVLevel(vType);

                    String lineName = row.getCell(2).getStringCellValue().replace("线","");
//                String lineNamesql = "select * from cm_line where LINE_NAME = ?";
                    String lineNamesql = "select line_id id from cm_line_section where LINE_NAME1 = ?1 and td_org_name like ?2";
                    Map<String, Object> map;
                    try {
                        map = execSqlSingleResult(lineNamesql, lineName, deptName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        hashOperations.put("failTwice" + execlPath, i + "", "线路");
                        continue;
                    }
                    Long lineID = Long.parseLong(map.get("ID").toString());
                    xsZcCycle.setLineId(lineID);

                    String lineSection = row.getCell(3).getStringCellValue().replace("#","");
                    String[] split = new String[2];
                    if(lineSection.contains("-")){
                        lineSection.replace("-",",");
                        split = lineSection.split(",");
                    }else{
                        split = new String[]{lineSection,lineSection};
                    }
                    if (split.length != 2) {
                        hashOperations.put("failTwice" + execlPath, i + "", "区段");
                        continue;
                    } else {
                        String sectionSql = "select tower_id,tower_name from cm_line_tower where line_id = ? and tower_name = ?";
                        try {
                            Map<String, Object> map1 = this.execSqlSingleResult(sectionSql, lineID, split[0]);
                            Map<String, Object> map2 = this.execSqlSingleResult(sectionSql, lineID, split[1]);
                            Object tower_id1 = map1.get("TOWER_ID");
                            if (tower_id1 == null) {
                                hashOperations.put("failTwice" + execlPath, i + "", "首杆没有");
                                continue;
                            }
                            Long start = Long.parseLong(tower_id1.toString());
                            Object tower_id2 = map2.get("TOWER_ID");
                            if (tower_id2 == null) {
                                hashOperations.put("failTwice" + execlPath, i + "", "末杆没有");
                                continue;
                            }
                            Long end = Long.parseLong(tower_id2.toString());
                            xsZcCycle.setXsStartSort(start);
                            xsZcCycle.setXsEndSort(end);
                        } catch (Exception e) {
                            System.err.println(lineID + " - " + split[0] + " -- " + split[1]);
                            hashOperations.put("failTwice" + execlPath, i + "", "区段2");
                            e.printStackTrace();
                            continue;
                        }
                    }

                    xsZcCycle.setSection(lineSection);
                    xsZcCycle.setTaskName(kv + "-" + lineName + "-" + lineSection);
                    Integer planXsNum = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(4)));
                    xsZcCycle.setPlanXsNum(planXsNum);
                    Integer cycle = Integer.parseInt(dataFormatter.formatCellValue(row.getCell(5)));
                    xsZcCycle.setCycle(cycle);

                    String planStarttime = row.getCell(6).getStringCellValue();
                    String planEndTime = row.getCell(7).getStringCellValue();
                    Date planStartDate = DateUtil.StringToDateForImport(planStarttime);
                    Date planEndDate = DateUtil.StringToDateForImport(planEndTime);
                    if (planStartDate.before(planEndDate)) {
                        xsZcCycle.setIsKt(1);
                    } else {
                        xsZcCycle.setIsKt(0);
                    }
                    xsZcCycle.setPlanStartTime(planStarttime);
                    xsZcCycle.setPlanEndTime(planEndTime);
                    xsZcCycle.setCreateTime(DateUtil.dateNow());

                    String account = row.getCell(8).getStringCellValue();

                    try {
                        String userSql = "select id,DEPTID,COMPANYID,GROUPID,CLASSNAME from rztsysuser where username = ?";
                        Map<String, Object> map1 = this.execSqlSingleResult(userSql, account);
                        if (map1.size() > 0) {
                            String id = map1.get("ID").toString();
                            String deptid = map1.get("DEPTID") == null ? null : map1.get("DEPTID").toString();
                            String companyid = map1.get("COMPANYID") == null ? null : map1.get("COMPANYID").toString();
                            String groupid = map1.get("GROUPID") == null ? null : map1.get("GROUPID").toString();
                            String classname = map1.get("CLASSNAME") == null ? null : map1.get("CLASSNAME").toString();
                            xsZcCycle.setCmUserId(id);
                            xsZcCycle.setTdywOrg(deptid);
                            xsZcCycle.setWxOrg(companyid);
                            xsZcCycle.setGroupId(groupid);
                            xsZcCycle.setClassId(classname);
                        } else {
                            hashOperations.put("failTwice" + execlPath, i + "", "account没有");
                            continue;
                        }
                    } catch (Exception e) {
                        hashOperations.put("failTwice" + execlPath, i + "", "account多条");
                        continue;
                    }


                    String sql = "select * from cm_line_tower where line_id = ?1 and tower_id between ?2 and ?3";
                    List<Map<String, Object>> lineTowerList = this.execSql(sql, xsZcCycle.getLineId(), xsZcCycle.getXsStartSort(), xsZcCycle.getXsEndSort());
                    if (lineTowerList.size() == 0) {
                        hashOperations.put("failTwice" + execlPath, i + "", "没有杆塔呢");
                        continue;
                    }
                    for (Map<String, Object> lineTower : lineTowerList) {
                        long towerId = Long.parseLong(lineTower.get("TOWER_ID").toString());
                        XsZcCycleLineTower xsZcCycleLineTower = new XsZcCycleLineTower();
                        xsZcCycleLineTower.setId();
                        xsZcCycleLineTower.setCmLineTowerId(towerId);
                        xsZcCycleLineTower.setXsZcCycleId(xsZcCycle.getId());
                        xsZcCycleLineTowerService.add(xsZcCycleLineTower);
                    }
                    this.add(xsZcCycle);
                    hashOperations.delete("fail"+execlPath,i + "");
                } catch (Exception e) {
                    hashOperations.put("failTwice" + execlPath, i + "", "大范围");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        String execlPath = "C:/Users/nwz/Documents/巡视周期导入模板.xlsx";
        try {
//            importCycle(execlPath);
            Date a = DateUtil.StringToDateForImport("9:00");
            Date b = DateUtil.StringToDateForImport("18:00");
            Date c = DateUtil.StringToDateForImport2("1月21日");

            if (a.before(b)) {
                System.out.println(1);
            } else {
                System.out.println(2);
            }
            String ac = "adj-kl";
            String[] split = ac.split("-");
            System.out.println(split.length);
            System.out.println("123456".substring(0,2));
            System.out.println(c.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void bornTask() {
        String sql = "SELECT * from XS_ZC_CYCLE where IN_USE = 0 and IS_DELETE = 0 and TOTAL_TASK_NUM = 0 and is_kt is not null";
        List<Map<String, Object>> maps = this.execSql(sql);
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        Long id = 1l;
        for (Map<String, Object> map : maps) {
            try {
                id = Long.parseLong(map.get("ID").toString());
                XSZCTASK xsZcTask = new XSZCTASK();
                xsZcTask.setId();
                xsZcTask.setTdOrg(map.get("TD_ORG") == null ? null : map.get("TD_ORG").toString());
                xsZcTask.setWxOrg(map.get("WX_ORG") == null ? null : map.get("WX_ORG").toString());
                xsZcTask.setGroupId(map.get("GROUP_ID") == null ? null : map.get("GROUP_ID").toString());
                xsZcTask.setClassId(map.get("CLASS_ID") == null ? null : map.get("CLASS_ID").toString());
                xsZcTask.setXsZcCycleId(id);
                xsZcTask.setStauts(0);
                xsZcTask.setPlanXsNum(Integer.parseInt(map.get("PLAN_XS_NUM").toString()));
                String plan_start_time = map.get("PLAN_START_TIME").toString();
                String plan_end_time = map.get("PLAN_END_TIME").toString();
                xsZcTask.setPlanStartTime(DateUtil.stringToDate1(DateUtil.stringNow().split(" ")[0] + " " + plan_start_time));
                Integer isKt = map.get("IS_KT") == null ? null : Integer.parseInt(map.get("IS_KT").toString());
                if (isKt == 0) {
                    xsZcTask.setPlanEndTime(DateUtil.stringToDate1(DateUtil.dayStringByIndex(1).split(" ")[0] + " " + plan_end_time));
                } else {
                    xsZcTask.setPlanEndTime(DateUtil.stringToDate1(DateUtil.stringNow().split(" ")[0] + " " + plan_end_time));

                }
                xsZcTask.setWxOrg(map.get("WX_ORG") == null ? null : map.get("WX_ORG").toString());
                xsZcTask.setPdTime(DateUtil.dateNow());
                xsZcTask.setTaskNumInCycle(0);
                xsZcTask.setTaskName(map.get("TASK_NAME").toString());
                xsZcTask.setCmUserId(map.get("CM_USER_ID").toString());
                try {
                    xszctaskService.add(xsZcTask);
                } catch (Exception e) {
                    hashOperations.put("faiiTask", Long.toString(id), "失败");
                    e.printStackTrace();
                    continue;
                }
                try {
                    this.reposiotry.updateCycleTotalBornNum(id);
                } catch (Exception e) {
                    hashOperations.put("faiiTask", Long.toString(id), "失败");
                    e.printStackTrace();
                    continue;
                }
            } catch (Exception e) {
                hashOperations.put("faiiTask", Long.toString(id), "失败");
                e.printStackTrace();
                continue;
            }
        }
    }

    @Transactional
    public void gaipinin() {
        String sql = "select t.*,tt.line_name from XS_ZC_CYCLE t join cm_line tt on (t.TD_ORG = '402881e6603a69b801603a71e1c10010' or TD_ORG = '402881e6603a69b801603a72525c0013') and tt.id = t.line_id";
        List<Map<String, Object>> maps = this.execSql(sql);
        for (Map<String, Object> map : maps) {
            long id = Long.parseLong(map.get("ID").toString());
            Integer v_level = Integer.parseInt(map.get("V_LEVEL").toString());
            String section = map.get("SECTION").toString();
            String line_name = map.get("LINE_NAME").toString();
            String v = "";
            switch (v_level) {
                case 0:
                    v = "35";
                    break;
                case 1:
                    v = "110";
                    break;
                case 2:
                    v = "220";
                    break;
                case 3:
                    v = "500";
                    break;
            }
            String taskName = v + "-" + line_name + "-" + section;
            this.reposiotry.updatetaskname(id, taskName);
        }


    }

    @Transactional
    public void gaipinin2() {
        String sql = "select tt.id, t.task_name from XS_ZC_CYCLE t join xs_zc_task tt on (t.TD_ORG = '402881e6603a69b801603a71e1c10010' or t.TD_ORG = '402881e6603a69b801603a72525c0013') and tt.xs_zc_cycle_id = t.id";
        List<Map<String, Object>> maps = this.execSql(sql);
        for (Map<String, Object> map : maps) {
            long id = Long.parseLong(map.get("ID").toString());
            String taskName = map.get("TASK_NAME").toString();
            this.reposiotry.updatetaskname2(id, taskName);
        }

    }


    public void zhengwanshuijiao() {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        Set<String> failTwice = redisTemplate.keys("failTwice*");
        for (String key:failTwice) {
            Set<Object> keys1 = hashOperations.keys(key);
            for (Object key2:keys1) {
                Object o = hashOperations.get(key, key2);
                int lineNum = Integer.parseInt(key2.toString()) + 1;
                System.err.println(key + "  " + lineNum + "  " + o.toString() );
            }

            System.err.println("------------------------------分界线------------------------------");
        }
    }



    public Object listPlanForMap(Pageable pageable,XsTaskSCh xsTaskSch, String currentUserId,String home) {

        StringBuffer sqlBuffer = new StringBuffer();
        ArrayList arrList = new ArrayList();
        sqlBuffer.append("SELECT * FROM xs_zc_task where is_delete = 0");
        //开始日期 结束日期
        Date startDate = xsTaskSch.getStartDate();
        Date endDate = xsTaskSch.getEndDate();
        if (startDate != null) {
            sqlBuffer.append(" and PLAN_END_TIME >= trunc(?) and  PLAN_START_TIME <= trunc(?+1)");
            arrList.add(startDate);
            arrList.add(startDate);
        } else {
            sqlBuffer.append(" and PLAN_END_TIME >= trunc(sysdate) and  PLAN_START_TIME <= trunc(sysdate+1)");
        }
        if (home!=null && home.equals("1")){
            sqlBuffer = new StringBuffer();
            sqlBuffer.append("SELECT * FROM xs_zc_task where is_delete = 0 AND trunc(PLAN_START_TIME) = trunc(sysdate)");
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

        //通道单位
        String tdOrg = xsTaskSch.getTdOrg();
        if (!StringUtils.isEmpty(tdOrg)) {
            sqlBuffer.append(" and (td_org = ? or wx_org = ? or group_id = ? or class_id = ? )");
            arrList.add(tdOrg);
            arrList.add(tdOrg);
            arrList.add(tdOrg);
            arrList.add(tdOrg);
        }

        //

        Page<Map<String, Object>> maps = this.execSqlPage(pageable, sqlBuffer.toString(), arrList.toArray());
        return maps;
    }


}

