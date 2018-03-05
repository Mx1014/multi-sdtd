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

import java.util.Locale;

/**
 * 类名称：KHYHHISTORYRepository
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/11/30 18:31:34
 * 修改人：张虎成
 * 修改时间：2017/11/30 18:31:34
 * 修改备注：
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
    @Query(value = "update KH_YH_HISTORY SET YHMS=?2,START_TOWER=?3,end_tower=?4,yhzrdw=?5,yhzrdwlxr=?6,yhzrdwdh=?7,section=?8,UPDATE_TIME=sysdate WHERE ID=?1", nativeQuery = true)
    void updateYhHistory(Long id, String yhms, String startTower, String endTower, String yhzrdw, String yhzrdwlxr, String yhzrdwlxdh, String section);

    @Modifying
    @Transactional
    @Query(value = "update KH_YH_HISTORY SET YHMS=?2,yhzrdw=?3,yhzrdwlxr=?4,yhzrdwdh=?5,YHJB1=?6,YHLB=?7,GKCS=?8,UPDATE_TIME=sysdate WHERE ID=?1", nativeQuery = true)
    void updateYhHistory2(Long id, String yhms, String yhzrdw, String yhzrdwlxr, String yhzrdwdh,String yhjb,String yhlb,String gkcs);


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

    @Modifying
    @Transactional
    @Query(value = "update CM_TOWER SET LONGITUDE=?2,LATITUDE=?3,STATUS=1 WHERE ID=?1", nativeQuery = true)
    void updateTowerById(long id, String lon, String lat);

    @Modifying
    @Transactional
    @Query(value = "update KH_CYCLE SET STATUS=2,XQ_TIME=sysdate WHERE YH_ID=?1", nativeQuery = true)
    void updateKhCycle(long yhId);

    @Modifying
    @Transactional
    @Query(value = "update KH_CYCLE SET SECTION=?2,TASK_NAME=?3 WHERE YH_ID=?1", nativeQuery = true)
    void updateKhCycle2(long yhId, String section, String taskName);

    @Modifying
    @Transactional
    @Query(value = "update KH_SITE SET SECTION=?2,TASK_NAME=?3 WHERE YH_ID=?1", nativeQuery = true)
    void updateKhSite(long yhId, String section, String taskName);

    @Modifying
    @Transactional
    @Query(value = "update KH_TASK SET TASK_NAME=?2 WHERE YH_ID=?1 AND PLAN_START_TIME >=trunc(sysdate)", nativeQuery = true)
    void updateKhTask(long yhId, String taskName);

    @Modifying
    @Transactional
    @Query(value = "update KH_TASK SET YWORG_ID=?2 ,WXORG_ID=?3  WHERE ID=?1", nativeQuery = true)
    void addTdOrgId(long id, String deptid, Object wx);

    @Modifying
    @Transactional
    @Query(value = "update KH_YH_HISTORY SET SECTION=?2 WHERE ID=?1", nativeQuery = true)
    void updateyhs(long id, String section);

    @Query(value = "select * from KH_YH_HISTORY WHERE id=?1", nativeQuery = true)
    KhYhHistory finds(long yhId);

    @Modifying
    @Transactional
    @Query(value = "update CM_TOWER_UPDATE_RECORD SET STATUS=1 WHERE ID=?1", nativeQuery = true)
    void deleteRecord(long id);

    @Modifying
    @Transactional
    @Query(value = "update CM_TOWER_UPDATE_RECORD SET STATUS=2 WHERE ID=?1", nativeQuery = true)
    void deleteRecord2(long id);

    @Modifying
    @Query(value = "insert into CHECK_LIVE_SITE(id,TASK_ID,TASK_TYPE,CREATE_TIME,TASK_NAME,STATUS,line_id,TDYW_ORGID,TDWX_ORGID,yh_id)" +
            "values(?1,?2,?3,sysdate,?4,?5,?6,?7,?8,?9)", nativeQuery = true)
    void addCheckSite(long l, Long taskId, int i1, String taskName, int i, Long lineId, String tdywOrgId, String wxOrgId, Long id);
}
