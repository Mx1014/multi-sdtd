/**    
 * 文件名：CMLINEController
 * 版本信息：    
 * 日期：2017/11/28 18:05:12    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.entity.CMLINE;
import com.rzt.service.CMLINEService;
import com.rzt.util.WebApiResponse;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 类名称：CMLINEController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 18:05:12 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 18:05:12    
 * 修改备注：
 */
@RestController
@RequestMapping("CMLINE")
@EnableSwagger2
public class CMLINEController extends
		CurdController<CMLINE,CMLINEService> {


	@PostMapping("fuck")
    public Object get() {
		WebApiResponse<CMLINE> one = this.findOne("69948D934CBA4EF09007BCB20BD7B4DC");
		return one;
	}


	@PatchMapping("testAddLineAndTower")
	public Object test() {

		CMLINE cmLine = new CMLINE();
		cmLine.setLineName("安朝线");
		cmLine.setVLevel(0);
		cmLine.setLineJb(0);
		WebApiResponse<String> add = this.add(cmLine);
		return add;
	}



}