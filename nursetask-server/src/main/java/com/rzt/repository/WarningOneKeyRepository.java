package com.rzt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.rzt.entity.WarningOneKey;
import org.springframework.web.bind.annotation.ResponseBody;

@Repository
public interface WarningOneKeyRepository extends JpaRepository<WarningOneKey, String> {

    @Modifying
    @Query(value = "UPDATE     SET          =?1 where id =?2 ",nativeQuery = true)
    void updateWaring(Long waringId,long id);
}
