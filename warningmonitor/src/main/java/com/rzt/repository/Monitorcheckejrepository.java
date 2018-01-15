/**
 * 文件名：MONITORCHECKEJRepository
 * 版本信息：
 * 日期：2018/01/08 11:06:23
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository;
import com.rzt.entity.Monitorcheckej;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 类名称：MONITORCHECKEJRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2018/01/08 11:06:23 
 * 修改人：张虎成    
 * 修改时间：2018/01/08 11:06:23    
 * 修改备注：    
 * @version
 */
 @Repository
public interface Monitorcheckejrepository extends JpaRepository<Monitorcheckej,String> {

 @Transactional
 @Modifying
 @Query(value = "INSERT INTO MONITOR_CHECK_EJ (ID,CREATE_TIME,TASK_ID,TASK_TYPE,WARNING_TYPE,USER_ID,DEPTID,TASK_NAME) " +
         "VALUES(?1,sysdate,?2,?3,?4,?5,?6,?7)",nativeQuery = true)
 void saveCheckEj(long ID, Long taskId, Integer taskType, Integer warnintType, String userId, String deptId, String taskName);

 //二级单位处理
 @Transactional
 @Modifying
 @Query(value = "UPDATE MONITOR_CHECK_EJ SET CREATE_TIME_Z = sysdate,CHECKC_INFO=?4,CHECKZ_APP_INFO=?5,STATUS=1" +
         "  WHERE TASK_ID=?1 AND TASK_TYPE=?2 AND WARNING_TYPE=?3",nativeQuery = true)
 int updateEJ(Long taskId, Integer taskType, Integer warningType, String checkInfo, String checkAppInfo);

 //一级单位处理
 @Transactional
 @Modifying
 @Query(value = "UPDATE MONITOR_CHECK_YJ SET CREATE_TIME_Z = sysdate,CHECKC_INFO=?4,CHECKZ_APP_INFO=?5,STATUS=1" +
         "  WHERE TASK_ID=?1 AND TASK_TYPE=?2 AND WARNING_TYPE=?3",nativeQuery = true)
 int updateYJ(Long taskId, Integer taskType, Integer warningType, String checkInfo, String checkAppInfo);

 @Transactional
 @Modifying
 @Query(value = "UPDATE MONITOR_CHECK_YJ SET CREATE_TIME_C = sysdate,CHECKC_INFO=?4,STATUS=2,CHECK_USER_ID=?5" +
         "  WHERE TASK_ID=?1 AND TASK_TYPE=?2 AND WARNING_TYPE=?3",nativeQuery = true)
 int updateYJC(Long taskId, Integer taskType, Integer warningType, String checkInfo,String userId);

 @Transactional
 @Modifying
 @Query(value = "UPDATE MONITOR_CHECK_EJ SET CREATE_TIME_C = sysdate,CHECKC_INFO=?4,STATUS=2,CHECK_USER_ID=?5" +
         "  WHERE TASK_ID=?1 AND TASK_TYPE=?2 AND WARNING_TYPE=?3",nativeQuery = true)
 int updateEJC(Long taskId, Integer taskType, Integer warningType, String checkInfo,String userId);
}
