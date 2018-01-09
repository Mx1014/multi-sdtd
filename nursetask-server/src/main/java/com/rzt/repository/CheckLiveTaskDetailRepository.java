package com.rzt.repository;

import com.rzt.entity.CheckLiveTaskDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by admin on 2017/12/5.
 */
@Repository
public interface CheckLiveTaskDetailRepository extends JpaRepository<CheckLiveTaskDetail,String> {
    @Modifying
    @Query(value = "update check_live_taskxs set WPTS=?2 where id =?1",nativeQuery = true)
    void updateWptsById(Long id, String str);
}
