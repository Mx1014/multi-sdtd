/**    
 * 文件名：MONITORCHECKEJController
 * 版本信息：    
 * 日期：2018/01/08 11:06:23    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.entity.Monitorcheckej;
import com.rzt.service.Monitorcheckejservice;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名称：MONITORCHECKEJController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2018/01/08 11:06:23
 * 修改人：张虎成
 * 修改时间：2018/01/08 11:06:23
 * 修改备注：
 * @version
 */
@RestController
@RequestMapping("MONITORCHECKEJ")
public class MonitorCheckeJController extends
		CurdController<Monitorcheckej, Monitorcheckejservice> {

	@Autowired
    private Monitorcheckejservice ejService;

	@GetMapping("GetDeptIds")
	public WebApiResponse getDeptId(String userId){
		try {
			return WebApiResponse.success(ejService.getDeptId(userId));
		}catch (Exception e){
			return WebApiResponse.erro("查询失败"+e.getMessage());
		}
	}

	@GetMapping("warningType")
	public WebApiResponse getWarningType(String taskType){
		try {
			return WebApiResponse.success(ejService.warningType(taskType));
		}catch (Exception e){
			return WebApiResponse.erro("查询失败"+e.getMessage());
		}
	}

	/**
	 *未处理告警任务列表
	 * @param page  必传
	 * @param size  必传
	 * @param type 必传  任务类型 巡视、看护
	 * @param userId 必传 当前登录人的id 用于判断权限
	 * @param startDate  条件查询 日期
	 * @param warningType  条件查询 告警类型
	 * @param deptID  条件查询 通道公司
     * @return
     */
	@GetMapping("GJW")
	public WebApiResponse GJW( Integer page, Integer size, String startDate,String userId,Integer warningType,String deptID,Integer type,String endDate){
		try {
			return WebApiResponse.success(ejService.XSGJW(page,size,startDate,userId,warningType,deptID,type,endDate));
		}catch (Exception e){
			return WebApiResponse.erro("巡视告警未处理查询失败"+e.getMessage());
		}
	}

	/**
	 *处理中告警任务列表
	 * @param page  必传
	 * @param size  必传
	 * @param type 必传  任务类型 巡视、看护
	 * @param userId 必传 当前登录人的id 用于判断权限
	 * @param startDate  条件查询 日期
	 * @param warningType  条件查询 告警类型
	 * @param deptID  条件查询 通道公司
	 * @return
	 */
	@GetMapping("GJZ")
	public WebApiResponse GJZ( Integer page, Integer size, String startDate,String userId,Integer warningType,String deptID,Integer type,String endDate){
		try {
			return WebApiResponse.success(ejService.XSGJZ(page,size,startDate,userId,warningType,deptID,type,endDate));
		}catch (Exception e){
			return WebApiResponse.erro("巡视告警未处理查询失败"+e.getMessage());
		}
	}
	/**
	 *已处理告警任务列表
	 * @param page  必传
	 * @param size  必传
	 * @param type 必传  任务类型 巡视、看护
	 * @param userId 必传 当前登录人的id 用于判断权限
	 * @param startDate  条件查询 日期
	 * @param warningType  条件查询 告警类型
	 * @param deptID  条件查询 通道公司
	 * @return
	 */
	@GetMapping("GJY")
	public WebApiResponse GJY( Integer page, Integer size, String startDate,String userId,Integer warningType,String deptID,Integer type,String endDate){
		try {
			return WebApiResponse.success(ejService.XSGJY(page,size,startDate,userId,warningType,deptID,type,endDate));
		}catch (Exception e){
			return WebApiResponse.erro("巡视告警未处理查询失败"+e.getMessage());
		}
	}

	/**未处理到处理中处理
	 * @param userId 当前登录用户存到数据库中，在检查完成展示
	 * @param taskId 任务id
	 * @param type  必传  任务类型 巡视、看护
	 * @param warningType 必传
	 * @param checkInfo
	 * @param checkAppInfo
	 * @return
	 */
	@PostMapping("GJCL")
	public WebApiResponse GJCL(String userId,Long taskId,Integer type,Integer warningType,String checkInfo,String checkAppInfo){
		return ejService.GJCL(userId,taskId,type,warningType,checkInfo,checkAppInfo);
	}

	@PostMapping("GJCLC")
	public WebApiResponse GJCLC(String userId,Long taskId,Integer type,Integer warningType,String checkInfo){
		return ejService.GJCLC(userId,taskId,type,warningType,checkInfo);
	}

}