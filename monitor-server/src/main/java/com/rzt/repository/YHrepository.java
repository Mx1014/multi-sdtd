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
}
