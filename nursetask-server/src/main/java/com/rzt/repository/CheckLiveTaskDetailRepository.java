package com.rzt.repository;

import com.rzt.entity.CheckLiveTaskDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by admin on 2017/12/5.
 */
@Repository
public interface CheckLiveTaskDetailRepository extends JpaRepository<CheckLiveTaskDetail,String> {
}
