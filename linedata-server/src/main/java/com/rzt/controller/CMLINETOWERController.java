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
	public WebApiResponse getLineTowerPosition(@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "size",defaultValue = "15") Integer size, String tdOrg, String kv, String lineId,String currentUserId) {
		Pageable pageable = new PageRequest(page, size);
		return service.getLineTowerPosition(pageable, tdOrg, kv, lineId,currentUserId);
	}

	@ApiOperation(value = "杆塔坐标修改接口",notes = "杆塔坐标修改接口")
	@GetMapping("updateTowerPosition")
	public WebApiResponse updateTowerPosition( String id,String lon,String lat) {
		return service.updateTowerPosition(id,lon,lat);
	}

	@ApiOperation(value = "公共接口--下拉框线路杆塔",notes = "根据lineId获取杆塔信息")
	@GetMapping("getTowerInfoCommOptions")
	public WebApiResponse getTowerInfoCommOptions(String lineId){
		List<String> list = new ArrayList<>();
		String sql = "select tower_id,tower_name from cm_line_tower where 1=1 ";
		if(lineId!=null&&!"".equals(lineId.trim())){
			list.add(lineId);
			sql += " and line_id= ?" + list.size();
		}
		sql += " ORDER BY sort";
		List<Map<String, Object>> maps = service.execSql(sql,list.toArray());
		return WebApiResponse.success(maps);
	}

}