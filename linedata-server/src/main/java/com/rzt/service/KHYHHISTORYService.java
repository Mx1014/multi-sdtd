/**
 * 文件名：KHYHHISTORYService
 * 版本信息：
 * 日期：2017/12/27 17:23:43
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.alibaba.fastjson.JSON;
import com.rzt.entity.KHYHHISTORY;
import com.rzt.repository.KHYHHISTORYRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import com.rzt.utils.ExcelUtil;
import com.rzt.utils.HanyuPinyinHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类名称：KHYHHISTORYService
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/27 17:23:43
 * 修改人：张虎成
 * 修改时间：2017/12/27 17:23:43
 * 修改备注：
 */
@Service
public class KHYHHISTORYService extends CurdService<KHYHHISTORY, KHYHHISTORYRepository> {

    protected static Logger LOGGER = LoggerFactory.getLogger(KHYHHISTORYService.class);
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Page<Map<String, Object>> getYHInfo(Pageable pageable, String tdOrg, String wxOrg, String kv, String lineId, String yhjb, String startTime, String endTime,String currentUserId) {
        List<Object> list = new ArrayList<>();
        String sql = "select * from KH_YH_HISTORY WHERE yhzt=0 ";
        if(StringUtils.isNotEmpty(currentUserId)){
            Map<String, Object> map = userInfoFromRedis(currentUserId);
            Integer roletype = Integer.parseInt(map.get("ROLETYPE").toString());
            String deptid  = map.get("DEPTID").toString();
            switch (roletype) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    tdOrg = deptid;
                    break;
                case 3:
                    //外协角色
                    break;
                case 4:
                    //班组角色
                    break;
                case 5:
                    //个人角色
                    break;
            }

        }
        if (tdOrg != null && !"".equals(tdOrg.trim())) {
            list.add(tdOrg);
            sql += " and yworg_id= ?" + list.size();
        }
        if (wxOrg != null && !"".equals(wxOrg.trim())) {
            list.add(wxOrg);
            sql += " and wxorg_id= ?" + list.size();
        }
        if (kv != null && !"".equals(kv.trim())) {
            list.add(kv);
            sql += " and vtype= ?" + list.size();
        }
        if (lineId != null && !"".equals(lineId.trim())) {
            list.add(lineId);
            sql += " and line_id= ?" + list.size();
        }
        if (yhjb != null && !"".equals(yhjb.trim())) {
            list.add("%"+yhjb+"%");
            sql += " and yhjb1 like ?" + list.size();
        }
        if (startTime != null && !"".equals(startTime.trim())) {
            Date date = DateUtil.parse(startTime + ":00", "yyyy-MM-dd HH:mm:ss");
            list.add(date);
            sql += " and YHFXSJ > ?" + list.size();
        }
        if (endTime != null && !"".equals(endTime.trim())) {
            Date date = DateUtil.parse(endTime + ":00", "yyyy-MM-dd HH:mm:ss");
            list.add(date);
            sql += " and YHFXSJ < ?" + list.size();
        }
        sql +=" order by create_time desc";
        return execSqlPage(pageable, sql, list.toArray());
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
            KHYHHISTORY yh = new KHYHHISTORY();
            HSSFRow row = sheet.getRow(i);
            while (row != null && !"".equals(row.toString().trim())) {
                HSSFCell cell = row.getCell(1);
                if (cell == null || "".equals(ExcelUtil.getCellValue(cell))) {
                    break;
                }
                yh.setId(null);
                yh.setTdywOrg(ExcelUtil.getCellValue(row.getCell(1))); //通道维护单位
                yh.setSbywOrg(ExcelUtil.getCellValue(row.getCell(2)));//设备维护单位
                try {
                    List<Map<String, Object>> list = execSql("select id from rztsysdepartment where deptpid=(select id from rztsysdepartment where deptname='通道运维单位')  and deptname like ?1", "%" + yh.getTdywOrg().substring(0, 2) + "%");
                    yh.setTdorgId(String.valueOf(list.get(0).get("ID")));
                } catch (Exception e) {
                    e.printStackTrace();
                    //LOGGER.error("通道单位匹配失败！",e);
                }
                //长青一
                String lineName = ExcelUtil.getCellValue(row.getCell(3));
                String linename1 = "";
                try {
                    List<Map<String, Object>> list = execSql("select a.id,b.line_jb as jb from cm_line a left join CM_LINE_SECTION b on a.id = b.LINE_ID where b.line_name1  like ?1 ", "%" + lineName + "%");
                    yh.setLineId(Long.valueOf(String.valueOf(list.get(0).get("ID"))));
                    yh.setXlzycd(String.valueOf(list.get(0).get("JB")));
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    //LOGGER.error("线路匹配失败！",e);
                }
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
                yh.setYhfxsj(DateUtil.parse(ExcelUtil.getCellValue(row.getCell(12))));//隐患发现时间
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
                if (wjcd !=null &&! wjcd.equals("")){
                    yh.setYhjb(wjcd);
                }else {
                    yh.setYhjb("一般");
                }
                yh.setGkcs(ExcelUtil.getCellValue(row.getCell(26)));//管控措施
                //yh.setZpxgsj(DateUtil.parse(ExcelUtil.getCellValue(row.getCell(27))));//照片修改时间
                yh.setYhzt(0);//隐患状态
                yh.setRadius("100.0");
                yh.setSdgs(2);//execl导入
                yh.setSfdj(1);//已定级
                yh.setCreateTime(DateUtil.parse(ExcelUtil.getCellValue(row.getCell(12))));
                this.add(yh);
                row = sheet.getRow(++i);
                String sql = "select t.tower_Id id from cm_line_tower t where t.line_name like ? and t.tower_name like ?";
                try {
                    System.out.println(lineName + ",杆塔号:" +linename1+":"+ startTower + ",终止杆塔：" + endTower);
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
                /*
                yh.setStartTower();
                yh.setEndTower();*/
            }
            return WebApiResponse.success("导入成功");
        } catch (Exception e) {
            return WebApiResponse.erro("导入失败" + e.getMessage());
        }
    }


    public Map<String, Object> userInfoFromRedis(String userId) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();

        Map<String,Object> jsonObject = null;
        Object userInformation = hashOperations.get("UserInformation", userId);
        if(userInformation == null) {
            String sql = "select * from userinfo where id = ?";
            try {
                jsonObject = this.execSqlSingleResult(sql, userId);
            } catch (Exception e) {
                LOGGER.error("currentUserId未获取到唯一数据!",e);
            }
            hashOperations.put("UserInformation",userId,jsonObject);
        } else {
            jsonObject = JSON.parseObject(userInformation.toString(),Map.class);
        }
        return jsonObject;
    }
}