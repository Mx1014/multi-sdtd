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
    @Query(value = "INSERT INTO ALARM_UNQUALIFIEDPATROL(ID,ALARM_TIME,TASK_ID,USER_ID,IS_DW_TOUR,UNQUALIFIED_REASONS,CREATE_TIME,EXEC_ID,ALARM_TYPE)\n" +
            "    VALUES (?1,?2,?3,?4,?5,?6,sysdate,?7,?8)",nativeQuery = true)
    void addBuDaoWei(Long id,Date warningTime,Long taskId, String userId, Integer isDwTour, String reason,Long execId,Integer alarmType);

    //更新不到位塔的个数
    @Transactional
    @Modifying
    @Query(value = "UPDATE ALARM_UNQUALIFIEDPATROL SET IS_DW_TOUR=?2 WHERE TASK_ID=?1 AND trunc(CREATE_TIME)=trunc(sysdate)",nativeQuery = true)
    void updateBuDaoWeiTour(Long taskId, Integer tour);

    //插入综合展示未按时开始任务
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO ALARM_NOT_ON_TIME_TASK(ID,TASK_ID,USER_ID,ALARM_TIME,CREATE_TIME,TASK_TYPE) VALUES (?1,?2,?3,sysdate,sysdate,?4)",nativeQuery = true)
    void addNotNoTimeTask(Long id, Long taskId, String userId,Integer taskType);

    //插入综合展示超期任务
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO ALARM_OVERDUE(ID,ALARM_TIME,TASK_ID,USER_ID) VALUES (?1,sysdate,?2,?3)",nativeQuery = true)
    void addOverdue(Long id, Long taskId, String userId);

    //更改离线状态
    @Transactional
    @Modifying
    @Query(value = "UPDATE ALARM_OFFLINE SET CHECK_STATUS=?2 WHERE USER_ID=?1",nativeQuery = true)
    void updateOffLineStatus(String userId, Integer status);

    //更改巡视不合格状态
    @Transactional
    @Modifying
    @Query(value = "UPDATE ALARM_UNQUALIFIEDPATROL SET AUDIT_STATUS=?3 WHERE USER_ID=?1 AND TASK_ID=?2",nativeQuery = true)
    void updateXS(String userId, Long taskId, Integer status);

    //更改未按时接任务状态
    @Transactional
    @Modifying
    @Query(value = "UPDATE ALARM_NOT_ON_TIME_TASK SET CHECK_STATUS=?3 WHERE USER_ID=?1 AND TASK_ID=?2",nativeQuery = true)
    void updateNotNoTimeStatus(String userId, Long taskId, Integer status);

    //更改超期任务状态
    @Transactional
    @Modifying
    @Query(value = "UPDATE ALARM_OVERDUE SET CHECK_STATUS=?3 WHERE USER_ID=?1 AND TASK_ID=?2",nativeQuery = true)
    void updateOverdue(String userId, Long taskId, Integer status);

    //更改脱岗时长、次数和最后一次刷新时间
    @Transactional
    @Modifying
    @Query(value = "UPDATE ALARM_OFFWORK SET OFFWORK_FREQUENCY=?2,OFFWORK_TIME_LONG=?3,OFFWORK_TIME=?4,LAST_FLUSH_TIME=sysdate,CURRENT_STATUS=1 " +
            "WHERE ID=?1 AND trunc(ALARM_TIME)=trunc(sysdate) ",nativeQuery = true)
    void updateoffWork(Long id,Integer frequency,Long timeLong,Date endTime);

    //添加脱岗
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO ALARM_OFFWORK(ID,USER_ID,OFFWORK_FREQUENCY,OFFWORK_TIME_LONG,ALARM_TIME,OFFWORK_TIME,LAST_FLUSH_TIME,TASK_ID) " +
            " VALUES (?1,?2,1,?3,sysdate,?4,sysdate,?5)",nativeQuery = true)
    void addoffWork(Long id,String userId,Long timeLong,Date endTime,Long taskId);
    //更新脱岗时长，不更新次数
    @Transactional
    @Modifying
    @Query(value = "UPDATE ALARM_OFFWORK SET OFFWORK_TIME_LONG=?2,LAST_FLUSH_TIME=sysdate " +
            "WHERE USER_ID=?1 AND TASK_ID=?3 AND trunc(ALARM_TIME)=trunc(sysdate) ",nativeQuery = true)
    void updateoffWorkTime(String userId,Long timeLong,Long taskId);

    //更改脱岗处理状态
    @Transactional
    @Modifying
    @Query(value = "UPDATE ALARM_OFFWORK SET CURRENT_STATUS=?3 WHERE USER_ID=?1 AND TASK_ID=?2",nativeQuery = true)
    void updateoffWorkStatus(String userId, Long taskId, Integer status);
}
