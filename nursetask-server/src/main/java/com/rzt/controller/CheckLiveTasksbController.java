/**    
 * 文件名：CheckLiveTasksbController
 * 版本信息：    
 * 日期：2018/01/21 08:27:36    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.entity.CheckLiveTasksb;
import com.rzt.service.CheckLiveTasksbService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 类名称：CheckLiveTasksbController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2018/01/21 08:27:36 
 * 修改人：张虎成    
 * 修改时间：2018/01/21 08:27:36    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("CheckLiveTasksb")
public class CheckLiveTasksbController extends
		CurdController<CheckLiveTasksb,CheckLiveTasksbService> {

	@ApiOperation(value = "生成待稽查任务",notes = "生成待稽查任务")
	@PostMapping("addCheckLiveTasksb")
	public WebApiResponse addCheckLiveTasksb(CheckLiveTasksb object) {
		try {
			service.addCheckLiveTasksb(object);
			return WebApiResponse.success("数据保存成功!");
		} catch (Exception var3) {
			return WebApiResponse.erro("数据保存失败" + var3.getMessage());
		}
	}

	@ApiOperation(value = "隐患待稽查任务",notes = "隐患待稽查任务")
	@GetMapping("pageCheckLiveTasksb")
	public WebApiResponse pageCheckLiveTasksb(@RequestParam(value = "page",defaultValue = "0") Integer page,
														@RequestParam(value = "size",defaultValue = "8") Integer size,
														String lineId,String tddwId,String currentUserId) {
		try {
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String, Object>> list = this.service.pageCheckLiveTasksb(pageable, lineId, tddwId,currentUserId);
			return WebApiResponse.success(list);
		} catch (Exception var3) {
			return WebApiResponse.erro("数据获取失败" + var3.getMessage());
		}
	}
	@ApiOperation(value = "隐患稽查任务派发",notes = "隐患稽查任务派发")
	@PostMapping("sendCheckLiveTasksb")
	public WebApiResponse sendCheckLiveTasksb(String planStartTime ,String planEndTime,String userId,String id) {
		try {
			this.service.sendCheckLiveTasksb(id,userId,planStartTime,planEndTime);
			return WebApiResponse.success("派发成功");
		} catch (Exception var3) {
			return WebApiResponse.erro("数据获取失败" + var3.getMessage());
		}
	}


	@ApiOperation(value = "隐患已派发稽查任务",notes = "隐患已派发稽查任务")
	@GetMapping("pageCheckLiveTasksbDone")
	public WebApiResponse pageCheckLiveTasksbDone(@RequestParam(value = "page",defaultValue = "0") Integer page,
											  @RequestParam(value = "size",defaultValue = "8") Integer size,
											  String lineId,String tddwId,String currentUserId) {
		try {
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String, Object>> list = this.service.pageCheckLiveTasksbDone(pageable, lineId, tddwId,currentUserId);
			return WebApiResponse.success(list);
		} catch (Exception var3) {
			return WebApiResponse.erro("数据获取失败" + var3.getMessage());
		}
	}
}