package com.rzt.controller;

import com.rzt.service.ProblemService;
import com.rzt.service.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 李成阳
 * 2018/1/14
 */

@RestController
@RequestMapping("/problem")
public class ProblemController  extends
        CurdController<ProblemController,ProblemService>{
    @Autowired
    private ProblemService problemService;

    /**
     * 问题审核一级页面列表展示
     * @param page 分页组件
     * @param size 分页组件
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param proType    隐患类型
     * @param tdORG      通道公司
     * @param lineName   线路名称
     * @param taskType   作业类型  巡视 看护 稽查
     * @param level      线路电压
     * @param userId     角色id
     */
    @GetMapping("/findProblemAll")
    public WebApiResponse findProblemAll(Integer page,Integer size,String startTime,String endTime,String proType,
                                         String tdORG,String lineName,String taskType,String wxORG,String level,String userId){
        return problemService.findProblemAll(page,size,startTime,endTime,proType,tdORG,lineName,taskType,wxORG,level,userId);
    }

    /**
     * 根据用户id查询当前用户的角色类型  前端准备按照权限显示筛选条件
     * @param userId
     * @return
     */
    @GetMapping("/findRoleTypeByUserId")
    public WebApiResponse findRoleTypeByUserId(String userId){
        return problemService.findRoleType(userId);
    }





}
