/**    
 * 文件名：KHYHHISTORYController
 * 版本信息：    
 * 日期：2017/12/27 17:23:43    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.entity.KHYHHISTORY;
import com.rzt.service.KHYHHISTORYService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 类名称：KHYHHISTORYController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/27 17:23:43 
 * 修改人：张虎成    
 * 修改时间：2017/12/27 17:23:43    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("KHYHHISTORY")
public class KHYHHISTORYController extends
		CurdController<KHYHHISTORY,KHYHHISTORYService> {

	@ApiOperation(value = "隐患查询接口",notes="分页查询")
	@GetMapping("getYHInfo")
	public Page<Map<String, Object>> getYHInfo(@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "size",defaultValue = "15") Integer size, String tdOrg, String wxOrg, String kv, String lineId, String yhjb, String startTime, String endTime,String currentUserId){
		Pageable pageable = new PageRequest(page, size);
		return service.getYHInfo( pageable,  tdOrg,  wxOrg,  kv,  lineId, yhjb,  startTime,  endTime,currentUserId);
	}


	@ApiOperation(value = "隐患导入接口",notes = "隐患导入接口")
	@PostMapping("ImportYh")
	public WebApiResponse ImportYh(){
//		MultipartFile file
		return service.ImportYh();
	}
}