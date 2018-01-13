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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
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
		return service.getLineInfoByOrg(pageable,tdOrg,kv,lineId);
	}

	@ApiOperation(value = "公共接口--获取属地单位线路信息",notes = "根据电压等级、通道单位id获取线路信息")
	@GetMapping("getLineInfoComm")
	public WebApiResponse getLineInfoComm(String tdOrg, String kv){
    		return service.getLineInfoComm(tdOrg,kv);
	}

	@ApiOperation(value = "公共接口--线路下拉框",notes = "根据电压等级、通道单位id获取线路信息")
	@GetMapping("getLineInfoCommOptions")
	public WebApiResponse getLineInfoCommOptions(String tdOrg, String kv,String currentUserId){
		return service.getLineInfoCommOptions(tdOrg,kv,currentUserId);
	}

	@ApiOperation(value = "获取通道单位列表",notes = "获取各通道单位列表")
	@GetMapping("getTdOrg")
	public WebApiResponse getTdOrg(){
		return service.getTdOrg();
	}

	@ApiOperation(value = "线路区段导入",notes = "线路区段导入")
	@PostMapping("importLineSection")
	public Map<String,Object> importLineSection(MultipartFile file){
		HashMap<String, Object> result = new HashMap<>();
		try {
			service.importLineSection(file);
			result.put("success",true);
		} catch (Exception e) {
			result.put("success",false);
			e.printStackTrace();
		}
		return result;
	}

	@ApiOperation(value = "线路区段新增",notes = "线路区段新增")
	@PostMapping("addLineSection")
	public Map<String,Object> addLineSection(CMLINESECTION cmlinesection){
		return service.addLineSection(cmlinesection);
	}

	
}