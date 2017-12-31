/**
 * 文件名：CHECKLIVETASKDETAILController
 * 版本信息：
 * 日期：2017/12/05 10:24:09
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.rzt.entity.CheckLiveTaskCycle;
import com.rzt.service.CheckLiveTaskCycleService;
import com.rzt.service.CheckLiveTaskExecService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类名称：CHECKLIVETASKDETAILController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/05 10:24:09
 * 修改人：张虎成
 * 修改时间：2017/12/05 10:24:09
 * 修改备注：
 * @version
 */
@RestController
@RequestMapping("CheckLiveTaskCycle")
public class CheckLiveTaskCycleController extends
        CurdController<CheckLiveTaskCycle, CheckLiveTaskCycleService> {



    @Autowired
    private CheckLiveTaskExecService execService;

    @Autowired
    private CheckLiveTaskCycleService cycleService;

//    @ApiOperation(value = "稽查维护查询",notes = "稽查维护的分页查询，条件搜索")
//	@GetMapping("/checkTaskCycleMain.do")
//    public WebApiResponse listCheckTaskMain(@RequestParam(value = "page",defaultValue = "0") Integer page,
//                                            @RequestParam(value = "size",defaultValue = "4") Integer size,
//                                            String startTime, String endTime, String userId,String taskName){
//		try{
//            Pageable pageable = new PageRequest(page, size);
//            Page<Map<String, Object>> list = this.service.listCheckTaskMain(startTime,endTime, userId,taskName, pageable);
//			return WebApiResponse.success(list);
//		}catch (Exception e){
//			return WebApiResponse.erro("数据获取失败"+e.getMessage());
//		}
//
//	}

    @ApiOperation(value = "稽查任务详情查询",notes = "稽查任务详情分页查询，条件搜索")
    @GetMapping("/listCheckTaskDetailById/{id}")
    public WebApiResponse listCheckTaskDetailById(@PathVariable String id,String taskType){
        try{
            List list = this.service.listCheckTaskDetailById(id,taskType);
            return WebApiResponse.success(list);
        }catch (Exception e) {
            return WebApiResponse.erro("数据获取失败"+e.getMessage());
        }
    }

//    @ApiOperation(value = "用户查询接口",notes = "用户查询")
//    @GetMapping("/listAllCheckUser")
//    public WebApiResponse listAllCheckUser(){
//        try{
//              List list = this.service.listAllCheckUser();
//              return WebApiResponse.success(list);
//        }catch (Exception e) {
//            return WebApiResponse.erro("数据获取失败"+e.getMessage());
//        }
//    }
    @ApiOperation(value = "稽查查询接口",notes = "稽查查询")
    @GetMapping("/listAllCheckTaskExec")
    public WebApiResponse listAllCheckTaskExec(){
        try{
            List list = this.service.listAllCheckTaskExec();
            return WebApiResponse.success(list);
        }catch (Exception e) {
            return WebApiResponse.erro("数据获取失败"+e.getMessage());
        }
    }


//    @ApiOperation(value = "稽查任务导出",notes = "稽查任务导出")
//    @GetMapping("/exportCheckTask.do")
//    public void exportCheckTask(HttpServletRequest request, HttpServletResponse response){
//        try {
//            this.service.exportExcel(response);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

//    @ApiOperation(value = "稽查任务更新",notes = "稽查任务更新")
//    @GetMapping("/updateCheckTask.do")
//    @Transactional
//    public void updateCheckTask(CheckLiveTaskCycleModel model){
//        try {
//
//            CheckLiveTaskExec exec = this.execService.findExec(model.getExecId());
//            Long cycleId = exec.getCycleId();
//            CheckLiveTaskCycle cycle = this.cycleService.findCycle(cycleId);
//            if(!StringUtils.isEmpty(model.getUserId())){
//                cycle.setUserId(model.getUserId());
//            }
//            if(!StringUtils.isEmpty(model.getTaskName())){
//               cycle.setTaskName(model.getTaskName());
//            }
//            if(model.getCreateTime()!=null){
//               cycle.setCreateTime(model.getCreateTime());
//            }
//            if(!StringUtils.isEmpty(model.getTaskType())){
//                cycle.setTaskType(model.getTaskType());
//            }
//            if(model.getPlanStartTime()!=null){
//                cycle.setPlanStartTime(model.getPlanStartTime());
//            }
//             if(!StringUtils.isEmpty(model.getCheckCycle())){
//               cycle.setCheckCycle(model.getCheckCycle());
//            }
//
//            this.service.updateCycle(cycle,cycleId);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }



}
