package com.rzt.service;

import com.rzt.entity.CheckDetail;
import com.rzt.repository.CheckDetailRepository;
import com.rzt.repository.XSZCTASKRepository;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CheckDetailService extends CurdService<CheckDetail,CheckDetailRepository> {
	
	@Autowired
	private CheckDetailRepository checkDetailRepository;
    @Autowired
	private XSZCTASKRepository xszctaskRepository;

	@Transactional
	public Long addCheckDetail(CheckDetail checkDetail){
        String checkUser = checkDetail.getCheckUser();
        ArrayList<Object> strings1 = new ArrayList<>();
        ArrayList<Object> strings2 = new ArrayList<>();
        if(null !=  checkDetail.getCheckDetailType() && !"".equals( checkDetail.getCheckDetailType())){
            strings1.add(checkDetail.getQuestionTaskId());
            String sql = "";
            if(1==checkDetail.getCheckDetailType()){//巡视
                sql = "SELECT x.TD_ORG tdOrg from XS_ZC_TASK x WHERE ID = ?"+strings1.size();
            }
            if(2==checkDetail.getCheckDetailType()){//看护
                sql = "SELECT c.TDYW_ORGID  FROM KH_TASK a LEFT JOIN KH_SITE  c ON a.SITE_ID = c.ID  WHERE a.ID = ?"+strings1.size();
            }
            List<Map<String, Object>> maps = this.execSql(sql, strings1.toArray());
            if(null != maps && maps.size()>0){
                checkDetail.setTdOrg(maps.get(0)!=null? (String) maps.get(0).get("TDYW_ORGID") : "");
            }
        }



		//生成id
		Long id = Long.valueOf(new SnowflakeIdWorker(0,0).nextId());
		//为checkDetail设置id
		checkDetail.setId(id);
		//添加创建时间
		checkDetail.setCreateTime(new Date());
        ArrayList<String> strings = new ArrayList<>();
        //根据审核人查询审核单位
		if(null != checkUser && !"".equals(checkUser)){
                strings.add(checkUser);
                String sql = "SELECT DEPTID from RZTSYSUSER WHERE ID = ?"+strings.size();
                List<Map<String, Object>> maps = this.execSql(sql, strings.toArray());
                checkDetail.setCheckUser(checkUser);

                    if(null != maps && maps.size()>0){
                        Object deptid = maps.get(0).get("DEPTID");
                        if(null != deptid){
                            checkDetail.setCheckOrg(deptid.toString());
                                }
                    }

        }
       /* if(null != checkDetail.getQuestionTaskId() && checkDetail.getQuestionTaskId()>0){
            xszctaskRepository.xsTaskUpdate(checkDetail.getQuestionTaskId());
        }*/

		checkDetailRepository.save(checkDetail);


        return id;
	}
	
	public Long findByCheckUserAndQuestionTaskId(String checkUser, Long questionTaskId) {
		return checkDetailRepository.findByCheckUserAndQuestionTaskId(checkUser, questionTaskId);
	}




}
