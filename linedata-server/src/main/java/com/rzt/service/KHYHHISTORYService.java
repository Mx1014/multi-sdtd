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
        String sql = "select * from KH_YH_HISTORY WHERE 1=1 ";
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
            list.add(yhjb);
            sql += " and yhjb= ?" + list.size();
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
        return execSqlPage(pageable, sql, list.toArray());
    }

    @Transactional
    public WebApiResponse ImportYh() {
        int i = 2;
        HanyuPinyinHelper helper = new HanyuPinyinHelper();
        try {
            FileInputStream file = new FileInputStream("F:\\隐患列表.xls");
            //HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
            HSSFWorkbook wb = new HSSFWorkbook(file);
            HSSFSheet sheet = wb.getSheetAt(0);
            KHYHHISTORY yh = new KHYHHISTORY();
            HSSFRow row = sheet.getRow(i);
            while (row != null && !"".equals(row.toString().trim())) {
                HSSFCell cell = row.getCell(0);
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
                String lineName = ExcelUtil.getCellValue(row.getCell(3));
                yh.setLineName(helper.toHanyuPinyin(lineName));//线路名称
                try {
                    List<Map<String, Object>> list = execSql("select a.id from cm_line a,CM_LINE_SECTION b where b.line_name1  like ?1 and a.id = b.LINE_ID GROUP BY a.id", "%" + lineName + "%");
                    yh.setLineId(Long.valueOf(String.valueOf(list.get(0).get("ID"))));
                } catch (Exception e) {
                    e.printStackTrace();
                    //LOGGER.error("线路匹配失败！",e);
                }
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
                yh.setYhjb(ExcelUtil.getCellValue(row.getCell(25)));//危急程度
                yh.setGkcs(ExcelUtil.getCellValue(row.getCell(26)));//管控措施
                yh.setZpxgsj(DateUtil.parse(ExcelUtil.getCellValue(row.getCell(27))));//照片修改时间
                yh.setYhzt(0);//隐患状态
                yh.setRadius("100.0");
                yh.setSdgs(2);//execl导入
                yh.setSfdj(1);//已定级
                yh.setCreateTime(new Date());
                this.add(yh);
                row = sheet.getRow(++i);
                System.out.println(i+lineName+"--");
                String sql = "select t.id from cm_line_tower t where t.line_name like ? and t.tower_name like?";
                try {
                    List<Map<String, Object>> list = this.execSql(sql, "%" + yh.getLineName() + "%", "%" + startTower + "%");
                    List<Map<String, Object>> list1 = this.execSql(sql, "%" + yh.getLineName() + "%", "%" + endTower + "%");
                }catch (Exception e){
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