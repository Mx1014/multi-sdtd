package com.rzt.service;

import com.rzt.entity.CheckDetail;
import com.rzt.entity.CheckResult;
import com.rzt.repository.CheckResultRepository;
import com.rzt.util.WebApiResponse;
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


	public Object getQuestion(Long questionTaskId) {
        ArrayList<Long> longs = new ArrayList<>();
        longs.add(questionTaskId);
        String sql = "SELECT r.*  " +
				"FROM CHECK_RESULT r LEFT JOIN CHECK_DETAIL d ON r.CHECK_DETAIL_ID = d.ID  " +
				"WHERE d.QUESTION_TASK_ID = ?"+longs.size();
        List<Map<String, Object>> maps = execSql(sql, longs.size());
        return maps;
    }

    public Object getCheckRecord(Integer page, Integer size,String startDate,String endDate,Integer taskType,Integer status) {
        Pageable pageable = new PageRequest(page,size);
        //暂时去数据库查，之后从redis拿就可以
        String sql = "SELECT ud.*,co.COMPANYNAME FROM   " +
				"  (SELECT uc.*,d.DEPTNAME FROM   " +
				"  (SELECT tc.*,u.DEPTID,u.COMPANYID,u.PHONE,u.REALNAME FROM   " +
				"  (SELECT t.TASKID,t.TASKNAME,c.CHECK_USER,c.CREATE_TIME,t.TASKTYPE,t.STATUS FROM TIMED_TASK t   " +
				"    JOIN CHECK_DETAIL c ON t.TASKID = c.QUESTION_TASK_ID WHERE t.STATUS=1) tc   " +
				"LEFT JOIN RZTSYSUSER u ON tc.CHECK_USER = u.ID) uc   " +
				"LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = uc.DEPTID) ud   " +
				"LEFT JOIN RZTSYSCOMPANY co ON ud.COMPANYID = co.ID";
        List<Object> list = new ArrayList<>();
        String s = "";
        if(taskType!=null){
            list.add(taskType);
            s+=" AND TASKTYPE =?"+list.size();
        }
        if(status!=null){
            list.add(status);
            s+="  AND STATUS =?"+list.size();
        }
        if (startDate!=null && !"".equals(startDate) && endDate!=null && !"".equals(endDate)){
            list.add(startDate);
            s+="  AND CREATE_TIME BETWEEN to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss') ";
            list.add(endDate);
            s+="  AND to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss')  ";
        }
        Page<Map<String, Object>> pageResult = null;
        try {
            String sqll = " select * from ( "+sql+" ) where 1=1 "+s;
            pageResult = this.execSqlPage(pageable, sqll,list.toArray());
        }catch (Exception e){
            return WebApiResponse.erro("查询失败"+e.getStackTrace());
        }
        return WebApiResponse.success(pageResult);
    }


}
