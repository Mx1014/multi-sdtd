package com.rzt.repository;

import com.rzt.entity.KhYhHistory;
import com.rzt.entity.TimedTask;
import com.rzt.util.WebApiResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * 李成阳
 * 2018/1/20
 */
@Repository
public interface YHrepository  extends JpaRepository<KhYhHistory, String> {
    @Query(value = "SELECT * FROM XS_SB_YH WHERE ID = ?1", nativeQuery = true)
    KhYhHistory findYhById(Long YHID);

    @Modifying
    @Query(value = "update XS_ZC_CYCLE_RECORD SET APPROVER_ID = ?2 , APPROVER_TIME = ?3 ,PROPOSER_STATUS = ?4 WHERE XS_ZC_CYCLE_ID = ?1", nativeQuery = true)
    int updateAppId(String xsid, String appId, Date time,String PROPOSER_STATUS);
    @Modifying
    @Query(value = "UPDATE XS_SB_YH SET YHMS = ?2 , YHTDQX = ?3 ,YHTDXZJD = ?4 , YHTDC = ?5 ,GKCS = ?6 ,XCP = ?7 WHERE ID = ?1",nativeQuery = true)
    int perfectYH(String yhid, String yhms, String yhtdqx, String yhtdxzjd, String yhtdc, String gkcs, String xcp);
    @Modifying
    @Query(value = "UPDATE XS_ZC_CYCLE SET CYCLE = ?2 , PLAN_START_TIME = ?3 , PLAN_END_TIME = ?4 , PLAN_XS_NUM = ?5 , CM_USER_ID = ?6 where ID = ?1",nativeQuery = true)
    int updateCycle(String xscycleId, String xsCycle, String planStartTime, String planEndTime, String xsNum, String userId);
    @Modifying
    @Query(value = "UPDATE XS_ZC_CYCLE_RECORD SET PROPOSER_STATUS = 1 ,APPROVER_TIME = ?2 WHERE ID = ?1", nativeQuery = true)
    int updateCycleRecord(String id,Date appTime);
}
