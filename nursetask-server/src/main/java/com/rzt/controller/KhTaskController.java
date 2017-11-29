/**    
 * 文件名：KhTaskController
 * 版本信息：    
 * 日期：2017/11/28 14:43:44    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.KhTask;
import com.rzt.service.KhTaskService;
import com.rzt.util.WebApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import com.rzt.controller.CurdController;

import java.util.List;


/**
 * 类名称：KhTaskController
 * 类描述：    
 * 创建时间：2017/11/28 14:43:44
 * 修改时间：2017/11/28 14:43:44
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("KhTask")
public class KhTaskController extends
		CurdController<KhTask,KhTaskService> {
	/***
	 * 新建看护任务
	 * @param task
	 */
	@GetMapping("/saveKhTask")
	public void findAllBy(KhTask task){

	}

	/***
	 * 看护任务查询方法
	 * @return
	 */
	@GetMapping("/findAllKhTask.do")
	@ResponseBody
	public WebApiResponse findAllKhTask(KhTask task, Pageable pageable){
		try{
			//分页参数 page size
			List list = this.service.findAllKhTask(task, pageable);
			return WebApiResponse.success(list);
		}catch (Exception e){
			e.printStackTrace();
			return WebApiResponse.erro("数据查询失败" + e.getMessage());
		}
	}
	/***
	 * 获取 已 安排的看护任务
	 * @return
	 */
	@GetMapping("/findAllTaskDoing")
	@ResponseBody
	public WebApiResponse findAllTaskDoing(KhTask task, Pageable pageable){
		try{
			//分页参数 page size
			List list = this.service.findAllTaskDoing(task, pageable);
			return WebApiResponse.success(list);
		}catch (Exception e){
			e.printStackTrace();
			return WebApiResponse.erro("数据查询失败" + e.getMessage());
		}
	}
	/***
	 * 获取 待 安排的看护任务
	 * @return
	 */
	@GetMapping("/findAllTaskNotDo")
	@ResponseBody
	public WebApiResponse findAllTaskNotDo(KhTask task, Pageable pageable){
		try{
			//分页参数 page size
			List list = this.service.findAllTaskNotDo(task, pageable);
			return WebApiResponse.success(list);
		}catch (Exception e){
			e.printStackTrace();
			return WebApiResponse.erro("数据查询失败" + e.getMessage());
		}
	}
}