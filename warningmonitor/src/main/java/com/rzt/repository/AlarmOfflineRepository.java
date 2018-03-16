package com.rzt.repository;

import com.rzt.entity.AlarmOffline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
public interface AlarmOfflineRepository extends JpaRepository<AlarmOffline, String> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO ALARM_OFFLINE(ID,USER_ID,OFFLINE_FREQUENCY,OFFLINE_TIME_LONG,CREATE_TIME,OFFLINE_END_TIME,LAST_FLUSH_TIME) " +
            "VALUES (?1,?2,1,?3,sysdate,?4,sysdate)",nativeQuery = true)
    void addoffLine(Long id,String userId,Long timeLong,Date endTime);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ALARM_OFFLINE SET OFFLINE_FREQUENCY=?2,OFFLINE_TIME_LONG=?3,OFFLINE_END_TIME=?4,LAST_FLUSH_TIME=sysdate " +
            "WHERE ID=?1 AND trunc(CREATE_TIME)=trunc(sysdate) ",nativeQuery = true)
    void updateoffLine(Long id,Integer frequency,Long timeLong,Date endTime);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ALARM_OFFLINE SET OFFLINE_TIME_LONG=?2,LAST_FLUSH_TIME=sysdate " +
            "WHERE USER_ID=?1 AND trunc(CREATE_TIME)=trunc(sysdate) ",nativeQuery = true)
    void updateoffLineTime(String userId,Long timeLong);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO ALARM_UNQUALIFIEDPATROL(ID,ALARM_TIME,TASK_ID,USER_ID,IS_DW_TOUR,UNQUALIFIED_REASONS,CREATE_TIME)\n" +
            "    VALUES (?1,?2,?3,?4,?5,?6,sysdate)",nativeQuery = true)
    void addBuDaoWei(Long id,Date warningTime,Long taskId, String userId, Integer isDwTour, String reason);

    //更新不到位塔的个数
    @Transactional
    @Modifying
    @Query(value = "UPDATE ALARM_UNQUALIFIEDPATROL SET IS_DW_TOUR=?2 WHERE TASK_ID=?1 AND trunc(CREATE_TIME)=trunc(sysdate)",nativeQuery = true)
    void updateBuDaoWeiTour(String taskId, Integer tour);
}
