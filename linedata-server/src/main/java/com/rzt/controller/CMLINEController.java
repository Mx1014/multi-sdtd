/**    
 * 文件名：CMLINEController
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.entity.CMLINE;
import com.rzt.service.CMLINEService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.HanyuPinyinHelper;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 类名称：CMLINEController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("CMLINE")
public class CMLINEController extends
		CurdController<CMLINE,CMLINEService> {

	@ApiOperation(value = "获取线路名称列表",notes = "获取线路名称列表")
	@GetMapping("getLineInfo")
	public WebApiResponse getLineInfo(String kv,String currentUserId){
		return service.getLineInfo(kv,currentUserId);
	}

	@ApiOperation(value = "获取线路名称",notes = "获取线路名称")
	@GetMapping("getLines")
	public WebApiResponse getLines(String kv,String currentUserId){
		return service.getLines(kv,currentUserId);
	}

	@ApiOperation(tags = "线路名转拼音",value = "")
	@GetMapping("test")
	public void test(){

		List<Map<String, Object>> list = service.execSql("select id,line_name1 from cm_line_section");
		for (Map map:list) {
			Long id = Long.valueOf(map.get("ID").toString());
			String linename = HanyuPinyinHelper.getPinyinString(String.valueOf(map.get("LINE_NAME1")));
			service.reposiotry.updateLineName(id,linename);
		}
	}

	
}