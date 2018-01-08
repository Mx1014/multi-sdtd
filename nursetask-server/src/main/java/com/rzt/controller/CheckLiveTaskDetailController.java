
/**
 * 文件名：CHECKLIVETASKDETAILController
 * 版本信息：
 * 日期：2017/12/05 10:24:09
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;
import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.service.CheckLiveTaskDetailService;
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


}