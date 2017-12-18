package com.rzt.repository;

import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.entity.KhTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * Created by admin on 2017/12/17.
 */
public interface AppKhTaskRepository extends JpaRepository<KhTask, String> {

    @Modifying
    @Query(value = "UPDATE KH_TASK SET REAL_START_TIME = ?2,STATUS='进行中' where id = ?1",nativeQuery = true)
    void updateRealStartTime(Long taskId, Date date);

    @Modifying
    @Query(value = "UPDATE KH_TASK SET WPQR_TIME = ?2 where id = ?1",nativeQuery = true)
    void updateWpqrTime(long taskId, Date date);

    @Modifying
    @Query(value = "update kh_task set ddxc_time = ?1 where id = ?2",nativeQuery = true)
    void updateDDTime(Date time, long id);

    @Modifying
    @Query(value = "update kh_task set sfqr_time = ?1 where id = ?2",nativeQuery = true)
    void updateSFQRTime(Date time, long id);
    /*@Query(value = "",nativeQuery = true)
    void query();*/
}
