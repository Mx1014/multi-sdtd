package com.rzt.activiti.Eureka;

import com.rzt.entity.KhYhHistory;
import com.rzt.util.WebApiResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("NURSETASK")
public interface nurseTaskService {
    /**
     * 生成看护任务   或 变更看护任务
     * @param YHID
     * @return
     */
    @PostMapping("/nurseTask/KhLsCycle/saveLsCycle")
    WebApiResponse saveLsCycle(@RequestParam(name = "yhId") String YHID);

    /**
     * 生成稽查任务
     * @param taskId
     * @param taskType
     * @param taskName
     * @param yhsbId
     * @param tdOrgId
     * @param tdOrgName
     * @param checkType
     * @param checkDept
     * @return
     */
    @PostMapping("/nurseTask/CheckLiveTasksb/addCheckLiveTasksb")
    WebApiResponse addCheckLiveTasksb(@RequestParam(name = "taskId")String taskId,
                                      @RequestParam(name = "taskType")String taskType,
                                      @RequestParam(name = "taskName")String taskName,
                                      @RequestParam(name = "yhsbId")String yhsbId,
                                      @RequestParam(name = "tdOrgId")String tdOrgId,
                                      @RequestParam(name = "tdOrgName")String tdOrgName,
                                      @RequestParam(name = "checkType")String checkType,
                                      @RequestParam(name = "checkDept")String checkDept);

    /**
     * 添加隐患台账的接口
     * @param yhId
     * @return
     */
    @GetMapping("/nurseTask/kyYhHistory/reviewYh")
    WebApiResponse reviewYh(@RequestParam(name = "yhId") long yhId);



}