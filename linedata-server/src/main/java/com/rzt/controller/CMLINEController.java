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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping("test")
    public WebApiResponse test(){
    		return WebApiResponse.success(service.test());
	}
	
}