/**    
 * 文件名：CHECKLIVETASKController
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.CheckLiveTask;
import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.entity.KhTask;
import com.rzt.entity.model.CheckLiveTaskDetailModel;
import com.rzt.entity.model.CheckLiveTasks;
import com.rzt.service.CheckLiveTaskDetailService;
import com.rzt.service.CheckLiveTaskService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
public class CheckLiveTaskController extends
		CurdController<CheckLiveTask, CheckLiveTaskService> {

	@Autowired
	private CheckLiveTaskDetailService checkService;

	@GetMapping("/listAllCheckTask.do")
//	@ResponseBody
    public WebApiResponse listAllCheckTask(KhTask task, Pageable pageable){
		try{
			List list = this.service.listAllCheckTask(task, pageable);
			return WebApiResponse.success(list);
		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}
	@GetMapping("/listAllCheckNotDo.do")
	public WebApiResponse listAllCheckNotDo(KhTask task, Pageable pageable){
		try{
			List list = this.service.listAllCheckNotDo(task, pageable);
			return WebApiResponse.success(list);
		}catch (Exception e) {
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}



	// 删除待稽查  /CheckLiveTask/{id}  请求方式Delete

	// 查看待稽查  查看的内容待查
	@GetMapping("/listTaskNotById.do")
	@ResponseBody
	public WebApiResponse listTaskNotById(String id){
		try{
			//List list = this.service.listAllCheckDoing(id);
			return WebApiResponse.success("");
		}catch (Exception e) {
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}
	//派发稽查任务  多条派发为1条
	@GetMapping("/paifaCheckTask.do")
	@ResponseBody
	public WebApiResponse paifaCheckTask(CheckLiveTaskDetailModel model){
		try{
			List<CheckLiveTasks> list = model.getList();
			for (CheckLiveTasks task:list) {
				
			}
			/*task.setTaskId(id);
			task.setStatus("0");  //未开始
			task.setCheckType("0");//看护任务
			checkService.add(task);*/
			return WebApiResponse.success("任务派发成功");
		}catch (Exception e) {
			return WebApiResponse.erro("任务派发失败"+e.getMessage());
		}
	}
	@GetMapping("/AppListCheckTaskByUserId.do")
	@ResponseBody
	public WebApiResponse AppListCheckTaskByUserId(CheckLiveTaskDetail task){
		try{
			List<Map<String, Object>> maps = this.service.AppListCheckTaskByUserId(task);
			return WebApiResponse.success(maps);
		}catch (Exception e) {
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}
	}
	/*@GetMapping("/App")*/

}