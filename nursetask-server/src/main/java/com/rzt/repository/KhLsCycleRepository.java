package com.rzt.repository;

import com.rzt.entity.KhCycle;
import com.rzt.entity.KhLsCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by admin on 2018/1/20.
 */
@Repository
public interface KhLsCycleRepository extends JpaRepository<KhLsCycle,String> {

    @Query(value ="SELECT * FROM KH_LS_CYCLE  where id=?1",nativeQuery = true)
    KhLsCycle findCycle(long l);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM KH_LS_CYCLE WHERE id=?1",nativeQuery = true)
    void deleteCycle(long l);

    @Modifying
    @Query(value = "UPDATE  KH_LS_CYCLE SET WX_ORG=?1,WX_ORGID=?2 WHERE id=?3",nativeQuery = true)
    void updateCycle(String wxname, String wxid, long id);
}
