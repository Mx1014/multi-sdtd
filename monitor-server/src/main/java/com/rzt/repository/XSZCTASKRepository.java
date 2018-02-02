package com.rzt.repository;

import com.rzt.entity.TimedTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface XSZCTASKRepository extends JpaRepository<TimedTask, String> {

    @Modifying
    @Query(value = " INSERT INTO TIMED_TASK (TARGETSTATUS,CREATETIME,TASKID,ID,USER_ID,TASKTYPE,TASKNAME,CHECKSTATUS,THREEDAY) VALUES (?1, TO_DATE(?2,'yyyy-mm-dd hh24:mi:ss'), ?3,?4,?5,?6,?7,?8,?9)  ", nativeQuery = true)
    int xsTaskAdd(String STAUTS, String REAL_START_TIME, String TASKID, String id,String CM_USER_ID,String TASKTYPE,String TASK_NAME,Integer CheckStatus,String threeDay);

 //,Date picTime,String currentUserId
    //, PIC_TIME = ?2 , CHECK_USER_ID = ?3
    @Modifying
    @Query(value = "update TIMED_TASK SET STATUS = 1  WHERE ID = ?1", nativeQuery = true)
    int xsTaskUpdate(String ID);


}
