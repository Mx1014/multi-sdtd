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
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
		return service.getGuZhang(pageable,tdOrg, kv, lineId, startTime, endTime);

	}


	@ApiOperation(value = "台账故障导入接口",notes = "搜索、分页获取台账故障信息")
	@PostMapping("ImportGuZhang")
	public Map<String,Object> ImportGuZhang(MultipartFile file, String flag){
		return service.importGuZhang(file,flag);
	}

	
}