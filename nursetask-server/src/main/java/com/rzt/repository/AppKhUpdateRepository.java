package com.rzt.repository;

import com.rzt.entity.KhTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by admin on 2017/12/22.
 */
@Repository
public interface AppKhUpdateRepository extends JpaRepository<KhTask, String> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE KH_TASK SET REAL_START_TIME = ?2,STATUS =1 where id = ?1 and status !=3", nativeQuery = true)
    void updateRealStartTime(Long taskId, Date date);

    @Modifying
    @Transactional
    @Query(value = "update KH_TASK set ZXYS_NUM = ?1 where id = ?2", nativeQuery = true)
    void updateZxnum(int i, long l);

    @Modifying
    @Transactional
    @Query(value = "UPDATE KH_TASK SET WPQR_TIME = ?2 where id = ?1", nativeQuery = true)
    void updateWpqrTime(long taskId, Date date);

    @Modifying
    @Transactional
    @Query(value = "update kh_task set ddxc_time = ?1,is_dw=?3,reason=?4 where id = ?2", nativeQuery = true)
    void updateDDTime(Date time, Long taskId, int id, String reason);

    @Modifying
    @Transactional
    @Query(value = "update kh_task set sfqr_time = ?1 where id = ?2", nativeQuery = true)
    void updateSFQRTime(Date time, long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE KH_TASK_WPQR SET WP_ZT =?1 WHERE TASKID =?2", nativeQuery = true)
    void updateWp(String wpzt, long taskId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE KH_TASK_WPQR SET CL_ZT =?1 WHERE TASKID =?2", nativeQuery = true)
    void updateClzt(String clzt, long taskId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE KH_TASK SET STATUS = 2,real_end_time = ?1 WHERE id =?2", nativeQuery = true)
    void updateEndTime(Date date, long taskId);

    @Query(value = "select ZXYS_NUM  FROM KH_TASK where id=?1", nativeQuery = true)
    int findNum(long taskId);

    @Modifying
    @Transactional
    @Query(value = "update kh_task set is_dw=?2,reason=?3 where id = ?1", nativeQuery = true)
    void updateIsDz(Long taskId, int i, String reason);

    @Modifying
    @Transactional
    @Query(value = "UPDATE MONITOR_CHECK_EJ SET TASK_STATUS=1 WHERE TASK_ID=?1 AND WARNING_TYPE=8", nativeQuery = true)
    void updateGj(long taskId);
}
