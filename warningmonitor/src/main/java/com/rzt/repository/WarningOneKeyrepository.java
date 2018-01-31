package com.rzt.repository;

import com.rzt.entity.WarningOneKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WarningOneKeyrepository extends JpaRepository<WarningOneKey,String> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE WARNING_ONE_KEY SET STATUS_EJ=1, CHECK_INFO_EJ_Z=?2 WHERE ID=?1",nativeQuery = true)
    int updateGjEj(Long taskId, String checkInfo);
    @Transactional
    @Modifying
    @Query(value = "UPDATE WARNING_ONE_KEY SET STATUS_YJ=1, CHECK_INFO_YJ_Z=?2 WHERE ID=?1",nativeQuery = true)
    int updateGjYj(Long taskId, String checkInfo);

    @Transactional
    @Modifying
    @Query(value = "UPDATE WARNING_ONE_KEY SET STATUS_EJ=2 ,CHECK_INFO_EJ_C=?2 WHERE ID=?1",nativeQuery = true)
    int updateGjEjc(Long taskId, String checkInfo);
    @Transactional
    @Modifying
    @Query(value = "UPDATE WARNING_ONE_KEY SET STATUS_YJ=2 ,CHECK_INFO_YJ_C=?2 WHERE ID=?1",nativeQuery = true)
    int updateGjYjc(Long taskId, String checkInfo);
}
