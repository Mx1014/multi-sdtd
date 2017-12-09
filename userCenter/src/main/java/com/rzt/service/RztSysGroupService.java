/**    
 * 文件名：RztSysGroupService           
 * 版本信息：    
 * 日期：2017/10/10 10:47:25    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.service.CurdService;
import com.rzt.entity.RztSysGroup;
import com.rzt.repository.RztSysGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**      
 * 类名称：RztSysGroupService    
 * 类描述：InnoDB free: 537600 kB    
 * 创建人：张虎成   
 * 创建时间：2017/10/10 10:47:25 
 * 修改人：张虎成    
 * 修改时间：2017/10/10 10:47:25    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class RztSysGroupService extends CurdService<RztSysGroup,RztSysGroupRepository> {
	//添加子节点
	public RztSysGroup addSonNode(String id, RztSysGroup rztSysGroup){
		rztSysGroup.setCreatetime(new Date());
		int lft = this.reposiotry.getLftById(id);
		this.reposiotry.updateLft(lft);
		this.reposiotry.updateRgt(lft);
		rztSysGroup.setLft(lft + 1);
		rztSysGroup.setRgt(lft + 2);
		this.reposiotry.save(rztSysGroup);
		return rztSysGroup;
	}

	//添加节点
	public RztSysGroup addNode(String id,RztSysGroup rztSysGroup){
		rztSysGroup.setCreatetime(new Date());
		int rgt = this.reposiotry.getRgtById(id);
		this.reposiotry.updateNodeRgt(rgt + 2,rgt);
		this.reposiotry.updateNodeLft(rgt + 2,rgt);
		rztSysGroup.setLft(rgt + 1);
		rztSysGroup.setRgt(rgt + 2);
		this.reposiotry.save(rztSysGroup);
		return rztSysGroup;
	}

	//根据父节点的左右值查询子孙节点
	public List<RztSysGroup> findGroupListByPid(String id){
		RztSysGroup rztSysGroup = this.reposiotry.getOne(id);
		return this.reposiotry.findGroupListByPid(rztSysGroup.getLft(),rztSysGroup.getRgt());
	}

	//根据父节点id查询所有子节点
	public List<RztSysGroup> findByGroupPid(String menuPid){
		return this.reposiotry.findByGrouppid(menuPid);
	}

	//获取本节点的直系祖先（到根的路径）
	public List<RztSysGroup> findByLftLessThanAndRgtGreaterThan(String id){
		RztSysGroup rztSysGroup = this.reposiotry.getOne(id);
		return this.reposiotry.findByLftLessThanAndRgtGreaterThan(rztSysGroup.getLft(),rztSysGroup.getRgt());
	}

	public void deleteNode(String id){
		RztSysGroup rztSysGroup = this.reposiotry.getOne(id);
		this.reposiotry.deleteByLftBetween(rztSysGroup.getLft(),rztSysGroup.getRgt());
		this.reposiotry.updateNodeRgt(rztSysGroup.getRgt() - 4,rztSysGroup.getRgt());
		this.reposiotry.updateNodeLft(rztSysGroup.getLft() - 4,rztSysGroup.getLft());
	}

}