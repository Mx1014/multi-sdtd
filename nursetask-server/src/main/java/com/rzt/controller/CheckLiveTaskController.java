/**    
 * 文件名：CHECKLIVETASKController
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.entity.CheckLiveTask;
import com.rzt.entity.CheckLiveTaskCycle;
import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.entity.CheckLiveTaskExec;
import com.rzt.entity.model.CheckLiveTaskCycleModel;
import com.rzt.service.CheckLiveTaskCycleService;
import com.rzt.service.CheckLiveTaskDetailService;
import com.rzt.service.CheckLiveTaskExecService;
import com.rzt.service.CheckLiveTaskService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
	private CheckLiveTaskCycleService cycleService;

	@Autowired
	private CheckLiveTaskExecService execService;

	@Autowired
	private CheckLiveTaskService liveTaskService;

	@Autowired
	private CheckLiveTaskDetailService detailService;


	@ApiOperation(value = "任务展示",notes = "任务展示分页查询，条件搜索")
	@GetMapping("/listAllCheckTask.do")
	public WebApiResponse listAllCheckTask(@RequestParam(value = "page",defaultValue = "0") Integer page,
											 @RequestParam(value = "size",defaultValue = "15") Integer size,
											 String startTime, String endTime,String taskName,Integer status,String userId){
		try{
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String, Object>> list = this.service.listAllCheckTask(startTime,endTime,taskName, status,userId,pageable);
			return WebApiResponse.success(list);
		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}


	@ApiOperation(value = "任务派发任务查询",notes = "任务派发分页查询，条件搜索")
	@GetMapping("/listpaifaCheckTask.do")
	public WebApiResponse listpaifaCheckTask(@RequestParam(value = "page",defaultValue = "0") Integer page,
											@RequestParam(value = "size",defaultValue = "15") Integer size,
											String startTime, String endTime, String userId,String taskName){
		try{
			Pageable pageable = new PageRequest(page, size);
			Page<Map<String, Object>> list = this.service.listpaifaCheckTask(startTime,endTime, userId,taskName, pageable);
			return WebApiResponse.success(list);
		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}

	}

	@ApiOperation(value = "稽查子任务详情",notes = "稽查子任务详情查询")
	@GetMapping("/getCheckTaskById/{id}")
	public WebApiResponse getCheckTaskById(@PathVariable String id){
		try{
			List task =  this.service.getCheckTaskById(id);
			return WebApiResponse.success(task);

		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}

	}

	@ApiOperation(value = "稽查子任务名字",notes = "稽查子任务名字查询")
	@GetMapping("/getCheckTaskName.do")
	public WebApiResponse getCheckTaskName(){
		try{
			List task =  this.service.getCheckTaskName();
			return WebApiResponse.success(task);

		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}

	}
	@ApiOperation(value = "稽查任务派发",notes = "稽查任务派发")
	@GetMapping("/paifaCheckTask.do")
	@Transactional
	public WebApiResponse paifaCheckTask(CheckLiveTaskCycleModel model){
		try{

			CheckLiveTaskCycle taskcycle = new CheckLiveTaskCycle();

			String[] values = model.getIds().split(",");//子任务id

			taskcycle.setId();//周期id
			long count =  this.execService.getCount(model.getUserId());
			taskcycle.setCount(count+1);//派发几次
			taskcycle.setUserId(model.getUserId());
			CheckLiveTask task = this.liveTaskService.findLiveTask(values[0]);
			taskcycle.setTaskName(task.getTaskName() + "...等");//任务名
			taskcycle.setCreateTime(DateUtil.dateNow());
			taskcycle.setPlanStartTime(model.getPlanStartTime());
			taskcycle.setPlanEndTime(model.getPlanEndTime());
			taskcycle.setStatus("0");//是否停用 0 不停用

			taskcycle.setCheckDept(model.getCheckDept());
			taskcycle.setCheckCycle(model.getCheckCycle());
			taskcycle.setTaskType(model.getTaskType());

			this.cycleService.add(taskcycle);

			//生成exec任务
			CheckLiveTaskExec exec = new CheckLiveTaskExec();
			exec.setId();
			exec.setCycleId(taskcycle.getId());
			exec.setTaskName(taskcycle.getTaskName());
			exec.setCount(count+1);
			exec.setStatus("0");
			exec.setCreateTime(DateUtil.dateNow());
			exec.setUserId(model.getUserId());
			exec.setTaskStatus(0L);

			exec.setTdwhOrg(task.getTdwhOrg()+"等"); // 通道维护通道单位  子任务拼接
			this.execService.add(exec);//保存主任务

			//生成主任务的子任务   多个
			for(int i=0;i<values.length;i++){
				CheckLiveTaskDetail detail = new CheckLiveTaskDetail();
				detail.setId();
				detail.setExecId(exec.getId());
				detail.setTaskId(Long.parseLong(values[i]));
				detail.setPlanStartTime(taskcycle.getPlanStartTime());
				detail.setPlanEndTime(taskcycle.getPlanEndTime());
				detail.setStatus("0");
				detail.setCount(count+1);
				detail.setCreateTime(DateUtil.dateNow());
				//添加到数据库
				this.detailService.add(detail);
				//更新稽查子任务
				CheckLiveTask tt = this.liveTaskService.findLiveTask(values[i]);
				tt.setStatus("1");
				tt.setCycleId(taskcycle.getId());
				this.liveTaskService.updateLiveTask(tt,values[i]);//更新重新写一下
			}

			return WebApiResponse.success("任务派发成功");

		} catch (Exception e) {
			e.printStackTrace();
			return WebApiResponse.erro("任务派发失败" + e.getMessage());
		}

	}


	@ApiOperation(value = "稽查任务删除",notes = "稽查任务删除")
	@DeleteMapping("/deleteCheckLiveTaskById/{id}")
	public WebApiResponse deleteById(@PathVariable  String id){
		  return this.service.deleteById(id);
	}




}