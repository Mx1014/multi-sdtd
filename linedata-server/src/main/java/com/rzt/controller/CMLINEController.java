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
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
	public WebApiResponse getLineInfo(String kv){
		ArrayList<String> params = new ArrayList<>();
		String sql = "select * from cm_line where is_del=0 " ;
		if(kv!=null&&!"".equals(kv)){
			params.add(kv);
			sql += " and v_level = ?" + params.size();
		}
		sql += " ORDER BY NLSSORT(line_name,'NLS_SORT = SCHINESE_PINYIN_M')";
		List<Map<String, Object>> maps = service.execSql(sql,params.toArray());
		return WebApiResponse.success(maps);
	}
	
}