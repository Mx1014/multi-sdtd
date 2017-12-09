/**    
 * 文件名：RztMenuPrivilegeController
 * 版本信息：    
 * 日期：2017/10/12 10:30:09    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.alibaba.fastjson.JSONArray;
import com.rzt.controller.CurdController;
import com.rzt.entity.RztMenuPrivilege;
import com.rzt.entity.RztMenuPrivilege;
import com.rzt.entity.RztSysMenu;
import com.rzt.service.RztMenuPrivilegeService;
import com.rzt.service.RztMenuPrivilegeService;
import com.rzt.util.WebApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 类名称：RztMenuPrivilegeController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/10/12 10:30:09 
 * 修改人：张虎成    
 * 修改时间：2017/10/12 10:30:09    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("RztMenuPrivilege")
public class RztMenuPrivilegeController extends
		CurdController<RztMenuPrivilege,RztMenuPrivilegeService> {

	@PostMapping("addRztMenuPrivilege/{roleid}")
	public WebApiResponse addRztMenuPrivilege(@PathVariable String roleid,
			@RequestParam("userJSON") String userJSON) {
		this.service.deleteByroleId(roleid);
		this.service.addAll(userJSON);
		return WebApiResponse.success("添加成功！");
	}

	@GetMapping("findRztMenuPrivilege/{roleid}")
	public List<RztMenuPrivilege> findRztMenuPrivilege(@PathVariable String roleid){
		return this.service.findRztMenuPrivilege(roleid);
	}

	@GetMapping("findUserPrivilege/{userid}")
	public List<Map<String, Object>> findUserPrivilege(@PathVariable String userid){
		return this.service.findUserPrivilege(userid);
	}

}