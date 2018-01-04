/**
 * 文件名：KhTaskService
 * 版本信息：
 * 日期：2017/11/28 14:43:44
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.entity.KhSite;
import com.rzt.entity.model.KhTaskModel;
import com.rzt.repository.KhSiteRepository;
import com.rzt.repository.KhTaskRepository;
import com.rzt.entity.KhTask;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rzt.service.CurdService;

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

    public Object listAllKhTask(KhTaskModel task, Pageable pageable, int roleType) {
        task = timeUtil(task);
        String result = "k.id as id, k.task_name as taskName,k.tdyw_org as yworg,k.CREATE_TIME as createTime,k.plan_start_time as startTime,k.plan_end_time as endTime,k.status as status,u.realname as userName,d.DEPTNAME as class";
        List params = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" where k.create_time between to_date(?,'YYYY-MM-DD hh24:mi') and to_date(?,'YYYY-MM-DD hh24:mi') ");
        params.add(task.getPlanStartTime());
        params.add(task.getPlanEndTime());
        /*if (task.getPlanStartTime()!=null){

        }*/
        if (task.getTaskName() != null && !task.getTaskName().equals("")) {
            task.setTaskName("%" + task.getTaskName() + "%");
            buffer.append(" and k.task_name like ? ");
            params.add(task.getTaskName());
        }
        //此处的状态要改
        if (task.getStatus() != null && !task.getStatus().equals("")) {
            task.setStatus("%" + task.getStatus() + "%");
            buffer.append(" and k.status like ? ");
            params.add(task.getStatus());
        }
        if (task.getUserName() != null && !task.getUserName().equals("")) {
            task.setUserName("%" + task.getUserName() + "%");
            buffer.append(" and u.realname like ?");
            params.add(task.getUserName());
        }
        String sql = "";

        if (roleType == 1 || roleType == 2) {
            sql = "select " + result + " from kh_task k  left join rztsysuser u on u.id = k.user_id left join RZTSYSDEPARTMENT d on u.classname = d.id " + buffer.toString() + " and k.tdyw_org = (select d.deptname from rztsysuser u, RZTSYSDEPARTMENT d where d.id = u.deptid and u.id = ?)";
            params.add(task.getUserId());
        } else if (roleType == 3) {
            sql = "select " + result + " from kh_task k  left join rztsysuser u on u.id = k.user_id left join RZTSYSDEPARTMENT d on u.classname = d.id " + buffer.toString() + " and k.wx_org = (select d.COMPANYNAME from rztsysuser u,RZTSYSCOMPANY d where u.companyid = d.id and u.id = ?)";
            params.add(task.getUserId());
        } else if (roleType == 4) {
            sql = "select " + result + " from kh_task k  left join rztsysuser u on u.id = k.user_id left join RZTSYSDEPARTMENT d on u.classname = d.id " + buffer.toString() + " and u.classname = (select u.classname from rztsysuser u, RZTSYSDEPARTMENT d where d.id = u.deptid and u.id = ?)";
            params.add(task.getUserId());
        } else if (roleType == 5) {
            sql = "select " + result + " from kh_task k  left join rztsysuser u on u.id = k.user_id left join RZTSYSDEPARTMENT d on u.classname = d.id " + buffer.toString() + " and k.user_id = ?";
            params.add(task.getUserId());
        } else {
            sql = "select " + result + " from kh_task k  left join rztsysuser u on u.id = k.user_id left join RZTSYSDEPARTMENT d on u.classname = d.id " + buffer.toString();
        }
        buffer.append(" order by k.create_time desc ");
        sql = sql + " order by k.create_time desc";
        //String sql = "select * from listAllKhTask "+buffer.toString();
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql, params.toArray());
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
            c.add(Calendar.DAY_OF_MONTH, -7);
            Date m = c.getTime();
            String mon = df.format(m);
            task.setPlanStartTime(mon + " 00:00");
            task.setPlanEndTime(df.format(new Date()) + " 23:59");
        }
        return task;
    }

    public List<Map<String, Object>> getKhTaskById(long id) {
        String result = " k.task_name as taskName,y.yhms as yhms,y.yhjb as yhjb,u.realname as userName,u.phone as phone ";
        String sql = "select " + result + " from kh_task k left join kh_yh_history y on k.yh_id=y.id left join rztsysuser u on u.id=k.user_id  where k.id=?";
        return this.execSql(sql, id);
    }

    public int getCount(long id, String userId) {
        return this.reposiotry.getCount(id, userId);
    }

    /*public void updateDDTime(Date time, long id) {
        this.reposiotry.updateDDTime(time,id);
    }

    public void updateSFQRTime(Date time, long id) {
        this.reposiotry.updateSFQRTime(time,id);
    }*/

    public void updateWPQRTime(Date time, long id) {
        this.reposiotry.updateWPQRTime(time, id);
    }

    public void updateRealStartTime(Date time, long id) {
        this.reposiotry.updateRealStartTime(time, id);
    }

    public void updateTaskById(String startTime, String endTime, String userId, String id) {
        this.reposiotry.updateSiteById(userId, id, startTime, endTime);
    }


    public WebApiResponse listCurrentTaskByUserId(String userId) {
        try {
            String date = DateUtil.getCurrentDate();
            String sql = "select k.id as taskId,k.status as status,k.task_name as taskname from kh_task k where k.user_id = ? and trunc(k.plan_start_time)>=trunc(sysdate)"; //to_date(?,'yyyy-mm-dd hh24:mi:ss') and k.plan_start_time<=to_date(?,'yyyy-mm-dd hh24:mi:ss')";
            String start = date + " 00:00:00";
            String end = date + " 23:59:59";
            return WebApiResponse.success(this.execSql(sql, userId));//, start, end));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }
    public WebApiResponse listTaskInfoById(String taskId) {
        try {
            String date = DateUtil.getCurrentDate();
            String sql = "SELECT TASK_NAME TASKNAME,CREATE_TIME PDTIME,TDYW_ORG YWORG,WX_ORG WXORG,PLAN_START_TIME STARTTIME,PLAN_END_TIME ENDTIME,STATUS from KH_TASK WHERE ID=?";
            return WebApiResponse.success(this.execSql(sql, taskId));//, start, end));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    //电压等级  任务执行人 线路名称  杆塔号 开始时间  通道单位 外协单位  区段
    public WebApiResponse listTaskInfoByYhId(String yhId) {
        try {
            String sql = "select l.v_level as voltage,l.line_name as linename,s.section as section,s.tdyw_org as yworg,y.TDWX_ORG as wxorg,U.REALNAME as name,T.PLAN_START_TIME as starttime,T.PLAN_END_TIME as endtime \n" +
                    "from KH_SITE S,KH_YH_HISTORY y,KH_TASK T,RZTSYSUSER U,cm_line l \n" +
                    "where s.YH_ID = ? and l.id = y.line_id and t.PLAN_END_TIME>=sysdate and y.id = s.YH_ID AND S.ID = T.SITE_ID AND T.USER_ID = U.ID order by t.plan_start_time";
            return WebApiResponse.success(this.execSql(sql, yhId));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public void CreateTask() {
        List<KhSite> list = siteRepository.findSites();
        for (KhSite site:list) {
            int cycle = site.getCycle();  //一轮任务时长
            String planStartTime = site.getPlanStartTime();
            String planEndTime = site.getPlanEndTime();
//            if (planStartTime)
        }
    }

    public List<Map<String, Object>> findAlls() {
        String sql = "select * from kh_task order by create_time desc";
        List<Map<String, Object>> list = this.execSql(sql);
        return list;
    }

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
            cell.setCellValue("派发时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 3);
            cell.setCellValue("计划开始时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 4);
            cell.setCellValue("计划结束时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 5);
            cell.setCellValue("通道运维单位");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 6);
            cell.setCellValue("外协单位");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 7);
            cell.setCellValue("任务状态");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 8);
            cell.setCellValue("实际开始时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 9);
            cell.setCellValue("身份确认时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 10);
            cell.setCellValue("到达现场时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 11);
            cell.setCellValue("物品确认时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 12);
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
                    String sql = "select realname from rztsysuser where id=?";
                    Map<String, Object> map = this.execSqlSingleResult(sql, task.get("USER_ID").toString());
                    row.createCell(1).setCellValue(map.get("REALNAME").toString());//通道单位
                }
                if (task.get("CREATE_TIME") != null) {
                    row.createCell(2).setCellValue(task.get("CREATE_TIME").toString().substring(0,task.get("CREATE_TIME").toString().length()-2));//计划开始时间
                }
                if (task.get("PLAN_START_TIME") != null) {
                    row.createCell(3).setCellValue(task.get("PLAN_START_TIME").toString().substring(0,task.get("PLAN_START_TIME").toString().length()-2));//计划结束时间
                }

                if (task.get("PLAN_END_TIME") != null) {
                    row.createCell(4).setCellValue(task.get("PLAN_END_TIME").toString().substring(0,task.get("PLAN_END_TIME").toString().length()-2));//通道单位
                }
                if (task.get("TDYW_ORG") != null) {
                    row.createCell(5).setCellValue(task.get("TDYW_ORG").toString());//班组
                }
                if (task.get("WX_ORG") != null) {
                    row.createCell(6).setCellValue(task.get("WX_ORG").toString());//巡视人员
                }
                if (task.get("STATUS") != null) {
                    row.createCell(7).setCellValue(task.get("STATUS").toString());
                }
                if (task.get("REAL_START_TIME") != null) {
                    row.createCell(8).setCellValue(task.get("REAL_START_TIME").toString().substring(0,task.get("REAL_START_TIME").toString().length()-2));
                }
                if (task.get("SFQR_TIME") != null) {
                    row.createCell(9).setCellValue(task.get("SFQR_TIME").toString().substring(0,task.get("SFQR_TIME").toString().length()-2));
                }
                if (task.get("DDXC_TIME") != null) {
                    row.createCell(10).setCellValue(task.get("DDXC_TIME").toString().substring(0,task.get("DDXC_TIME").toString().length()-2));
                }
                if (task.get("REAL_END_TIME") != null) {
                    row.createCell(11).setCellValue(task.get("REAL_END_TIME").toString().substring(0,task.get("REAL_END_TIME").toString().length()-2));
                }
                //int status = Integer.parseInt(task.get("STATUS").toString());
                //该次执行状态(0待办,1进行中,2完成)

                /*if (status == 0) {
                    row.createCell(8).setCellValue("未开始");
                } else if (status == 1) {
                    row.createCell(8).setCellValue("已派发");
                } else if (status == 2) {
                    row.createCell(8).setCellValue("已消缺");
                }*/

            }
            OutputStream output = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + new String("看护任务导出表.xlsx".getBytes("utf-8"), "iso8859-1"));
            response.setContentType("Content-Type:application/vnd.ms-excel ");
            wb.write(output);
            output.close();
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
    }
}

