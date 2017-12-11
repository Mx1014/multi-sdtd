/**    
 * 文件名：CMLINETOWERController
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.CMLINETOWER;
import com.rzt.service.CMLINETOWERService;
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
 * 类名称：CMLINETOWERController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("CMLINETOWER")
public class CMLINETOWERController extends
		CurdController<CMLINETOWER,CMLINETOWERService> {

	@ApiOperation(value = "杆塔坐标维护接口",notes = "获取杆塔坐标，分页查询")
	@GetMapping("getLineTowerPosition")
	public WebApiResponse getLineTowerPosition(@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "size",defaultValue = "15") Integer size, String tdOrg, String kv, String lineId) {
		Pageable pageable = new PageRequest(page, size);
		List<String> list = new ArrayList<>();
		Object[] objects = list.toArray();
		String sql = "select l.v_level,l.line_name,l.section,LT.tower_name,t.longitude,t.latitude from cm_line l,cm_line_tower lt,cm_tower t " +
				"where l.id=LT.line_id and LT.tower_id=t.id";
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