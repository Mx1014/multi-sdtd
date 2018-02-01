/**    
 * 文件名：CMLINEController
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.entity.CMSETTING;
import com.rzt.service.CMSETTINGService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("CMSETTING")
public class CMSETTINGController extends
		CurdController<CMSETTING,CMSETTINGService> {


	@ApiOperation(tags = "配置保存",value = "")
	@PostMapping("saveOrUpdateSetting")
	public WebApiResponse saveOrUpdateSetting(@RequestBody ArrayList<CMSETTING> list){
		try {
			service.saveOrUpdateSetting(list);
			return WebApiResponse.success("数据保存成功");
		}catch (Exception e){
			e.printStackTrace();
			return WebApiResponse.erro("数据保存失败");
		}

	}
	@ApiOperation(tags = "配置回显",value = "")
	@GetMapping("listSetting")
	public WebApiResponse listSetting(CMSETTING cmsetting){
		try {
			List<Map<String,Object>> list = service.listSetting(cmsetting);
			return WebApiResponse.success(list);
		}catch (Exception e){
			return WebApiResponse.erro("获取数据失败");
		}

	}

	
}