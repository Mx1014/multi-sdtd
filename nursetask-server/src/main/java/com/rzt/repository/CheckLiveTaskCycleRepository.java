package com.rzt.repository;

import com.rzt.entity.CheckLiveTaskCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by admin on 2017/12/5.
 */
@Repository
public interface CheckLiveTaskCycleRepository extends JpaRepository<CheckLiveTaskCycle,String> {
}
