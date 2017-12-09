/**    
 * 文件名：RztSysGroupRepository           
 * 版本信息：    
 * 日期：2017/10/10 10:47:25    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import com.rzt.entity.RztSysGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名称：RztSysGroupRepository
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/10/10 10:47:25
 * 修改人：张虎成
 * 修改时间：2017/10/10 10:47:25
 * 修改备注：
 * @version
 */
 @Repository
public interface RztSysGroupRepository extends JpaRepository<RztSysGroup,String> {
 //根据父节点的左右值查询子孙节点
 @Query(value = "SELECT " +
         "       node.id groupId, " +
         "		node.groupName groupName, " +
         "       node.lft, " +
         "       node.rgt, " +
         "       (COUNT(parent.id)-1) groupLevel, " +
         "       node.groupPid pid " +
         "FROM rzt_sys_group node, " +
         "       rzt_sys_group parent " +
         "WHERE " +
         "        node.lft BETWEEN parent.lft AND parent.rgt " +
         "        AND node.lft > ?1 AND node.rgt <  ?2" +
         "GROUP BY node.id,node.lft,node.groupName,node.rgt,node.groupPid",nativeQuery = true)
 public List<RztSysGroup> findGroupListByPid(int lft, int rgt);

 //根据父节点id查询所有子节点
 public List<RztSysGroup> findByGrouppid(String GroupPid);

 //获取本节点的直系祖先（到根的路径）
 public List<RztSysGroup> findByLftLessThanAndRgtGreaterThan(int lft, int rgt);

 //根据节点id获取节点左值
 public int getLftById(String id);

 //根据节点id获取节点右值
 public int getRgtById(String id);

 //更新所有节点右值
 @Modifying
 @Query(value = "UPDATE rzt_sys_group SET rgt = rgt + ?1 WHERE rgt > ?1",nativeQuery = true)
 public void updateRgt(int rgt);

 //更新所有节点左值
 @Modifying
 @Query(value = "UPDATE rzt_sys_group SET lft = lft + ?1 WHERE lft > ?1 ",nativeQuery = true)
 public void updateLft(int lft);

 //更新所有节点左值
 @Modifying
 @Query(value = "UPDATE rzt_sys_group SET rgt = ?1 WHERE rgt > ?2",nativeQuery = true)
 public void updateNodeRgt(int rgt, int second);

 //更新所有节点左值
 @Modifying
 @Query(value = "UPDATE rzt_sys_group SET lft = ?1 WHERE lft > ?2",nativeQuery = true)
 public void updateNodeLft(int lft, int second);

 public void deleteByLftBetween(int lft, int rgt);
}
