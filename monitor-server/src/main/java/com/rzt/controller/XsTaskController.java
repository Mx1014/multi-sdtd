package com.rzt.controller;

import com.rzt.entity.CheckDetail;
import com.rzt.service.CheckDetailService;
import com.rzt.service.TimedService;
import com.rzt.service.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("XSZCTASKController")
public class XsTaskController extends
        CurdController<XsTaskController,XSZCTASKService>{
    @Autowired
    private TimedService timedService;
    @Autowired
    private CheckDetailService detailService;

    /**
     * 根据taskId获取当前任务的隐患信息
     * 根据taskId 查询当前任务进度
     * @param taskId
     * @return
     */
    @GetMapping("/fingYHByTaskId")
    public WebApiResponse fingYHByTaskId(Long taskId,String TASKTYPE){
        if(null!= taskId && !"".equals(taskId)){
            return service.findYHByTaskId(taskId,TASKTYPE);
        }
        return WebApiResponse.erro("参数错误");
    }

    /**
     * 查询抽查表内的所有数据
     * @param taskType  任务类型
     * @return
     */
    @GetMapping("/getXsTaskAll")
    public WebApiResponse getXsTaskAll(Integer page,Integer size, String taskType,String currentUserId,String userName,String TD,String targetType){
        return service.getXsTaskAll(page,size,taskType,currentUserId,userName,TD,targetType);
    }
    @GetMapping("/findDeptAuth")
    public String findDeptAuth(String currentUserId){
        return service.findDeptAuth(currentUserId);
    }


    /**
     * 根据当前用户权限获取当前的刷新周期
     * @param currentUserId
     * @return
     */
    @GetMapping("/getTimeConfig")
    public WebApiResponse getTimeConfig(String currentUserId){
        return timedService.getTimedConfig(currentUserId);
    }



    /**
     * 提交审核
     * @param checkDetail
     * @return
     */
    @GetMapping("checkOff")
    public WebApiResponse checkOff(CheckDetail checkDetail,String timedTaskId,String currentUserId){
        try {
            checkDetail.setCheckUser(currentUserId);
            //根据审核人id和问题任务id查询该条审核记录是否存在
            Long detailID = detailService.findByCheckUserAndQuestionTaskId(checkDetail.getCheckUser(),checkDetail.getQuestionTaskId());
            if(detailID==null){
                 detailService.addCheckDetail(checkDetail);
            }

            service.checkOff(timedTaskId);
            return WebApiResponse.success("success");
        }catch (Exception e){
            return WebApiResponse.erro("更改状态失败："+e.getMessage());
        }
    }

    /**
     * 查看所有单位排班情况
     * @return
     */
    @GetMapping("/findWorkings")
    public WebApiResponse findWorkings(String currentUserId){
        return service.findWorking(currentUserId);
    }

    /**
     * 修改排班情况
     * @param currentUserId  当前登录人id
     * @param deptId         部门id
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @return
     */
    @GetMapping("/updateWorkings")
    public WebApiResponse updateWorkings(String currentUserId,String deptId,String startTime
                    ,String endTime,String dayUserId,String nightUserId){

            return service.updateWorkings(currentUserId,deptId,startTime,endTime,dayUserId,nightUserId);
    }




}
