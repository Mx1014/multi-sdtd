package com.rzt.controller;

import com.rzt.entity.RztSysUser;
import com.rzt.service.CheckLiveTaskService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("CHECKLIVETASK")
public class CheckLiveTaskController extends CurdController<RztSysUser, CheckLiveTaskService> {
    protected static Logger LOGGER = LoggerFactory.getLogger(CheckLiveTaskService.class);

    @ApiOperation(value = "看护稽查任务已派发看护任务列表接口",notes = "看护稽查任务已派发看护任务列表分页查询，条件搜索")
    @GetMapping("/listKhCheckTaskPage")
    public WebApiResponse listKhCheckTaskPage(@RequestParam(value = "page",defaultValue = "0") Integer page,
                                              @RequestParam(value = "size",defaultValue = "8") Integer size,
                                              String userId,String tddwId,String currentUserId,String startTime, String endTime,String status,String queryAll,String loginType,String home){
        try{
            Pageable pageable = new PageRequest(page, size);
            Page<Map<String, Object>> list = this.service.listKhCheckTaskPage(pageable, userId, tddwId,currentUserId,startTime,endTime,status,queryAll,loginType,home);
            return WebApiResponse.success(list);
        }catch (Exception e){
            return WebApiResponse.erro("数据获取失败"+e.getMessage());
        }
    }

    @ApiOperation(value = "巡视稽查任务已派发任务列表接口", notes = "巡视稽查任务已派发任务列表分页查询，条件搜索")
    @GetMapping("/listXsCheckTaskPage")
    public WebApiResponse listXsCheckTaskPage(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "15") Integer size,
                                              String startTime, String endTime, String userId, String tddwId, String currentUserId, String status) {
        try {
            Pageable pageable = new PageRequest(page, size);
            Page<Map<String, Object>> list = this.service.listXsCheckTaskPage(pageable, startTime, endTime, userId, tddwId, currentUserId, status);
            return WebApiResponse.success(list);
        } catch (Exception e) {
            LOGGER.error("巡视稽查任务已派发任务列表接口", e);
            return WebApiResponse.erro("数据获取失败");
        }
    }

    @ApiOperation(value = "任务情况展示展示接口", notes = "任务情况展示分页查询一级与二级运检部")
    @GetMapping("/listHtCheckTaskPage")
    public WebApiResponse listHtCheckTaskPage(Integer page, Integer size, String currentUserId, String taskType, String status, String deptId) {
        try {
            Pageable pageable = new PageRequest(page, size);
            return WebApiResponse.success(this.service.listHtCheckTaskPage(currentUserId, pageable, taskType, status, deptId));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("查询失败");
        }
    }

}
