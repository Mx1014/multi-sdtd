/**    
 * 文件名：KHYHHISTORYController
 * 版本信息：    
 * 日期：2017/11/30 18:31:34    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.KhTask;
import com.rzt.entity.KhYhHistory;
import com.rzt.service.KhYhHistoryService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名称：KHYHHISTORYController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/30 18:31:34 
 * 修改人：张虎成    
 * 修改时间：2017/11/30 18:31:34    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("kyYhHistory")
public class KhYhHistoryController extends
		CurdController<KhYhHistory, KhYhHistoryService> {

	@ApiOperation(notes = "今日治理、新增、修改隐患数量",value = "今日治理、新增、修改隐患数量")
	@GetMapping("/listYhCount")
	public WebApiResponse listYhCount(){
		return this.service.listYhCount();
	}
	@ApiOperation(notes = "今日治理、新增、修改隐患数量",value = "今日治理、新增、修改隐患数量")
	@GetMapping("/listSgCount")
	public WebApiResponse listSgCount(){
		return this.service.listSgCount();
	}


	/*@ApiOperation(notes = "今日调整隐患数量",value = "今日调整隐患数量")
	@GetMapping("/listUpdateYhCount")
	public WebApiResponse listUpdateYhCount(){
		return this.service.listUpdateYhCount();
	}*/
}