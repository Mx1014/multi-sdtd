/**
 * 文件名：XSZCTASKController
 * 版本信息：
 * 日期：2017/12/05 10:02:41
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller.appcontroller;

import com.rzt.controller.CurdController;
import com.rzt.entity.appentity.XSZCTASK;
import com.rzt.service.appservice.XSZCTASKService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 类名称：XSZCTASKController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/05 10:02:41
 * 修改人：张虎成
 * 修改时间：2017/12/05 10:02:41
 * 修改备注：
 */
@RestController
@RequestMapping("app")
public class XSZCTASKController extends
        CurdController<XSZCTASK, XSZCTASKService> {
    /**
     * xszt=1 正常巡视 =2 保电特寻 正常和保电 代办查询
     * dbyb 1 代办 2 已办
     *
     * @param userId
     * @param xsZt
     * @param dbyb
     * @return
     */
    @ApiOperation(
            value = "巡视任务列表",
            notes = "xszt=1 正常巡视 =2 保电特寻 正常和保电 代办查询 dbyb 1 代办 2 已办"
    )
    @RequestMapping("xsTaskDb")
    public List<Map<String, Object>> xsTask(String userId, int xsZt, int dbyb) {
        return this.service.xsTask(userId, xsZt, dbyb);
    }
}