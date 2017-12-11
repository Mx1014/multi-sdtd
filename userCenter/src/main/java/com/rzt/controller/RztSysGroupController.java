/**    
 * 文件名：RztSysGroupController
 * 版本信息：    
 * 日期：2017/10/10 10:47:25    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.controller.CurdController;
import com.rzt.entity.RztSysGroup;
import com.rzt.service.RztSysGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**      
 * 类名称：RztSysGroupController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/10/10 10:47:25 
 * 修改人：张虎成    
 * 修改时间：2017/10/10 10:47:25    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("RztSysGroup")
public class RztSysGroupController extends
		CurdController<RztSysGroup,RztSysGroupService> {
	//新增子节点
	@ResponseBody
	@PostMapping("addSonNode/{id}")
	public RztSysGroup addSonNode(@PathVariable String id,@RequestBody RztSysGroup rztSysGroup){
		return this.service.addSonNode(id,rztSysGroup);
	}

	//新增同级节点
	@ResponseBody
	@PostMapping("addNode/{id}")
	public RztSysGroup addNode(@PathVariable String id,@RequestBody RztSysGroup rztSysGroup){
		return this.service.addNode(id,rztSysGroup);
	}

	//删除节点
	@DeleteMapping("deleteNode/{id}")
	public void deleteNode(String id){
		this.service.deleteNode(id);
	}

	//根据父节点的id查询子孙节点
	@ResponseBody
	@GetMapping("findGroupListByPid/{id}")
	public List<RztSysGroup> findGroupListByPid(String id){
		return this.service.findGroupListByPid(id);
	}

	
}