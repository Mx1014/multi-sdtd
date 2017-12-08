/**    
 * 文件名：XsZcCycleController
 * 版本信息：    
 * 日期：2017/12/07 07:50:10    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller.pc;

import com.rzt.controller.CurdController;
import com.rzt.entity.appentity.XSZCTASK;
import com.rzt.entity.pc.XsZcCycle;
import com.rzt.service.app.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名称：XsZcCycleController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 07:50:10 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 07:50:10    
 * 修改备注：    
 * @version        
 */
@RestController
@Api(value = "pc")
@RequestMapping("XsZcCycle")
public class XsZcCycleController extends
		CurdController<XsZcCycle, com.rzt.service.pc.XsZcCycleService> {

	@Autowired
	private XSZCTASKService xszctaskService;

	/**
    * @Method addCycle
    * @Description 添加周期
    * @param [xsZcCycle]
    * @return java.lang.Object
    * @date 2017/12/7 16:25
    * @author nwz
    */
    @ApiOperation(value = "周期维护 新增周期",notes = "pc端新增周期")
    @PostMapping("addCycle")
    public Object addCycle(@RequestBody XsZcCycle xsZcCycle) {

		return this.service.addCycle(xsZcCycle);

	}
	
	
	/**
	* @Method addPlan
	* @Description  任务派发 生成任务和人员信息
	* @param [xszctask]
	* @return java.lang.Object
	* @date 2017/12/7 17:57
	* @author nwz
	*/
	@ApiOperation(value = "计划制定",notes = "pc端计划制定")
	@PostMapping("addPlan")
	public Object addPlan(@RequestBody XSZCTASK xszctask) {
		try {
			xszctaskService.add(xszctask);
			return WebApiResponse.success("数据保存成功!");
		} catch (Exception var3) {
			return WebApiResponse.erro("数据保存失败" + var3.getMessage());
		}

	}
	
}