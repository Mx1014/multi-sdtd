package com.rzt.repository;

import com.rzt.entity.KhYhHistory;
import com.rzt.entity.WarningOneKey;
import com.rzt.entity.XsSbYh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by admin on 2018/1/13.
 */
@Repository
public interface XsSbYhRepository extends JpaRepository<XsSbYh, String> {

    @Query(value = "SELECT * FROM XS_SB_YH WHERE ID = ?1",nativeQuery = true)
    XsSbYh findYh(long yhId);

}
