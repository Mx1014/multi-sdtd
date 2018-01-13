/**
 * 文件名：KHYHHISTORYService
 * 版本信息：
 * 日期：2017/11/30 18:31:34
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.entity.KhCycle;
import com.rzt.entity.XsSbYh;
import com.rzt.repository.KhYhHistoryRepository;
import com.rzt.entity.KhYhHistory;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import com.rzt.utils.ExcelUtil;
import com.rzt.utils.HanyuPinyinHelper;
import com.rzt.utils.MapUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
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

    public WebApiResponse list() {
        try {

            return WebApiResponse.success("");
        } catch (Exception e) {
            return WebApiResponse.erro("数据获取失败");
        }
    }

    @Transactional
    public WebApiResponse saveYh(XsSbYh yh, String fxtime, String startTowerName, String endTowerName, String pictureId) {
        try {
            yh.setYhfxsj(DateUtil.dateNow());
            //yh.setYhfxsj(DateUtil.parseDate(fxtime));
            yh.setId(0l);
            if (!(yh.getStartTower() == null) && yh.getStartTower().equals("")) {
                String startTower = "select longitude,latitude from cm_tower where id = ?";
                //String endTower = "select longitude,latitude from cm_tower where id = ?";
                Map<String, Object> map = execSqlSingleResult(startTower, Integer.parseInt(yh.getStartTower()));
                //Map<String, Object> map1 = execSqlSingleResult(endTower, Integer.parseInt(yh.getEndTower()));
                //经度
                if (map.get("LONGITUDE") != null) {
                    //double jd = (Double.parseDouble(map.get("LONGITUDE").toString()) + Double.parseDouble(map1.get("LONGITUDE").toString())) / 2;
                    // double wd = (Double.parseDouble(map.get("LATITUDE").toString()) + Double.parseDouble(map1.get("LATITUDE").toString())) / 2;
                    // double radius = MapUtil.GetDistance(Double.parseDouble(map.get("LONGITUDE").toString()), Double.parseDouble(map.get("LATITUDE").toString()), Double.parseDouble(map1.get("LONGITUDE").toString()), Double.parseDouble(map1.get("LATITUDE").toString())) / 2;
                    yh.setRadius("100.0");
                    yh.setJd(map.get("LONGITUDE").toString());
                    yh.setWd(map.get("LATITUDE").toString());
                }
            }
            try {
                if (yh.getVtype().equals("0")) {
                    yh.setVtype("35");
                } else if (yh.getVtype().equals("1")) {
                    yh.setVtype("110");
                } else if (yh.getVtype().equals("2")) {
                    yh.setVtype("220");
                } else if (yh.getVtype().equals("3")) {
                    yh.setVtype("550");
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
            if (!radius.contains(".0")) {
                radius = radius + ".0";
            }
            this.reposiotry.updateYh(Long.parseLong(yhId), lat, lon, radius);
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

    @Transactional
    public WebApiResponse ImportYh() {
        int i = 2;
        HanyuPinyinHelper helper = new HanyuPinyinHelper();
        try {
            FileInputStream file = new FileInputStream("E:\\826708743\\FileRecv\\隐患列表_20180111-程焕竹.xls");
            //HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
            HSSFWorkbook wb = new HSSFWorkbook(file);
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
                if (yworg.contains("供电")){
                    yworg = yworg.replace("供电","");
                }else if(yworg.contains("分")){
                    yworg = yworg.replace("分","");
                }else if(yworg.contains("工程")){
                    yworg = "工程公司";
                }
                if (sborg.contains("供电")){
                    sborg = sborg.replace("供电","");
                }else if(sborg.contains("分")){
                    sborg = sborg.replace("分","");
                }else if(yworg.contains("工程")){
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
                String startTower = ExcelUtil.getCellValue(row.getCell(6));
                String endTower = ExcelUtil.getCellValue(row.getCell(7));
                yh.setSection(startTower + "-" + endTower);//段落
                // 8涉及线路
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
                    System.out.println(i+":"+lineName + ",杆塔号:" + linename1 + ":" + startTower + ",终止杆塔：" + endTower);
                    if (startTower.startsWith("0")){
                        startTower = startTower.substring(1,startTower.length()-1);
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
                if (ExcelUtil.getCellValue(row.getCell(9)).equals("施工隐患")){
                    KhCycle cycle = new KhCycle();
                    cycle.setId();
                    yh.setTaskId(cycle.getId());
                    addKhCycle(yh,cycle);
                }
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
            if (lineName.contains("三")) {
                lineName = lineName.replace("三", "3");
            }
            if (lineName.contains("四")) {
                lineName = lineName.replace("四", "4");
            }
            linename1 = helper.toHanyuPinyin(lineName);//changqing1
            if (linename1.contains("1")) {
                linename1 = linename1.replace("1", "一");
            }
            if (linename1.contains("2")) {
                linename1 = linename1.replace("2", "二");
            }
            if (linename1.contains("3")) {
                linename1 = linename1.replace("3", "三");
            }
            if (linename1.contains("4")) {
                linename1 = linename1.replace("4", "四");
            }
        }
        if (linename1.contains("dou")) {
            linename1 = linename1.replace("dou", "du");
        }
        return linename1;
    }

    //导入隐患生成看护点的方法
    public void addKhCycle(KhYhHistory yh,KhCycle cycle) {
        try {
            String kv = yh.getVtype();
            if (kv.contains("kV")) {
                kv = kv.substring(0, kv.indexOf("k"));
            }
            cycle.setId();
            cycle.setTdywOrg(yh.getTdywOrg());
            cycle.setTdywOrgId(yh.getTdorgId());
            cycle.setWxOrg(yh.getTdwxOrg());
            cycle.setWxOrgId(yh.getWxorgId());
            cycle.setLineId(yh.getLineId());
            cycle.setYhId(yh.getId());
            cycle.setSection(yh.getSection());
            String taskName = kv + "-" + yh.getLineName()+ yh.getSection() + "号杆塔看护任务";
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
}