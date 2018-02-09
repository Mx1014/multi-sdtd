package com.rzt.controller;

import com.rzt.entity.GUZHANG;
import com.rzt.service.GJService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 李成阳
 * 2018/1/31
 * 管理app告警数据统计
 */
@RestController
@RequestMapping("/GJ")
public class GJController extends CurdController<GUZHANG,GJService>  {
    @Autowired
    private GJService gjService;

    /**
     * 一级页面使用
     * 告警统计信息
     * @return
     */
    @GetMapping("/findGJSum")
    public WebApiResponse findGJ(){
        return gjService.AlarmList1("");
    }

    /**
     * 二级页面使用  按照部门分组 返回所有部门   不传deptId
     * 三级页面使用 按照部门查询     传deptId
     * @param deptId
     * @return
     */
    @GetMapping("/findGJTwoOrThree")
    public WebApiResponse findGJThree(String deptId){
        return gjService.AlarmList(deptId);
    }
    /*----------------------------- 三级页下半部  列表数据开始-------------------------------------------- */
    /**
     * 离线
     * @param workType 工作类型条件
     * @param page
     * @param size
     * @param startTime  时间条件
     * @param endTime
     * @param deptId   部门
     * @param taskType  任务类型
     * @param loginType  登录状态
     * @return
     */
    @GetMapping("/OfflinesList")
    public WebApiResponse OfflinesList(Integer workType, Integer page, Integer size, String startTime, String endTime, String deptId, String taskType,String loginType){
        return gjService.OfflinesList( workType,  page,  size,  startTime, endTime ,  deptId,  taskType, loginType);
    }

    /**
     * 未按时开始
     * @param page
     * @param size
     * @param startTime   时间条件
     * @param endTime
     * @param deptId    部门
     * @param taskType   任务类型
     * @return
     */
    @GetMapping("/answertimeList")
    public WebApiResponse answertimeList(Integer page, Integer size, String startTime, String endTime, String deptId, String taskType){
        return gjService.answertimeList( page,  size,  startTime,  endTime,  deptId,  taskType);
    }

    /**
     * 任务超期
     * @param page
     * @param size
     * @param startTime  时间
     * @param endTime
     * @param deptId   部门
     * @return
     */
    @RequestMapping("overdueList")
    public WebApiResponse overdueList(Integer page, Integer size,  String startTime, String endTime, String deptId){
        return gjService.overdueList( page,  size,   startTime,  endTime,  deptId);
    }

    /**
     * 看护人员脱岗
     * @param page
     * @param size
     * @param startTime   时间
     * @param endTime
     * @param deptId    部门
     * @return
     */
    @RequestMapping("khtemporarilyList")
    public WebApiResponse khtemporarilyList(Integer page, Integer size, String startTime, String endTime, String deptId){
        return  gjService.khanswertimeList( page,  size,  startTime,  endTime,  deptId);
    }

    /**
     * 巡视不合格
     * @param page
     * @param size
     * @param startTime   时间
     * @param endTime
     * @param deptId   部门
     * @param userName  人名模糊查询
     * @return
     */
    @RequestMapping("unqualifiedpatrolList")
    public WebApiResponse unqualifiedpatrolList(Integer page, Integer size, String startTime, String endTime, String deptId, String userName){
        return gjService.unqualifiedpatrolList( page,  size,  startTime,  endTime,  deptId,  userName);
    }
     /*----------------------------- 三级页下半部  列表数据结束-------------------------------------------- */

}
