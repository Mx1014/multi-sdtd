/**    
 * 文件名：WarningOffPostUserRepository           
 * 版本信息：    
 * 日期：2017/12/27 03:58:05    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;

import com.rzt.entity.OffPostUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 类名称：WarningOffPostUserRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/27 03:58:05 
 * 修改人：张虎成    
 * 修改时间：2017/12/27 03:58:05    
 * 修改备注：
 * @version        
 */
 @Repository
public interface OffPostUserRepository extends JpaRepository<OffPostUser,String> {
 @Query(value = "select * from warning_off_post_user where user_id=?1 and task_id=?2",nativeQuery = true)
 OffPostUser findByUserIdAndTaskId(String userId, Long taskId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE WARNING_OFF_POST_USER_TIME SET TIME_STATUS=1,OVER_STATUS=1 WHERE FK_TASK_ID=?1 AND FK_USER_ID=?2 AND ID=?3",nativeQuery = true)
    int updateTimeStatus(Object fk_task_id, Object fk_user_id,Long id);

    //看护脱岗告警
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO MONITOR_CHECK_EJ (ID,CREATE_TIME,TASK_ID,TASK_TYPE,WARNING_TYPE,USER_ID,DEPTID,TASK_NAME) " +
            "VALUES(?1,sysdate,?2,?3,?4,?5,?6,?7)",nativeQuery = true)
    void saveCheckEj(long ID, Long taskId, Integer taskType, Integer warnintType, String userId, String deptId, String taskName);

    //人员回岗后，将ALARM_OFFWORK中的状态置为回岗
    @Transactional
    @Modifying
    @Query(value = "UPDATE ALARM_OFFWORK SET CURRENT_STATUS=0 WHERE USER_ID=?1 AND TASK_ID=?2",nativeQuery = true)
    void updateAlarmOffWorkStatus(String userId, Long taskId);

   //更改脱岗时长、次数和最后一次刷新时间
   @Transactional
   @Modifying
   @Query(value = "UPDATE ALARM_OFFWORK SET OFFWORK_FREQUENCY=?2,OFFWORK_TIME_LONG=?3,OFFWORK_TIME=?4,LAST_FLUSH_TIME=sysdate,CURRENT_STATUS=1 " +
           "WHERE ID=?1 AND trunc(ALARM_TIME)=trunc(sysdate) ",nativeQuery = true)
   void updateoffWork(Long id,Integer frequency,Long timeLong,Date endTime);

   //添加脱岗
   @Transactional
   @Modifying
   @Query(value = "INSERT INTO ALARM_OFFWORK(ID,USER_ID,OFFWORK_FREQUENCY,OFFWORK_TIME_LONG,ALARM_TIME,OFFWORK_TIME,LAST_FLUSH_TIME,TASK_ID) " +
           " VALUES (?1,?2,1,?3,sysdate,?4,sysdate,?5)",nativeQuery = true)
   void addoffWork(Long id,String userId,Long timeLong,Date endTime,Long taskId);

}
