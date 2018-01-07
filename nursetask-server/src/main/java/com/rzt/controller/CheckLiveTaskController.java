/**    
 * 文件名：CHECKLIVETASKController
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.entity.CheckLiveTask;
import com.rzt.service.CheckLiveTaskService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 类名称：CHECKLIVETASKController
 * 类描述：看护类
 * 创建人： 李泽州
 * 创建时间：2018/01/02 15:13:15
 * 修改备注：
 * @version
 */
@RestController
@RequestMapping("CheckLiveTask")
public class CheckLiveTaskController extends CurdController<CheckLiveTask, CheckLiveTaskService> {

	protected static Logger LOGGER = LoggerFactory.getLogger(CheckLiveTaskController.class);
	@ApiOperation(value = "看护稽查任务未派发看护任务列表接口",notes = "看护稽查任务未派发看护任务列表分页查询，条件搜索")
	@GetMapping("/listKhCheckPage")
	public WebApiResponse listKhCheckPage(@RequestParam(value = "page",defaultValue = "0") Integer page,
										  @RequestParam(value = "size",defaultValue = "15") Integer size,
										 String lineId,String tddwId){
		try{
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String, Object>> list = this.service.listKhCheckPage(pageable, lineId, tddwId);
			return WebApiResponse.success(list);
		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}

	@ApiOperation(value = "看护任务详情",notes = "看护任务详情")
	@GetMapping("/khTaskDetail")
	public WebApiResponse khTaskDetail(String taskId){
		Map<String, Object> map = new HashMap<>();
		try {
			map = service.khTaskDetail(taskId);
		} catch (Exception e) {
			WebApiResponse.erro(e.getMessage());
		}
		return WebApiResponse.success(map);
	}

	@ApiOperation(value = "看护稽查任务派发",notes = "看护稽查任务派发")
	@GetMapping("/paifaKhCheckTask")
	public WebApiResponse paifaKhCheckTask(CheckLiveTask task , String username){
		try {

			this.service.paifaKhCheckTask(task,username);
			return WebApiResponse.success("任务派发成功");
		}catch (Exception e){
			return WebApiResponse.erro("任务派发失败");
		}

	}

	@ApiOperation(value = "看护稽查任务已派发看护任务列表接口",notes = "看护稽查任务已派发看护任务列表分页查询，条件搜索")
	@GetMapping("/listKhCheckTaskPage")
	public WebApiResponse listKhCheckTaskPage(@RequestParam(value = "page",defaultValue = "0") Integer page,
											  @RequestParam(value = "size",defaultValue = "15") Integer size,
											  String userId,String tddwId){
		try{
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String, Object>> list = this.service.listKhCheckTaskPage(pageable, userId, tddwId);
			return WebApiResponse.success(list);
		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}


	/**
	 * app任务列表
	 * @param page
	 * @param size
	 * @param userId
	 * @return
	 */
	@ApiOperation(value = "app任务列表",notes = "app任务列表")
	@GetMapping("/appCheckList")
	public WebApiResponse appCheckList(@RequestParam(value = "page",defaultValue = "0") Integer page,
										  @RequestParam(value = "size",defaultValue = "15") Integer size,
										  String userId,String taskType){
		try{
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String, Object>> list = this.service.appCheckList(pageable, userId,taskType);
			return WebApiResponse.success(list);
		}catch (Exception e){
			LOGGER.error("app任务列表",e);
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}

	@ApiOperation(value = "看护人员信息",notes = "看护人员信息")
	@GetMapping("/userInfo")
	public WebApiResponse userInfo(String userId){
		try{
			Map<String,Object> map = this.service.userInfo(userId);
			return WebApiResponse.success(map);
		}catch (Exception e){
			LOGGER.error("看护人员信息数据获取失败",e);
			return WebApiResponse.erro("看护人员信息数据获取失败");
		}
	}



}