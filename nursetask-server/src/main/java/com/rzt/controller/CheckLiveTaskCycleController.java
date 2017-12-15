/**
 * 文件名：CHECKLIVETASKDETAILController
 * 版本信息：
 * 日期：2017/12/05 10:24:09
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;
import com.rzt.entity.CheckLiveTaskCycle;
import com.rzt.service.CheckLiveTaskCycleService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 类名称：CHECKLIVETASKDETAILController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/05 10:24:09
 * 修改人：张虎成
 * 修改时间：2017/12/05 10:24:09
 * 修改备注：
 * @version
 */
@RestController
//@RequestMapping("CheckLiveTaskCycle")
public class CheckLiveTaskCycleController extends
        CurdController<CheckLiveTaskCycle, CheckLiveTaskCycleService> {

    @ApiOperation(value = "稽查维护查询",notes = "稽查维护的分页查询，条件搜索")
	@GetMapping("/checkTaskCycleMain.do")
    public WebApiResponse listCheckTaskMain(@RequestParam(value = "page",defaultValue = "0") Integer page,
                                            @RequestParam(value = "size",defaultValue = "15") Integer size,
                                            String startTime, String endTime, String userId,String taskName){
		try{
            System.out.println(startTime);
            System.out.println(endTime);
            Pageable pageable = new PageRequest(page, size);
            Page<Map<String, Object>> list = this.service.listCheckTaskMain(startTime,endTime, userId,taskName, pageable);
			return WebApiResponse.success(list);
		}catch (Exception e){
			return WebApiResponse.erro("数据获取失败"+e.getMessage());
		}

	}

    @ApiOperation(value = "稽查任务详情查询",notes = "稽查任务详情分页查询，条件搜索")
    @GetMapping("/listCheckTaskDetailById/{id}")
    public WebApiResponse listCheckTaskDetailById(@PathVariable String id){
        try{
            List list = this.service.listCheckTaskDetailById(id);
            return WebApiResponse.success(list);
        }catch (Exception e) {
            return WebApiResponse.erro("数据获取失败"+e.getMessage());
        }
    }

    @ApiOperation(value = "用户查询接口",notes = "用户查询")
    @GetMapping("/listAllCheckUser")
    public WebApiResponse listAllCheckUser(){
        try{
              List list = this.service.listAllCheckUser();
              return WebApiResponse.success(list);
        }catch (Exception e) {
            return WebApiResponse.erro("数据获取失败"+e.getMessage());
        }
    }
    @ApiOperation(value = "用户查询接口",notes = "稽查查询")
    @GetMapping("/listAllCheckTaskExec")
    public WebApiResponse listAllCheckTaskExec(){
        try{
            List list = this.service.listAllCheckTaskExec();
            return WebApiResponse.success(list);
        }catch (Exception e) {
            return WebApiResponse.erro("数据获取失败"+e.getMessage());
        }
    }





}
