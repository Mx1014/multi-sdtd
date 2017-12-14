/**
 * 文件名：XSZCTASKController
 * 版本信息：
 * 日期：2017/12/05 10:02:41
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller.app;

import com.rzt.controller.CurdController;
import com.rzt.entity.app.XSZCTASK;
import com.rzt.service.app.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

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
@RequestMapping("xsAppQuery")
@Api(value = "巡视任务app查询接口")
public class XSZCTASKController extends
        CurdController<XSZCTASK, XSZCTASKService> {

    /**
     * xslx=1 正常巡视 =2 保电特寻 正常和保电 代办查询
     * dbyb 1 代办 2 已办
     * xslxnum 0 特殊巡视 1 保电巡视 2 正常巡视
     *
     * @param userId
     * @param dbyb
     * @return
     */
    @ApiOperation(
            value = "巡视任务列表",
            notes = "xszt=1 正常巡视 =2 保电特寻 正常和保电 代办查询 dbyb 1 代办 2 已办"
    )
    @RequestMapping("xsTask")
    public WebApiResponse xsTask(int page, int size, String userId, int dbyb) {
        try {
            return WebApiResponse.success(this.service.xsTask(page, size, userId, dbyb));
        } catch (Exception e) {
            return WebApiResponse.erro("数据异常");
        }
    }

    /**
     * xslx  0 特殊巡视 1 保电巡视 2 正常巡视
     * id 任务ID
     *
     * @param xslx
     * @param id
     * @return
     */
    @ApiOperation(
            value = "巡视任务详情",
            notes = "xslx 巡视类型 0 特殊巡视 1 保电巡视 2 正常巡视， id 任务ID"
    )
    @PostMapping("tourMissionDetails")
    public WebApiResponse tourMissionDetails(int xslx, Long id) {
        try {
            return WebApiResponse.success(this.service.tourMissionDetails(xslx, id));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据请求");
        }
    }

    /**
     * 人员信息采集查询
     *
     * @param xslx 0 特殊 1 保电 2 正常
     * @param id   任务ID
     * @return
     */
    @GetMapping("personCollection")
    @ApiOperation(value = "人员信息采集查询", notes = "xslx 巡视类型 0 特殊巡视 1 保电巡视 2 正常巡视， id 任务ID")
    public WebApiResponse personCollection(int xslx, Long id) {
        try {
            return WebApiResponse.success(this.service.personCollection(xslx, id));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据请求失败");
        }
    }

    /**
     * 物品提醒
     *
     * @param xslx   0 特殊 1 保电 2 正常
     * @param taskId 任务ID
     * @return
     */
    @GetMapping("itemsToRemind")
    @ApiOperation(value = "物品提醒", notes = "xslx 0 特殊 1 保电 2 正常")
    public WebApiResponse itemsToRemind(int xslx, Long taskId) {
        try {
            return WebApiResponse.success(this.service.itemsToRemind(xslx, taskId));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据库请求失败");
        }
    }
}