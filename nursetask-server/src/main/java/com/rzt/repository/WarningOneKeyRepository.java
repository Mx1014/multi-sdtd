package com.rzt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.rzt.entity.WarningOneKey;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

@Repository
public interface WarningOneKeyRepository extends JpaRepository<WarningOneKey, String> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE  PICTURE_WARN  SET TASK_ID =?1 where id =?2 ",nativeQuery = true)
    void updateWaring(Long waringId,long id);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO WARNING_ONE_KEY(ID,GJLX,GJMS,LON,LAT,USER_ID,CREATE_TIME)  VALUES(?1,?2,?3,?4,?5,?6,sysdate)",nativeQuery = true)
    void insertWarn(Long id, String gjlx, String gjms, String lon, String lat, String userId);
}
