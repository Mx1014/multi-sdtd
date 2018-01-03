/**    
 * 文件名：GUZHANGService           
 * 版本信息：    
 * 日期：2017/12/13 14:37:30    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.entity.GUZHANG;
import com.rzt.repository.GUZHANGRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import com.rzt.utils.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：GUZHANGService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/13 14:37:30 
 * 修改人：张虎成    
 * 修改时间：2017/12/13 14:37:30    
 * 修改备注：    
 * @version        
 */
@Service
public class GUZHANGService extends CurdService<GUZHANG,GUZHANGRepository> {

    protected static Logger LOGGER = LoggerFactory.getLogger(GUZHANGService.class);

    public WebApiResponse getGuZhang(Pageable pageable, String tdOrg, String kv, String lineId, String startTime, String endTime) {
        List<String> list = new ArrayList<>();
        String sql = "select id,create_time,v_level,line_name,gz_tower,gz_reason,gz_reason1,sb_org,td_org,description,is_reout,msg_time,result_time,pms,sgxz from guzhang where 1=1";
        if(tdOrg!=null&&!"".equals(tdOrg.trim())){
            list.add(tdOrg);
            sql += " and td_org_id= ?" + list.size();
        }
        if(kv!=null&&!"".equals(kv.trim())){
            list.add(kv);
            sql += " and v_level= ?" + list.size();
        }
        if(lineId!=null&&!"".equals(lineId.trim())){
            list.add(lineId);
            sql += " and line_id= ?" + list.size();
        }
        if(startTime!=null&&!"".equals(startTime.trim())){
            list.add(startTime);
            sql += " and create_time > ?" + list.size();
        }
        if(endTime!=null&&!"".equals(endTime.trim())){
            list.add(endTime);
            sql += " and create_time < ?" + list.size();
        }
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql,list.toArray());
        return WebApiResponse.success(maps);
    }

    public WebApiResponse importGuZhang(MultipartFile file) {
        int i = 1;
        try{
            //读取excel文档
            HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
            HSSFSheet sheet = wb.getSheetAt(0);

            System.out.println(sheet.getLastRowNum());
            GUZHANG guzhang = new GUZHANG();
            HSSFRow row = sheet.getRow(i);
            while (row!=null&&!"".equals(row.toString().trim())){
                HSSFCell cell = row.getCell(0);
                if(cell==null||"".equals(ExcelUtil.getCellValue(cell))){
                    break;
                }
                System.out.println(i);
                guzhang.setId(null);
                String month = ExcelUtil.getCellValue(row.getCell(1));
                guzhang.setMonth(month);
                String createDate = ExcelUtil.getCellValue(row.getCell(2));
                Date date = DateUtil.parse(createDate,"yyyy-MM-dd");
                guzhang.setCreateData(date);
                String creteTime = ExcelUtil.getCellValue(row.getCell(3)).replace(";",":");
                guzhang.setCreateTime(DateUtil.format(date,"yyyy-MM-dd")+" "+creteTime);
                String kv = ExcelUtil.getCellValue(row.getCell(4));
                guzhang.setVLevel(kv+"kV");
                String lineName = ExcelUtil.getCellValue(row.getCell(5));
                guzhang.setLineName(lineName);
                try{
                    Map<String, Object> result = execSqlSingleResult("select id from cm_line where line_name like ?1", "%" + lineName + "%");
                    guzhang.setLineId(Long.valueOf(String.valueOf(result.get("ID"))));
                }catch (Exception e){
                    LOGGER.error("线路匹配失败！",e);
                }

                String sbOrg = ExcelUtil.getCellValue(row.getCell(6));
                guzhang.setSbOrg(sbOrg);
                String tdOrg = ExcelUtil.getCellValue(row.getCell(7));
                guzhang.setTdOrg(tdOrg);

                try{
                    Map<String, Object> maps = execSqlSingleResult("select id from rztsysdepartment where deptpid=(select id from rztsysdepartment where deptname='通道运维单位')  and deptname like ?1", tdOrg.substring(0, 2));
                    guzhang.setTdOrgId(String.valueOf(maps.get("ID")));
                }catch (Exception e){
                    LOGGER.error("通道单位匹配失败！",e);
                }

                String gzReason = ExcelUtil.getCellValue(row.getCell(8));
                guzhang.setGzReason(gzReason);
                String gzReason1 = ExcelUtil.getCellValue(row.getCell(9));
                guzhang.setGzReason1(gzReason1);
                String description = ExcelUtil.getCellValue(row.getCell(10));
                guzhang.setDescription(description);
                String isReout = ExcelUtil.getCellValue(row.getCell(11));
                guzhang.setIsReout(isReout);
                String remark1 = ExcelUtil.getCellValue(row.getCell(12));
                guzhang.setRemark1(remark1);
                String remark2 = ExcelUtil.getCellValue(row.getCell(13));
                guzhang.setRemark2(remark2);
                String kkx = ExcelUtil.getCellValue(row.getCell(14));
                guzhang.setKkx(Integer.valueOf(0+kkx));
                String rangeTower = ExcelUtil.getCellValue(row.getCell(15));
                guzhang.setRangeTower(rangeTower);
                String gzTower = ExcelUtil.getCellValue(row.getCell(16));
                guzhang.setGzTower(gzTower);
                String msgTime = ExcelUtil.getCellValue(row.getCell(17));
                guzhang.setMsgTime(msgTime);
                String resultTime = ExcelUtil.getCellValue(row.getCell(18));
                guzhang.setResultTime(resultTime);
                String quickTime = ExcelUtil.getCellValue(row.getCell(19));
                guzhang.setQuickTime(quickTime);
                String reportTime = ExcelUtil.getCellValue(row.getCell(20));
                guzhang.setReportTime(reportTime);
                String reportZl = ExcelUtil.getCellValue(row.getCell(21));
                guzhang.setReportZl(reportZl);
                String pms = ExcelUtil.getCellValue(row.getCell(22));
                guzhang.setPms(pms);
                String xsIsLate = ExcelUtil.getCellValue(row.getCell(23));
                guzhang.setXsIsLate(xsIsLate);
                String thunder = ExcelUtil.getCellValue(row.getCell(24));
                guzhang.setThunder(thunder);
                String matter = ExcelUtil.getCellValue(row.getCell(25));
                guzhang.setMatter(matter);
                String sgxz = ExcelUtil.getCellValue(row.getCell(26));
                guzhang.setSgxz(sgxz);
                String isZs = ExcelUtil.getCellValue(row.getCell(27));
                guzhang.setIsZs(isZs);
                String zsMoney = ExcelUtil.getCellValue(row.getCell(28));
                guzhang.setZsMoney(Integer.valueOf(0+zsMoney));
                add(guzhang);
                row = sheet.getRow(++i);
            }

        }catch(Exception e){
            LOGGER.error("----------导入第"+i+"条故障数据时出错------",e);
        }
        LOGGER.info("故障导入成功！");

        return WebApiResponse.success(null);
    }
}