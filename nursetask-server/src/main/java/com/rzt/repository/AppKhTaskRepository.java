package com.rzt.repository;

import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.entity.KhTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by admin on 2017/12/17.
 */

public interface AppKhTaskRepository extends JpaRepository<KhTask, String> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE KH_TASK SET REAL_START_TIME = ?2,STATUS ='进行中' where id = ?1",nativeQuery = true)
    void updateRealStartTime(Long taskId, Date date);

    @Modifying
    @Transactional
    @Query(value = "UPDATE KH_TASK SET WPQR_TIME = ?2 where id = ?1",nativeQuery = true)
    void updateWpqrTime(long taskId, Date date);

    @Modifying
    @Transactional
    @Query(value = "update kh_task set ddxc_time = ?1 where id = ?2",nativeQuery = true)
    void updateDDTime(Date time, long id);

    @Modifying
    @Transactional
    @Query(value = "update kh_task set sfqr_time = ?1 where id = ?2",nativeQuery = true)
    void updateSFQRTime(Date time, long id);

 /*   @Query(value = "select count(*) from KH_TASK WHERE (STATUS LIKE '未开始' OR  status like '进行中') AND user_id = ?",nativeQuery = true)
    int getdbCount(String userId);

    @Query(value = "select count(*) from KH_TASK WHERE STATUS LIKE '已完成' AND user_id = ?",nativeQuery = true)
    int getybCount(String userId);*/
    /*@Query(value = "",nativeQuery = true)
    void query();*/
}
