/**
 * 文件名：ANOMALYMONITORINGController
 * 版本信息：
 * 日期：2017/12/31 16:25:17
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.rzt.entity.Anomalymonitoring;
import com.rzt.service.Anomalymonitoring2Service;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名称：ANOMALYMONITORINGController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/31 16:25:17
 * 修改人：张虎成
 * 修改时间：2017/12/31 16:25:17
 * 修改备注：
 * @version
 */
@RestController
@RequestMapping("ANOMALYMONITORING2")
public class Anomalymonitoring2Controller extends
		CurdController<Anomalymonitoring, Anomalymonitoring2Service> {

	@Autowired
	private Anomalymonitoring2Service service;

	/**
	 * 查询所有
	 *@Author hyn
	 *@Method XSGJ
	 *@Params [type, page, size]
	 *@Date 2018/1/1 11:09
	 */
	@GetMapping("XSGJ")
    public Object XSGJ(String userId, Integer page, Integer size, String date, String orgid, String type){
		try {
			return WebApiResponse.success(service.XSGJ(userId, page, size, date, orgid, type));
		}catch (Exception e){
			return WebApiResponse.erro("查询失败"+e.getMessage());
		}
	}
	/*@GetMapping("xsgj")
	public Object xsgj(String userId,Integer page, Integer size, String date, String orgid, String type){
		try {
			return WebApiResponse.success(service.xsgj(userId, page, size, date, orgid, type));
		}catch (Exception e){
			return WebApiResponse.erro("查询失败"+e.getMessage());
		}
	}*/

	/**
	 * 查询已处理
	 * @param type
	 * @param page
	 * @param size
     * @return
     */
	@GetMapping("XSGJC")
    public Object XSGJC(String userId, Integer page, Integer size, String date, String orgid, String type){
		try {
			return WebApiResponse.success(service.XSGJC(userId, page, size, date, orgid, type));
		}catch (Exception e){
			return WebApiResponse.erro("查询失败"+e.getMessage());
		}
	}
	@GetMapping("XSGJCZ")
    public Object XSGJCZ(String userId, Integer page, Integer size, String date, String orgid, String type){
		try {
			return WebApiResponse.success(service.XSGJCZ(userId, page, size, date, orgid, type));
		}catch (Exception e){
			return WebApiResponse.erro("查询失败"+e.getMessage());
		}
	}



    @GetMapping("khGj")
    public WebApiResponse khGj(String userId, Integer page, Integer size, String date, String orgid, String type) {
        return this.service.khGj(userId, page, size, date, orgid, type);
    }
    @GetMapping("khGjC")
    public WebApiResponse khGjC(String userId, Integer page, Integer size, String date, String orgid, String type) {
        return this.service.khGjC(userId, page, size, date, orgid, type);
    }
    @GetMapping("khGjCZ")
    public WebApiResponse khGjCZ(String userId, Integer page, Integer size, String date, String orgid, String type) {
        return this.service.khGjCZ(userId, page, size, date, orgid, type);
    }

    /**
     * 处理中
     *
     * @param userId     一级还是二级单位
     * @param explain   审核详情
     * @param taskId   任务id
     * @param tasktype  任务类型
     * @param anomalytype  警告类型
     * @return
     */
    @PostMapping("anomalyIns")
    public WebApiResponse anomalyIns(String userId, String explain,String explainApp, Long taskId, Integer tasktype, Integer anomalytype) {
        return this.service.anomalyIns(userId, explain,explainApp, taskId, tasktype, anomalytype);
    }

    /**
     * 已完成处理  处理中告警处理
	 * assessment  是否自动纳入考核
     * @return
     */
    @PostMapping("anomalyInsO")
    public WebApiResponse anomalyInsO(String userId, String explain, Long taskId,Integer anomalytype) {
        return this.service.anomalyInsO(userId, explain, taskId,anomalytype);
    }


}