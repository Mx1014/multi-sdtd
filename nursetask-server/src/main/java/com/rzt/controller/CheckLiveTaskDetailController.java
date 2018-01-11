
/**
 * 文件名：CHECKLIVETASKDETAILController
 * 版本信息：
 * 日期：2017/12/05 10:24:09
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.entity.CheckLiveTaskDetailXs;
import com.rzt.service.CheckLiveTaskDetailService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 类名称：CHECKLIVETASKDETAILController
 * 类描述：看护任务详情
 * 创建人：李泽州
 * 创建时间：2017/12/05 10:24:09
 */
@RestController
@RequestMapping("CheckLiveTaskDetail")
public class CheckLiveTaskDetailController extends
        CurdController<CheckLiveTaskDetail,CheckLiveTaskDetailService> {

    protected static Logger LOGGER = LoggerFactory.getLogger(CheckLiveTaskDetailController.class);

    @ApiOperation(value = "稽查问卷",notes = "稽查问卷")
    @GetMapping("/checkQuestionUpdate")
    public WebApiResponse checkQuestionUpdate(CheckLiveTaskDetail checkLiveTaskDetail, CheckLiveTaskDetailXs checkLiveTaskDetailXs, String taskType){
        try{
            Object obj = service.checkQuestionUpdate(checkLiveTaskDetail,checkLiveTaskDetailXs,taskType);
            return WebApiResponse.success(obj);
        }catch (Exception e){
            LOGGER.error("稽查问卷更新失败",e);
            return WebApiResponse.erro("稽查问卷更新失败");
        }
    }

    @ApiOperation(value = "稽查问卷回显",notes = "稽查问卷回显")
    @GetMapping("/checkQuestionInfo")
    public WebApiResponse checkQuestionInfo(String id, String taskType){
        try{
            Object obj = service.checkQuestionInfo(id,taskType);
            return WebApiResponse.success(obj);
        }catch (Exception e){
            LOGGER.error("稽查问卷更新失败",e);
            return WebApiResponse.erro("稽查问卷更新失败");
        }
    }

}