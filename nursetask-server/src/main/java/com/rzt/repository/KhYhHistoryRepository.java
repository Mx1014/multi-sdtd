/**
 * 文件名：KHYHHISTORYRepository
 * 版本信息：
 * 日期：2017/11/30 18:31:34
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository;

import com.rzt.entity.KhYhHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 类名称：KHYHHISTORYRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/30 18:31:34 
 * 修改人：张虎成    
 * 修改时间：2017/11/30 18:31:34    
 * 修改备注：    
 * @version
 */
@Repository
public interface KhYhHistoryRepository extends JpaRepository<KhYhHistory, String> {

    @Modifying
    @Query(value = "UPDATE PICTURE_YH SET YH_ID = ?2,TASK_ID=?3,YH_ORIGIN = 0,PROCESS_TYPE=0 WHERE ID =?1", nativeQuery = true)
    void updateYhPicture(long id, Long yhId, long xstaskId);

    @Modifying
    @Query(value = "UPDATE PICTURE_YH SET YH_ID = ?2,YH_ORIGIN = 0 WHERE ID =?1", nativeQuery = true)
    void updatePicture(long id, Long yhId);

    @Modifying
    @Transactional
    @Query(value = "update KH_YH_HISTORY SET JD=?3,WD=?2,RADIUS=?4 WHERE ID=?1", nativeQuery = true)
    void updateYh(long yhId, String lat, String lon, String radius);


    @Modifying
    @Transactional
    @Query(value = "update KH_CYCLE SET LATITUDE=?2,LONGITUDE=?3,RADIUS=?4 WHERE YH_ID=?1", nativeQuery = true)
    void updateCycle(long yhId, String lat, String lon, String radius);

    KhYhHistory findById(Long id);

    @Modifying
    @Transactional
    @Query(value = "update KH_YH_HISTORY SET JD=?2,WD=?3 WHERE ID=?1", nativeQuery = true)
    void xiugai(long id, String lon, String lat);

    @Modifying
    @Transactional
    @Query(value = "update KH_YH_HISTORY SET sjxl=?2 WHERE ID=?1", nativeQuery = true)
    void updateyh(long id, String xl);

    @Modifying
    @Transactional
    @Query(value = "update kh_site SET USER_ID=?2 WHERE ID=?1", nativeQuery = true)
    void updates(long id, String id1);

    @Modifying
    @Transactional
    @Query(value = "update KH_YH_HISTORY SET YHMS=?2,START_TOWER=?3,end_tower=?4,yhzrdw=?5,yhzrdwlxr=?6,yhzrdwdh=?7,section=?8 WHERE ID=?1", nativeQuery = true)
    void updateYhHistory(Long id, String yhms, String startTower, String endTower, String yhzrdw, String yhzrdwlxr, String yhzrdwlxdh, String section);

    @Modifying
    @Transactional
    @Query(value = "update KH_YH_HISTORY SET YHMS=?2,yhzrdw=?3,yhzrdwlxr=?4,yhzrdwdh=?5 WHERE ID=?1", nativeQuery = true)
    void updateYhHistory2(Long id, String yhms, String yhzrdw, String yhzrdwlxr, String yhzrdwdh);


    @Modifying
    @Transactional
    @Query(value = "update KH_YH_HISTORY SET class_id=?2 WHERE ID=?1", nativeQuery = true)
    void updatess(String id, Long id1);

    @Modifying
    @Transactional
    @Query(value = "update KH_LS_CYCLE SET STATUS=2 WHERE YH_ID=?1", nativeQuery = true)
    void updateLsCycle(Long id);

    @Modifying
    @Transactional
    @Query(value = "update KH_YH_HISTORY SET YHZT=1 WHERE ID=?1", nativeQuery = true)
    void deleteYhById(long yhId);
}
