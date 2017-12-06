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
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashMap;
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
@EnableSwagger2
public class XSZCTASKController extends
        CurdController<XSZCTASK, XSZCTASKService> {
    /**
     * xslx=1 正常巡视 =2 保电特寻 正常和保电 代办查询
     * dbyb 1 代办 2 已办
     *
     * @param userId
     * @param xslx
     * @param dbyb
     * @return
     */
    @ApiOperation(
            value = "巡视任务列表",
            notes = "xszt=1 正常巡视 =2 保电特寻 正常和保电 代办查询 dbyb 1 代办 2 已办"
    )
    @RequestMapping("xsTaskDb")
    public List<Map<String, Object>> xsTask(String userId, int xslx, int dbyb) {
        return this.service.xsTask(userId, xslx, dbyb);
    }

    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视
     * id 任务ID
     *
     * @param xslx
     * @param id
     * @return
     */
    @ApiOperation(
            value = "巡视任务详情",
            notes = "xslx 巡视类型 1 正常巡视 2 保电巡视， id 任务ID"
    )
    @RequestMapping("tourMissionDetails")
    public List<Map<String, Object>> tourMissionDetails(int xslx, String id) {
        return this.service.tourMissionDetails(xslx, id);
    }

    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视 ID 任务id
     *
     * @param xslx
     * @param id
     * @return
     */
    @ApiOperation(value = "身份确认修改时间", notes = "xslx 巡视类型 1 正常巡视 2 保电巡视 ID 任务id")
    @RequestMapping("updateSfqrTime")
    public HashMap updateSfqrTime(int xslx, String id) {
        int updateSfqrTime = this.service.updateSfqrTime(id, xslx);
        int one = 1;
        HashMap<String, Boolean> hashMap = new HashMap(0);
        if (updateSfqrTime == one) {
            hashMap.put("success", true);
            return hashMap;
        }
        hashMap.put("success", false);
        return hashMap;
    }
}