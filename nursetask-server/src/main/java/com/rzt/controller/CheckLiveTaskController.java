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
import java.util.List;
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
										  @RequestParam(value = "size",defaultValue = "8") Integer size,
										 String lineId,String tddwId,String currentUserId){
		try{
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String, Object>> list = this.service.listKhCheckPage(pageable, lineId, tddwId,currentUserId);
			return WebApiResponse.success(list);
		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}

	@ApiOperation(value = "看护任务详情",notes = "看护任务详情")
	@GetMapping("/khTaskDetail")
	public WebApiResponse khTaskDetail(String taskId){
		Map<String, Object> map = new HashMap<String,Object>();
		try {
			map = service.khTaskDetail(taskId);
		} catch (Exception e) {
			return WebApiResponse.erro(e.getMessage());
		}
		return WebApiResponse.success(map);
	}

	@ApiOperation(value = "看护稽查任务派发",notes = "看护稽查任务派发")
	@GetMapping("/paifaKhCheckTask")
	public WebApiResponse paifaKhCheckTask(CheckLiveTask task , String username,String currentUserId){
		try {
			this.service.paifaKhCheckTask(task,username,currentUserId);
			return WebApiResponse.success("任务派发成功");
		}catch (Exception e){
			LOGGER.error("任务派发失败",e);
			return WebApiResponse.erro("任务派发失败");
		}

	}

	@ApiOperation(value = "看护稽查任务已派发看护任务列表接口",notes = "看护稽查任务已派发看护任务列表分页查询，条件搜索")
	@GetMapping("/listKhCheckTaskPage")
	public WebApiResponse listKhCheckTaskPage(@RequestParam(value = "page",defaultValue = "0") Integer page,
											  @RequestParam(value = "size",defaultValue = "8") Integer size,
											  String userId,String tddwId,String currentUserId,String startTime, String endTime,String status,String queryAll,String loginType){
		try{
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String, Object>> list = this.service.listKhCheckTaskPage(pageable, userId, tddwId,currentUserId,startTime,endTime,status,queryAll,loginType);
			return WebApiResponse.success(list);
		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}
	@ApiOperation(value = "看护稽查任务已派发看护任务详情",notes = "看护稽查任务已派发看护任务详情")
	@GetMapping("/listKhCheckTaskDetail")
	public WebApiResponse listKhCheckTaskDetail(String id){
		try{
			List<Map<String, Object>> list = this.service.listKhCheckTaskDetail(Long.valueOf(id));
			return WebApiResponse.success(list);
		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}
	@ApiOperation(value = "看护稽查任务已派发看护任务详情的照片",notes = "看护稽查任务已派发看护任务详情的照片")
	@GetMapping("/listKhCheckTaskDetailPicture")
	public WebApiResponse listKhCheckTaskDetailPicture(String id,String detailId){
		try{
			List<Map<String, Object>> list = this.service.listKhCheckTaskDetailPicture(id,detailId);
			return WebApiResponse.success(list);
		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}
	@ApiOperation(value = "地图上稽查任务的图片位置信息",notes = "地图上稽查任务的图片位置信息")
	@GetMapping("/mapKhCheckTaskDetailPicture")
	public WebApiResponse mapKhCheckTaskDetailPicture(String ids){
		try{
			List<Map<String, Object>> list = this.service.mapKhCheckTaskDetailPicture(ids);
			return WebApiResponse.success(list);
		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}
	@ApiOperation(value = "修改已派发看护任务稽查人",notes = "修改已派发看护任务稽查人")
	@GetMapping("/updateKhCheckUser")
	public WebApiResponse updateKhCheckUser(String id,String userId,String userName){
		try{
			this.service.updateKhCheckUser(Long.valueOf(id),userId,userName);
			return WebApiResponse.success("数据更新成功");
		}catch (Exception e){
			return WebApiResponse.erro("数据更新失败"+e.getMessage());
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
										  @RequestParam(value = "size",defaultValue = "8") Integer size,
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

	@ApiOperation(value = "app看护人员信息",notes = "看护人员信息")
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

	@ApiOperation(value = "app物品提示",notes = "物品提示")
	@GetMapping("/updateGoodsInfo")
	public WebApiResponse updateGoodsInfo(Long id,String taskType,String wpts){
		try{
			this.service.updateGoodsInfo(id,taskType,wpts);
			return WebApiResponse.success("");
		}catch (Exception e){
			LOGGER.error("物品提示报错",e);
			return WebApiResponse.erro("物品提示报错");
		}
	}

	@ApiOperation(value = "app根据id获取稽查母任务",notes = "根据id获取稽查母任务")
	@GetMapping("/getById")
	public WebApiResponse getById(Long id,String taskType){
		try{
			Object obj = this.service.getById(id,taskType);
			return WebApiResponse.success(obj);
		}catch (Exception e){
			LOGGER.error("根据id获取稽查母任务信息数据获取失败",e);
			return WebApiResponse.erro("根据id获取稽查母任务信息数据获取失败");
		}
	}

	@ApiOperation(value = "app稽查子任务列表",notes = "稽查子任务列表")
	@GetMapping("/checkChildrenList")
	public WebApiResponse checkChildrenList(@RequestParam(value = "page",defaultValue = "0") Integer page,
											@RequestParam(value = "size",defaultValue = "15") Integer size,String id,String taskId,String taskType){
		try{
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String,Object>> data = this.service.checkChildrenList(pageable,id,taskId,taskType);
			return WebApiResponse.success(data);
		}catch (Exception e){
			LOGGER.error("看护稽查子任务列表数据获取失败",e);
			return WebApiResponse.erro("看护稽查子任务列表数据获取失败");
		}
	}

	@ApiOperation(value = "app稽查子任务详情",notes = "稽查子任务详情")
	@GetMapping("/checkChildrenDetail")
	public WebApiResponse checkChildrenDetail(String id,String taskType){
		try{
			Object obj = service.checkChildrenDetail(id,taskType);
			return WebApiResponse.success(obj);
		}catch (Exception e){
			LOGGER.error("稽查子任务列表数据获取失败",e);
			return WebApiResponse.erro("稽查子任务列表数据获取失败");
		}

	}

	@ApiOperation(value = "app稽查完成按钮",notes = "稽查完成按钮")
	@GetMapping("/taskComplete")
	public WebApiResponse taskComplete(String id,String taskType){
		try{
			service.taskComplete(id,taskType);
			return WebApiResponse.success("稽查任务完成!");
		}catch (Exception e){
			LOGGER.error("稽查任务更新失败",e);
			return WebApiResponse.erro("稽查任务更新失败");
		}
	}

	@ApiOperation(value = "app获取隐患的电子围栏信息",notes = "获取隐患的电子围栏信息")
	@GetMapping("/getKhRange")
	public WebApiResponse getKhRange(String yhId){
		try{
			Map<String,Object> map = service.getKhRange(yhId);
			return WebApiResponse.success(map);
		}catch (Exception e){
			LOGGER.error("稽查任务更新失败",e);
			return WebApiResponse.erro("稽查任务更新失败");
		}
	}
	//这个还没有用
	@ApiOperation(value = "app已稽查任务详情",notes = "已稽查任务详情")
	@GetMapping("/checkDetailDone")
	public WebApiResponse checkDetailDone(String id,String taskId,String taskType){
		try{
			Object obj = service.checkDetailDone(id,taskId,taskType);
			return WebApiResponse.success(obj);
		}catch (Exception e){
			LOGGER.error("稽查任务更新失败",e);
			return WebApiResponse.erro("稽查任务更新失败");
		}
	}

	@ApiOperation(value = "生成稽查看护待派发",notes = "生成稽查看护待派发")
	@GetMapping("/generalKhSite")
	public void generalKhSite(){
		service.generalKhSite();
	}



}