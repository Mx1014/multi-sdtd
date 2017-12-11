/**    
 * 文件名：KhTaskController
 * 版本信息：    
 * 日期：2017/11/28 14:43:44    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.KhSite;
import com.rzt.entity.KhTask;
import com.rzt.service.KhTaskService;
import com.rzt.service.KhYhHistoryService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


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
	@Autowired
	private KhYhHistoryService yhservice;

	/***
	 * 所有看护计划查询
	 * @return
	 */
	@GetMapping("/listAllKhTask.do")
	@ResponseBody
	public WebApiResponse listAllKhTask(KhTask task, Pageable pageable) {
		try {
			//分页参数 page size
			Object o = this.service.listAllKhTask(task, pageable);
			return WebApiResponse.success(o);
		} catch (Exception e) {
			e.printStackTrace();
			return WebApiResponse.erro("数据查询失败" + e.getMessage());
		}
	}
	/**
	 * 修改已安排任务
	 */
	@GetMapping("/updateTaskById")
	@ResponseBody
	public WebApiResponse updateTaskById(KhSite site,String id){
	// 提交申请给 管理员  如何提交待定  还是说没有修改功能
		try {
			//分页参数 page size
			this.service.updateTaskById(site,id);
			return WebApiResponse.success("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return WebApiResponse.erro("修改失败" + e.getMessage());
		}
	}

	/**
	 * 删除已安排任务 KhTask/{id}  请求方式 DELETE
	 */

	/**
	 * 查看已安排任务详情
	 * @param id
	 * @return
	 */
	@GetMapping("/getKhTaskById.do")
	@ResponseBody
	public WebApiResponse listKhTaskById(String id){
		try {
			List<Map<String, Object>> maps = this.service.getKhTaskById(id);
			return WebApiResponse.success(maps);
		} catch (Exception e) {
			e.printStackTrace();
			return WebApiResponse.erro("数据获取失败" + e.getMessage());
		}
	}

}


