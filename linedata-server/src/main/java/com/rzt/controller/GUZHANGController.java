/**    
 * 文件名：GUZHANGController
 * 版本信息：    
 * 日期：2017/12/13 14:37:30    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.entity.GUZHANG;
import com.rzt.service.GUZHANGService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类名称：GUZHANGController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/13 14:37:30 
 * 修改人：张虎成    
 * 修改时间：2017/12/13 14:37:30    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("GUZHANG")
public class GUZHANGController extends
		CurdController<GUZHANG,GUZHANGService> {

	protected static Logger LOGGER = LoggerFactory.getLogger(GUZHANGController.class);

	@ApiOperation(value = "台账故障接口",notes = "搜索、分页获取台账故障信息")
	@GetMapping("getGuZhang")
    public WebApiResponse getGuZhang(@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "size",defaultValue = "15") Integer size, String tdOrg, String kv, String lineId,String startTime,String endTime){
		Pageable pageable = new PageRequest(page, size);
		List<String> list = new ArrayList<>();
		Object[] objects = list.toArray();
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
		Page<Map<String, Object>> maps = service.execSqlPage(pageable, sql,list.toArray());
		return WebApiResponse.success(maps);
	}


	@ApiOperation(value = "台账故障导入接口",notes = "搜索、分页获取台账故障信息")
	@PostMapping("ImportGuZhang")
	public WebApiResponse ImportGuZhang(MultipartFile file){
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
				if(cell==null||"".equals(getCellValue(cell))){
					break;
				}
				System.out.println(i);
				guzhang.setId(null);
				String month = getCellValue(row.getCell(1));
				guzhang.setMonth(month);
				String createDate = getCellValue(row.getCell(2));
				Date date = DateUtil.parse(createDate,"yyyy-MM-dd");
				guzhang.setCreateData(date);
				String creteTime = getCellValue(row.getCell(3)).replace(";",":");
				guzhang.setCreateTime(DateUtil.format(date,"yyyy-MM-dd")+" "+creteTime);
				String kv = getCellValue(row.getCell(4));
				guzhang.setVLevel(kv+"kV");
				String lineName = getCellValue(row.getCell(5));
				guzhang.setLineName(lineName);
				try{
					Map<String, Object> result = service.execSqlSingleResult("select id from cm_line where line_name like ?1", "%" + lineName + "%");
					guzhang.setLineId(Long.valueOf(String.valueOf(result.get("ID"))));
				}catch (Exception e){
					LOGGER.error("线路匹配失败！",e);
				}

				String sbOrg = getCellValue(row.getCell(6));
				guzhang.setSbOrg(sbOrg);
				String tdOrg = getCellValue(row.getCell(7));
				guzhang.setTdOrg(tdOrg);

				try{
					Map<String, Object> maps = service.execSqlSingleResult("select id from rztsysdepartment where deptpid=(select id from rztsysdepartment where deptname='通道运维单位')  and deptname like ?1", tdOrg.substring(0, 2));
					guzhang.setTdOrgId(String.valueOf(maps.get("ID")));
				}catch (Exception e){
					LOGGER.error("通道单位匹配失败！",e);
				}

				String gzReason = getCellValue(row.getCell(8));
				guzhang.setGzReason(gzReason);
				String gzReason1 = getCellValue(row.getCell(9));
				guzhang.setGzReason1(gzReason1);
				String description = getCellValue(row.getCell(10));
				guzhang.setDescription(description);
				String isReout = getCellValue(row.getCell(11));
				guzhang.setIsReout(isReout);
				String remark1 = getCellValue(row.getCell(12));
				guzhang.setRemark1(remark1);
				String remark2 = getCellValue(row.getCell(13));
				guzhang.setRemark2(remark2);
				String kkx = getCellValue(row.getCell(14));
				guzhang.setKkx(Integer.valueOf(0+kkx));
				String rangeTower = getCellValue(row.getCell(15));
				guzhang.setRangeTower(rangeTower);
				String gzTower = getCellValue(row.getCell(16));
				guzhang.setGzTower(gzTower);
				String msgTime = getCellValue(row.getCell(17));
				guzhang.setMsgTime(msgTime);
				String resultTime = getCellValue(row.getCell(18));
				guzhang.setResultTime(resultTime);
				String quickTime = getCellValue(row.getCell(19));
				guzhang.setQuickTime(quickTime);
				String reportTime = getCellValue(row.getCell(20));
				guzhang.setReportTime(reportTime);
				String reportZl = getCellValue(row.getCell(21));
				guzhang.setReportZl(reportZl);
				String pms = getCellValue(row.getCell(22));
				guzhang.setPms(pms);
				String xsIsLate = getCellValue(row.getCell(23));
				guzhang.setXsIsLate(xsIsLate);
				String thunder = getCellValue(row.getCell(24));
				guzhang.setThunder(thunder);
				String matter = getCellValue(row.getCell(25));
				guzhang.setMatter(matter);
				String sgxz = getCellValue(row.getCell(26));
				guzhang.setSgxz(sgxz);
				String isZs = getCellValue(row.getCell(27));
				guzhang.setIsZs(isZs);
				String zsMoney = getCellValue(row.getCell(28));
				guzhang.setZsMoney(Integer.valueOf(0+zsMoney));
				service.add(guzhang);
				row = sheet.getRow(++i);
			}

		}catch(Exception e){
			LOGGER.error("----------导入第"+i+"条故障数据时出错------",e);
		}
		LOGGER.info("故障导入成功！");

    		return WebApiResponse.success(null);
	}


	private String getCellValue(HSSFCell cell) {
		if(cell==null){
			return "";
		}
		String cellValue = "";
		switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_STRING:
				cellValue = cell.getRichStringCellValue().getString().trim();
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:// 数字类型
				if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
					SimpleDateFormat sdf = null;
					if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
						sdf = new SimpleDateFormat("HH:mm");
					} else {// 日期
						sdf = new SimpleDateFormat("yyyy-MM-dd");
					}
					Date date = cell.getDateCellValue();
					cellValue = sdf.format(date);
				} else if (cell.getCellStyle().getDataFormat() == 58) {
					// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					double value = cell.getNumericCellValue();
					Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
					cellValue = sdf.format(date);
				} else {
					double value = cell.getNumericCellValue();
					CellStyle style = cell.getCellStyle();
					DecimalFormat format = new DecimalFormat();
					String temp = style.getDataFormatString();
					// 单元格设置成常规
					if (temp.equals("General")) {
						format.applyPattern("#");
					}
					cellValue = format.format(value);
				}
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				cellValue = String.valueOf(cell.getBooleanCellValue()).trim();
				break;
			case HSSFCell.CELL_TYPE_FORMULA:
				cellValue = cell.getCellFormula();
				break;
			default:
				cellValue = "";
		}
		return cellValue;
	}
	
}