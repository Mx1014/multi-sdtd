/**    
 * 文件名：KhTaskRepository           
 * 版本信息：    
 * 日期：2017/11/28 14:43:44    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import com.rzt.entity.KhTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 类名称：KhTaskRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 14:43:44 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 14:43:44    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface KhTaskRepository extends JpaRepository<KhTask,String> {



    @Query(value = "SELECT COUNT(*) FROM kh_task where status != 3",nativeQuery = true)
    int getcount();

    @Query(value = "SELECT COUNT(*) FROM kh_task where site_id=?1 and user_id=?2",nativeQuery = true)
    int getCount(long id, String userId);



    @Modifying
    @Query(value = "update kh_task set wpqr_time = ?1 where id = ?2",nativeQuery = true)
    void updateWPQRTime(Date time, long id);

    @Modifying
    @Query(value = "update kh_task set real_start_time = ?1 where id = ?2",nativeQuery = true)
    void updateRealStartTime(Date time, long id);

    @Query(value = "SELECT COUNT(*) FROM kh_task where status = 1",nativeQuery = true)
    int getCount2();

  /*  @Modifying
    @Transactional
    @Query(value = "update kh_site set khfzr_id1 = ?2, khfzr_id2 = ?3,khdy_id1 = ?4,khdy_id2 = ?5 where id = ?1",nativeQuery = true)
    void updateTaskById(long id, String khfzrId1, String khfzrId2, String khdyId1, String khdyId2);*/

    @Modifying
    @Transactional
    @Query(value = "update KH_SITE set user_id = ?1 where id = ?2",nativeQuery = true)
    void updateSiteById(String userId, String id);

    @Modifying
    @Transactional
    @Query(value = "update KH_TASK set user_id = ?1 where SITE_ID = ?2",nativeQuery = true)
    void updateTaskBySiteId(String userId, String id);

    @Modifying
    @Transactional
    @Query(value = "update kh_site set PLAN_START_TIME = ?1,PLAN_END_TIME=?2,COUNT=?4 where id = ?3",nativeQuery = true)
    void updateSite(Date startTime, Date endTime, Long id, int count);

    @Modifying
    @Transactional
    @Query(value ="INSERT INTO KH_TASK (ID,SITE_ID,USER_ID,TASK_NAME,YH_ID,CREATE_TIME,STATUS,PLAN_START_TIME,PLAN_END_TIME,WX_ORG,COUNT,TDYW_ORG,ZXYS_NUM) " +
            " VALUES (?1,?2,?3,?4,?5,sysdate,0,?6,?7,?8,?9,?10,0)",nativeQuery = true)
    void addTask(Long id, Long siteId, String userId, String taskName, Long yhId, Date planStartTime, Date planEndTime, String wxOrg, int count, String tdywOrg);


}
