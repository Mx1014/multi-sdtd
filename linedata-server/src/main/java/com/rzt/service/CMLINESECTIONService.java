/**    
 * 文件名：CMLINESECTIONService           
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.entity.CMLINESECTION;
import com.rzt.repository.CMLINESECTIONRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.ExcelUtil;
import com.rzt.utils.HanyuPinyinHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：CMLINESECTIONService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
@Service
public class CMLINESECTIONService extends CurdService<CMLINESECTION,CMLINESECTIONRepository> {

    protected static Logger LOGGER = LoggerFactory.getLogger(CMLINESECTIONService.class);

    public WebApiResponse getLineInfoByOrg(Pageable pageable, String tdOrg, String kv, String lineId) {
        List<String> list = new ArrayList<>();
        Object[] objects = list.toArray();
        String sql = "select * from cm_line_section where is_del=0 ";
        if(tdOrg!=null&&!"".equals(tdOrg.trim())){
            list.add(tdOrg);
            sql += " and td_org= ?" + list.size();
        }
        if(kv!=null&&!"".equals(kv.trim())){
            list.add(kv);
            sql += " and v_level= ?" + list.size();
        }
        if(lineId!=null&&!"".equals(lineId.trim())){
            list.add(lineId);
            sql += " and line_id= ?" + list.size();
        }
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql,list.toArray());
        return WebApiResponse.success(maps);
    }

    public WebApiResponse getLineInfoComm(String tdOrg, String kv) {
        List<String> list = new ArrayList<>();
        Object[] objects = list.toArray();
        String sql = "select * from cm_line_section where is_del=0 ";
        if(tdOrg!=null&&!"".equals(tdOrg.trim())){
            list.add(tdOrg);
            sql += " and td_org= ?" + list.size();
        }
        if(kv!=null&&!"".equals(kv.trim())){
            list.add(kv);
            sql += " and v_level= ?" + list.size();
        }
        List<Map<String, Object>> maps = execSql(sql,list.toArray());
        return WebApiResponse.success(maps);
    }

    public WebApiResponse getLineInfoCommOptions(String tdOrg, String kv) {
        List<String> list = new ArrayList<>();
        String sql = "select line_id,line_name,line_jb from cm_line_section where is_del=0 ";
        if(tdOrg!=null&&!"".equals(tdOrg.trim())){
            list.add(tdOrg);
            sql += " and td_org= ?" + list.size();
        }
        if(kv!=null&&!"".equals(kv.trim())){
            list.add(kv);
            sql += " and v_level= ?" + list.size();
        }
        sql += " ORDER BY NLSSORT(line_name,'NLS_SORT = SCHINESE_PINYIN_M')";
        List<Map<String, Object>> maps = execSql(sql,list.toArray());
        return WebApiResponse.success(maps);
    }

    public WebApiResponse getTdOrg() {
        List<Map<String, Object>> maps = execSql("select id,deptname from rztsysdepartment where deptpid=(select id from rztsysdepartment where deptname='通道运维单位') ");
        return WebApiResponse.success(maps);
    }

    @Transactional
    public void importLineSection(MultipartFile file) throws Exception {
        int i = 2;
        try{
            //读取excel文档
            HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
            HSSFSheet sheet = wb.getSheetAt(0);

            CMLINESECTION cmlinesection = new CMLINESECTION();
            HSSFRow row = sheet.getRow(i);
            while (row!=null&&!"".equals(row.toString().trim())){
                HSSFCell cell = row.getCell(0);
                if(cell==null||"".equals(ExcelUtil.getCellValue(cell))){
                    break;
                }
                cmlinesection.setId(null);
                String line_jb = ExcelUtil.getCellValue(row.getCell(1));
                cmlinesection.setLineJb(line_jb);
                String line_name = ExcelUtil.getCellValue(row.getCell(2));
                cmlinesection.setLineName(HanyuPinyinHelper.getPinyinString(line_name));
                cmlinesection.setLineName1(line_name);
                String kv = ExcelUtil.getCellValue(row.getCell(3));
                cmlinesection.setVLevel(kv);
                try{
                    Map<String, Object> result = execSqlSingleResult("select id from cm_line where line_name = ?1 and v_level = ?2", line_name,kv);
                    cmlinesection.setLineId(Long.valueOf(String.valueOf(result.get("ID"))));
                }catch (Exception e){
                    LOGGER.error("线路匹配失败！请先录入此线路信息--->"+kv+line_name);
                    //throw e;
                    row = sheet.getRow(++i);
                    continue;
                }
                String fj_tower = ExcelUtil.getCellValue(row.getCell(4));
                if(StringUtils.isNotEmpty(fj_tower)){
                    cmlinesection.setFjTower(Integer.valueOf(fj_tower));
                }
                String section = ExcelUtil.getCellValue(row.getCell(5));
                cmlinesection.setSection(section);
                if(section.contains("-")){
                    section = section.replace("--",",");
                    section = section.replace("-",",");
                    section = section.replace("J","1");
                    String[] split = section.split(",");
                    cmlinesection.setStartSort(Integer.valueOf(split[0]));
                    cmlinesection.setEndSort(Integer.valueOf(split[1]));
                }else{
                    cmlinesection.setStartSort(Integer.valueOf(section));
                    cmlinesection.setEndSort(Integer.valueOf(section));
                }

                String length = ExcelUtil.getCellValue(row.getCell(6));
                cmlinesection.setLength(length);
                String bt_ord = ExcelUtil.getCellValue(row.getCell(7));
                cmlinesection.setBtOrg(bt_ord);//设备单位(本体单位)
                String td_org = ExcelUtil.getCellValue(row.getCell(8));
                try{
                    Map<String, Object> maps = execSqlSingleResult("select ID,DEPTNAME from rztsysdepartment where deptpid=(select id from rztsysdepartment where deptname='通道运维单位')  and deptname like ?1", "%"+td_org.substring(0, 2)+"%");
                    cmlinesection.setTdOrg(String.valueOf(maps.get("ID")));
                    cmlinesection.setTdOrgName(String.valueOf(maps.get("DEPTNAME")));
                }catch (Exception e){
                    LOGGER.error("通道单位匹配失败！");
                    throw e;
                }

                //班组
                String bz = ExcelUtil.getCellValue(row.getCell(9));
                cmlinesection.setWxOrg(bz);
                add(cmlinesection);
                row = sheet.getRow(++i);
            }

        }catch(Exception e){
            LOGGER.error("----------导入第"+(i-1)+"条故障数据(excel第"+(i+1)+"行)时出错------");
            throw e;
        }

    }
}