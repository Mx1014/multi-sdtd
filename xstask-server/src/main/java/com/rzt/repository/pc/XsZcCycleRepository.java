/**    
 * 文件名：XsZcCycleRepository           
 * 版本信息：    
 * 日期：2017/12/07 07:50:10    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository.pc;

import com.rzt.entity.pc.XsZcCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 类名称：XsZcCycleRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 07:50:10 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 07:50:10    
 * 修改备注：    
 * @version        
 */
@Repository
public interface XsZcCycleRepository extends JpaRepository<XsZcCycle,String> {
    @Modifying
    @Transactional
    @Query(value = "update xs_zc_cycle set is_delete = 1 where id in (?1)", nativeQuery = true)
    void logicalDelete(List<Long> longs);


    @Modifying
    @Transactional
    @Query(value = "update xs_zc_cycle set cycle = ?2, in_use = ?3,plan_xs_num = ?4,plan_start_time = ?5,plan_end_time = ?6,is_kt = ?7,cm_user_id = ?8,td_org = ?9,wx_org = ?10,group_id = ?11,class_id = ?12 where id= ?1", nativeQuery = true)
    void updateCycle(Long id, Integer cycle, Integer inUse, Integer planXsNum, String planStartTime, String planEndTime, Integer isKt, String cm_user_id, Object deptid, Object companyid, Object groupid, Object classid);


    @Modifying
    @Transactional
    @Query(value = "update xs_zc_cycle set TOTAL_TASK_NUM = 1,class_id = ?2,wx_org = ?3,cm_user_id = ?4,td_org = ?5 where id= ?1", nativeQuery = true)
    void updateTotalTaskNum(Long xsZcCycleId, String classid, String companyid, String userId, String deptid);


    @Modifying
    @Transactional
    @Query(value = "update xs_zc_task set is_delete = 1 where id in (?1)", nativeQuery = true)
    void logicalDeletePlan(Long[] ids);

    @Modifying
    @Transactional
    @Query(value = "update xs_zc_cycle set TOTAL_TASK_NUM = 1 where id in (?1)", nativeQuery = true)
    void updateCycleTotalBornNum(long id);

    @Modifying
    @Transactional
    @Query(value = "update xs_zc_cycle set task_name = ?2 where id = ?1", nativeQuery = true)
    void updatetaskname(long id, String taskName);

    @Modifying
    @Transactional
    @Query(value = "update xs_zc_task set task_name = ?2 where id = ?1", nativeQuery = true)
    void updatetaskname2(long id, String taskName);


    @Modifying
    @Transactional
    @Query(value = "update xs_zc_cycle set cycle = ?2, in_use = ?3,plan_xs_num = ?4,plan_start_time = ?5,plan_end_time = ?6,is_kt = ?7 where id= ?1", nativeQuery = true)
    void updateCycleTwo(Long id, Integer cycle, Integer inUse, Integer planXsNum, String planStartTime, String planEndTime, Integer isKt);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO XS_ZC_CYCLE_RECORD (id, XS_ZC_CYCLE_ID, XS_ZC_CYCLE, PLAN_XS_NUM, CHANGE_REASON, DESCRIPTION, PROPOSER_ID, PROPOSER_TIME, PROPOSER_STATUS,PROPOSER_TYPE) values (?1,?2,?3,?4,?5,?6,?7,sysdate,?8,?9)", nativeQuery = true)
    void insertCycleRecord(Long id,Long xsZcCycleId,int cycle,int planXsNum,String changeReason,String description,String proposerId,int proposerStatus,int proposerType);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM MONITOR_CHECK_EJ WHERE TASK_ID IN (?1)  AND TASK_TYPE=1", nativeQuery = true)
    void deleteMonitorEj(Long[] ids);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM MONITOR_CHECK_YJ WHERE TASK_ID IN (?1)  AND TASK_TYPE=1", nativeQuery = true)
    void deleteMonitorYj(Long[] ids);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM ALARM_NOT_ON_TIME_TASK WHERE TASK_ID IN(?1)", nativeQuery = true)
    void deleteAlarmNotNoTimeTask(Long[] ids);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM ALARM_UNQUALIFIEDPATROL WHERE TASK_ID IN(?1)", nativeQuery = true)
    void deleteAlarmUnqualifiedpatrol(Long[] ids);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM ALARM_OVERDUE WHERE TASK_ID IN(?1)", nativeQuery = true)
    void deleteAlarmOVERDUE(Long[] ids);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO XS_ZC_CYCLE_RECORD (id, XS_ZC_CYCLE_ID, XS_ZC_CYCLE, PLAN_XS_NUM, CHANGE_REASON, DESCRIPTION, proposer_id, PROPOSER_TIME, PROPOSER_STATUS, PROPOSER_TYPE)\n" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, sysdate, 0, 1)", nativeQuery = true)
    void addXsCycleRecord(long newId, Long id, Integer cycle, Integer planXsNum, String changeReson, String description, String currentUserId);

    @Modifying
    @Transactional
    @Query(value = "update xs_zc_cycle set PROPOSER_STATUS = 1 where id = ?", nativeQuery = true)
    void updateXsCycleProposerStatus(long id);
}