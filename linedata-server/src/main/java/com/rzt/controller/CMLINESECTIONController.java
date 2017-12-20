/**    
 * 文件名：CMLINESECTIONController
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.entity.CMLINESECTION;
import com.rzt.service.CMLINESECTIONService;
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
 * 类名称：CMLINESECTIONController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("CMLINESECTION")
public class CMLINESECTIONController extends
		CurdController<CMLINESECTION,CMLINESECTIONService> {

	@ApiOperation(value = "通道单位线路维护",notes = "通道单位线路维护的分页查询，条件搜索")
	@GetMapping("getLineInfoByOrg")
    public WebApiResponse getLineInfoByOrg(@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "size",defaultValue = "15") Integer size, String tdOrg, String kv, String lineId){
		Pageable pageable = new PageRequest(page, size);
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
		Page<Map<String, Object>> maps = service.execSqlPage(pageable, sql,list.toArray());
		return WebApiResponse.success(maps);
	}

	@ApiOperation(value = "公共接口--获取属地单位线路信息",notes = "根据电压等级、通道单位id获取线路信息")
	@GetMapping("getLineInfoComm")
	public WebApiResponse getLineInfoComm(String tdOrg, String kv){
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
		List<Map<String, Object>> maps = service.execSql(sql,list.toArray());
		return WebApiResponse.success(maps);
	}

	@ApiOperation(value = "公共接口--线路下拉框",notes = "根据电压等级、通道单位id获取线路信息")
	@GetMapping("getLineInfoCommOptions")
	public WebApiResponse getLineInfoCommOptions(String tdOrg, String kv){
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
		List<Map<String, Object>> maps = service.execSql(sql,list.toArray());
		return WebApiResponse.success(maps);
	}

	@ApiOperation(value = "获取通道单位列表",notes = "获取各通道单位列表")
	@GetMapping("getTdOrg")
	public WebApiResponse getTdOrg(){
		List<Map<String, Object>> maps = service.execSql("select id,deptname from rztsysdepartment where deptpid=(select id from rztsysdepartment where deptname='通道运维单位') ");
		return WebApiResponse.success(maps);
	}

	
}