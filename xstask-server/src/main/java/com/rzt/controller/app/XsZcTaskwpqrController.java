package com.rzt.controller.app;

import com.rzt.controller.CurdController;
import com.rzt.entity.app.XsZcTaskwpqr;
import com.rzt.service.app.XsZcTaskwpqrService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.controller.app
 * @Author: liuze
 * @date: 2017-12-7 19:41
 */
@RestController
@RequestMapping("xsAppChange")
@Api(value = "巡视任务app修改接口")
public class XsZcTaskwpqrController extends CurdController<XsZcTaskwpqr, XsZcTaskwpqrService> {

    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视 ID 任务id
     * 身份确认修改时间
     *
     * @param xslx
     * @param id
     * @return
     */
    @ApiOperation(value = "接单修改时间", notes = "接单修改时间")
    @PatchMapping("updateJdTime")
    public Object updateJdTime(Integer xslx, Long id,String userId) {
        try {
            this.service.updateJdTime(id, xslx,userId);
            return WebApiResponse.success("数据保存成功!");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据保存失败" + var3.getMessage());
        }

    }

    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视 ID 任务id
     * 身份确认修改时间
     *
     * @param xslx
     * @param id
     * @return
     */
    @ApiOperation(value = "身份确认修改时间", notes = "身份确认修改时间")
    @PatchMapping("updateSfqrTime")
    public Object updateSfqrTime(Integer xslx, Long id) {
        try {
            this.service.updateSfqrTime(id, xslx);
            return WebApiResponse.success("数据保存成功!");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据保存失败" + var3.getMessage());
        }

    }



    /**
     * 物品提醒
     *
     * @param xslx   巡视类型 0 特巡 1 保电 2 正常巡视
     * @param taskId 任务ID
     * @param wpZt   选择物品状态
     * @return
     */
    @PatchMapping("articlesUpdate")
    @ApiOperation(value = "物品提醒", notes = "物品提醒")
    public Object articlesUpdate(Integer xslx, Long id, String wpZt,Date wptxTime ) {
        try {
            this.service.articlesReminding(id, wpZt, xslx,wptxTime);
            return WebApiResponse.success("数据保存成功!");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据保存失败" + var3.getMessage());
        }
    }

    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视 ID 任务id
     * 到达现场时间修改
     *
     * @param xslx
     * @param id
     * @return
     */
    @ApiOperation(value = "到达现场时间修改", notes = "到达现场时间修改")
    @PatchMapping("reachSpot")
    public Object reachSpot(Integer xslx, Long id) {
        try {
            Object o = this.service.reachSpot(xslx, id);
            return WebApiResponse.success(o);
        } catch (Exception var3) {
            return WebApiResponse.erro("数据保存失败" + var3.getMessage());
        }
    }




    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视 ID 任务id
     * 到达现场时间修改
     *
     * @param xslx
     * @param taskId
     * @return
     */
    @ApiOperation(value = "插入轮数据", notes = "插入轮数据")
    @PostMapping("insertExec")
    public Object insertExe(Integer xslx,Long taskId,Integer repeatNum) {
        try {
            this.service.insertExec(xslx, taskId,repeatNum);
            return WebApiResponse.success("数据保存成功");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据保存失败" + var3.getMessage());
        }
    }

    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视 ID 任务id
     * 到达现场时间修改
     *
     * @param xslx
     * @param taskId
     * @return
     */
    @ApiOperation(value = "更新轮数据", notes = "更新轮数据")
    @PatchMapping("updateExec")
    public Object updateExec(Integer xslx,Long execId,Integer status) {
        try {
            this.service.updateExec(xslx, execId,status);
            return WebApiResponse.success("数据保存成功");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据保存失败" + var3.getMessage());
        }
    }


    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视 ID 任务id
     * 到达现场时间修改
     *
     * @param xslx
     * @param id
     * @return
     */
    @ApiOperation(value = "插入轮详情数据", notes = "插入轮详情数据")
    @PostMapping("insertExecDetail")
    public Object insertExecDetail(Integer xslx,Long execId,String gznr,Long startTowerId,Long endTowerId) {
        try {
            Object o = this.service.insertExecDetail(xslx, execId,gznr,startTowerId,endTowerId);
            return WebApiResponse.success(o);
        } catch (Exception var3) {
            return WebApiResponse.erro("数据保存失败" + var3.getMessage());
        }
    }


    /**
     * xslx 巡视类型
     * 到达现场时间修改
     *
     * @param xslx  巡视类型
     * @param sfdw 是否到位
     * @param reason 不到位原因
     * @param execDetailId 轮详情id
     * @return
     */
    @ApiOperation(value = "更新轮详情数据", notes = "更新轮详情数据 ")
    @PatchMapping("updateExecDetail")
    public Object updateExecDetail(Integer xslx,Integer sfdw,String reason,Long execDetailId,String longtitude,String latitude) {
        try {
            this.service.updateExecDetail(xslx,sfdw, reason,execDetailId,longtitude,latitude);
            return WebApiResponse.success("数据更新成功");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据更新失败" + var3.getMessage());
        }
    }


    /**
     * xslx 巡视类型
     * 到达现场时间修改
     *
     * @param xslx  巡视类型
     * @param id 任务id
     * @return
     */
    @ApiOperation(value = "更新任务状态", notes = "更新任务状态 ")
    @PatchMapping("finishTask")
    public Object updateTaskStatus(Integer xslx,Long id,String userId) {
        try {
            this.service.updateTaskStatus(xslx,id,userId);
            return WebApiResponse.success("数据更新成功");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据更新失败" + var3.getMessage());
        }
    }


    /**
     * xslx 巡视类型
     * 到达现场时间修改
     *
     * @param xslx  巡视类型
     * @param id 任务id
     * @return
     */
    @ApiOperation(value = "更新任务状态", notes = "更新任务状态 ")
    @PostMapping("lsyhInXs")
    public Object lsyhInXs(Integer xslx,Long id,Long execId,Long execDetailId,Long yhId,String yhInfo) {
        try {
            this.service.lsyhInXs(xslx,id,execId,execDetailId,yhId,yhInfo);
            return WebApiResponse.success("数据更新成功");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据更新失败" + var3.getMessage());
        }
    }

    /**
     * xslx 巡视类型
     * 插入异常状态
     *
     * @param xslx  巡视类型
     * @param id 任务id
     * @return
     */
    @ApiOperation(value = "插入异常状态", notes = "插入异常状态 ")
    @PostMapping("insertException")
    public Object insertException(Long taskId,String ycms,String ycdata) {
        try {
            this.service.insertException(taskId,ycms,ycdata);
            return WebApiResponse.success("数据更新成功");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据更新失败" + var3.getMessage());
        }
    }



}
