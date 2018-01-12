package com.rzt.repository;

import com.rzt.entity.TimedConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface TimedConfigRepository  extends JpaRepository<TimedConfig, String> {

    @Modifying
    @Query(value = " UPDATE TIMED_CONFIG SET NIGHT_ZQ = ?1  , DAY_ZQ = ?2  , START_TIME = ?3, END_TIME = ?4 WHERE ID = ?5", nativeQuery = true)
    int updateTimedConfig(String nightTime, String daytime, String startTime, String endTime,String id);
}
