package com.rzt.repository;

import com.rzt.entity.CheckLiveTaskDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by admin on 2017/12/5.
 */
@Repository
public interface CheckLiveTaskDetailRepository extends JpaRepository<CheckLiveTaskDetail,String> {

   /* @Query(value = "select * FROM CHECK_LIVE_TASK",nativeQuery = true)
    void findCheckById();*/

}
