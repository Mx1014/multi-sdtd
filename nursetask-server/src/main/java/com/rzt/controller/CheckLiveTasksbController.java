/**    
 * 文件名：CheckLiveTasksbController
 * 版本信息：    
 * 日期：2018/01/21 08:27:36    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.config.MonitorFeign;
import com.rzt.entity.CheckLiveTasksb;
import com.rzt.entity.XsSbYh;
import com.rzt.service.CheckLiveTasksbService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

	@Autowired
	private MonitorFeign monitorFeign;

	protected static Logger LOGGER = LoggerFactory.getLogger(CheckLiveTasksbController.class);

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
	@GetMapping("sendCheckLiveTasksb")
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

	//以上是隐患稽查pc的接口
	//以下是隐患稽查app的接口
	@ApiOperation(value = "app隐患稽查任务列表",notes = "app隐患稽查任务列表")
	@GetMapping("pageCheckLiveTasksbApp")
	public WebApiResponse pageCheckLiveTasksbApp(@RequestParam(value = "page",defaultValue = "0") Integer page,
												 @RequestParam(value = "size",defaultValue = "8") Integer size,
												 String userId,String taskType){
		try{
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String, Object>> list = this.service.appChecksbList(pageable, userId,taskType);
			return WebApiResponse.success(list);
		}catch (Exception e){
			LOGGER.error("app任务列表获取失败",e);
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}

	@ApiOperation(value = "app隐患稽查详情",notes = "app隐患稽查详情")
	@GetMapping("checkLiveTasksbDetail")
	public WebApiResponse checkLiveTasksbDetail(String id){
		try{
			Map<String, Object> map = this.service.checkLiveTasksbDetail(Long.valueOf(id));
			return WebApiResponse.success(map);
		}catch (Exception e){
			LOGGER.error("app任务详情获取失败",e);
			return WebApiResponse.erro("app任务详情获取失败"+e.getMessage());
		}
	}

	@ApiOperation(value = "app隐患稽查接单",notes = "app隐患稽查接单")
	@GetMapping("checkLiveTasksbStart")
	public WebApiResponse checkLiveTasksbStart(String id){
		try{
			this.service.checkLiveTasksbStart(Long.valueOf(id));
			return WebApiResponse.success("app任务接单成功");
		}catch (Exception e){
			LOGGER.error("app任务接单失败",e);
			return WebApiResponse.erro("app任务接单失败"+e.getMessage());
		}
	}

	@ApiOperation(value = "app隐患稽查任务完成按钮",notes = "app隐患稽查任务完成按钮")
	@PostMapping("checkLiveTasksbComplete")
	public WebApiResponse checkLiveTasksbComplete(XsSbYh yh,String activityId,String flag){
		//taskId是check_live_tasksb的id
		try{
			service.checkLiveTasksbComplete(yh);
			monitorFeign.jicha(activityId,yh.getId().toString(),flag);
			return WebApiResponse.success("");
		}catch (Exception e){
			LOGGER.error("app任务列表获取失败",e);
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}

	@ApiOperation(value = "三级联动,区县镇",notes = "三级联动,区县镇")
	@GetMapping("areas")
	public WebApiResponse areas(){
		try{
			List<Map<String, Object>> list = service.areas();
			return WebApiResponse.success(list);
		}catch (Exception e){
			LOGGER.error("三级联动,区县镇数据获取失败",e);
			return WebApiResponse.erro("三级联动,区县镇数据获取失败"+e.getMessage());
		}
	}





}