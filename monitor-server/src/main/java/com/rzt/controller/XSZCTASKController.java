package com.rzt.controller;

import com.rzt.entity.CheckDetail;
import com.rzt.service.CheckDetailService;
import com.rzt.service.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 李成阳
 * 2018/1/2
 */
@RestController
@RequestMapping("XSZCTASKController")
public class XSZCTASKController extends
        CurdController<XSZCTASKController,XSZCTASKService>{

    /**
     * 按照taskId查询当前任务的详细信息
     * @param taskId
     * @return
     */
  /*  @GetMapping("/getTASkXQ")
    public WebApiResponse getTASkXQ(String taskId){
        return service.findByTaskId(taskId);
    }*/

    /**
     * 根据taskId获取当前任务的隐患信息
     * 根据taskId 查询当前任务进度
     * @param taskId
     * @return
     */
    @GetMapping("/fingYHByTaskId")
    public WebApiResponse fingYHByTaskId(String taskId,String TASKTYPE){
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
    public WebApiResponse getXsTaskAll(Integer page,Integer size, String taskType){
        return service.getXsTaskAll(page,size,taskType);
    }

    @Autowired
    private CheckDetailService detailService;
    @PostMapping("checkOff")
    public WebApiResponse checkOff(CheckDetail checkDetail){
        try {
            //根据审核人id和问题任务id查询该条审核记录是否存在
            Long detailID = detailService.findByCheckUserAndQuestionTaskId(checkDetail.getCheckUser(),checkDetail.getQuestionTaskId());
            if(detailID==null){
                 detailService.addCheckDetail(checkDetail);
            }

            service.checkOff(checkDetail.getQuestionTaskId());
            return WebApiResponse.success("success");
        }catch (Exception e){
            return WebApiResponse.erro("更改状态失败："+e.getMessage());
        }
    }

}
