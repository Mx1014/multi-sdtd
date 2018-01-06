package com.rzt.repository;

import com.rzt.entity.CheckResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckResultRepository extends JpaRepository<CheckResult,String> {


}
