/**    
 * 文件名：RztSysDepartmentController
 * 版本信息：    
 * 日期：2017/10/10 10:26:33    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.controller.CurdController;
import com.rzt.entity.RztSysDepartment;
import com.rzt.service.RztSysDepartmentService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**      
 * 类名称：RztSysDepartmentController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/10/10 10:26:33 
 * 修改人：张虎成    
 * 修改时间：2017/10/10 10:26:33    
 * 修改备注：    
 * @version
 * 部门表
 */
@RestController
@RequestMapping("RztSysDepartment")
public class RztSysDepartmentController extends
		CurdController<RztSysDepartment,RztSysDepartmentService> {

	//新增子节点
	@ResponseBody
	@RequestMapping(value = "addSonNode",method = RequestMethod.POST)
	public RztSysDepartment addSonNode(@RequestParam(required = false) String id, @ModelAttribute RztSysDepartment rztSysDepartment){
		if (StringUtils.isEmpty(id))
			id = this.service.getRootId();
		return this.service.addSonNode(id,rztSysDepartment);
	}

	//新增子节点
	@ResponseBody
	@GetMapping("findAll")
	public List<RztSysDepartment> findAll(){
		return this.service.findAll();
	}

	//新增同级节点
	@ResponseBody
	@RequestMapping(value = "addNode/{id}",method = RequestMethod.POST)
	public RztSysDepartment addNode(@PathVariable String id, @ModelAttribute RztSysDepartment rztSysDepartment){
		return this.service.addNode(id,rztSysDepartment);
	}

	//删除节点
	@RequestMapping(value = "deleteNode/{id}",method = RequestMethod.DELETE)
	public void deleteNode(@PathVariable String id){
		this.service.deleteNode(id);
	}

	//根据父节点的id查询子孙节点
	@RequestMapping(value = "findDeptListByPid",method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> findDeptListByPid(@RequestParam(required = false) String id){
		if (StringUtils.isEmpty(id))
			id = this.service.getRootId();
		return this.service.findDeptListByPid(0,0,id);
	}

	//根据父节点id查询所有子节点
	@RequestMapping(value = "findByDeptPid/{id}",method = RequestMethod.GET)
	@ResponseBody
	public List<RztSysDepartment> findByDeptPid(@PathVariable("id") String menuPid){
		return this.service.findByDeptPid(menuPid);
	}
}