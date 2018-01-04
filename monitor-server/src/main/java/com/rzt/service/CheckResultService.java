package com.rzt.service;

import com.rzt.entity.CheckDetail;
import com.rzt.entity.CheckResult;
import com.rzt.repository.CheckResultRepository;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service

public class CheckResultService extends CurdService<CheckResult, CheckResultRepository> {
	
	@Autowired
	private CheckResultRepository checkResultRepository;
	
	/*
	 * 添加审核结果
	 */
	@Transactional
	public void addResult(CheckResult checkResult){
		//为checkResult设置id
		checkResult.setId(Long.valueOf(new SnowflakeIdWorker(0,0).nextId()));
		//添加创建时间
		checkResult.setCreateTime(new Date());
		checkResultRepository.save(checkResult);
	}
	
	public Page<Map<String,Object>> getCheckResult(Integer page, Integer size, CheckDetail checkDetail) {
		Pageable pageable = new PageRequest(page, size);
		String sql = "select * from check_result_view t"+
				" where 1=1";
		List<Object> list = new ArrayList<Object>();
		if(checkDetail!=null){
			if(checkDetail.getTdOrg()!=null && !"".equals(checkDetail.getTdOrg().trim())){
				list.add(checkDetail.getTdOrg());
				sql+=" and t.td_org = ?"+list.size();
			}
			if(checkDetail.getCheckUser()!=null && !"".equals(checkDetail.getCheckUser().trim())){
				list.add(checkDetail.getCheckUser());
				sql+=" and t.check_user = ?"+list.size();
			}
			if(checkDetail.getCheckOrg()!=null && !"".equals(checkDetail.getCheckOrg().trim())){
				list.add(checkDetail.getCheckOrg());
				sql+=" and t.check_org = ?"+list.size();
			}
			if(checkDetail.getQuestionTaskId()!=null){
				list.add(checkDetail.getQuestionTaskId());
				sql+=" and t.question_task_id = ?"+list.size();
			}
		}
		Page<Map<String,Object>> execSqlPage = this.execSqlPage(pageable, sql, list.toArray());
		return execSqlPage;
	}
	
}
