package com.rzt.repository;

import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.entity.KhTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by admin on 2017/12/17.
 */
public interface AppKhTaskRepository extends JpaRepository<KhTask, String> {

    /*@Query(value = "",nativeQuery = true)
    void query();*/
}
