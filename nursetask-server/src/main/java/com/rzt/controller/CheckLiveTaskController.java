/**    
 * 文件名：CHECKLIVETASKController
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.CheckLiveTask;
import com.rzt.entity.CheckLiveTaskCycle;
import com.rzt.service.CheckLiveTaskCycleService;
import com.rzt.service.CheckLiveTaskService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * 类名称：CHECKLIVETASKController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/04 15:13:15
 * 修改人：张虎成
 * 修改时间：2017/12/04 15:13:15
 * 修改备注：
 * @version
 */
@RestController
@RequestMapping("CheckLiveTask")
public class CheckLiveTaskController extends CurdController<CheckLiveTask, CheckLiveTaskService> {

	@Autowired
	private CheckLiveTaskCycleService checkService;

	@ApiOperation(value = "任务展示",notes = "任务展示分页查询，条件搜索")
	@GetMapping("/listAllCheckTask.do")
	public WebApiResponse listAllCheckTask(@RequestParam(value = "page",defaultValue = "0") Integer page,
											 @RequestParam(value = "size",defaultValue = "15") Integer size,
											 String startTime, String endTime,String taskName,String status){
		try{
			System.out.println(startTime);
			System.out.println(endTime);
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String, Object>> list = this.service.listAllCheckTask(startTime,endTime,taskName, status,pageable);
			return WebApiResponse.success(list);
		}catch (java.lang.Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}


	@ApiOperation(value = "任务派发任务查询",notes = "任务派发分页查询，条件搜索")
	@GetMapping("/listpaifaCheckTask.do")
	public WebApiResponse listpaifaCheckTask(@RequestParam(value = "page",defaultValue = "0") Integer page,
											@RequestParam(value = "size",defaultValue = "15") Integer size,
											String startTime, String endTime, String userId,String taskName){
		try{
			System.out.println(startTime);
			System.out.println(endTime);
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String, Object>> list = this.service.listpaifaCheckTask(startTime,endTime, userId,taskName, pageable);
			return WebApiResponse.success(list);
		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}

	}

	@ApiOperation(value = "稽查子任务详情",notes = "稽查子任务详情查询")
	@GetMapping("/getCheckTaskById/{id}")
	public WebApiResponse listPaiFaCheckTask(@PathVariable String id){

		try{
			//这里详情页面查询需不需要连接其他表还不知道
			CheckLiveTask task =  this.service.findOne(id);
			return WebApiResponse.success(task);

		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}

	}


	//派发稽查任务  多条派发为1条
	@GetMapping("/paifaCheckTask.do")
	@ResponseBody
	@Transactional
	public WebApiResponse paifaCheckTask(CheckLiveTaskCycle model,String userId,String [] ids){
		try{
			/*//不是第一次派发   修改原来的派发次数
            if(model.getId()!=null){
				CheckLiveTaskCycle c = this.checkService.findOne(model.getId().toString());
				if(c!=null){
					c.setCount(c.getCount()+1);
					this.checkService.update(c,model.getId().toString());
					return WebApiResponse.success("任务派发成功");
				}
				return WebApiResponse.success("任务派发失败");
			}else {*/

		//		model.setId(1L);//稽查周期表id
				model.setUserId(userId);//稽查人id
				model.setCreateTime(new Date());//派发时间
		//		model.setPlanStartTime(model.getPlanStartTime());//任务计划开始时间
		//		model.setPlanEndTime(model.getPlanEndTime());//任务计划结束时间
				model.setStatus("0");//未稽查
				model.setCheckDept(model.getCheckDept());//稽查部名 属地公司一天一次  北京公司3天一次
				model.setCheckCycle(model.getCheckCycle());//稽查周期
				//第几次派发稽查任务  定时派发
				model.setCount(1L);//初始都是第一次

				for (int i = 0; i < ids.length; i++) {
					CheckLiveTask c = this.service.findOne(ids[i]);
					if (i == 0) {
						model.setTaskName(c.getTaskName() + "等");
					}
					//设置稽查周期外键
				//	c.setCycleId(model.getId());
					//c.setPlanStartTime(model.getPlanStartTime());
				//	c.setPlanEndTime(model.getPlanEndTime());
				//	c.setTaskStatus("0");//待稽查
					c.setCheckCycle(model.getCheckCycle());//稽查周期
					c.setCheckDept(model.getCheckDept());//稽查部名
					c.setStatus("1");//已派发
					c.setUserId(userId);//稽查人id

					//更新稽查任务
					this.service.update(c, ids[i]);

				}
				this.checkService.add(model);//保存稽查主任务
				return WebApiResponse.success("任务派发成功");

		}catch (Exception e) {
			return WebApiResponse.erro("任务派发失败"+e.getMessage());
		}
	}

	/**
	 * 基本思路        稽查模块还涉及到权限的问题   以后再说
	 * 稽查任务的派发   页面展示的数据是未派发的任务  点击派发  周期表(相当于一条主任务)插入一条 exec表(主任务表)插入一条   detail插入遍历插入
	 * 选中多少条子任务  然后再更改字任务的状态  为已派发  还有一些时间的填写
	 */








}