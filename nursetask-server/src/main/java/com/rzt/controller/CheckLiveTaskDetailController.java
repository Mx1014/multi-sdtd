
/**
 * 文件名：CHECKLIVETASKDETAILController
 * 版本信息：
 * 日期：2017/12/05 10:24:09
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;
import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.service.CheckLiveTaskDetailService;
import com.rzt.util.WebApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
@RequestMapping("CheckLiveTaskDetail")
public class CheckLiveTaskDetailController extends
        CurdController<CheckLiveTaskDetail,CheckLiveTaskDetailService> {

    //删除已经稽查的任务   /CheckLiveTasks/{id}  请求方式Delete

    //查看已稽查的任务详情  详细查询的内容未完成
    @GetMapping("/listCheckDoingById.do")
    public WebApiResponse listCheckNotDoById(String id){
        try {
            List list = this.service.listCheckDoingById(id);
            return WebApiResponse.success(list);
        }catch (Exception e){
            return WebApiResponse.erro("数据查询失败"+e.getMessage());
        }
    }
    @GetMapping("/listAllCheckDoing.do")
    public WebApiResponse listAllCheckDoing(CheckLiveTaskDetail task, Pageable pageable){
        try{
            List list = this.service.listAllCheckDoing(task, pageable);
            return WebApiResponse.success(list);
        }catch (Exception e) {
            return WebApiResponse.erro("数据获取失败"+e.getMessage());
        }
    }
    @GetMapping("/exportCheckTask.do")
    public WebApiResponse ExportCheckTask(HttpServletResponse response){
        try{
            this.service.exportExcel(response);
            return WebApiResponse.success("数据导出成功");
        }catch (Exception e) {
            return WebApiResponse.erro("数据导出失败"+e.getMessage());
        }
    }
}