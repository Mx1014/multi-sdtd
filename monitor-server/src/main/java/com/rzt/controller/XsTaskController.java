package com.rzt.controller;

import com.rzt.entity.CheckDetail;
import com.rzt.service.CheckDetailService;
import com.rzt.service.TimedService;
import com.rzt.service.XSZCTASKService;
import com.rzt.util.WebApiResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.params.ExcelExportEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


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
    public WebApiResponse getXsTaskAll(Integer page,Integer size, String taskType,String currentUserId,String userName,String TD,String targetType,String TaskName){
        return service.getXsTaskAll(page,size,taskType,currentUserId,userName,TD,targetType,TaskName);
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
    public WebApiResponse checkOff(CheckDetail checkDetail,String timedTaskId,String currentUserId,String picTime){
        try {
            checkDetail.setCheckUser(currentUserId);
            //根据审核人id和问题任务id查询该条审核记录是否存在
            Long detailID = detailService.findByCheckUserAndQuestionTaskId(checkDetail.getCheckUser(),checkDetail.getQuestionTaskId());
            if(detailID==null){
                 detailService.addCheckDetail(checkDetail);
            }

            service.checkOff(timedTaskId,picTime,currentUserId);
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
     * 查询为完成的任务
     * @return
     */
    @GetMapping("/failTask")
    public WebApiResponse failTask(Integer page,Integer size, String taskType,String currentUserId,String userName,String TD,String targetType){

            return service.getFailTask(page,size,taskType,currentUserId,userName,TD,targetType);


    }

    /**
     * 一级单位手动抽查
     * @return
     */
    @GetMapping("/timedTaskOne")
    public WebApiResponse timedTaskOne(){
        service.xsTaskAddAndFindThree();
        return WebApiResponse.success("success");
    }
    /**
     * 二级单位手动抽查
     * @return
     */
    @GetMapping("/timedTaskTwo")
    public WebApiResponse timedTaskTwo(){
        service.xsTaskAddAndFind();
        return WebApiResponse.success("success");
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
   /* @GetMapping("/useEasyPoiByTimedTask")
    public void useEasyPoi(HttpServletResponse response, String taskType, String currentUserId, String userName, String TD, String targetType){
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

    }*/














    /*private static final String taskNames = "220-luochen一-23-50,35-liugao一-1-7,110-chengliang-1-29,220-fangluo一-58-61,110-chengliangtaizhi-1-4,220-yunxing-1-23,220-anxing一-1-16,110-yuantuocunzhi-4-32,35-fanxin-21-40,220-yuntai一-16-35,220-yuntai一-35-50,220-yuntai一-50-6,110-beihuai-1-27,110-xianie一-75-84,220-changhuai一-56-75,35-qiunan一yuzhi-1-6,35-qiunan一-1-15,110-liban一-1-9,35-niuchang-30-68,35-qiunan一-15-44,220-shunping一-52-57,35-yanglong-60-73,220-shunping一-41-53,110-yunbei一huizhi-4-8,35-changwang-32-60,35-wuda一-36-54,35-canghe-1-13,110-niezhang二-1-20,110-qingqian一lizhi-28-57,110-liji-68-90,220-fusui一-9-37,110-tiansu一nianzhi-1-12,110-liusu一-1-19,110-liusu一lizhi-1-8,220-duyuan一-24-35,220-duchen一-1-13,110-lizhu-67-83,220-zhoucao一-1-25,110-nanhonglizhi-1-8,110-nanmao-22-26,110-nanhong-31-35,110-nanhonghangzhi-10-13,110-nanhong-18-25,110-junhe二-30-38,220-fangluo一-61-83,220-luochen一-1-23,110-huanbai一-1-11,35-huili-1-39,220-xishang一-26-52,110-zhuangwang二dizhi-1-2,220-yunxing-23-40,35-wangqiao二hanyangzhi-1-6,110-cunhuicizhi-1-26,110-dongcun-1-25,220-shunba一-78-100,35-niubei二-50-66,35-niubei二zhongzhi-1-7,35-niubei一-57-70,220-changhuai一-75-93,220-sunhuai一-98-116,110-huainiu一-36-67,35-niuchang-1-30,35-niubei二-16-50,220-shunba一-60-61,220-beisui一-27-28,220-shunshang一-62-63,220-beisui一-28-38,220-shunshang一-64-73,220-shunba一-62-71,220-beisui一-18-27,110-lihe一dongzhi-1-8,110-lihe一-1-18,220-fusui一-37-41,220-shunshang一-3-6,220-shunba一-3-6,110-liyang一fuzhi-24-42,220-shunfu一-3-6,220-shungao一-3-25,35-niushuibeizhi-1-15,220-shunba一-14-24,220-fusui一-1-9,220-shunfu一-15-17,220-shunshang一-14-25,35-niuchangshuizhi-1-11,35-niushui-28-48,220-shunba一-6-15,220-shunshang一-6-15,220-shunfu一-6-15,220-shunfu一-1-3,220-shunshang一-1-3,110-lichang一qianzhi-1-3,110-lichang一-60-101,35-niuchang-68-105,220-gaoyuan一-1-7,110-libai一-5-17,220-shunba一-24-47,110-lichang一-4-41,220-sunhuai一-030-050,110-sunji-25-47,35-tiansun一-1-7";



    */

    /**
     * 导出excle  临时   按照任务名称 导出任务拍照的做标
     */

    /*
    @GetMapping("/taskPoi")
    public String taskPoi(HttpServletResponse response){
        try {
            int i = 1 ;
            ArrayList<Map<String,Object>> objects = new ArrayList<>();
            String[] split = taskNames.split(",");
            for (String taskName : split) {
                if(null != taskName && !"".equals(taskName)){
                    Map<String, Object> mapHead = new HashMap<>();
                    mapHead.put("FILE_PATH","-----------------");
                    mapHead.put("LON","-----------------");
                    mapHead.put("LAT",taskName);
                    mapHead.put("TASK_NAME","------------");
                    mapHead.put("PROCESS_NAME","------------");
                    objects.add(mapHead);
                    //String taskName = "220-luochen一-23-50";

                    String sqla = "(SELECT ID\n" +
                            "    FROM XS_ZC_TASK WHERE TASK_NAME = '"+taskName+"' AND PLAN_START_TIME =\n" +
                            "    (SELECT max(PLAN_START_TIME)\n" +
                            "     FROM XS_ZC_TASK WHERE TASK_NAME = '"+taskName+"'))";
                    List<Map<String, Object>> maps1 = this.service.execSql(sqla);
                    if(maps1.size()<1){
                        continue;
                    }
                    String id = maps1.get(0).get("ID").toString();

                    String sql = "SELECT DISTINCT PROCESS_NAME" +
                            "     FROM PICTURE_TOUR WHERE TASK_ID = '"+id+"'" +
                            "                        AND PROCESS_NAME LIKE '%塔号照片' ORDER BY PROCESS_NAME";

                    List<Map<String, Object>> list = this.service.execSql(sql);
                    for (Map<String, Object> map : list) {
                        String sql2 = "SELECT FILE_PATH,LAT,LON,x.TASK_NAME,PROCESS_NAME" +
                                "     FROM PICTURE_TOUR p LEFT JOIN XS_ZC_TASK x ON x.ID = p.TASK_ID" +
                                "    WHERE TASK_ID =" +
                                "      (SELECT ID" +
                                "       FROM XS_ZC_TASK WHERE TASK_NAME = '"+taskName+"' AND PLAN_START_TIME =" +
                                "  (SELECT max(PLAN_START_TIME)" +
                                "   FROM XS_ZC_TASK WHERE TASK_NAME = '"+taskName+"')) AND PROCESS_NAME = '"+map.get("PROCESS_NAME")+"'";
                        List<Map<String, Object>> maps = this.service.execSql(sql2);
                        if(null != maps  &&  maps.size() > 0){
                            objects.add(maps.get(0));
                        }
                    }

                }
                Map<String, Object> stringObjectHashMap = new HashMap<>();
                stringObjectHashMap.put("FILE_PATH","-----------------");
                stringObjectHashMap.put("LON","-----------------");
                stringObjectHashMap.put("LAT","-----------------");
                stringObjectHashMap.put("TASK_NAME","------------");
                stringObjectHashMap.put("PROCESS_NAME","------------");
                objects.add(stringObjectHashMap);
                System.out.println(i++);
            }


            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            String format = simpleDateFormat.format(date);
            //第一个属性代表表头  第二个属性代表日期  第三个属性代表sheet
            ExportParams params = new ExportParams("任务照片做标导出", "日期："+format, "任务照片做标导出");
            List<ExcelExportEntity> entity = new ArrayList<ExcelExportEntity>();

            //责任人
            ExcelExportEntity entity5= new ExcelExportEntity("任务名称", "TASK_NAME");
            String[] userarrs = {"-_null"};
            entity5.setReplace(userarrs);
            entity5.setWidth(20);
            entity.add(entity5);

            //通道公司
            ExcelExportEntity entity1 = new ExcelExportEntity("照片路径", "FILE_PATH");
            String[] arrs = {"-_null"};
            entity1.setReplace(arrs);
            entity1.setWidth(20);
            entity.add(entity1);

            //外协单位
            ExcelExportEntity entity3= new ExcelExportEntity("LAT", "LAT");
            String[] wxarrs = {"-_null"};
            entity3.setReplace(wxarrs);
            entity3.setWidth(40);
            entity.add(entity3);

            //任务详情   PROCESS_NAME
            ExcelExportEntity entity4= new ExcelExportEntity("LON", "LON");
            String[] taskarrs = {"-_null"};
            entity4.setReplace(taskarrs);
            entity4.setWidth(40);
            entity.add(entity4);


            //任务详情
            ExcelExportEntity entity8= new ExcelExportEntity("照片名称", "PROCESS_NAME");
            String[] taskarrs8 = {"-_null"};
            entity8.setReplace(taskarrs8);
            entity8.setWidth(40);
            entity.add(entity8);

            // 模板   自定义导出模板     实体类集合
            Workbook workbook = ExcelExportUtil.exportExcel(params, entity, objects);

            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("坐标导出.xls", "UTF-8"));
            workbook.write(response.getOutputStream());
        }catch (Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
        return  "success";
    }*/


}
