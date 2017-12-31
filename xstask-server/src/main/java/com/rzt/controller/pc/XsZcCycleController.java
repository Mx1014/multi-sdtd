/**    
 * 文件名：XsZcCycleController
 * 版本信息：    
 * 日期：2017/12/07 07:50:10    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller.pc;

import com.rzt.controller.CurdController;
import com.rzt.entity.app.XSZCTASK;
import com.rzt.entity.pc.XsZcCycle;
import com.rzt.entity.sch.XsTaskSCh;
import com.rzt.service.app.XSZCTASKService;
import com.rzt.service.pc.XsZcCycleService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类名称：XsZcCycleController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 07:50:10 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 07:50:10    
 * 修改备注：    
 * @version        
 */
@RestController
@Api(value = "pc巡视任务管理")
@RequestMapping("XsZcCycle")
public class XsZcCycleController extends
		CurdController<XsZcCycle, XsZcCycleService> {

	@Autowired
	private XSZCTASKService xszctaskService;

	/**
    * @Method addCycle
    * @Description 添加周期
    * @param [xsZcCycle]
    * @return java.lang.Object
    * @date 2017/12/7 16:25
    * @author nwz
    */
    @ApiOperation(value = "周期维护 新增周期",notes = "pc端新增周期")
    @PostMapping("addCycle")
    public Object addCycle( XsZcCycle xsZcCycle) {
		try {
			xsZcCycle.setTotalTaskNum(0);
			xsZcCycle.setCreateTime(DateUtil.dateNow());
			Object o = this.service.addCycle(xsZcCycle);
			return WebApiResponse.success("数据新增成功");
		} catch (Exception var) {
			return WebApiResponse.erro("数据新增失败" + var.getMessage());
		}
	}

	/**
	 * @Method addCycle
	 * @Description 周期维护页面列表
	 * @param [cycleList]
	 * @return java.lang.Object
	 * @date 2017/12/7 16:25
	 * @author nwz
	 */
	@ApiOperation(value = "周期维护页面列表",notes = "周期维护页面列表 查询的接口")
	@PostMapping("listCycle")
	public Object cycleList(Pageable pageable,XsTaskSCh xsTaskSCh) {
		try {
			Object cycleList = this.service.cycleList(pageable, xsTaskSCh);
			return WebApiResponse.success(cycleList);
		} catch (Exception var7) {
			return WebApiResponse.erro("数据查询失败" + var7.getMessage());
		}
	}


	@ApiOperation(value = "查找周期",notes = "查找周期")
	@GetMapping("getCycle")
	public Object getCycle(Long id) {
		try {
			Object cycle = this.service.getCycle(id);
			return WebApiResponse.success(cycle);
		} catch (Exception var3) {
			return WebApiResponse.erro("数据查询失败" + var3.getMessage());
		}

	}



    @ApiOperation(value = "周期删除",notes = "周期删除")
    @DeleteMapping("deleteCycle")
    public Object deleteCycle(@RequestParam(value = "ids[]") Long[] ids) {
        try {
			this.service.logicalDelete(ids);
            return WebApiResponse.success("数据删除成功");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据删除失败" + var3.getMessage());
        }

    }

    @ApiOperation(value = "周期更新",notes = "周期更新")
    @PatchMapping("updateCycle")
		public Object updateCycle(Long id,Integer cycle,Integer inUse,Integer planXsNum,String planStartTime,String planEndTime) {
        try {
            this.service.updateCycle(id,cycle,inUse,planXsNum,planStartTime,planEndTime);
            return WebApiResponse.success("数据保存成功");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据保存失败" + var3.getMessage());
        }

    }


	/**
	 * @Method addPlan
	 * @Description  任务派发 生成任务和人员信息
	 * @param [xszctask]
	 * @return java.lang.Object
	 * @date 2017/12/7 17:57
	 * @author nwz
	 */
	@PostMapping("addPlan")
	public Object addPlan(XSZCTASK xszctask) {
		return this.service.addPlan(xszctask);
	}

	/**
	 * @Method listPlan
	 * @Description  任务派发 生成任务和人员信息
	 * @param
	 * @return java.lang.Object
	 * @date 2017/12/7 17:57
	 * @author nwz
	 */
	@ApiOperation(value = "pc端任务派发列表",notes = "pc端任务派发列表")
	@GetMapping("listPlan")
	public Object listPlan(Pageable pageable, XsTaskSCh xsTaskSch) {
		try {
			return this.service.listPlan(pageable,xsTaskSch);
		} catch (Exception var7) {
			return WebApiResponse.erro("数据查询失败" + var7.getMessage());
		}

	}

	/**
	 * @Method listPlan
	 * @Description  任务派发 生成任务和人员信息
	 * @param
	 * @return java.lang.Object
	 * @date 2017/12/7 17:57
	 * @author nwz
	 */
	@ApiOperation(value = "pc端任务派发列表",notes = "pc端任务派发列表")
	@GetMapping("listPictureById")
	public Object listPictureById(Long taskId) {
		try {
			return WebApiResponse.success(this.service.listPictureById(taskId));
		} catch (Exception var) {
			return WebApiResponse.erro("图片查找失败" + var.getMessage());
		}
	}


	@InitBinder
	public void initBinder(ServletRequestDataBinder binder) {
		 /*** 自动转换日期类型的字段格式
		 */
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		 binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));

   }

}