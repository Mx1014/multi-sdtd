package com.rzt.repository;

import com.rzt.entity.TimedTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * 李成阳
 * 2018/1/2
 */
public interface XSZCTASKRepository extends JpaRepository<TimedTask, String> {

    @Modifying
    @Query(value = " INSERT INTO TIMED_TASK (TARGETSTATUS,CREATETIME,TASKID,ID,USER_ID,TASKTYPE,TASKNAME,CHECKSTATUS) VALUES (?1, TO_DATE(?2,'yyyy-mm-dd hh24:mi:ss'), ?3,?4,?5,?6,?7,?8)  ", nativeQuery = true)
    int xsTaskAdd(String STAUTS, String REAL_START_TIME, String TASKID, String id,String CM_USER_ID,String TASKTYPE,String TASK_NAME,Integer CheckStatus);


    @Modifying
    @Query(value = "update TIMED_TASK SET STATUS = 1 WHERE TASKID = ?1", nativeQuery = true)
    int xsTaskUpdate(Long TASKID);



}
