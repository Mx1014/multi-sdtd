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
    @Query(value = "UPDATE WARNING_ONE_KEY SET STATUS=1, CHECK_INFO_Z=?2 WHERE ID=?1",nativeQuery = true)
    int updateGj(Long taskId, String checkInfo);

    @Transactional
    @Modifying
    @Query(value = "UPDATE WARNING_ONE_KEY SET STATUS=2 ,CHECK_INFO_C=?2 WHERE ID=?1",nativeQuery = true)
    int updateGjc(Long taskId, String checkInfo);
}
