/**
 * 文件名：KhCycleService
 * 版本信息：
 * 日期：2017/11/28 14:43:44
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.netflix.discovery.converters.Auto;
import com.rzt.entity.*;
import com.rzt.entity.model.KhTaskModel;
import com.rzt.repository.KhCycleRepository;
import com.rzt.repository.KhSiteRepository;
import com.rzt.repository.KhTaskRepository;
import com.rzt.repository.KhYhHistoryRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import com.rzt.utils.MapUtil;
import com.rzt.utils.RedisUtil;
import com.rzt.utils.SnowflakeIdWorker;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPUse;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 类名称：KhCycleService
 * 类描述：InnoDB free: 536576 kB
 * 创建人：张虎成
 * 创建时间：2017/11/28 14:43:44
 * 修改人：张虎成
 * 修改时间：2017/11/28 14:43:44
 * 修改备注：
 */
@Service
public class KhSiteService extends CurdService<KhSite, KhSiteRepository> {

    @Autowired
    private KhTaskRepository taskRepository;
    @Autowired
    private KhYhHistoryService yhservice;
    @Autowired
    private KhYhHistoryRepository yhRepository;
    @Autowired
    private KhTaskService taskService;
    @Autowired
    private KhCycleService cycleService;
    @Autowired
    private KhCycleRepository cycleRepository;


    public Object listAllTaskNotDo(KhTaskModel task, Pageable pageable, String userName, String deptId, String roleType) {
        List params = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        String result = " k.id as id,k.task_name as taskName,k.tdyw_org as yworg,y.yhms as ms,y.yhjb as jb,k.create_time as createTime,k.COUNT as COUNT,u.realname as username,k.jbd as jbd,k.plan_start_time as starttime,k.plan_end_time as endtime,u.id as userId";
        String result1 = " k.id as id,k.task_name as taskName,k.tdyw_org as yworg,y.yhms as ms,y.yhjb as jb,k.create_time as createTime ";
        buffer.append(" where k.status = ?");// 0为未派发的任务
        params.add(task.getStatus());
        if (task.getPlanStartTime() != null && !task.getPlanStartTime().equals("")) {
            buffer.append(" and k.create_time between to_date(?,'YYYY-MM-DD hh24:mi') and to_date(?,'YYYY-MM-DD hh24:mi') ");
            params.add(task.getPlanStartTime());
            params.add(task.getPlanEndTime());
        }
        if (task.getTaskName() != null && !task.getTaskName().equals("")) {  //线路名查询
            task.setTaskName("%" + task.getTaskName() + "%");
            buffer.append(" and k.task_name like ? ");
            params.add(task.getTaskName());
        }
        if (userName != null) {
            buffer.append(" and u.realname like ? ");
            params.add(userName);
        }

        String sql = "";

        //公司本部、属地公司权限
        if (roleType.equals("1") || roleType.equals("2")) {
            if (task.getStatus().equals("1")) {
                sql = "select " + result + " from kh_site k ,kh_yh_history y,rztsysuser u,RZTSYSDEPARTMENT d  " + buffer.toString() + " and k.yh_id = y.id and u.id = k.user_id and d.id = u.deptid and u.deptid = (select DEPTID FROM RZTSYSUSER where id =?) ";
            } else {
                sql = "select " + result1 + "from kh_cycle k,kh_yh_history y " + buffer.toString() + " and k.yh_id = y.id  and k.tdyw_org = (select d.deptname FROM RZTSYSUSER u,RZTSYSDEPARTMENT d where d.id= u.deptid and u.id =?) ";
            }
            params.add(task.getUserId());

            //部门权限
        } else if (roleType.equals("3")) {
            if (task.getStatus().equals("1")) {
                sql = "select " + result + " from kh_site k ,kh_yh_history y,rztsysuser u,RZTSYSCOMPANY d  " + buffer.toString() + " and  k.yh_id = y.id and u.id = k.user_id and d.id = u.COMPANYID and u.COMPANYID = (select COMPANYID FROM RZTSYSUSER where id =?) ";
            } else {
                sql = "select " + result1 + "from kh_cycle k,kh_yh_history y " + buffer.toString() + " and k.yh_id = y.id and k.WX_ORG = (select d.COMPANYNAME FROM RZTSYSUSER u,RZTSYSCOMPANY d where d.id= u.COMPANYID and u.id =?) ";
            }
            params.add(task.getUserId());

            //组织权限
        } else if (roleType.equals("4")) {
            if (task.getStatus().equals("1")) {
                sql = "select " + result + " from kh_site k ,kh_yh_history y,rztsysuser u,RZTSYSDEPARTMENT d  " + buffer.toString() + " and  k.yh_id = y.id and u.id = k.user_id and d.id = u.classname and u.classname = (select classname FROM RZTSYSUSER where id =?) ";
            } else {
                // sql = "select " + result1 + "from kh_cycle k,kh_yh_history y " + buffer.toString()+" and k.yh_id = y.id and k.tdyw_org = d.deptname and k.tdyw_org = (select d.deptname FROM RZTSYSUSER u,RZTSYSDEPARTMENT d where d.id= u.deptid and u.id =?) ";
                return new ArrayList<>();
            }
            params.add(task.getUserId());

            //个人权限
        } else if (roleType.equals("5")) {
            if (task.getStatus().equals("1")) {
                sql = "select " + result + " from kh_site k ,kh_yh_history y,rztsysuser u  " + buffer.toString() + " and k.yh_id = y.id and u.id = k.user_id and k.user_id = ?";
            } else {
                // sql = "select " + result1 + "from kh_cycle k,kh_yh_history y,RZTSYSDEPARTMENT d " + buffer.toString()+" and k.yh_id = y.id and k.tdyw_org = d.deptname and k.tdyw_org = (select d.deptname FROM RZTSYSUSER u,RZTSYSDEPARTMENT d where d.id= u.deptid and u.id =?) ";
                return new ArrayList<>();
            }
            params.add(task.getUserId());

            //所有权限
        } else {
            if (task.getStatus().equals("1")) {
                sql = "select " + result + " from kh_site k ,kh_yh_history y,rztsysuser u " + buffer.toString() + " and k.yh_id = y.id and u.id = k.user_id";
            } else {
                sql = "select " + result1 + "from kh_cycle k left join kh_yh_history y on k.yh_id = y.id " + buffer.toString();
            }
        }
        sql = sql + " order by k.create_time desc";
        //String sql = "select * from listAllTaskNotDo "+buffer.toString();
        Page<Map<String, Object>> maps1 = this.execSqlPage(pageable, sql, params.toArray());
        List<Map<String, Object>> content1 = maps1.getContent();
        for (Map map : content1) {
            map.put("ID", map.get("ID") + "");
        }
        return maps1;
    }

    //消缺已派发的任务
    public void updateQxTask(long id) {
        KhSite site = this.reposiotry.findSite(id);
        long yhid = site.getYhId();
        this.reposiotry.updateQxTask(yhid, DateUtil.dateNow());
        this.reposiotry.updateDoingTask(yhid, DateUtil.dateNow());
        this.reposiotry.updateYH(yhid, DateUtil.dateNow());
        this.reposiotry.updateKhCycle(yhid);
    }


    //消缺未派发的任务
    public void xiaoQueCycle(long id) {
        KhCycle site = this.cycleRepository.findCycle(id);
        this.reposiotry.updateYH(site.getYhId(), DateUtil.dateNow());
        //  this.reposiotry.updateCheckTask(id);
        this.reposiotry.updateKhCycle(id);
    }

    //导出返回List<Map>
    public List<Map<String, Object>> findAlls() {
        String sql = "select * from kh_site";
        List<Map<String, Object>> maps = this.execSql(sql);
        return maps;
    }

    public List listKhtaskById(long id) {
        String result = "k.task_name as taskname,k.plan_start_time starttime,k.plan_end_time endtime,d.deptname deptname,y.yhms as ms,y.yhjb as jb,u.realname as name";
        String sql = "select " + result + " from kh_site k left join rztsysuser u on u.id = k.user_id left join kh_yh_history y on y.id = k.yh_id left join rztsysdepartment d on d.id=u.classname where k.id=?";
        return this.execSql(sql, id);
    }

    @Transactional
    public WebApiResponse saveYh(KhYhHistory yh, String fxtime, String startTowerName, String endTowerName, String pictureId) {
        try {
            yh.setYhfxsj(DateUtil.parseDate(fxtime));
            yh.setSfdj(0);
            if (!yh.getStartTower().isEmpty()) {
                String startTower = "select longitude,latitude from cm_tower where id = ?";
                String endTower = "select longitude,latitude from cm_tower where id = ?";
                Map<String, Object> map = execSqlSingleResult(startTower, Integer.parseInt(yh.getStartTower()));
                Map<String, Object> map1 = execSqlSingleResult(endTower, Integer.parseInt(yh.getEndTower()));
                //经度
                double jd = (Double.parseDouble(map.get("LONGITUDE").toString()) + Double.parseDouble(map1.get("LONGITUDE").toString())) / 2;
                double wd = (Double.parseDouble(map.get("LATITUDE").toString()) + Double.parseDouble(map1.get("LATITUDE").toString())) / 2;
                double radius = MapUtil.GetDistance(Double.parseDouble(map.get("LONGITUDE").toString()), Double.parseDouble(map.get("LATITUDE").toString()), Double.parseDouble(map1.get("LONGITUDE").toString()), Double.parseDouble(map1.get("LATITUDE").toString())) / 2;
                yh.setRadius("100.0");
                yh.setJd(map.get("LONGITUDE").toString());
                yh.setWd(map.get("LATITUDE").toString());
            }
            KhCycle task = new KhCycle();
            task.setId();
            String kv = yh.getVtype();
            if (yh.getVtype().contains("kV")) {
                 kv = kv.substring(0, kv.indexOf("k"));
            }
            yh.setTaskId(task.getId());
            yh.setYhzt(0);//隐患未消除
            yh.setId(0L);
            yh.setCreateTime(DateUtil.dateNow());
            yh.setSection(startTowerName + "-" + endTowerName);
            yhservice.add(yh);
            String taskName = kv + "-" + yh.getLineName() +" "+ startTowerName + "-" + endTowerName + " 号杆塔看护任务";
            task.setVtype(yh.getVtype());
            task.setLineName(yh.getLineName());
            task.setTdywOrg(yh.getTdywOrg());
            task.setSection(yh.getSection());
            task.setLineId(yh.getLineId());
            task.setTaskName(taskName);
            task.setWxOrgId(yh.getWxorgId());
            task.setTdywOrgId(yh.getTdorgId());
            task.setWxOrg(yh.getTdwxOrg());
            task.setStatus(0);// 未派发
            task.setYhId(yh.getId());
            task.setCreateTime(DateUtil.dateNow());
            this.cycleService.add(task);
            long id = new SnowflakeIdWorker(2, 4).nextId();
            this.reposiotry.addCheckSite(id,task.getId(),0,task.getTaskName(),0,task.getLineId(),task.getTdywOrgId(),task.getWxOrgId(),task.getYhId());
            if (null != pictureId && !pictureId.equals("")) {
                String[] split = pictureId.split(",");
                for (int i = 0; i < split.length; i++) {
                    yhRepository.updatePicture(Long.parseLong(split[i]), yh.getId());
                    //审批完成后   为图片添加taskId
                }
            }
            return WebApiResponse.success("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    public WebApiResponse deleteById(String id) {
        try {
            String[] split = id.split(",");
            if (split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    this.reposiotry.deleteById(Long.parseLong(split[i].toString()));
                }
            } else {
                this.reposiotry.deleteById(Long.parseLong(id));
            }
            return WebApiResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("删除失败");
        }
    }

    public WebApiResponse paifaTask(String id, String tasks, KhTaskModel model) {
        try {
            List<Map<Object, String>> list = (List<Map<Object, String>>) JSONObject.parse(tasks);
            KhCycle cycle = this.cycleRepository.findCycle(Long.parseLong(id));
            String groupFlag = new SnowflakeIdWorker(0, 0).nextId() + "";
            Date time1 = DateUtil.parseDate(list.get(0).get("planStartTime").toString());
            Date time2 = DateUtil.parseDate(list.get(list.size() - 1).get("planEndTime").toString());
            double cycle1 = DateUtil.getDatePoor(time2, time1);
            DecimalFormat df = new DecimalFormat("0.00");
            cycle1 = Double.parseDouble(df.format(cycle1));
//            int cycle1 =List.get(0).get("planStartTime").toString();
            for (Map map : list) {
                KhTask task = new KhTask();
                KhSite site = new KhSite();
                String capatain = map.get("capatain").toString();
                String userId = map.get("userId").toString();
                String startTime = map.get("planStartTime").toString();
                String endTime = map.get("planEndTime").toString();
                site.setId();
                site.setVtype(cycle.getVtype());
                site.setLineName(cycle.getLineName());
                site.setCycle(cycle1);
                site.setLineId(cycle.getLineId());
                site.setSection(cycle.getSection());
                site.setStatus(1);
                site.setUserid(userId);
                site.setPlanStartTime(DateUtil.parseDate(startTime));
                site.setPlanEndTime(DateUtil.parseDate(endTime));
                site.setTaskName(cycle.getTaskName());
                site.setTdywOrg(cycle.getTdywOrg());
                site.setYhId(cycle.getYhId());
                site.setCount(1);
                site.setWxOrg(cycle.getWxOrg());
                site.setCreateTime(DateUtil.dateNow());
                site.setJbd(map.get("jbd").toString());
                site.setGroupFlag(groupFlag + capatain);
                site.setTdywOrgId(cycle.getTdywOrgId());
                site.setWxOrgId(cycle.getWxOrgId());
                if (capatain.endsWith("1")) {
                    site.setCapatain(1);
                } else {
                    site.setCapatain(0);
                }
                this.add(site);
                int count = taskService.getCount(Long.parseLong(id), userId);
                task.setPlanStartTime(DateUtil.getPlanStartTime(startTime));
                task.setPlanEndTime(DateUtil.getPlanStartTime(endTime));
                task.setUserId(userId);
                task.setCount(count);
                task.setWxOrg(cycle.getWxOrg());
                task.setTdywOrg(cycle.getTdywOrg());
                task.setCreateTime(new Date());
                task.setStatus(0);
                task.setSiteId(site.getId());
                task.setYhId(cycle.getYhId());
                task.setTaskName(cycle.getTaskName());
                task.setId();
                taskService.add(task);
            }
            this.reposiotry.updateCycleById(id);  // 重新生成多个周期
            return WebApiResponse.success("任务派发成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("任务派发失败" + e.getMessage());
        }
    }


    public WebApiResponse listJpgById(String taskId) {
        try {
            String sql = "select file_path,PROCESS_ID,create_time,PROCESS_NAME,FILE_SMALL_PATH as smallPath from picture_kh where task_id = ? and file_type=1 order by PROCESS_ID";
            return WebApiResponse.success(this.execSql(sql, Long.parseLong(taskId)));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("图片获取失败" + e.getMessage());
        }
    }

    public WebApiResponse listOverdueKh() {
        try {
            String date = DateUtil.getCurrentDate();
            //获取未按时开始的任务
            String sql = "select count(*) from kh_task where plan_start_time <=nvl(sysdate,real_start_time) and to_char(plan_start_time) >= ?";
            return WebApiResponse.success(this.execSql(sql, date + " 00:00:00"));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("图片获取失败" + e.getMessage());
        }
    }

    public void exportNursePlan(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Map<String, Object>> taskList = this.findAlls();
            //this.service.exportExcel(response);
            //String ecxcelModelPath = rootpath + "excelModels"+File.separator+"看护任务导出表.xlsx";
            //InputStream in = new FileInputStream(ecxcelModelPath);
            //XSSFWorkbook wb = new XSSFWorkbook(in);
            String rootpath = request.getSession().getServletContext().getRealPath(File.separator);
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("看护周期");

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
            cell.setCellValue("电压等级");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 2);
            cell.setCellValue("线路名称");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 3);
            cell.setCellValue("段落");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 4);
            cell.setCellValue("看护人");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 5);
            cell.setCellValue("所属队伍");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 6);
            cell.setCellValue("通道运维单位");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 7);
            cell.setCellValue("外协单位");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 8);
            cell.setCellValue("创建时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 9);
            cell.setCellValue("几班倒");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 10);
            cell.setCellValue("任务状态");
            cell.setCellStyle(headerStyle);

            for (int i = 0; i < taskList.size(); i++) {
                row = sheet.createRow(i + 1);
                Map<String, Object> task = taskList.get(i);
                if (task.get("TASK_NAME") != null) {
                    row.createCell(0).setCellValue(task.get("TASK_NAME").toString());//任务名称
                }
                if (task.get("VTYPE") != null) {
                    row.createCell(1).setCellValue(task.get("VTYPE").toString());//派发时间
                }
                if (task.get("LINE_NAME") != null) {
                    row.createCell(2).setCellValue(task.get("LINE_NAME").toString());//计划开始时间
                }
                if (task.get("SECTION") != null) {
                    row.createCell(3).setCellValue(task.get("SECTION").toString());//计划结束时间
                }

                if (task.get("USER_ID") != null) {
                    String sql = "select u.realname REALNAME,d.deptname DEPTNAME from rztsysuser u left join rztsysdepartment d on d.id = u.classname where u.id=?";
                    List<Map<String, Object>> list = this.execSql(sql, task.get("USER_ID").toString());
                    if (!list.isEmpty()) {
                        row.createCell(4).setCellValue(list.get(0).get("REALNAME").toString());//通道单位
                        try {
                            String deptname = list.get(0).get("DEPTNAME").toString();
                            row.createCell(5).setCellValue(list.get(0).get("DEPTNAME").toString());
                        } catch (Exception e) {
                            row.createCell(5).setCellValue("");
                        }
                    }
                }
                if (task.get("TDYW_ORG") != null) {
                    row.createCell(6).setCellValue(task.get("TDYW_ORG").toString());//班组
                }
                if (task.get("WX_ORG") != null) {
                    row.createCell(7).setCellValue(task.get("WX_ORG").toString());//巡视人员
                }
                if (task.get("CREATE_TIME") != null) {
                    row.createCell(8).setCellValue(task.get("CREATE_TIME").toString());
                }
                if (task.get("JBD") != null) {
                    row.createCell(9).setCellValue(task.get("JBD").toString());
                }
                int status = Integer.parseInt(task.get("STATUS").toString());
                //该次执行状态(0待办,1进行中,2完成)

                if (status == 0) {
                    row.createCell(10).setCellValue("未派发");
                } else if (status == 1) {
                    row.createCell(10).setCellValue("已派发");
                } else if (status == 2) {
                    row.createCell(10).setCellValue("已消缺");
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
}