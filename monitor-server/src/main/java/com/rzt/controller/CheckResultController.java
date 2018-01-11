package com.rzt.controller;

import com.rzt.entity.CheckDetail;
import com.rzt.entity.CheckResult;
import com.rzt.service.CheckDetailService;
import com.rzt.service.CheckResultService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/checkResult")
public class CheckResultController extends CurdController<CheckResult,CheckResultService> {
	
	@Autowired
	private CheckResultService resultservice;
	
	@Autowired
	private CheckDetailService detailService;
	
	/**
	 * 添加审核问题
	 *@return String
	 *@author huyuening
	 *@date 2017年12月18日
	 */
	@PostMapping("/add")
	public WebApiResponse add(CheckResult checkResult,CheckDetail checkDetail){
		String tdOrg = "";
        List<Map<String, Object>> checkResultInfo = service.getCheckResultInfo(checkResult, checkDetail);
        if(null != checkResultInfo && checkResultInfo.size()>0){
                try{
                    Map<String, Object> stringObjectMap = checkResultInfo.get(0);
                    String id = stringObjectMap.get("ID").toString();
                    if(null != id && !"".equals(id)){
                        service.updateByCheckId(checkResult,checkDetail,id);
                    }
                }catch (Exception e){

                    return WebApiResponse.erro("参数错误");
                }
        }else{
            try {
                //根据审核人id和问题任务id查询该条审核记录是否存在
                Long detailID = detailService.findByCheckUserAndQuestionTaskId(checkDetail.getCheckUser(),checkDetail.getQuestionTaskId());
                Long checkDetailID = null;
                if(detailID==null){
                    checkDetailID = detailService.addCheckDetail(checkDetail);
                }else{
                    checkDetailID = detailID;
                }
                try {
                    //为检查结果添加检查详情id
                    checkResult.setCheckDetailID(checkDetailID);
                    resultservice.addResult(checkResult);
                    return WebApiResponse.success("保存成功！");
                } catch (Exception e) {
                    return WebApiResponse.erro("保存审核信息失败！"+e.getMessage());
                }
            } catch (Exception e) {
                return WebApiResponse.erro("保存审查结果失败！"+e.getMessage());
            }
        }

		return WebApiResponse.success("添加成功");
	}
	
	
	/**
	 * 根据条件查询结果
	 *@return Object
	 *@author huyuening
	 *@date 2017年12月19日
	 */
	@RequestMapping("/getCheckResult")
	public WebApiResponse getCheckResult(@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "size",defaultValue = "8") Integer size,CheckDetail checkDetail){
		try {
			return WebApiResponse.success(resultservice.getCheckResult(page,size,checkDetail));
		} catch (Exception e) {
			return WebApiResponse.erro("查询失败！"+e.getMessage());
		}
	}

	/**
	 *@Author hyn
	 *@Method getQuestion
	 *@Params [taskId]
	 *@Date 2018/1/5 16:18
	 */
	@GetMapping("getQuestion")
	public WebApiResponse getQuestion(Long questionTaskId){
		try {
			return WebApiResponse.success(resultservice.getQuestion(questionTaskId));
		}catch (Exception e){
			return WebApiResponse.erro("查询失败："+e.getMessage());
		}
	}

	/**
	 * 检查记录
	 */
	@RequestMapping("/getCheckRecord")
	public WebApiResponse getCheckRecord(String userId,Integer page, Integer size,String startDate,String endDate,Integer taskType,String vLevel,Integer lineId){
		try {
			return WebApiResponse.success(resultservice.getCheckRecord(userId,page,size,startDate,endDate,taskType,vLevel,lineId));
		} catch (Exception e) {
			return WebApiResponse.erro("查询失败！"+e.getMessage());
		}
	}
	@GetMapping("/getQuestionInfo")
	public WebApiResponse getQuestionInfo(String id){
		if(null != id && !"".equals(id)){
			return service.getQuestionInfo(id);

		}
		return WebApiResponse.success("");

	}

	
}
