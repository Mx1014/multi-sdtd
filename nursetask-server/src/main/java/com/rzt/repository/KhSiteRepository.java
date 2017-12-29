/**
 * 文件名：KhCycleRepository
 * 版本信息：
 * 日期：2017/11/28 14:43:44
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository;

import com.rzt.entity.KhCycle;
import com.rzt.entity.KhSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 类名称：KhCycleRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 14:43:44 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 14:43:44    
 * 修改备注：    
 * @version
 */
@Repository
public interface KhSiteRepository extends JpaRepository<KhSite, String> {

    @Modifying
    @Query(value = "update kh_site set status=2,khxq_time=?2 where id= ?1", nativeQuery = true)
    @Transactional
    int updateQxTask(long id, Date date);

    @Modifying
    @Query(value = "update kh_task set status='已完成',plan_end_time=?2 where site_id= ?1 and status in ('未开始','进行中')", nativeQuery = true)
    @Transactional
    void updateDoingTask(long id, Date date);

    @Modifying
    @Query(value = "update kh_site set khfzr_id1=?2,khdy_id1=?3,khfzr_id2=?4,khdy_id2=?5 where id= ?1", nativeQuery = true)
    void updateSite(long id, String khfzrId1, String khdyId1, String khfzrId2, String khdyId2);

    @Modifying
    @Query(value = "update kh_yh_history set yhzt=1,YHXQ_TIME=?2 where id=?1", nativeQuery = true)
    @Transactional
    void updateYH(Long id, Date date);

    @Modifying
    @Query(value = "update check_live_task set status=2,update_time=?2 where task_id=?1", nativeQuery = true)
    @Transactional
    void updateCheckTask(long id, Date date);

    @Query(value = "select count(*) from kh_site where status = ?1 ", nativeQuery = true)
    int getCount(String status);

    /*@Query(value = "select * from kh_site where id = ?",nativeQuery = true)
    KhSite site(long id);*/

    @Modifying
    @Query(value = "DELETE FROM KH_CYCLE  where id=?1", nativeQuery = true)
    void deleteById(long id);


    @Modifying
    @Query(value = "update kh_cycle set xq_time = sysdate,status=2 where id = ?1", nativeQuery = true)
    @Transactional
    void updateKhCycle(long id);

    @Modifying
    @Query(value = "update kh_cycle set status=1,pf_time = sysdate where id = ?1", nativeQuery = true)
    void updateCycleById(String id);

    @Modifying
    @Query(value = "update kh_cycle set JC_USER_ID=?1,JC_STATUS=?2 where id=?3", nativeQuery = true)
    void updateKhSite(String jcUserId, Integer jcStatus, Long khSiteId);

    @Modifying
    @Query(value = "update kh_cycle set JC_STATUS=0", nativeQuery = true)
    void updateKhSiteJcStatus();

    @Query(value = "select longitude,latitude from cm_tower where id = ?", nativeQuery = true)
    void findStartTower(String startTower);

    @Query(value ="SELECT * FROM KH_SITE  where id=?1",nativeQuery = true)
        //SELECT id,VTYPE,LINE_NAME,SECTION,status,line_id,KH_RANGE,to_date(KHXQ_TIME,'yyyy-mm-dd hh24:mi:ss'),to_date(create_time,'yyyy-mm-dd hh24:mi:ss'),KHFZR_ID1,KHFZR_ID2,KHDY_ID1,KHDY_ID2,TDYW_ORG,YH_ID,TASK_NAME,COUNT FROM KH_SITE  where id=?1
    KhSite findSite(long id);
}
