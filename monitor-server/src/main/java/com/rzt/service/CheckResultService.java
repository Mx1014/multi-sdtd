package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.CheckDetail;
import com.rzt.entity.CheckResult;
import com.rzt.repository.CheckResultRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service

public class CheckResultService extends CurdService<CheckResult, CheckResultRepository> {
	
	@Autowired
	private CheckResultRepository checkResultRepository;

	@Autowired
	private RedisTemplate<String,Object> redisTemplate;
	
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
        ArrayList<String> longs = new ArrayList<>();
        longs.add(questionTaskId+"");
        String sql = "SELECT r.*,d.CHECK_DETAIL_TYPE  " +
				" FROM CHECK_RESULT r LEFT JOIN CHECK_DETAIL d ON r.CHECK_DETAIL_ID = d.ID  " +
				" WHERE d.QUESTION_TASK_ID = ?"+longs.size();
        List<Map<String, Object>> maps = execSql(sql, longs.toArray());
        return maps;
    }

    public static void main(String[] args) {
        String ids = "399188390399967232,,399188359655718912,,399187987696451584,,399187952967614464,,399187454755602432,399187415350116352,";
        String substring = ids.substring(0, ids.length() - 1);
        System.out.println(substring);
    }


    public Object getCheckRecord(Integer page, Integer size,String startDate,String endDate,Integer taskType,String vLevel,Integer lineId) {
        Pageable pageable = new PageRequest(page,size);
        //暂时去数据库查，之后从redis拿就可以
        String sql = "SELECT tcr.*,cm.V_LEVEL FROM   " +
				"  (SELECT DISTINCT tc.TASKID,tc.TASKNAME,tc.CHECK_USER,tc.CREATE_TIME,tc.ID,tc.TASKTYPE,cr.LINE_ID FROM   " +
				"(SELECT   " +
				"  t.TASKID,   " +
				"  t.TASKNAME,   " +
				"  c.CHECK_USER,   " +
				"  c.CREATE_TIME,   " +
				"  c.ID,   " +
				"  t.TASKTYPE   " +
				"FROM TIMED_TASK t   " +
				"RIGHT JOIN CHECK_DETAIL c ON t.TASKID = c.QUESTION_TASK_ID   " +
				"WHERE t.STATUS = 1) tc LEFT JOIN CHECK_RESULT cr ON tc.ID = cr.CHECK_DETAIL_ID) tcr   " +
				"LEFT JOIN CM_LINE cm ON tcr.LINE_ID = cm.ID";
        List<Object> list = new ArrayList<>();
        String s = "";
        if(taskType!=null){
            list.add(taskType);
            s+=" AND TASKTYPE =?"+list.size();
        }
        if (startDate!=null && !"".equals(startDate) && endDate!=null && !"".equals(endDate)){
            list.add(startDate);
            s+="  AND CREATE_TIME BETWEEN to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss') ";
            list.add(endDate);
            s+="  AND to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss')  ";
        }
		if(vLevel!=null && !"".equals(vLevel)){
			list.add(vLevel);
			s+=" AND V_LEVEL = ?"+list.size();
		}
		if(lineId!=null){
			list.add(lineId);
			s+=" AND LINE_ID =?"+list.size();
		}
        Page<Map<String, Object>> pageResult = null;
        try {
            String sqll = " select * from ( "+sql+" ) where 1=1 "+s;
            pageResult = this.execSqlPage(pageable, sqll,list.toArray());
			Iterator<Map<String, Object>> iterator = pageResult.iterator();
			HashOperations hashOperations = redisTemplate.opsForHash();

			while (iterator.hasNext()){
				Map<String, Object> next = iterator.next();

				String userID =(String)next.get("CHECK_USER");
				Object userInformation = hashOperations.get("UserInformation", userID);
				if(userInformation==null){
					System.out.println(userInformation);
					continue;
				}
				JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
				if(jsonObject!=null){
					next.put("DEPT",jsonObject.get("DEPT"));
					next.put("COMPANYNAME",jsonObject.get("COMPANYNAME"));
					next.put("REALNAME",jsonObject.get("REALNAME"));
					next.put("PHONE",jsonObject.get("PHONE"));
				}
			}
        }catch (Exception e){
            return WebApiResponse.erro("查询失败"+e.getStackTrace());
        }
        return WebApiResponse.success(pageResult);
    }


}
