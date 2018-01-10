package com.rzt.repository;

import com.rzt.entity.CheckResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CheckResultRepository extends JpaRepository<CheckResult, String> {
    @Modifying
    @Query(value = " INSERT INTO MONITOR_CHECK_EJ (ID, TASK_ID, CREATE_TIME, TASK_TYPE, STATUS, WARNING_TYPE,USER_ID,DEPTID,TASK_NAME) " +
            "            VALUES (?1, ?2, sysdate, 2, 0, ?3,?4,?5,6) ", nativeQuery = true)
    void xsTourScope(Long id, Long taskid, Integer warningtype, String orgid, String userid, String taskname);

    @Modifying
    @Query("UPDATE CheckResult SET PHOTO_IDS = ?2,QUESTION_INFO=?3 WHERE ID = ?1  ")
    void updateByCheckId(String id, String photoIds, String questionInfo);


}
