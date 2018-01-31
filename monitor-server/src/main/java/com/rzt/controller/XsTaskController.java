package com.rzt.controller;

import com.rzt.entity.CheckDetail;
import com.rzt.service.CheckDetailService;
import com.rzt.service.TimedService;
import com.rzt.service.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.params.ExcelExportEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("XSZCTASKController")
public class XsTaskController extends
        CurdController<XsTaskController,XSZCTASKService>{
    @Autowired
    private TimedService timedService;
    @Autowired
    private CheckDetailService detailService;
    /**
     * 根据taskId获取当前任务的隐患信息
     * 根据taskId 查询当前任务进度
     * @param taskId
     * @return
     */
    @GetMapping("/fingYHByTaskId")
    public WebApiResponse fingYHByTaskId(Long taskId,String TASKTYPE){
        if(null!= taskId && !"".equals(taskId)){
            return service.findYHByTaskId(taskId,TASKTYPE);
        }
        return WebApiResponse.erro("参数错误");
    }

    /**
     * 查询抽查表内的所有数据
     * @param taskType  任务类型
     * @return
     */
    @GetMapping("/getXsTaskAll")
    public WebApiResponse getXsTaskAll(Integer page,Integer size, String taskType,String currentUserId,String userName,String TD,String targetType){
        return service.getXsTaskAll(page,size,taskType,currentUserId,userName,TD,targetType);
    }
    @GetMapping("/findDeptAuth")
    public String findDeptAuth(String currentUserId){
        return service.findDeptAuth(currentUserId);
    }


    /**
     * 根据当前用户权限获取当前的刷新周期
     * @param currentUserId
     * @return
     */
    @GetMapping("/getTimeConfig")
    public WebApiResponse getTimeConfig(String currentUserId){
        return timedService.getTimedConfig(currentUserId);
    }



    /**
     * 提交审核
     * @param checkDetail
     * @return
     */
    @GetMapping("checkOff")
    public WebApiResponse checkOff(CheckDetail checkDetail,String timedTaskId,String currentUserId){
        try {
            checkDetail.setCheckUser(currentUserId);
            //根据审核人id和问题任务id查询该条审核记录是否存在
            Long detailID = detailService.findByCheckUserAndQuestionTaskId(checkDetail.getCheckUser(),checkDetail.getQuestionTaskId());
            if(detailID==null){
                 detailService.addCheckDetail(checkDetail);
            }

            service.checkOff(timedTaskId);
            return WebApiResponse.success("success");
        }catch (Exception e){
            return WebApiResponse.erro("更改状态失败："+e.getMessage());
        }
    }

    /**
     * 查看所有单位排班情况
     * @return
     */
    @GetMapping("/findWorkings")
    public WebApiResponse findWorkings(String currentUserId){
        return service.findWorking(currentUserId);
    }

    /**
     * 修改排班情况
     * @param currentUserId  当前登录人id
     * @param deptId         部门id
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @return
     */
    @GetMapping("/updateWorkings")
    public WebApiResponse updateWorkings(String currentUserId,String deptId,String startTime
                    ,String endTime,String dayUserId,String nightUserId){

            return service.updateWorkings(currentUserId,deptId,startTime,endTime,dayUserId,nightUserId);
    }

    /**
     * 导出excle
     * @param response
     * @param taskType
     * @param currentUserId
     * @param userName
     * @param TD
     * @param targetType
     */
    @GetMapping("/useEasyPoiByTimedTask")
    public void useEasyPoi(HttpServletResponse response,String taskType,String currentUserId,String userName,String TD,String targetType){
      try {
          List<Map<String, Object>> maps = service.usePoi(taskType, currentUserId, userName, TD, targetType);
          Date date = new Date();
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
          String format = simpleDateFormat.format(date);
          //第一个属性代表表头  第二个属性代表日期  第三个属性代表sheet
          ExportParams params = new ExportParams("抽查任务列表展示", "日期："+format, "用户列表");
          List<ExcelExportEntity> entity = new ArrayList<ExcelExportEntity>();
          //抽查时间
          ExcelExportEntity entity2 = new ExcelExportEntity("抽查时间", "CREATETIME");
          String[] CREATETIMEarrs = {"--_null"};
          entity2.setReplace(CREATETIMEarrs);
          entity2.setFormat("YYYY-MM-dd HH:mm:ss");
          entity2.setWidth(20);
          entity.add(entity2);
          //通道公司
          ExcelExportEntity entity1 = new ExcelExportEntity("通道公司", "DEPT");
          String[] arrs = {"-_null"};
          entity1.setReplace(arrs);
          entity1.setWidth(20);
          entity.add(entity1);

            //外协单位
          ExcelExportEntity entity3= new ExcelExportEntity("外协单位", "COMPANYNAME");
          String[] wxarrs = {"-_null"};
          entity3.setReplace(wxarrs);
          entity3.setWidth(40);
          entity.add(entity3);

          //任务详情
          ExcelExportEntity entity4= new ExcelExportEntity("任务详情", "TASKNAME");
          String[] taskarrs = {"-_null"};
          entity4.setReplace(taskarrs);
          entity4.setWidth(40);
          entity.add(entity4);

          //责任人
          ExcelExportEntity entity5= new ExcelExportEntity("责任人", "REALNAME");
          String[] userarrs = {"-_null"};
          entity5.setReplace(userarrs);
          entity5.setWidth(20);
          entity.add(entity5);
          //电话
          ExcelExportEntity entity6= new ExcelExportEntity("电话", "PHONE");
          String[] phonearrs = {"-_null"};
          entity6.setReplace(phonearrs);
          entity6.setWidth(40);
          entity.add(entity6);
            //任务类型
          ExcelExportEntity entity7= new ExcelExportEntity("任务类型", "TASKTYPE");

          String[] typearrs = {"-_null","巡视_1","看护_2","稽查_3"};
          entity7.setReplace(typearrs);
          entity7.setWidth(20);
          entity.add(entity7);
          //任务状态
          ExcelExportEntity entity8= new ExcelExportEntity("任务状态", "TASKTYPE");
          String[] statusarrs = {"-_null","进行中_1","已完成_2","已消缺_3"};
          entity8.setReplace(statusarrs);
          entity8.setWidth(20);
          entity.add(entity8);


          // 模板   自定义导出模板     实体类集合
          Workbook workbook = ExcelExportUtil.exportExcel(params, entity, maps);

          response.setCharacterEncoding("UTF-8");
          response.setHeader("content-Type", "application/vnd.ms-excel");
          response.setHeader("Content-Disposition",
                  "attachment;filename=" + URLEncoder.encode("用户列表.xls", "UTF-8"));
          workbook.write(response.getOutputStream());
      }catch (Exception e){

      }

    }




}
