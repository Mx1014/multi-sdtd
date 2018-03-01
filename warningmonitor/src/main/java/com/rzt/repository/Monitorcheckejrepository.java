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

import java.util.Date;

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

 //二级单位处理  AND CREATE_TIME=to_date( ?6,'yyyy-MM-dd hh24:mi:ss')
 @Transactional
 @Modifying
 @Query(value = "UPDATE MONITOR_CHECK_EJ SET CREATE_TIME_Z = sysdate,CHECKC_INFO=?4,CHECKZ_APP_INFO=?5,STATUS=1" +
         "  WHERE TASK_ID=?1 AND TASK_TYPE=?2 AND WARNING_TYPE=?3  AND CREATE_TIME=to_date( ?6,'yyyy-MM-dd hh24:mi:ss')",nativeQuery = true)
 int updateEJ(Long taskId, Integer taskType, Integer warningType, String checkInfo, String checkAppInfo, String createTime);

 //一级单位处理 AND CREATE_TIME=to_date( ?6,'yyyy-MM-dd hh24:mi:ss')
 @Transactional
 @Modifying
 @Query(value = "UPDATE MONITOR_CHECK_YJ SET CREATE_TIME_Z = sysdate,CHECKC_INFO=?4,CHECKZ_APP_INFO=?5,STATUS=1" +
         "  WHERE TASK_ID=?1 AND TASK_TYPE=?2 AND WARNING_TYPE=?3  AND CREATE_TIME=to_date( ?6,'yyyy-MM-dd hh24:mi:ss')",nativeQuery = true)
 int updateYJ(Long taskId, Integer taskType, Integer warningType, String checkInfo, String checkAppInfo, String createTime);

 @Transactional
 @Modifying
 @Query(value = "UPDATE MONITOR_CHECK_YJ SET CREATE_TIME_C = sysdate,CHECKC_INFO=?4,STATUS=2,CHECK_USER_ID=?5" +
         "  WHERE TASK_ID=?1 AND TASK_TYPE=?2 AND WARNING_TYPE=?3  AND CREATE_TIME_Z=to_date( ?6,'yyyy-MM-dd hh24:mi:ss')",nativeQuery = true)
 int updateYJC(Long taskId, Integer taskType, Integer warningType, String checkInfo,String userId, String createTime);

 @Transactional
 @Modifying
 @Query(value = "UPDATE MONITOR_CHECK_EJ SET CREATE_TIME_C = sysdate,CHECKC_INFO=?4,STATUS=2,CHECK_USER_ID=?5" +
         "  WHERE TASK_ID=?1 AND TASK_TYPE=?2 AND WARNING_TYPE=?3  AND CREATE_TIME_Z=to_date( ?6,'yyyy-MM-dd hh24:mi:ss')",nativeQuery = true)
 int updateEJC(Long taskId, Integer taskType, Integer warningType, String checkInfo,String userId, String createTime);

 @Transactional
 @Modifying
 @Query(value = "UPDATE MONITOR_CHECK_EJ SET ONLINE_TIME = sysdate" +
         "  WHERE TASK_ID=?2 AND USER_ID=?1 ",nativeQuery = true)
    int updateOnlineTime(String userId, Long id);

 //未到位插入数据用
 @Transactional
 @Modifying
 @Query(value = "INSERT INTO MONITOR_CHECK_EJ (ID,CREATE_TIME,TASK_ID,TASK_TYPE,WARNING_TYPE,USER_ID,DEPTID,TASK_NAME,REASON) " +
         "VALUES(?1,sysdate,?2,?3,?4,?5,?6,?7,?8)",nativeQuery = true)
 void saveCheckEjWdw(long ID, Long taskId, Integer taskType, Integer warnintType, String userId, String deptId, String taskName, Object reason);

 @Transactional
 @Modifying
 @Query(value = " UPDATE RZTSYSUSER SET LOGINSTATUS = 0 WHERE id=?1 ", nativeQuery = true)
 int quitUserLOGINSTATUS(String id);

 /**
  *更改二级表中的登录状态
  */
 @Transactional
 @Modifying
 @Query(value = " UPDATE MONITOR_CHECK_EJ SET USER_LOGIN_TYPE = ?1 WHERE USER_ID = ?2 ", nativeQuery = true)
 void updateMonitorCheckEjUserLoginType(Integer loginType, String userId);
}
