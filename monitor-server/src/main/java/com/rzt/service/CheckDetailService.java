package com.rzt.service;

import com.rzt.entity.CheckDetail;
import com.rzt.repository.CheckDetailRepository;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
public class CheckDetailService extends CurdService<CheckDetail,CheckDetailRepository> {
	
	@Autowired
	private CheckDetailRepository checkDetailRepository;

	@Transactional
	public Long addCheckDetail(CheckDetail checkDetail){
		//生成id
		Long id = Long.valueOf(new SnowflakeIdWorker(0,0).nextId());
		//为checkDetail设置id
		checkDetail.setId(id);
		//添加创建时间
		checkDetail.setCreateTime(new Date());
		checkDetailRepository.save(checkDetail);
		return id;
	}
	
	public Long findByCheckUserAndQuestionTaskId(String checkUser, Long questionTaskId) {
		return checkDetailRepository.findByCheckUserAndQuestionTaskId(checkUser, questionTaskId);
	}

	

	
}
