/**
 * 文件名：CHECKLIVETASKController
 * 版本信息：
 * 日期：2017/12/04 15:13:15
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.rzt.entity.CheckLiveTaskXs;
import com.rzt.service.CheckLiveTaskXsService;
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
 * 类描述：巡视类
 * 创建人： 李泽州
 * 创建时间：2017/12/04 15:13:15
 * 修改时间：2017/12/04 15:13:15
 * 修改备注：
 * @version
 */
@RestController
@RequestMapping("CheckLiveTaskXs")
public class CheckLiveTaskXsController extends CurdController<CheckLiveTaskXs, CheckLiveTaskXsService> {

	protected static Logger LOGGER = LoggerFactory.getLogger(CheckLiveTaskXsController.class);

	@ApiOperation(value = "巡视稽查任务派发前，巡视任务列表接口",notes = "巡视稽查任务派发前，巡视任务列表分页查询，条件搜索")
	@GetMapping("/listXsCheckPage")
	public WebApiResponse listXsCheckPage(@RequestParam(value = "page",defaultValue = "0") Integer page,
											 @RequestParam(value = "size",defaultValue = "15") Integer size,
											 String startTime, String endTime, String lineId,String tddwId,String currentUserId){
		try{
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String, Object>> list = this.service.listXsCheckPage(pageable,startTime, endTime, lineId, tddwId,currentUserId);
			return WebApiResponse.success(list);
		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}

	@ApiOperation(value = "巡视稽查任务派发前,巡视任务详情",notes = "巡视任务详情")
	@GetMapping("/xsTaskDetail")
	public WebApiResponse xsTaskDetail(String taskId){
		Map<String, Object> map = new HashMap<>();
		try {
			map = service.xsTaskDetail(taskId);
		} catch (Exception e) {
			WebApiResponse.erro(e.getMessage());
		}
		return WebApiResponse.success(map);
	}

	@ApiOperation(value = "巡视稽查任务派发",notes = "巡视稽查任务派发")
	@GetMapping("/paifaXsCheckTask")
	public WebApiResponse paifaXsCheckTask(CheckLiveTaskXs task ,String planStartTime,String planEndTime, String username){
		try {
			this.service.paifaXsCheckTask(task, planStartTime, planEndTime,username);
			return WebApiResponse.success("任务派发成功");
		}catch (Exception e){
			return WebApiResponse.erro("任务派发失败");
		}

	}

	@ApiOperation(value = "巡视稽查任务已派发任务列表接口",notes = "巡视稽查任务已派发任务列表分页查询，条件搜索")
	@GetMapping("/listXsCheckTaskPage")
	public WebApiResponse listXsCheckTaskPage(@RequestParam(value = "page",defaultValue = "0") Integer page,
										  @RequestParam(value = "size",defaultValue = "15") Integer size,
										  String startTime, String endTime, String userId,String tddwId,String currentUserId,String status){
		try{
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String, Object>> list = this.service.listXsCheckTaskPage(pageable,startTime, endTime, userId, tddwId,currentUserId,status);
			return WebApiResponse.success(list);
		}catch (Exception e){
			LOGGER.error("巡视稽查任务已派发任务列表接口",e);
			return WebApiResponse.erro("数据获取失败");
		}
	}

	@ApiOperation(value = "修改已派发巡视任务稽查人",notes = "修改已派发巡视任务稽查人")
	@GetMapping("/updateKhCheckUser")
	public WebApiResponse updateXsCheckUser(String id,String userId,String userName){
		try{
			this.service.updateXsCheckUser(Long.valueOf(id),userId,userName);
			return WebApiResponse.success("数据更新成功");
		}catch (Exception e){
			return WebApiResponse.erro("数据更新失败"+e.getMessage());
		}
	}

/*	@ApiOperation(value = "巡视稽查任务已派发巡视稽查任务详情",notes = "巡视稽查任务已派发看护任务详情")
	@GetMapping("/listXsCheckTaskDetail")
	public WebApiResponse listXsCheckTaskDetail(String id){
		try{
			Map<String, Object> map = this.service.listXsCheckTask(Long.valueOf(id));
			return WebApiResponse.success(map);
		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}*/


}