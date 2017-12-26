package com.rzt.repository;


import com.rzt.entity.CheckLiveTaskExec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckLiveTaskExecRepository extends JpaRepository<CheckLiveTaskExec,String> {
    @Query(value = "SELECT COUNT(*) FROM check_live_task_exec where user_id=?1",nativeQuery = true)
    long getCount(String userId);
    @Query(value = "SELECT * FROM check_live_task_exec where id=?1",nativeQuery = true)
    CheckLiveTaskExec findExec(long id);
}
