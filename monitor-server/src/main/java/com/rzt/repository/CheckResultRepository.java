package com.rzt.repository;

import com.rzt.entity.CheckResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckResultRepository extends JpaRepository<CheckResult,String> {

    @Modifying
    @Query("UPDATE CheckResult SET PHOTO_IDS = ?3 WHERE ID = ?1 AND QUESTION_TYPE = ?2 ")
    void updateByCheckId(String id, String questionType, String photoIds);
}
