/**    
 * 文件名：CMTOWERTTBJController
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.CMTOWERTTBJ;
import com.rzt.service.CMTOWERTTBJService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类名称：CMTOWERTTBJController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("CMTOWERTTBJ")
public class CMTOWERTTBJController extends
		CurdController<CMTOWERTTBJ,CMTOWERTTBJService> {

	@ApiOperation(value = "获取同塔并架线路信息接口",notes = "获取同塔并架线路信息接口,分页搜索")
	@GetMapping("getTtbjLine")
	public WebApiResponse getTtbjLine(@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "size",defaultValue = "15") Integer size, String tdOrg, String kv, String lineId) {
		Pageable pageable = new PageRequest(page, size);
		List<String> list = new ArrayList<>();
		Object[] objects = list.toArray();
		String sql = "select * from cm_tower_ttbj";
		if(tdOrg!=null&&!"".equals(tdOrg.trim())){
			list.add(tdOrg);
			sql += " and l.td_org= ?" + list.size();
		}
		if(kv!=null&&!"".equals(kv.trim())){
			list.add(kv);
			sql += " and v_level= ?" + list.size();
		}
		if(lineId!=null&&!"".equals(lineId.trim())){
			list.add(lineId);
			sql += " and line_id= ?" + list.size();
		}
		sql += " order by lt.sort";
		Page<Map<String, Object>> maps = service.execSqlPage(pageable, sql,list.toArray());
		return WebApiResponse.success(maps);
	}
}