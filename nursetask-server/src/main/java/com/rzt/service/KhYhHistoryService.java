/**
 * 文件名：KHYHHISTORYService
 * 版本信息：
 * 日期：2017/11/30 18:31:34
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.alibaba.fastjson.JSON;
import com.netflix.ribbon.proxy.annotation.Http;
import com.rzt.entity.KhCycle;
import com.rzt.entity.KhSite;
import com.rzt.entity.XsSbYh;
import com.rzt.repository.KhYhHistoryRepository;
import com.rzt.entity.KhYhHistory;
import com.rzt.repository.XsSbYhRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import com.rzt.utils.ExcelUtil;
import com.rzt.utils.HanyuPinyinHelper;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * 类名称：KHYHHISTORYService
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/11/30 18:31:34
 * 修改人：张虎成
 * 修改时间：2017/11/30 18:31:34
 * 修改备注：
 */
@Service
public class KhYhHistoryService extends CurdService<KhYhHistory, KhYhHistoryRepository> {

    @Autowired
    private KhCycleService cycleService;
    @Autowired
    private XsSbYhService xsService;
    @Autowired
    private XsSbYhRepository xsRepository;
    @Autowired
    private KhSiteService siteService;

    public WebApiResponse list() {
        try {

            return WebApiResponse.success("");
        } catch (Exception e) {
            return WebApiResponse.erro("数据获取失败");
        }
    }

    @Transactional
    public WebApiResponse saveYh(XsSbYh yh, String startTowerName, String endTowerName, String pictureId) {
        try {
            yh.setYhfxsj(DateUtil.dateNow());
            yh.setId(0l);
            if (!(yh.getStartTower() == null) && yh.getStartTower().equals("")) {
                String startTower = "select longitude,latitude from cm_tower where id = ?";
                Map<String, Object> map = execSqlSingleResult(startTower, Integer.parseInt(yh.getStartTower()));
                //Map<String, Object> map1 = execSqlSingleResult(endTower, Integer.parseInt(yh.getEndTower()));
                //经度
                if (map.get("LONGITUDE") != null) {
                    //double jd = (Double.parseDouble(map.get("LONGITUDE").toString()) + Double.parseDouble(map1.get("LONGITUDE").toString())) / 2;
                    // double wd = (Double.parseDouble(map.get("LATITUDE").toString()) + Double.parseDouble(map1.get("LATITUDE").toString())) / 2;
                    // double radius = MapUtil.GetDistance(Double.parseDouble(map.get("LONGITUDE").toString()), Double.parseDouble(map.get("LATITUDE").toString()), Double.parseDouble(map1.get("LONGITUDE").toString()), Double.parseDouble(map1.get("LATITUDE").toString())) / 2;
                    yh.setRadius("200.0");
                    yh.setJd(map.get("LONGITUDE").toString());
                    yh.setWd(map.get("LATITUDE").toString());
                }
            }
            try {
                if (yh.getVtype().equals("0")) {
                    yh.setVtype("35kV");
                } else if (yh.getVtype().equals("1")) {
                    yh.setVtype("110kV");
                } else if (yh.getVtype().equals("2")) {
                    yh.setVtype("220kV");
                } else if (yh.getVtype().equals("3")) {
                    yh.setVtype("550kV");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (yh.getTdywOrg() != null) {
                    String sql = "select id from rztsysdepartment where deptname like ?";
                    Map<String, Object> map = this.execSqlSingleResult(sql, yh.getTdywOrg() + "%");
                    yh.setTdorgId(map.get("ID").toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            yh.setYhjb("一般");
            yh.setSdgs(1);//手机导入
            yh.setSfdj(0);  //未定级
            yh.setYhzt(0);//隐患未消除
            yh.setCreateTime(DateUtil.dateNow());
            yh.setSection(startTowerName + "-" + endTowerName);
            if (null != pictureId && !pictureId.equals("")) {
                String[] split = pictureId.split(",");
                for (int i = 0; i < split.length; i++) {
                    this.reposiotry.updateYhPicture(Long.parseLong(split[i]), yh.getId(), yh.getXstaskId());
                }
            }
            this.xsService.add(yh);

            return WebApiResponse.success("数据保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据保存失败" + e.getMessage());
        }
    }

    public WebApiResponse saveCoordinate(String yhId, String lat, String lon, String radius) {
        try {
            if (!radius.contains(".")) {
                radius = radius + ".0";
            }
            // this.reposiotry.updateYh(Long.parseLong(yhId), lat, lon, radius);
            this.reposiotry.updateCycle(Long.parseLong(yhId), lat, lon, radius);
            return WebApiResponse.success("保存成功");
        } catch (Exception e) {
            return WebApiResponse.erro("保存失败" + e.getMessage());
        }
    }

    public WebApiResponse listCoordinate(String yhjb, String yhlb) {
        try {
            StringBuffer buffer = new StringBuffer();
            List<Object> params = new ArrayList<>();
            buffer.append(" where trunc(create_time)=trunc(sysdate) ");
            if (yhjb != null && !yhjb.equals("")) {
                buffer.append(" and yhjb1 like");
                params.add("%" + yhjb + "%");
            }
            if (yhlb != null && !yhlb.equals("")) {
                buffer.append(" and yhlb like");
                params.add("%" + yhlb + "%");
            }
            buffer.append(" and yhzt = 0 ");
            String sql = "select * from kh_yh_history " + buffer.toString();
            List<Map<String, Object>> list = this.execSql(sql, params.toArray());
            List<Object> list1 = new ArrayList<>();
            for (Map map : list) {
                // System.out.println(map.get("JD").toString());
                if (map != null && map.size() > 0 && map.get("JD") != null) {
                    list1.add(map);
                }
            }
            return WebApiResponse.success(list1);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("获取失败" + e.getMessage());
        }
    }

    public WebApiResponse listYhById(String yhId) {
        try {
            String sql = "select * from kh_yh_history where id=?";
            return WebApiResponse.success(this.execSql(sql, Long.parseLong(yhId)));
        } catch (Exception e) {
            return WebApiResponse.erro("保存失败" + e.getMessage());
        }
    }

    //导入隐患生成看护点的方法
    public void addKhCycle(KhYhHistory yh, KhCycle cycle) {
        try {
            String kv = yh.getVtype();
            if (kv.contains("kV")) {
                kv = kv.substring(0, kv.indexOf("k"));
            }
            cycle.setId(0L);
            cycle.setTdywOrg(yh.getTdywOrg());
            cycle.setTdywOrgId(yh.getTdorgId());
            cycle.setWxOrg(yh.getTdwxOrg());
            cycle.setWxOrgId(yh.getWxorgId());
            cycle.setLineId(yh.getLineId());
            cycle.setYhId(yh.getId());
            cycle.setSection(yh.getSection());
            String taskName = kv + "-" + yh.getLineName() + yh.getSection() + "号杆塔看护任务";
            cycle.setTaskName(taskName);
            cycle.setCreateTime(DateUtil.dateNow());
            cycle.setVtype(yh.getVtype());
            cycle.setStatus(0);
            cycle.setLineName(yh.getLineName());
            cycleService.add(cycle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WebApiResponse exportYhHistory(HttpServletResponse response, Object josn, String userId) {
        String sql1 = "select y.* from kh_yh_history y left join kh_site c on y.id=c.yh_id where y.yhzt=0 ";
        Map jsonObject = JSON.parseObject(josn.toString(), Map.class);
        Integer roleType = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object tdId = jsonObject.get("DEPTID");
        Object classid = jsonObject.get("CLASSID");
        Object companyid = jsonObject.get("COMPANYID");
        List params = new ArrayList<>();
        if (roleType == 1 || roleType == 2) {
            sql1 += " and y.yworg_id=" + tdId;
        }
        if (roleType == 3) {
            sql1 += " and c.wxorg_Id=" + companyid;
        }
        if (roleType == 4) {
            sql1 += " and y.class_id=" + classid;
        }
        if (roleType == 5) {
            sql1 += " and c.user_id=" + userId;
        }

        List<Map<String, Object>> yhList = this.execSql(sql1);
        try {
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("隐患列表");

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
            sheet.setColumnWidth((short) 14, (short) 6000);
            sheet.setColumnWidth((short) 15, (short) 6000);// 空列设置小一些
            sheet.setColumnWidth((short) 16, (short) 6000);// 设置列宽
            sheet.setColumnWidth((short) 17, (short) 6000);
            sheet.setColumnWidth((short) 18, (short) 6000);
            sheet.setColumnWidth((short) 19, (short) 6000);
            sheet.setColumnWidth((short) 20, (short) 6000);
            sheet.setColumnWidth((short) 21, (short) 6000);
            sheet.setColumnWidth((short) 22, (short) 6000);
            sheet.setColumnWidth((short) 23, (short) 9000);
            sheet.setColumnWidth((short) 24, (short) 6000);
            sheet.setColumnWidth((short) 25, (short) 9000);
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
            cell.setCellValue("隐患编号");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 1);
            cell.setCellValue("通道维护单位");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 2);
            cell.setCellValue("设备维护单位");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 3);
            cell.setCellValue("主要线路");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 4);
            cell.setCellValue("所属班组");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 5);
            cell.setCellValue("电压等级");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 6);
            cell.setCellValue("起始杆号");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 7);
            cell.setCellValue("终止杆号");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 8);
            cell.setCellValue("涉及线路");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 9);
            cell.setCellValue("隐患级别");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 10);
            cell.setCellValue("隐患类别");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 11);
            cell.setCellValue("隐患描述");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 12);
            cell.setCellValue("隐患发现时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 13);
            cell.setCellValue("隐患地点(区、县)");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 14);
            cell.setCellValue("隐患地点(乡镇、街道)");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 15);
            cell.setCellValue("隐患地点(村)");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 16);
            cell.setCellValue("隐患责任单位");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 17);
            cell.setCellValue("隐患责任单位联系人");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 18);
            cell.setCellValue("隐患责任单位电话");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 19);
            cell.setCellValue("树木数");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 20);
            cell.setCellValue("导线对隐患垂直距离");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 21);
            cell.setCellValue("导线对隐患水平距离");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 22);
            cell.setCellValue("导线对隐患净空距离");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 23);
            cell.setCellValue("隐患形成原因");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 24);
            cell.setCellValue("是否栽装警示牌");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 25);
            cell.setCellValue("危急程度");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 26);
            cell.setCellValue("管控措施");
            cell.setCellStyle(headerStyle);
            //Sheet sheet = wb.getSheetAt(0);
            for (int i = 0; i < yhList.size(); i++) {
                row = sheet.createRow(i + 1);
                //Row row = sheet.getRow(i+1);
                Map<String, Object> task = yhList.get(i);
                if (task.get("ID") != null) {
                    row.createCell(0).setCellValue(task.get("ID").toString());//任务名称
                }
                if (task.get("TDYW_ORG") != null) {
                    row.createCell(1).setCellValue(task.get("TDYW_ORG").toString());//计划开始时间
                }
                if (task.get("SBYW_ORG") != null) {
                    row.createCell(2).setCellValue(task.get("SBYW_ORG").toString());//计划开始时间
                }
                if (task.get("LINE_NAME") != null) {
                    row.createCell(3).setCellValue(task.get("LINE_NAME").toString());//计划开始时间
                }
                if (task.get("CLASSNAME") != null) {
                    row.createCell(4).setCellValue(task.get("CLASSNAME").toString());//计划结束时间
                }
                if (task.get("VTYPE") != null) {
                    row.createCell(5).setCellValue(task.get("VTYPE").toString());//通道单位
                }
                if (task.get("SECTION") != null) {
                    String[] sections = task.get("SECTION").toString().split("-");
                    row.createCell(6).setCellValue(sections[0]);//起始杆塔
                    row.createCell(7).setCellValue(sections[1]);//终止杆塔
                }
                if (task.get("SJXL") != null) {
                    row.createCell(8).setCellValue(task.get("SJXL").toString());
                }
                if (task.get("YHJB1") != null) {
                    row.createCell(9).setCellValue(task.get("YHJB1").toString());
                }
                if (task.get("YHLB") != null) {
                    row.createCell(10).setCellValue(task.get("YHLB").toString());
                }
                if (task.get("YHMS") != null) {
                    row.createCell(11).setCellValue(task.get("YHMS").toString());
                }
                if (task.get("YHFXSJ") != null) {
                    row.createCell(12).setCellValue(task.get("YHFXSJ").toString().substring(0, task.get("YHFXSJ").toString().length() - 2));
                }
                if (task.get("YHTDQX") != null) {
                    row.createCell(13).setCellValue(task.get("YHTDQX").toString());
                }
                if (task.get("YHTDXZJD") != null) {
                    row.createCell(14).setCellValue(task.get("YHTDXZJD").toString());
                }
                if (task.get("YHTDC") != null) {
                    row.createCell(15).setCellValue(task.get("YHTDC").toString());
                }
                if (task.get("YHZRDW") != null) {
                    row.createCell(16).setCellValue(task.get("YHZRDW").toString());
                }
                if (task.get("YHZRDWLXR") != null) {
                    row.createCell(17).setCellValue(task.get("YHZRDWLXR").toString());
                }
                if (task.get("YHZRDWDH") != null) {
                    row.createCell(18).setCellValue(task.get("YHZRDWDH").toString());
                }
                if (task.get("SMS") != null) {
                    row.createCell(19).setCellValue(task.get("SMS").toString());
                }
                if (task.get("DXXYHCZJL") != null) {
                    row.createCell(20).setCellValue(task.get("DXXYHCZJL").toString());
                }
                if (task.get("DXDYHSPJL") != null) {
                    row.createCell(21).setCellValue(task.get("DXDYHSPJL").toString());
                }
                if (task.get("XDXYHJKJL") != null) {
                    row.createCell(22).setCellValue(task.get("XDXYHJKJL").toString());
                }
                if (task.get("YHXCYY") != null) {
                    row.createCell(23).setCellValue(task.get("YHXCYY").toString());
                }
                if (task.get("JSP") != null) {
                    row.createCell(24).setCellValue(task.get("JSP").toString());
                }
                if (task.get("YHJB") != null) {
                    row.createCell(25).setCellValue(task.get("YHJB").toString());
                }
                if (task.get("GKCS") != null) {
                    row.createCell(26).setCellValue(task.get("GKCS").toString());
                }

               /* int status = Integer.parseInt(task.get("STATUS").toString());
                //该次执行状态(0待办,1进行中,2完成)

                if (status == 0) {
                    row.createCell(8).setCellValue("未开始");
                } else if (status == 1) {
                    row.createCell(8).setCellValue("进行中");
                } else if (status == 2) {
                    row.createCell(8).setCellValue("已完成");
                } else {
                    row.createCell(8).setCellValue("已取消");
                }*/

            }
            OutputStream output = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + new String("隐患列表.xlsx".getBytes("utf-8"), "iso8859-1"));
            response.setContentType("Content-Type:application/vnd.ms-excel ");
            wb.write(output);
            output.close();
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
        return WebApiResponse.success("");
    }

    public WebApiResponse updateYhHistory(KhYhHistory yh, String startTowerName, String endTowerName) {
        try {
            if (startTowerName != null && !startTowerName.equals("")) {
                String section = startTowerName + "-" + endTowerName;
                this.reposiotry.updateYhHistory(yh.getId(), yh.getYhms(), yh.getStartTower(), yh.getEndTower(), yh.getYhzrdw(), yh.getYhzrdwlxr(), yh.getYhzrdwdh(), section);
            } else {
                this.reposiotry.updateYhHistory2(yh.getId(), yh.getYhms(), yh.getYhzrdw(), yh.getYhzrdwlxr(), yh.getYhzrdwdh());
            }
            return WebApiResponse.success("");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("修改失败" + e.getMessage());
        }
    }

    public WebApiResponse updateYhjb(String yhjb) {
        try {
//            if (yhjb)
            return WebApiResponse.success("");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("修改失败" + e.getMessage());
        }
    }

    public WebApiResponse lineArea(Integer id) {
        try {
            String sql = "select id as \"value\",NAME as \"label\",PID, ID,NAME from LINE_AREA start with pid= ?1 CONNECT by prior id =  PID";
            List<Map<String, Object>> list = this.execSql(sql, id);
            List list1 = treeOrgList(list, list.get(0).get("PID").toString());
            return WebApiResponse.success(list1);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("获取失败" + e.getMessage());
        }
    }

    public List treeOrgList(List<Map<String, Object>> orgList, String parentId) {
        List childOrg = new ArrayList<>();
        for (Map<String, Object> map : orgList) {
            String menuId = String.valueOf(map.get("ID"));
            String pid = String.valueOf(map.get("PID"));
            if (parentId.equals(pid)) {
                List c_node = treeOrgList(orgList, menuId);
                map.put("children", c_node);
                childOrg.add(map);
            }
        }
        return childOrg;
    }

    public void find() {
        try {
            List<KhYhHistory> all = this.reposiotry.findAll();
            for (KhYhHistory yh : all) {
                String className = yh.getClassName();
                String tdorgId = yh.getTdorgId();
                if (className != null && tdorgId != null) {
                    try {
                        String sql = "select * from rztsysdepartment where deptpid=? and deptname like ?";
                        Map<String, Object> map = this.execSqlSingleResult(sql, tdorgId, className);
                        this.reposiotry.updatess(map.get("ID").toString(), yh.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public WebApiResponse reviewYh(long yhId) {
        try {
            //先查出这条数据
            XsSbYh sbYh = xsRepository.findYh(yhId);
            //根据yhid将原lscycle停用
            this.reposiotry.updateLsCycle(sbYh.getId());
            //保存隐患、生成周期、
            KhYhHistory yh = new KhYhHistory();
            yh.setId(sbYh.getId());
//            yh.setCreateTime();
            yh.setYhzrdwlxr(sbYh.getYhzrdwlxr());
            yh.setYhzrdw(sbYh.getYhzrdw());
            yh.setYhzrdwdh(sbYh.getYhzrdwdh());
            yh.setTbrid(sbYh.getTbrid());
            yh.setTdywOrg(sbYh.getTdywOrg());
            yh.setTdorgId(sbYh.getTdorgId());
            yh.setWxorgId(sbYh.getWxorgId());
            yh.setTdwxOrg(sbYh.getTdwxOrg());
            yh.setYhxcyy(sbYh.getYhxcyy());
            yh.setYhtdqx(sbYh.getYhtdqx());
            yh.setYhtdxzjd(sbYh.getYhtdxzjd());
            yh.setYhtdc(sbYh.getYhtdc());
            yh.setYhms(sbYh.getYhms());
            yh.setYhjb(sbYh.getYhjb());
            yh.setYhlb(sbYh.getYhlb());
            yh.setYhjb1(sbYh.getYhjb1());
            yh.setXstaskId(sbYh.getXstaskId());
            yh.setVtype(sbYh.getVtype());
            yh.setGkcs(sbYh.getGkcs());
            yh.setSms(sbYh.getSms());
            yh.setJsp(sbYh.getJsp());
            yh.setSjxl(sbYh.getSjxl());
            yh.setSdgs(0);
            yh.setLineId(sbYh.getLineId());
            yh.setLineName(sbYh.getLineName());
            yh.setTbsj(sbYh.getCreateTime());
            yh.setStartTower(sbYh.getStartTower());
            yh.setEndTower(sbYh.getEndTower());
            yh.setXlzycd(sbYh.getXlzycd());
            yh.setSbywOrg(sbYh.getSbywOrg());
            yh.setDjyid(sbYh.getDjyid());
            yh.setDxdyhspjl(sbYh.getDxdyhspjl());
            yh.setDxxyhczjl(sbYh.getDxxyhczjl());
            yh.setXdxyhjkjl(sbYh.getXdxyhjkjl());
            yh.setJd(sbYh.getJd());
            yh.setWd(sbYh.getWd());
            yh.setRadius("200.0");
//            yh.setClassName(sbYh.getLineName());
//            yh.setClassId(sbYh.getclass);
            String[] split = sbYh.getSection().split("-");
            siteService.saveYh(yh, DateUtil.getStringDate(), split[0].toString(), split[1].toString(), "");
            //将原上报隐患的图片关联
//            String sql = "";
            //
            return WebApiResponse.success("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("保存失败");
        }
    }

    public WebApiResponse deleteYhById(long yhId) {
        try {
            String sql = "SELECT * FROM KH_CYCLE WHERE YH_ID=? and STATUS IN (1,0)";
            List<Map<String, Object>> maps = this.execSql(sql, yhId);
            if (maps.size() > 0) {
                throw new Exception();
            } else {
                this.reposiotry.deleteYhById(yhId);
            }
            return WebApiResponse.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("删除失败" + e.getMessage());
        }
    }

    public void ImportYhExam(HttpServletResponse response) {
        try {
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("隐患列表");

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
            sheet.setColumnWidth((short) 14, (short) 6000);
            sheet.setColumnWidth((short) 15, (short) 6000);// 空列设置小一些
            sheet.setColumnWidth((short) 16, (short) 6000);// 设置列宽
            sheet.setColumnWidth((short) 17, (short) 6000);
            sheet.setColumnWidth((short) 18, (short) 6000);
            sheet.setColumnWidth((short) 19, (short) 6000);
            sheet.setColumnWidth((short) 20, (short) 6000);
            sheet.setColumnWidth((short) 21, (short) 6000);
            sheet.setColumnWidth((short) 22, (short) 6000);
            sheet.setColumnWidth((short) 23, (short) 9000);
            sheet.setColumnWidth((short) 24, (short) 6000);
            sheet.setColumnWidth((short) 25, (short) 6000);
            sheet.setColumnWidth((short) 26, (short) 6000);
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
            cell.setCellValue("隐患编号");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 1);
            cell.setCellValue("通道维护单位");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 2);
            cell.setCellValue("设备维护单位");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 3);
            cell.setCellValue("主要线路");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 4);
            cell.setCellValue("所属班组");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 5);
            cell.setCellValue("电压等级");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 6);
            cell.setCellValue("起始杆号");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 7);
            cell.setCellValue("终止杆号");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 8);
            cell.setCellValue("涉及线路");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 9);
            cell.setCellValue("隐患级别");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 10);
            cell.setCellValue("隐患类别");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 11);
            cell.setCellValue("隐患描述");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 12);
            cell.setCellValue("隐患发现时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 13);
            cell.setCellValue("隐患地点(区、县)");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 14);
            cell.setCellValue("隐患地点(乡镇、街道)");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 15);
            cell.setCellValue("隐患地点(村)");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 16);
            cell.setCellValue("隐患责任单位");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 17);
            cell.setCellValue("隐患责任单位联系人");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 18);
            cell.setCellValue("隐患责任单位电话");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 19);
            cell.setCellValue("树木数");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 20);
            cell.setCellValue("导线对隐患垂直距离");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 21);
            cell.setCellValue("导线对隐患水平距离");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 22);
            cell.setCellValue("导线对隐患净空距离");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 23);
            cell.setCellValue("隐患形成原因");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 24);
            cell.setCellValue("是否栽装警示牌");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 25);
            cell.setCellValue("危急程度");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 26);
            cell.setCellValue("管控措施");
            cell.setCellStyle(headerStyle);
            OutputStream output = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + new String("隐患列表模板.xlsx".getBytes("utf-8"), "iso8859-1"));
            response.setContentType("Content-Type:application/vnd.ms-excel ");
            wb.write(output);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public WebApiResponse ImportYh(MultipartFile file) {
        int i = 2;
        try {
            // FileInputStream file = new FileInputStream("E:\\win10\\新建文件夹\\WeChat Files\\yawang-\\Files\\检分安云云台看护任务.xls ");
//            FileInputStream file = new FileInputStream("E:\\826708743\\FileRecv\\隐患列表_20180111-程焕竹.xls");
            //HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
            HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row = sheet.getRow(i);
            while (row != null && !"".equals(row.toString().trim())) {

                KhYhHistory yh = new KhYhHistory();
                HSSFCell cell = row.getCell(1);
                if (cell == null || "".equals(ExcelUtil.getCellValue(cell))) {
                    break;
                }

                yh.setId(null);
                String yworg = ExcelUtil.getCellValue(row.getCell(1));
                String sborg = ExcelUtil.getCellValue(row.getCell(2));
                if (yworg.contains("供电")) {
                    yworg = yworg.replace("供电", "");
                } else if (yworg.contains("分")) {
                    yworg = yworg.replace("分", "");
                } else if (yworg.contains("工程")) {
                    yworg = "工程公司";
                }
                if (sborg.contains("供电")) {
                    sborg = sborg.replace("供电", "");
                } else if (sborg.contains("分")) {
                    sborg = sborg.replace("分", "");
                } else if (yworg.contains("工程")) {
                    sborg = "工程公司";
                }
                yh.setTdywOrg(yworg); //通道维护单位
                yh.setSbywOrg(sborg);//设备维护单位
                try {
                    System.out.println(yh.getTdywOrg().substring(0, 2));
                    List<Map<String, Object>> list = execSql("select id from rztsysdepartment where deptpid=(select id from rztsysdepartment where deptname='通道运维单位')  and deptname like ?1", "%" + yh.getTdywOrg().substring(0, 2) + "%");
                    yh.setTdorgId(String.valueOf(list.get(0).get("ID")));
                } catch (Exception e) {
                    e.printStackTrace();
                    //LOGGER.error("通道单位匹配失败！",e);
                }
                //长青一
                String lineName = ExcelUtil.getCellValue(row.getCell(3));
                String linename1 = lineNamePY(lineName);
                try {
                    List<Map<String, Object>> list = execSql("select a.id,b.line_jb as jb from cm_line a left join CM_LINE_SECTION b on a.id = b.LINE_ID where b.line_name1  like ?1 ", "%" + lineName + "%");
                    yh.setLineId(Long.valueOf(String.valueOf(list.get(0).get("ID"))));
                    yh.setXlzycd(String.valueOf(list.get(0).get("JB")));
                } catch (Exception e) {
                    e.printStackTrace();
                    //LOGGER.error("线路匹配失败！",e);
                }
                yh.setLineName(linename1);//线路名称
                yh.setClassName(ExcelUtil.getCellValue(row.getCell(4)));//所属班组
                yh.setVtype(ExcelUtil.getCellValue(row.getCell(5)));//电压等级
                String startTower = ExcelUtil.getCellValue(row.getCell(6));//起始杆塔
                String endTower = ExcelUtil.getCellValue(row.getCell(7));//终止杆塔
                yh.setSection(startTower + "-" + endTower);//段落
                yh.setSjxl(ExcelUtil.getCellValue(row.getCell(8)));//涉及线路
                yh.setYhjb1(ExcelUtil.getCellValue(row.getCell(9)));//隐患级别
                yh.setYhlb(ExcelUtil.getCellValue(row.getCell(10)));//隐患类别
                yh.setYhms(ExcelUtil.getCellValue(row.getCell(11)));//隐患描述
                yh.setYhfxsj(DateUtil.parseDate(ExcelUtil.getCellValue(row.getCell(12))));//隐患发现时间
                yh.setYhtdqx(ExcelUtil.getCellValue(row.getCell(13)));//字段描述: 隐患地点(区县)
                yh.setYhtdxzjd(ExcelUtil.getCellValue(row.getCell(14)));//字段描述: 隐患地点(乡镇街道)
                yh.setYhtdc(ExcelUtil.getCellValue(row.getCell(15)));//字段描述: 隐患地点(村)
                yh.setYhzrdw(ExcelUtil.getCellValue(row.getCell(16)));//隐患责任单位
                yh.setYhzrdwlxr(ExcelUtil.getCellValue(row.getCell(17)));//隐患责任单位联系人
                yh.setYhzrdwdh(ExcelUtil.getCellValue(row.getCell(18)));//隐患责任单位电话
                yh.setSms(ExcelUtil.getCellValue(row.getCell(19)));//树木数
                yh.setDxxyhczjl(ExcelUtil.getCellValue(row.getCell(20)));//导线对隐患垂直距离
                yh.setDxdyhspjl(ExcelUtil.getCellValue(row.getCell(21)));//导线对隐患水平距离
                yh.setXdxyhjkjl(ExcelUtil.getCellValue(row.getCell(22)));//导线对隐患净空距离
                yh.setYhxcyy(ExcelUtil.getCellValue(row.getCell(23)));//隐患形成原因
                yh.setJsp(ExcelUtil.getCellValue(row.getCell(24)));//是否栽装警示牌
                String wjcd = ExcelUtil.getCellValue(row.getCell(25));//危急程度
                if (wjcd != null && !wjcd.equals("")) {
                    yh.setYhjb(wjcd);
                } else {
                    yh.setYhjb("一般");
                }
                yh.setGkcs(ExcelUtil.getCellValue(row.getCell(26)));//管控措施
                //yh.setZpxgsj(DateUtil.parse(ExcelUtil.getCellValue(row.getCell(27))));//照片修改时间
                yh.setYhzt(0);//隐患状态
                yh.setRadius("100.0");
                yh.setSdgs(2);//execl导入
                yh.setSfdj(1);//已定级
                yh.setCreateTime(DateUtil.parseDate(ExcelUtil.getCellValue(row.getCell(12))));

                String sql = "select t.tower_Id id from cm_line_tower t where t.line_name like ? and t.tower_name like ?";
                try {
                    System.out.println(i + ":" + lineName + ",杆塔号:" + linename1 + ":" + startTower + ",终止杆塔：" + endTower);
                    if (startTower.startsWith("0")) {
                        startTower = startTower.substring(1, startTower.length() - 1);
                    }
                    List<Map<String, Object>> start = this.execSql(sql, "%" + linename1 + "%", "%" + startTower + "%");
                    List<Map<String, Object>> end = this.execSql(sql, "%" + linename1 + "%", "%" + endTower + "%");
                    yh.setStartTower(start.get(0).get("ID").toString());
                    yh.setEndTower(end.get(0).get("ID").toString());
                    String sql2 = "select longitude lon,latitude lat from cm_tower where id=?";
                    List<Map<String, Object>> zuobiao = this.execSql(sql2, Long.parseLong(yh.getStartTower()));
                    yh.setJd(zuobiao.get(0).get("LON").toString());
                    yh.setWd(zuobiao.get(0).get("LAT").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String cellValue = ExcelUtil.getCellValue(row.getCell(9));
                //if (ExcelUtil.getCellValue(row.getCell(9)).equals("施工隐患")){
                KhCycle cycle = new KhCycle();
                cycle.setId(0l);
                yh.setTaskId(cycle.getId());
                addKhCycle(yh, cycle);
                //}
                this.add(yh);
                row = sheet.getRow(++i);
                /*
                yh.setStartTower();
                yh.setEndTower();*/
            }
            return WebApiResponse.success("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("导入失败" + e.getMessage());
        }
    }

    @Transactional
    public WebApiResponse ImportYh2(MultipartFile file) {
        int i = 2;
        try {
            // FileInputStream file = new FileInputStream("E:\\win10\\新建文件夹\\WeChat Files\\yawang-\\Files\\检分安云云台看护任务.xls ");
//            FileInputStream file = new FileInputStream("E:\\826708743\\FileRecv\\隐患列表_20180111-程焕竹.xls");
            //HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
            XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row = sheet.getRow(i);
            while (row != null && !"".equals(row.toString().trim())) {

                KhYhHistory yh = new KhYhHistory();
                XSSFCell cell = row.getCell(1);
                if (cell == null || "".equals(ExcelUtil.getCellValue2(cell))) {
                    break;
                }

                yh.setId(null);
                String yworg = ExcelUtil.getCellValue2(row.getCell(1));
                String sborg = ExcelUtil.getCellValue2(row.getCell(2));
                if (yworg.contains("供电")) {
                    yworg = yworg.replace("供电", "");
                } else if (yworg.contains("分")) {
                    yworg = yworg.replace("分", "");
                } else if (yworg.contains("工程")) {
                    yworg = "工程公司";
                }
                if (sborg.contains("供电")) {
                    sborg = sborg.replace("供电", "");
                } else if (sborg.contains("分")) {
                    sborg = sborg.replace("分", "");
                } else if (yworg.contains("工程")) {
                    sborg = "工程公司";
                }
                yh.setTdywOrg(yworg); //通道维护单位
                yh.setSbywOrg(sborg);//设备维护单位
                try {
                    System.out.println(yh.getTdywOrg().substring(0, 2));
                    List<Map<String, Object>> list = execSql("select id from rztsysdepartment where deptpid=(select id from rztsysdepartment where deptname='通道运维单位')  and deptname like ?1", "%" + yh.getTdywOrg().substring(0, 2) + "%");
                    yh.setTdorgId(String.valueOf(list.get(0).get("ID")));
                } catch (Exception e) {
                    e.printStackTrace();
                    //LOGGER.error("通道单位匹配失败！",e);
                }
                //长青一
                String lineName = ExcelUtil.getCellValue2(row.getCell(3));
                String linename1 = lineNamePY(lineName);
                try {
                    List<Map<String, Object>> list = execSql("select a.id,b.line_jb as jb from cm_line a left join CM_LINE_SECTION b on a.id = b.LINE_ID where b.line_name1  like ?1 ", "%" + lineName + "%");
                    yh.setLineId(Long.valueOf(String.valueOf(list.get(0).get("ID"))));
                    yh.setXlzycd(String.valueOf(list.get(0).get("JB")));
                } catch (Exception e) {
                    e.printStackTrace();
                    //LOGGER.error("线路匹配失败！",e);
                }
                yh.setLineName(linename1);//线路名称
                yh.setClassName(ExcelUtil.getCellValue2(row.getCell(4)));//所属班组
                yh.setVtype(ExcelUtil.getCellValue2(row.getCell(5)));//电压等级
                String startTower = ExcelUtil.getCellValue2(row.getCell(6));//起始杆塔
                String endTower = ExcelUtil.getCellValue2(row.getCell(7));//终止杆塔
                yh.setSection(startTower + "-" + endTower);//段落
                yh.setSjxl(ExcelUtil.getCellValue2(row.getCell(8)));//涉及线路
                yh.setYhjb1(ExcelUtil.getCellValue2(row.getCell(9)));//隐患级别
                yh.setYhlb(ExcelUtil.getCellValue2(row.getCell(10)));//隐患类别
                yh.setYhms(ExcelUtil.getCellValue2(row.getCell(11)));//隐患描述
                yh.setYhfxsj(DateUtil.parseDate(ExcelUtil.getCellValue2(row.getCell(12))));//隐患发现时间
                yh.setYhtdqx(ExcelUtil.getCellValue2(row.getCell(13)));//字段描述: 隐患地点(区县)
                yh.setYhtdxzjd(ExcelUtil.getCellValue2(row.getCell(14)));//字段描述: 隐患地点(乡镇街道)
                yh.setYhtdc(ExcelUtil.getCellValue2(row.getCell(15)));//字段描述: 隐患地点(村)
                yh.setYhzrdw(ExcelUtil.getCellValue2(row.getCell(16)));//隐患责任单位
                yh.setYhzrdwlxr(ExcelUtil.getCellValue2(row.getCell(17)));//隐患责任单位联系人
                yh.setYhzrdwdh(ExcelUtil.getCellValue2(row.getCell(18)));//隐患责任单位电话
                yh.setSms(ExcelUtil.getCellValue2(row.getCell(19)));//树木数
                yh.setDxxyhczjl(ExcelUtil.getCellValue2(row.getCell(20)));//导线对隐患垂直距离
                yh.setDxdyhspjl(ExcelUtil.getCellValue2(row.getCell(21)));//导线对隐患水平距离
                yh.setXdxyhjkjl(ExcelUtil.getCellValue2(row.getCell(22)));//导线对隐患净空距离
                yh.setYhxcyy(ExcelUtil.getCellValue2(row.getCell(23)));//隐患形成原因
                yh.setJsp(ExcelUtil.getCellValue2(row.getCell(24)));//是否栽装警示牌
                String wjcd = ExcelUtil.getCellValue2(row.getCell(25));//危急程度
                if (wjcd != null && !wjcd.equals("")) {
                    yh.setYhjb(wjcd);
                } else {
                    yh.setYhjb("一般");
                }
                yh.setGkcs(ExcelUtil.getCellValue2(row.getCell(26)));//管控措施
                //yh.setZpxgsj(DateUtil.parse(ExcelUtil.getCellValue(row.getCell(27))));//照片修改时间
                yh.setYhzt(0);//隐患状态
                yh.setRadius("100.0");
                yh.setSdgs(2);//execl导入
                yh.setSfdj(1);//已定级
                yh.setCreateTime(DateUtil.parseDate(ExcelUtil.getCellValue2(row.getCell(12))));

                String sql = "select t.tower_Id id from cm_line_tower t where t.line_name like ? and t.tower_name like ?";
                try {
                    System.out.println(i + ":" + lineName + ",杆塔号:" + linename1 + ":" + startTower + ",终止杆塔：" + endTower);
                    if (startTower.startsWith("0")) {
                        startTower = startTower.substring(1, startTower.length() - 1);
                    }
                    List<Map<String, Object>> start = this.execSql(sql, "%" + linename1 + "%", "%" + startTower + "%");
                    List<Map<String, Object>> end = this.execSql(sql, "%" + linename1 + "%", "%" + endTower + "%");
                    yh.setStartTower(start.get(0).get("ID").toString());
                    yh.setEndTower(end.get(0).get("ID").toString());
                    String sql2 = "select longitude lon,latitude lat from cm_tower where id=?";
                    List<Map<String, Object>> zuobiao = this.execSql(sql2, Long.parseLong(yh.getStartTower()));
                    yh.setJd(zuobiao.get(0).get("LON").toString());
                    yh.setWd(zuobiao.get(0).get("LAT").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String cellValue = ExcelUtil.getCellValue2(row.getCell(9));
                //if (ExcelUtil.getCellValue(row.getCell(9)).equals("施工隐患")){
                KhCycle cycle = new KhCycle();
                cycle.setId(0l);
                yh.setTaskId(cycle.getId());
                addKhCycle(yh, cycle);
                //}
                this.add(yh);
                row = sheet.getRow(++i);
                /*
                yh.setStartTower();
                yh.setEndTower();*/
            }
            return WebApiResponse.success("导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("导入失败" + e.getMessage());
        }
    }

    //转换线路名的方法
    public String lineNamePY(String lineName) {
        HanyuPinyinHelper helper = new HanyuPinyinHelper();
        String linename1 = "";
        if (lineName != null) {
            if (lineName.contains("一")) {
                lineName = lineName.replace("一", "1");
            }
            if (lineName.contains("二")) {
                lineName = lineName.replace("二", "2");
            }
            linename1 = helper.toHanyuPinyin(lineName);//changqing1
            if (linename1.contains("1")) {
                linename1 = linename1.replace("1", "一");
            }
            if (linename1.contains("2")) {
                linename1 = linename1.replace("2", "二");
            }
        }
        if (linename1.contains("dou")) {
            linename1 = linename1.replace("dou", "du");
        }
        return linename1;
    }


    public WebApiResponse updateTowerById(long id, String lon, String lat) {
        try {
            this.reposiotry.updateTowerById(id,lon,lat);
            return  WebApiResponse.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return  WebApiResponse.erro("修改失败");
        }
    }

    public WebApiResponse findLineOrg(long lineId) {
        try {
            String sql = "SELECT TD_ORG_NAME\n" +
                    "FROM CM_LINE_SECTION where line_id=? and TD_ORG_NAME in ('门头沟公司','通州公司') ";
            List<Map<String, Object>> maps = this.execSql(sql, lineId);
            if (maps.size()>0){
                return  WebApiResponse.success("可以采集");
            }else {
                return  WebApiResponse.erro("不可以采集");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return  WebApiResponse.erro("不可以采集");
        }
    }
}