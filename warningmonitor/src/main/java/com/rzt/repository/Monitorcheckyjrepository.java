/**    
 * 文件名：MONITORCHECKYJRepository           
 * 版本信息：    
 * 日期：2018/01/08 11:06:23    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import com.rzt.entity.Monitorcheckyj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 类名称：MONITORCHECKYJRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2018/01/08 11:06:23 
 * 修改人：张虎成    
 * 修改时间：2018/01/08 11:06:23    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface Monitorcheckyjrepository extends JpaRepository<Monitorcheckyj,String> {

  @Transactional
  @Modifying
  @Query(value = "update MONITOR_CHECK_EJ set IS_VIEW = 1 where TASK_ID=?1 and TASK_TYPE =?2 and WARNING_TYPE=?3",nativeQuery = true)
  void changStatus(Long aLong, Integer integer, Integer integer1);

  @Transactional
    @Modifying
    @Query(value = "INSERT INTO MONITOR_CHECK_YJ (ID,CREATE_TIME,TASK_ID,TASK_TYPE,WARNING_TYPE,USER_ID,DEPTID,TASK_NAME) " +
            "VALUES(?1,sysdate,?2,?3,?4,?5,?6,?7)",nativeQuery = true)
    void saveCheckYj(long ID, Long taskId, Integer taskType, Integer warnintType, String userId, String deptId, String taskName);

  @Transactional
  @Modifying
  @Query(value = "INSERT INTO MONITOR_CHECK_YJ (ID,CREATE_TIME,TASK_ID,TASK_TYPE,WARNING_TYPE,USER_ID,DEPTID,TASK_NAME,REASON) " +
          "VALUES(?1,sysdate,?2,?3,?4,?5,?6,?7,?8)",nativeQuery = true)
    void saveCheckYjWdw(long ID, Long taskId, Integer taskType, Integer warnintType, String userId, String deptId, String taskName, String reason);
}
