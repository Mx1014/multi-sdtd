/**
 * 文件名：XSZCTASKController
 * 版本信息：
 * 日期：2017/12/05 10:02:41
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller.app;

import com.rzt.controller.CurdController;
import com.rzt.encode.DataEncode;
import com.rzt.entity.app.XSZCTASK;
import com.rzt.service.app.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

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
    @GetMapping("xsTask")
    public WebApiResponse xsTask(Integer page, Integer size, String userId, Integer dbyb) {
        try {
            return WebApiResponse.success(this.service.xsTask(page, size, userId, dbyb));
        } catch (Exception e) {
            return WebApiResponse.erro("数据异常" + e.getMessage());
        }
    }
    /**
     * @Method xsTaskCount
     * @Description  返回待办/已办任务数 0 待办 1 已办
     * @param [userId, status]
     * @return java.lang.Object
     * @date 2017/12/15 11:58
     * @author nwz
     */
    @ApiOperation(
            value = "巡视任务列表 待办或已办数量",
            notes = "巡视任务列表 待办或已办数量 0 待办 1 已办"
    )
    @GetMapping("xsTaskCount")
    public WebApiResponse xsTaskCount( String userId, Integer dbyb) {
        try {
            return WebApiResponse.success(this.service.xsTaskCount( userId, dbyb));
        } catch (Exception e) {
            return WebApiResponse.erro("数据异常" + e.getMessage());
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
    @GetMapping("tourMissionDetails")
    public WebApiResponse tourMissionDetails(Integer xslx, Long id) {
        try {
            return WebApiResponse.success(this.service.tourMissionDetails(xslx, id));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据请求" + e.getMessage());
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
    public WebApiResponse personCollection(Integer xslx, Long id,Long cycleId,String userId) {
        try {
            return WebApiResponse.success(this.service.personCollection(xslx, id));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据请求失败" + e.getMessage());
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
    public WebApiResponse itemsToRemind(Integer xslx, Long id) {
        try {
            return WebApiResponse.success(this.service.itemsToRemind(xslx, id));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据库请求失败" + e.getMessage());
        }
    }


    @GetMapping("xsTowerList")
    @ApiOperation(value = "巡视页面", notes = "xslx 0 特殊 1 保电 2 正常")
//    @DataEncode(includes = {"ID","TOWER_NAME"})
    public WebApiResponse xsTowerList(Integer xslx, Long id) {
        try {
            return WebApiResponse.success(this.service.xsTowerList(xslx, id));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据库请求失败"  + e.getMessage());
        }
    }

    @GetMapping("getExecDetail")
    @ApiOperation(value = "巡视页面", notes = "xslx 0 特殊 1 保电 2 正常")
//    @DataEncode(includes = {"ID","TOWER_NAME"})
    public WebApiResponse getExecDetail(Integer xslx, Long execDetailId) {
        try {
            return WebApiResponse.success(this.service.getExecDetail(xslx, execDetailId));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据库请求失败" + e.getMessage());
        }
    }


    @GetMapping("historyXsTowerList")
    @ApiOperation(value = "巡视页面", notes = "xslx 0 特殊 1 保电 2 正常")
//    @DataEncode(includes = {"ID","TOWER_NAME"})
    public WebApiResponse historyXsTowerList(Integer xslx, Long execId,Long taskId) {
        try {
            return WebApiResponse.success(this.service.historyXsTowerList(xslx, execId,taskId));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据库请求失败" + e.getMessage());
        }
    }

    @GetMapping("getImgsByExecId")
    @ApiOperation(value = "巡视已办", notes = "execId")
//    @DataEncode(includes = {"ID","TOWER_NAME"})
    public Object getImgsByExecId(String execId) {
        try {
            return this.service.getImgsByExecId(execId);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据库请求失败" + e.getMessage());
        }
    }

    @GetMapping("shangbaoYh")
    @ApiOperation(value = "上报隐患所需要的参数", notes = "上报隐患所需要的参数")
    public WebApiResponse shangbaoYh(Integer xslx, Long taskId,String userId) {
        try {
            return WebApiResponse.success(this.service.shangbaoYh(xslx, taskId,userId));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据库请求失败" + e.getMessage());
        }
    }

    @PostMapping("shangBaoQueXian")
    @ApiOperation(value = "上报缺陷", notes = "上报缺陷")
    public WebApiResponse shangBaoQueXian(Long taskId,String userId,Long towerId,Long processId,String qxMs,Integer qxType,Integer qxPosition,String pictureIds) {
        try {
            this.service.shangBaoQueXian(taskId, userId,towerId,processId,qxMs,qxType,qxPosition,pictureIds);
            return WebApiResponse.success("成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据库请求失败" + e.getMessage());
        }
    }
    // 修改不间断巡视的结束
    @GetMapping("updateBjd")
    public Object insertCycleTower(Long id) {
        try {
            Map<String, Object> map = this.service.updateBjd(id);
            return WebApiResponse.success(map);
        } catch (Exception var) {
            return WebApiResponse.erro("图片查找失败" + var.getStackTrace());
        }
    }
}