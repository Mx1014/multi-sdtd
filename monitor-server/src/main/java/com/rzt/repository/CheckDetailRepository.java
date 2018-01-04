package com.rzt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rzt.entity.CheckDetail;
import com.rzt.entity.CheckResult;

@Repository
public interface CheckDetailRepository extends JpaRepository<CheckDetail,String> {
	
	@Query("select t.id from CheckDetail t where t.checkUser = ?1 and t.questionTaskId = ?2")
	Long findByCheckUserAndQuestionTaskId(String checkUser, Long questionTaskId);

	

}
