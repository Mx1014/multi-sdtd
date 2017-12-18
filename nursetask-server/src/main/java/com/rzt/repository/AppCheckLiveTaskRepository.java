package com.rzt.repository;

import com.rzt.entity.CheckLiveTaskDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by admin on 2017/12/16.
 */
public interface AppCheckLiveTaskRepository extends JpaRepository<CheckLiveTaskDetail, String> {
    @Modifying
    @Query(value = "UPDATE CHECK_LIVE_TASK_DETAIL SET DDXC_TIME = ?3 ,status = 1,REAL_START_TIME = ?3,kh_task_id = ?2 WHERE id = ?4", nativeQuery = true)
    void updateDdxcTime(String userId, Long taskId, Date date, Long detailId);

    @Modifying
    @Query(value = "UPDATE CHECK_LIVE_TASK_DETAIL SET SFZG = ?1,RYYZ = ?2 WHERE id = ?3", nativeQuery = true)
    void updateDgdwCheck(String sfzg, String ryyz, Long execId);

    @Modifying
    @Query(value = "UPDATE CHECK_LIVE_TASK SET DZWL = ?2 WHERE id = ?1", nativeQuery = true)
    void updateDzwl(Long taskId, String dydj);

    /*@Query(value = "", nativeQuery = true)
    void updateDdxcTSSime(String userId, Long taskId, Date date, Long detailId);*/

    @Modifying
    @Query(value = "UPDATE CHECK_LIVE_TASK_DETAIL SET DYDJ=?2,YHXX = ?3,CZFA=?4,QTWT=?5,REAL_END_TIME=?6,STATUS=2  WHERE id = ?1", nativeQuery = true)
    void completeTask(Long taskId, String dydj, String yhxx, String czfa, String qtwt, Date date);
}
