package com.rzt.repository;

import com.rzt.entity.CheckLiveTaskCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by admin on 2017/12/5.
 */
@Repository
public interface CheckLiveTaskCycleRepository extends JpaRepository<CheckLiveTaskCycle,String> {
    @Query(value ="SELECT * FROM Chenk_Live_Task_Cycle  where id=?1",nativeQuery = true)
    CheckLiveTaskCycle findCycle(long id);

    @Modifying
    @Query(value ="UPDATE CHENK_LIVE_TASK_CYCLE set TASK_NAME=?1,TASK_TYPE=?2,USER_ID=?3,CREATE_TIME=?4,PLAN_START_TIME=?5,CHECK_CYCLE=?6  where id=?7",nativeQuery = true)
    void updateCycle(String taskName, String taskType, String userId, Date createTime, Date planStartTime, String checkCycle, Long cycleId);
}
