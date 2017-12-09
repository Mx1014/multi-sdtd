/**    
 * 文件名：RztSysDepartmentRepository           
 * 版本信息：    
 * 日期：2017/10/10 10:26:33    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import com.rzt.entity.RztSysDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**      
 * 类名称：RztSysDepartmentRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/10/10 10:26:33 
 * 修改人：张虎成    
 * 修改时间：2017/10/10 10:26:33    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface RztSysDepartmentRepository extends JpaRepository<RztSysDepartment,String> {
 //根据父节点的左右值查询子孙节点
 @Query(value = "SELECT " +
         "       node.id, " +
         "		node.deptName, " +
         "       node.lft, " +
         "       node.rgt, " +
         "       node.deptPid " +
         "FROM RztSysDepartment node, " +
         "       RztSysDepartment parent " +
         "WHERE " +
         "        node.lft BETWEEN parent.lft AND parent.rgt " +
         "        AND node.lft > ?1 AND node.rgt <  ?2 " +
         "GROUP BY node.id,node.lft,node.deptName,node.rgt,node.deptPid",nativeQuery = true)
 public List<Object[]> findDeptListByPid(int lft, int rgt);

 @Query(value = "select id from rztsysdepartment WHERE deptPid = '0'",nativeQuery = true)
 public String getRootId();

 //根据父节点id查询所有子节点
 public List<RztSysDepartment> findByDeptpid(String DeptPid);

 //获取本节点的直系祖先（到根的路径）
 public List<RztSysDepartment> findByLftLessThanAndRgtGreaterThan(int lft, int rgt);

 //根据节点id获取节点左值
 public RztSysDepartment getLftById(String id);

 //根据节点id获取节点右值
 public RztSysDepartment getRgtById(String id);

 //更新所有节点右值
 @Modifying
 @Query(value = "UPDATE RztSysDepartment SET rgt = rgt + 2 WHERE rgt > ?1",nativeQuery = true)
 public void updateRgt(int rgt);

 //更新所有节点左值
 @Modifying
 @Query(value = "UPDATE RztSysDepartment SET lft = lft + 2 WHERE lft > ?1 ",nativeQuery = true)
 public void updateLft(int lft);

 //更新所有节点左值
 @Modifying
 @Query(value = "UPDATE RztSysDepartment SET rgt = rgt - ?2 WHERE rgt > ?1",nativeQuery = true)
 public void updateNodergt(int rgt,int width);

 //更新所有节点左值
 @Modifying
 @Query(value = "UPDATE RztSysDepartment SET lft = lft - ?2 WHERE lft > ?1",nativeQuery = true)
 public void updateNodelft(int lft,int width);

 public void deleteByLftBetween(int lft, int rgt);
}
