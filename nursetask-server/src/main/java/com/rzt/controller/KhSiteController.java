/**
 * 文件名：KhCycleController
 * 版本信息：
 * 日期：2017/11/28 14:43:44
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.CheckLiveTask;
import com.rzt.entity.KhSite;
import com.rzt.entity.KhTask;
import com.rzt.entity.KhYhHistory;
import com.rzt.entity.model.KhTaskModel;
import com.rzt.service.CheckLiveTaskService;
import com.rzt.service.KhSiteService;
import com.rzt.service.KhTaskService;
import com.rzt.service.KhYhHistoryService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类名称：KhCycleController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 14:43:44 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 14:43:44    
 * 修改备注：    
 * @version
 */
@RestController
    @RequestMapping("KhSite")
public class KhSiteController extends
        CurdController<KhSite, KhSiteService> {
    @Autowired
    private KhYhHistoryService yhservice;
    @Autowired
    private KhTaskService taskService;
    @Autowired
    private CheckLiveTaskService checkService;


    //  数据没有设置完成  稽查任务实体类有部分修改
    @PostMapping("/saveYh.do")
    @ResponseBody
    public WebApiResponse saveYh(KhYhHistory yh, String fxtime,String startTowerName,String endTowerName) {
        return this.service.saveYh(yh, fxtime,startTowerName,endTowerName);
    }


    /***
     * 获取 待安排的看护任务
     * @return
     */
    @GetMapping("/listAllTaskNotDo.do")
    @ResponseBody
    public WebApiResponse listAllTaskNotDo(HttpServletResponse response, KhTaskModel task, Pageable pageable, String userName,String deptId) {
        try {
            //分页参数 page size
            Page list = this.service.listAllTaskNotDo(task, pageable, userName,deptId);
            return WebApiResponse.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    /**
     * 消缺待安排任务   同时将隐患状态修改？
     */
    @PatchMapping("/xiaoQueTask.do")
    @ResponseBody
    @Transactional
    public WebApiResponse updateQxTask(String id) {
        try {
            String[] split = id.split(",");
            if (split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    this.service.updateQxTask(Long.parseLong(split[i]));
                }
            }
            return WebApiResponse.success("任务消缺成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("任务消缺失败" + e.getMessage());
        }
    }

    @GetMapping("/listKhtaskById.do")
    @ResponseBody
    public WebApiResponse listKhtaskByid(HttpServletResponse response,String id) {
        try {
            response.setHeader("Access-Control-Allow-Origin","*");
            List list = this.service.listKhtaskByid(Long.parseLong(id));
            return WebApiResponse.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    /**
     * 删除待安排任务  请求方式 DELETE  删除看护点
     */
    @DeleteMapping("/deleteById/{id}")
    @ResponseBody
    @Transactional
    public WebApiResponse deleteById(@PathVariable("id") String id) {
        return this.service.deleteById(id);
    }

    //派发任务  参数传递taskList[0].planStartTime
    @PostMapping("/paifaTask.do")
    @ResponseBody
    @Transactional
    public WebApiResponse paifaTask(String id, String tasks, KhTaskModel model) {
        return this.service.paifaTask(id,tasks,model);
    }
    @PostMapping("/listJpgById.do")
    @ResponseBody
    public WebApiResponse listJpgById(String taskId) {
        return this.service.listJpgById(taskId);
    }

    @GetMapping("/listOverdueKh.do")
    @ResponseBody
    public WebApiResponse listOverdueKh(){
        return this.service.listOverdueKh();
    }
/**
 *
 */

    /**
     * 导出文件的接口
     * @param request
     * @param response
     */
    @GetMapping("/exportNursePlan.do")
    public void exportNursePlan(HttpServletRequest request, HttpServletResponse response) {
        try {
            List all = this.service.findAll();
            this.service.exportExcel(response);
            /*String rootpath = request.getSession().getServletContext().getRealPath(File.separator);
			*//*String ecxcelModelPath = rootpath + "excelModels"+File.separator+"巡视任务导出表.xlsx";
			InputStream in = new FileInputStream(ecxcelModelPath);
			XSSFWorkbook wb = new XSSFWorkbook(in);*//*
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet("看护任务");

			// 设置列宽
			sheet.setColumnWidth((short) 0, (short) 6000);
			sheet.setColumnWidth((short) 1, (short) 6000);
			sheet.setColumnWidth((short) 2, (short) 6000);
			sheet.setColumnWidth((short) 3, (short) 6000);
			sheet.setColumnWidth((short) 4, (short) 6000);
			sheet.setColumnWidth((short) 5, (short) 6000);// 空列设置小一些
			sheet.setColumnWidth((short) 6, (short) 6000);// 设置列宽
			sheet.setColumnWidth((short) 7, (short) 6000);
			sheet.setColumnWidth((short) 8, (short) 6000);
			sheet.setColumnWidth((short) 9, (short) 6000);

			XSSFCellStyle cellstyle = wb.createCellStyle();// 设置表头样式
			cellstyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 设置居中

			XSSFCellStyle headerStyle = wb.createCellStyle();// 创建标题样式
			headerStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);    //设置垂直居中
			headerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);   //设置水平居中
			XSSFFont headerFont = wb.createFont(); //创建字体样式
			headerFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD); // 字体加粗
			headerFont.setFontName("Times New Roman");  //设置字体类型
			headerFont.setFontHeightInPoints((short) 12);    //设置字体大小
			headerStyle.setFont(headerFont);    //为标题样式设置字体样式

			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("任务名称");
			cell.setCellStyle(headerStyle);
			cell = row.createCell((short) 1);
			cell.setCellValue("派发时间");
			cell.setCellStyle(headerStyle);
			cell = row.createCell((short) 2);
			cell.setCellValue("计划开始时间");
			cell.setCellStyle(headerStyle);
			cell = row.createCell((short) 3);
			cell.setCellValue("计划结束时间");
			cell.setCellStyle(headerStyle);
			cell = row.createCell((short) 4);
			cell.setCellValue("通道单位");
			cell.setCellStyle(headerStyle);
			cell = row.createCell((short) 5);
			cell.setCellValue("班组");
			cell.setCellStyle(headerStyle);
			cell = row.createCell((short) 6);
			cell.setCellValue("巡视人员");
			cell.setCellStyle(headerStyle);
			cell = row.createCell((short) 7);
			cell.setCellValue("实际开始时间");
			cell.setCellStyle(headerStyle);
			cell = row.createCell((short) 8);
			cell.setCellValue("实际完成时间");
			cell.setCellStyle(headerStyle);
			cell = row.createCell((short) 9);
			cell.setCellValue("状态");
			cell.setCellStyle(headerStyle);
			//Sheet sheet = wb.getSheetAt(0);
			for (int i = 0; i < taskList.size(); i++) {
				row = sheet.createRow(i + 1);
				//Row row = sheet.getRow(i+1);
				Map<String, Object> task = taskList.get(i);
				if (task.get("TASKNAME") != null) {
					row.createCell(0).setCellValue(task.get("TASKNAME").toString());//任务名称
				}
				if (task.get("CREATETIME") != null) {
					row.createCell(1).setCellValue(task.get("CREATETIME").toString());//派发时间
				}
				if (task.get("PLANSTARTTIME") != null) {
					row.createCell(2).setCellValue(task.get("PLANSTARTTIME").toString());//计划开始时间
				}
				if (task.get("PLANENDTIME") != null) {
					row.createCell(3).setCellValue(task.get("PLANENDTIME").toString());//计划结束时间
				}

				if (task.get("tddwName") != null) {
					row.createCell(4).setCellValue(task.get("tddwName").toString());//通道单位
				}
				if (task.get("orgName") != null) {
					row.createCell(5).setCellValue(task.get("orgName").toString());//班组
				}
				if (task.get("nickName") != null) {
					row.createCell(6).setCellValue(task.get("nickName").toString());//巡视人员
				}
				if (task.get("REALSTARTTIME") != null) {
					row.createCell(7).setCellValue(task.get("REALSTARTTIME").toString());
				}
				if (task.get("REALENDTIME") != null) {
					row.createCell(8).setCellValue(task.get("REALENDTIME").toString());
				}
				int status = Integer.parseInt(task.get("STATUS").toString());
				//该次执行状态(0待办,1进行中,2完成)

				if (status == 0) {
					row.createCell(9).setCellValue("待办");
				} else if (status == 1) {
					row.createCell(9).setCellValue("进行中");
				} else if (status == 2) {
					row.createCell(9).setCellValue("完成");
				}

			}

			OutputStream output = response.getOutputStream();
			response.reset();
			response.setHeader("Content-disposition", "attachment; filename=" + new String("看护任务导出表.xlsx".getBytes("utf-8"), "iso8859-1"));
			response.setContentType("Content-Type:application/vnd.ms-excel ");
			wb.write(output);
			output.close();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 查看图片记录的接口
     */
	/*
	<select id="getRecordAndPic" resultType="java.util.HashMap" parameterType="java.lang.String">
		select av.mokuai,av.PROCESS_ID step,tn.STEPNAME,av.file_path,av.file_name,av.create_time from CM_UPLOAD_AV av
		<if test="taskType == 2">
			left join SUBTOURNOTE tn on av.task_id=tn.taskid and av.process_id=tn.step
			where av.mokuai=#{taskType} and av.file_type=1 and av.task_id=#{id}
		</if>
		<if test="taskType == 1">
			left join SDTD_TDKH_STEP tn on av.TASK_DETAILS_ID=tn.TASKDETAILSID and av.process_id=tn.STEPNUM
			where av.mokuai=#{taskType} and av.file_type=1 and av.TASK_DETAILS_ID=#{id}
		</if>
		order by to_number(av.process_id)
	</select>

	 */
	 /* @Autowired
    private UserCenter userCenter;
    @Autowired
    private LineData line;

    @GetMapping("/getLineInfoCommOptions")
    public WebApiResponse getLineInfoCommOptions(String kv){
        return line.getLine(kv);
    }

    @GetMapping("/getTowerInfoCommOptions")
    public WebApiResponse getTowerInfoCommOptions(String LineId){
        return line.getTower(LineId);
    }
    @GetMapping("/treeQuery")
    public List userCenter(String id){
        return userCenter.treeQuery(id);
    }

    @GetMapping("/userQuery")
    public WebApiResponse userQuery(@RequestParam("classname") String classname,@RequestParam(value = "realname",required = false)String realname){
        return userCenter.userQuery(classname,realname);
    }
    //外协单位
    @GetMapping("/queryOrgName")
    public WebApiResponse queryOrgName(){
        return userCenter.queryOrgName();
    }
    //通道单位
    @GetMapping("/queryCompanyname")
    public WebApiResponse queryCompanyname(){
        return userCenter.queryCompanyname();
    }*/

	 /* *//**
     * 审批隐患后
     * @param id
     * @return
     *//*
    @GetMapping("/shenpiYh")
    @ResponseBody
    public WebApiResponse shenpiYh(String id, KhYhHistory yh1) {
        try {
//			yhservice.update(); 修改隐患审批状态

            KhYhHistory yh = yhservice.findOne(id);
            KhSite task = new KhSite();
            String taskName = yh.getVtype() + yh.getLineName() + yh.getStartTower() + "-" + yh.getEndTower() + "号杆塔看护任务";
            task.setCreateTime(new Date());
            task.setVtype(yh.getVtype());
            task.setLineName(yh.getLineName());
            task.setTdywOrg(yh.getTdywOrg());
            task.setTaskName(taskName);
            task.setStatus(0);//隐患未消除
            task.setStatus(0);//未停用
            task.setCount(0);//生成任务次数0
            task.setYhId(yh.getId());
            task.setCreateTime(new Date());
            this.service.add(task);
            //	yh1.setYhdm("已定级");
            //yh1.setTaskId(task.getId());
            yhservice.update(yh1, id);
            return WebApiResponse.success("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }*/
}