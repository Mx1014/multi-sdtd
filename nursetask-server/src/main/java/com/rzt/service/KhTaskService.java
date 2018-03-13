/**
 * 文件名：KhTaskService
 * 版本信息：
 * 日期：2017/11/28 14:43:44
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.KhSite;
import com.rzt.entity.model.KhTaskModel;
import com.rzt.repository.KhSiteRepository;
import com.rzt.repository.KhTaskRepository;
import com.rzt.entity.KhTask;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import io.swagger.models.auth.In;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rzt.service.CurdService;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class KhTaskService extends CurdService<KhTask, KhTaskRepository> {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private KhSiteRepository siteRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Object listAllKhTask(KhTaskModel task, String status, Pageable pageable, JSONObject json, String yworg, String currentUserId, String home, String tdOrg) {
        task = timeUtil(task);
        Map jsonObject = JSON.parseObject(json.toString(), Map.class);
        Integer roleType = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object tdId = jsonObject.get("DEPTID");
        Object classid = jsonObject.get("CLASSID");
        Object companyid = jsonObject.get("COMPANYID");
        String result = "k.wx_org company,k.id as id, k.task_name as taskName,k.tdyw_org as yworg,k.CREATE_TIME as createTime,k.plan_start_time as startTime,k.plan_end_time as endTime,k.status as status,u.realname as userName,d.DEPTNAME as class,k.task_type as type,u.phone,u.loginstatus " +
                " from kh_task k  left join rztsysuser u on u.id = k.user_id left join RZTSYSDEPARTMENT d on u.classname = d.id  ";
        List params = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        if (home != null && home.equals("1")) {
            buffer = new StringBuffer();
            buffer.append(" where  PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)");
            params = new ArrayList<>();
        } else {
            buffer.append(" where k.plan_start_time <= trunc(to_date('" + task.getPlanEndTime() + "','YYYY-MM-DD hh24:mi')+1) and plan_end_time>= trunc(to_date('" + task.getPlanStartTime() + "','YYYY-MM-DD hh24:mi')) ");
//            params.add(task.getPlanEndTime());
//            params.add(task.getPlanStartTime());
        }
        //查询框
        if (task.getTaskName() != null && !task.getTaskName().equals("")) {
            task.setTaskName("%" + task.getTaskName() + "%");
            buffer.append(" and k.task_name like ? ");
            params.add(task.getTaskName());
        }
        if (status != null && !status.equals("")) {
            buffer.append(" and k.status = ? ");
            params.add(Integer.parseInt(status));
        }
        if (task.getUserName() != null && !task.getUserName().equals("")) {
            task.setUserName("%" + task.getUserName() + "%");
            buffer.append(" and u.realname like ? ");
            params.add(task.getUserName());
        }
        if (task.getTaskType() != null && !task.getTaskType().equals("")) {
            buffer.append(" and k.task_type =? ");
            params.add(Integer.parseInt(task.getTaskType()));
        }
      /*  if (yworg != null && !yworg.equals("")) {
            buffer.append(" and k.tdyw_org like ? ");
            params.add("%" + yworg + "%");
        }*/
        if (task.getLoginType() != null && !task.getLoginType().equals("")) {
            buffer.append(" and u.LOGINSTATUS = ? ");
            params.add(Integer.parseInt(task.getLoginType()));
        }
        String sql = "";

        if (roleType == 1 || roleType == 2) {
            if (!StringUtils.isEmpty(tdOrg)) {
                buffer.append(" and ( DEPTID=? or COMPANYID=? or CLASSNAME=? or GROUPID=? )");
                params.add(tdOrg);
                params.add(tdOrg);
                params.add(tdOrg);
                params.add(tdOrg);
            } else {
                buffer.append("  and k.YWORG_ID = '" + tdId + "'");
            }
            sql = "select " + result + buffer.toString();
        } else if (roleType == 3) {
            sql = "select " + result + buffer.toString() + " and k.wx_org = (select d.COMPANYNAME from rztsysuser u,RZTSYSCOMPANY d where u.companyid = d.id and u.id = ?)";
            params.add(currentUserId);
        } else if (roleType == 4) {
            sql = "select " + result + buffer.toString() + " and u.classname = (select u.classname from rztsysuser u, RZTSYSDEPARTMENT d where d.id = u.deptid and u.id = ?)";
            params.add(currentUserId);
        } else if (roleType == 5) {
            sql = "select " + result + buffer.toString() + " and k.user_id = ?";
            params.add(currentUserId);
        } else {
            if (yworg != null && !yworg.equals("")) {
                buffer.append(" and k.tdyw_org like ? ");
                params.add("%" + yworg + "%");
            } else if (task.getTdOrg() != null && !task.getTdOrg().equals("")) {
                buffer.append(" and k.yworg_id = ?");
                params.add((task.getTdOrg()));
            }
            sql = "select " + result + buffer.toString();
        }
        sql += " order by k.id desc";
        //String sql = "select * from listAllKhTask "+buffer.toString();
        Page<Map<String, Object>> maps = this.execSqlPage(pageable, sql, params.toArray());
        List<Map<String, Object>> content1 = maps.getContent();
        for (Map map : content1) {
            map.put("ID", map.get("ID") + "");
        }
        return maps;
    }

    public KhTaskModel timeUtil(KhTaskModel task) {
        if (task.getPlanStartTime() == null || task.getPlanStartTime().equals("")) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            //c.add(Calendar.DAY_OF_MONTH, -6);
            Date m = c.getTime();
            String mon = df.format(m);
            task.setPlanStartTime(mon + " 00:00");
            task.setPlanEndTime(df.format(new Date()) + " 23:59");
        }
        return task;
    }

    public List<Map<String, Object>> getKhTaskById(long id) {
        String result = " k.task_name as taskName,y.yhms as yhms,y.yhjb1 as yhjb,u.realname as userName,u.phone as phone ";
        String sql = "select " + result + " from kh_task k left join kh_yh_history y on k.yh_id=y.id left join rztsysuser u on u.id=k.user_id  where k.id=?";
        return this.execSql(sql, id);
    }

    public int getCount(long id, String userId) {
        return this.reposiotry.getCount(id, userId);
    }

    //可能需要修改外协单位
    public void updateTaskById(String userId, String id) {
        if (!StringUtils.isEmpty(userId)) {
            Map<String, Object> map = new HashMap<>();
            try {
                String sql = "select c.id as id,c.companyname as name from rztsyscompany c left join rztsysuser u on u.companyid = c.id where u.id=?";
                this.execSqlSingleResult(sql, userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.reposiotry.updateSiteById(userId, id);
            this.reposiotry.updateTaskBySiteId(userId, id);
        }
    }


    public WebApiResponse listCurrentTaskByUserId(String userId, String startDate) {
        try {
            String sql = "";
            if (startDate == null) {
                sql = "select k.id as id,k.status as status,k.task_name as task_name,k.plan_end_time as endTime from kh_task k where k.user_id = ? and k.PLAN_END_TIME >= trunc(sysdate) and user_id is not null and k.PLAN_START_TIME <= trunc(sysdate+1) ";
                return WebApiResponse.success(this.execSql(sql, userId));
            } else {
                sql = "select k.id as id,k.status as status,k.task_name as task_name,k.plan_end_time as endTime from kh_task k where k.user_id = ? and k.PLAN_END_TIME >=trunc(to_date(?,'yyyy-mm-dd hh24:mi:ss')) and k.plan_start_time<=trunc(to_date(?,'yyyy-mm-dd hh24:mi:ss')+1)";
                return WebApiResponse.success(this.execSql(sql, userId, startDate, startDate));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse listTaskInfoById(String taskId) {
        try {
            String sql = "SELECT REAL_START_TIME,REAL_END_TIME,DDXC_TIME,SFQR_TIME,WPQR_TIME,IS_DW AS ISDW,REASON,TASK_NAME TASKNAME,DDXC_TIME AS DDXCTIME,CREATE_TIME PDTIME,TDYW_ORG YWORG,WX_ORG WXORG,PLAN_START_TIME STARTTIME,PLAN_END_TIME ENDTIME,STATUS,TASK_TYPE TYPE from KH_TASK WHERE ID=?";
            return WebApiResponse.success(this.execSql(sql, taskId));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    //电压等级  任务执行人 线路名称  杆塔号 开始时间  通道单位 外协单位  区段
    public WebApiResponse listTaskInfoByYhId(String yhId) {
        try {
            String sql = "SELECT  s.task_name taskname,l.v_level as voltage,l.line_name as linename,s.section as section,s.tdyw_org as yworg,\n" +
                    "y.TDWX_ORG as wxorg,U.REALNAME as name,T.PLAN_START_TIME as starttime,T.PLAN_END_TIME as endtime\n" +
                    "FROM KH_SITE S LEFT JOIN KH_YH_HISTORY y on s.yh_id = y.id LEFT JOIN KH_TASK T on t.SITE_ID=s.id\n" +
                    "LEFT JOIN RZTSYSUSER U ON u.id=s.USER_ID LEFT JOIN cm_line l on l.id = s.LINE_ID\n" +
                    "WHERE s.YH_ID = ? and t.PLAN_END_TIME>=sysdate  order by t.plan_start_time";
            List<Map<String, Object>> list = this.execSql(sql, Long.parseLong(yhId));
            for (Map map : list) {
                String kv = map.get("VOLTAGE").toString();
                if (kv.contains("kV")) {
                    map.put("VOLTAGE", kv.substring(0, kv.indexOf("k")));
                }
            }
            return WebApiResponse.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败" + e.getMessage());
        }
    }


    public List<Map<String, Object>> findAlls(JSONObject josn, String userId) {
        Map jsonObject = JSON.parseObject(josn.toString(), Map.class);
        Integer roleType = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object tdId = jsonObject.get("DEPTID");
        Object classid = jsonObject.get("CLASSID");
        Object companyid = jsonObject.get("COMPANYID");
        String sql = "select t.* from kh_task t ";
        if (roleType == 1 || roleType == 2) {
            sql += " left join kh_site s on s.id = t.site_id where s.tdyw_orgId = '" + tdId.toString() + "'";
        } else if (roleType == 3) {
            sql += " left join kh_site s on s.id = t.site_id where s.wx_orgId= '" + companyid.toString() + "'";
        } else if (roleType == 4) {
            sql += ",rztsysuser u where u.id= t.user_id and u.classname = '" + classid.toString() + "'";//(select classname FROM RZTSYSUSER where id ='" + userId + "')";
        } else if (roleType == 5) {
            sql += " where t.user_id= '" + userId + "'";
        }
        sql += " order by t.create_time desc";
        List<Map<String, Object>> maps = this.execSql(sql);
        return maps;
    }

    //导出文件
    public void exportNursePlan(List<Map<String, Object>> taskList, HttpServletRequest request, HttpServletResponse response) {
        try {
            String rootpath = request.getSession().getServletContext().getRealPath(File.separator);
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("看护任务");

            // 设置列宽
            sheet.setColumnWidth((short) 0, (short) 6000);
            sheet.setColumnWidth((short) 1, (short) 6000);
            sheet.setColumnWidth((short) 2, (short) 6000);
            sheet.setColumnWidth((short) 3, (short) 6000);
            sheet.setColumnWidth((short) 4, (short) 6000);
            sheet.setColumnWidth((short) 5, (short) 6000);// 空列设置小一些
            sheet.setColumnWidth((short) 6, (short) 6000);// 设置列宽
            sheet.setColumnWidth((short) 7, (short) 6000);
            sheet.setColumnWidth((short) 8, (short) 6000);
            sheet.setColumnWidth((short) 9, (short) 6000);
            sheet.setColumnWidth((short) 10, (short) 6000);
            sheet.setColumnWidth((short) 11, (short) 6000);
            sheet.setColumnWidth((short) 12, (short) 6000);
            sheet.setColumnWidth((short) 13, (short) 9000);
            XSSFCellStyle cellstyle = wb.createCellStyle();// 设置表头样式
            cellstyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 设置居中

            XSSFCellStyle headerStyle = wb.createCellStyle();// 创建标题样式
            headerStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);    //设置垂直居中
            headerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);   //设置水平居中
            XSSFFont headerFont = wb.createFont(); //创建字体样式
            headerFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 字体加粗
            headerFont.setFontName("Times New Roman");  //设置字体类型
            headerFont.setFontHeightInPoints((short) 12);    //设置字体大小
            headerStyle.setFont(headerFont);    //为标题样式设置字体样式
            XSSFRow row = sheet.createRow(0);
            XSSFCell cell = row.createCell((short) 0);
            cell.setCellValue("任务名称");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 1);
            cell.setCellValue("看护人");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 2);
            cell.setCellValue("所属队伍");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 3);
            cell.setCellValue("派发时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 4);
            cell.setCellValue("计划开始时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 5);
            cell.setCellValue("计划结束时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 6);
            cell.setCellValue("通道运维单位");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 7);
            cell.setCellValue("外协单位");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 8);
            cell.setCellValue("任务状态");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 9);
            cell.setCellValue("实际开始时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 10);
            cell.setCellValue("身份确认时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 11);
            cell.setCellValue("物品确认时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 12);
            cell.setCellValue("到达现场时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 13);
            cell.setCellValue("实际结束时间/任务取消时间");
            cell.setCellStyle(headerStyle);
            //Sheet sheet = wb.getSheetAt(0);
            for (int i = 0; i < taskList.size(); i++) {
                row = sheet.createRow(i + 1);
                //Row row = sheet.getRow(i+1);
                Map<String, Object> task = taskList.get(i);
                if (task.get("TASK_NAME") != null) {
                    row.createCell(0).setCellValue(task.get("TASK_NAME").toString());//任务名称
                }
                if (task.get("USER_ID") != null) {
                    try {
                        String sql = "select u.realname REALNAME,d.deptname DEPTNAME from rztsysuser u left join rztsysdepartment d on d.id = u.classname where u.id=?";
                        List<Map<String, Object>> list = this.execSql(sql, task.get("USER_ID").toString());
                        if (!list.isEmpty()) {
                            row.createCell(1).setCellValue(list.get(0).get("REALNAME").toString());//通道单位
                            try {
                                String deptname = list.get(0).get("DEPTNAME").toString();
                                row.createCell(2).setCellValue(list.get(0).get("DEPTNAME").toString());
                            } catch (Exception e) {
                                row.createCell(2).setCellValue("");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (task.get("CREATE_TIME") != null) {
                    row.createCell(3).setCellValue(task.get("CREATE_TIME").toString().substring(0, task.get("CREATE_TIME").toString().length() - 2));//计划开始时间
                }
                if (task.get("PLAN_START_TIME") != null) {
                    row.createCell(4).setCellValue(task.get("PLAN_START_TIME").toString().substring(0, task.get("PLAN_START_TIME").toString().length() - 2));//计划结束时间
                }

                if (task.get("PLAN_END_TIME") != null) {
                    row.createCell(5).setCellValue(task.get("PLAN_END_TIME").toString().substring(0, task.get("PLAN_END_TIME").toString().length() - 2));//通道单位
                }
                if (task.get("TDYW_ORG") != null) {
                    row.createCell(6).setCellValue(task.get("TDYW_ORG").toString());//班组
                }
                if (task.get("WX_ORG") != null) {
                    row.createCell(7).setCellValue(task.get("WX_ORG").toString());//巡视人员
                }
                if (task.get("REAL_START_TIME") != null) {
                    row.createCell(9).setCellValue(task.get("REAL_START_TIME").toString().substring(0, task.get("REAL_START_TIME").toString().length() - 2));
                }
                if (task.get("SFQR_TIME") != null) {
                    row.createCell(10).setCellValue(task.get("SFQR_TIME").toString().substring(0, task.get("SFQR_TIME").toString().length() - 2));
                }
                if (task.get("WPQR_TIME") != null) {
                    row.createCell(11).setCellValue(task.get("WPQR_TIME").toString().substring(0, task.get("WPQR_TIME").toString().length() - 2));
                }
                if (task.get("DDXC_TIME") != null) {
                    row.createCell(12).setCellValue(task.get("DDXC_TIME").toString().substring(0, task.get("DDXC_TIME").toString().length() - 2));
                }
                if (task.get("REAL_END_TIME") != null) {
                    row.createCell(13).setCellValue(task.get("REAL_END_TIME").toString().substring(0, task.get("REAL_END_TIME").toString().length() - 2));
                }
                int status = Integer.parseInt(task.get("STATUS").toString());
                //该次执行状态(0待办,1进行中,2完成)

                if (status == 0) {
                    row.createCell(8).setCellValue("未开始");
                } else if (status == 1) {
                    row.createCell(8).setCellValue("进行中");
                } else if (status == 2) {
                    row.createCell(8).setCellValue("已完成");
                } else {
                    row.createCell(8).setCellValue("已取消");
                }

            }
            OutputStream output = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + new String("看护任务导出表.xlsx".getBytes("utf-8"), "iso8859-1"));
            response.setContentType("Content-Type:application/vnd.ms-excel ");
            wb.write(output);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WebApiResponse appListPicture(long taskId, Integer zj) {
        try {
            String sql = "select PROCESS_NAME \\\"name\\\",FILE_SMALL_PATH \\\"smallFilePath\\\",FILE_PATH \\\"filePath\\\",CREATE_TIME \\\"createTime\\\",LON,LAT from PICTURE_KH WHERE TASK_ID = ? and file_type=1 order by CREATE_TIME desc  ";
            return WebApiResponse.success(this.execSql(sql, taskId));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }


    //生成任务的逻辑
    public void createTask() {
        siteRepository.deleteSite();
        List<KhSite> list = siteRepository.findSites();
        for (KhSite site : list) {
            double cycle = site.getCycle();  //一轮任务时长
            Date startTime = site.getPlanStartTime();
            Date endTime = site.getPlanEndTime();
            if (cycle > 0) {
                //如果下次任务开始时间是今天  生成任务
                while (DateUtil.addDate(startTime, cycle).getTime() < DateUtil.getBiggest()) {
                    startTime = DateUtil.addDate(startTime, cycle);
                    endTime = DateUtil.addDate(endTime, cycle);
                    saveTask(site, startTime, endTime);
                }
                //如果周期小于24
            } /*else if (cycle <= 24 && cycle > 0) {
                while (DateUtil.addDate(startTime, cycle).getTime() < DateUtil.getBiggest()) {
                    startTime = DateUtil.addDate(startTime, cycle);
                    endTime = DateUtil.addDate(endTime, cycle);
                    saveTask(site, startTime, endTime);
                }*/
        }

    }


    public void saveTask(KhSite site, Date startTime, Date endTime) {
        int count = this.getCount(site.getId(), site.getUserid());
        this.reposiotry.updateSite(startTime, endTime, site.getId(), count);
        KhTask task = new KhTask();
        task.setId();
        try {
            task.setWxOrg(site.getWxOrg());
            task.setWxOrgId(site.getWxOrgId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        task.setYwOrgId(site.getTdywOrgId());
        task.setSiteId(site.getId());
        task.setTdywOrg(site.getTdywOrg());
        task.setCount(count);
        task.setUserId(site.getUserid());
        task.setPlanStartTime(startTime);
        task.setPlanEndTime(endTime);
        task.setTaskName(site.getTaskName());
        task.setCreateTime(DateUtil.dateNow());
        task.setYhId(site.getYhId());
        task.setStatus(0);
        this.reposiotry.addTask(task.getId(), task.getSiteId(), task.getUserId(), task.getTaskName(), task.getYhId(),
                task.getPlanStartTime(), task.getPlanEndTime(), task.getWxOrg(), task.getCount(), task.getTdywOrg(), 0, task.getYwOrgId(), task.getWxOrgId());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public WebApiResponse listPictureByYhId(String yhId) {
        try {
            String sql = "select file_path filepath from picture_yh where yh_id=?";
            return WebApiResponse.success(this.execSql(sql, Long.parseLong(yhId)));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public void deleteSiteById(long id) throws Exception {
        String sql = "SELECT *\n" +
                "FROM KH_TASK where PLAN_START_TIME<=sysdate and PLAN_END_TIME>=sysdate and status = 1 and SITE_ID=?";
        List<Map<String, Object>> maps = this.execSql(sql, id);
        if (maps.size() > 0) {
            throw new Exception();
        } else {
            this.reposiotry.deleteSiteById(id);
            this.reposiotry.deleteCycleByYhId(maps.get(0).get("YH_ID").toString());
        }
    }

    public void deleteTaskById(long id) {
        try {
            this.reposiotry.deleteTaskById(id);
            this.reposiotry.deleteYjById(id);
            this.reposiotry.deleteEjById(id);
            try {
                String s = "TWO+" + id + "+2+*";
                removeSomeKey(s);
                String s1 = "ONE+" + id + "+2+*";
                removeSomeKey(s1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WebApiResponse listTowerPoint(long id) {
        try {
            String sql = "select y.id as id,y.radius as ROUND,y.jd as jd,y.wd as wd from kh_yh_history y left join kh_task k on y.id = k.yh_id where k.id=?";
            Map<String, Object> map = this.execSqlSingleResult(sql, id);
            sql = "SELECT section,START_TOWER,END_TOWER,LINE_NAME,LINE_ID \n" +
                    "FROM KH_YH_HISTORY where id=?";
            Map<String, Object> yh = this.execSqlSingleResult(sql, Long.parseLong(map.get("ID").toString()));
            String[] split = yh.get("SECTION").toString().split("-");
            int start = Integer.parseInt(split[0]);
            int end = Integer.parseInt(split[1]);
            for (int i = 0; i <= end - start; i++) {
                sql = "SELECT ID,NAME,LONGITUDE lon,LATITUDE lat FROM CM_TOWER WHERE LINE_ID=? and NAME=?";
                Map<String, Object> tower = this.execSqlSingleResult(sql, Long.parseLong(yh.get("LINE_ID").toString()), start + i);
                tower.put("LINENAME", yh.get("LINE_NAME"));
                map.put("tower" + i, tower);
            }
            return WebApiResponse.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    public WebApiResponse listTowerPoint2(long id) {
        try {
            String sql = "select y.id,section,START_TOWER,END_TOWER,LINE_NAME,LINE_ID from kh_yh_history y left join kh_task k on y.id = k.yh_id where k.id=?";
            Map<String, Object> map = this.execSqlSingleResult(sql, id);
            sql = "SELECT LATITUDE lat,LONGITUDE lon,NAME\n" +
                    "FROM CM_TOWER WHERE id=?";
            Map<String, Object> start = this.execSqlSingleResult(sql, Long.parseLong(map.get("START_TOWER").toString()));
            Map<String, Object> end = this.execSqlSingleResult(sql, Long.parseLong(map.get("END_TOWER").toString()));
            map.put("STARTLON", start.get("LON").toString());
            map.put("STARTLAT", start.get("LAT").toString());
            map.put("ENDLON", end.get("LON").toString());
            map.put("ENDLAT", end.get("LAT").toString());
            return WebApiResponse.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    public void removeSomeKey(String s) {
        //String s = "TWO+" + id + "+2+*";
        RedisConnection connection = null;
        try {
            connection = redisTemplate.getConnectionFactory().getConnection();
            connection.select(1);
            Set<byte[]> keys = connection.keys(s.getBytes());
            byte[][] ts = keys.toArray(new byte[][]{});
            if (ts.length > 0) {
                connection.del(ts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
}

