package com.rzt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rzt.entity.CheckResult;

@Repository
public interface CheckResultRepository extends JpaRepository<CheckResult,String> {

	

}
