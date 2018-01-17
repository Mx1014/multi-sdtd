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
    @GetMapping("addCycle")
    public Object addCycle( XsZcCycle xsZcCycle,String currentUserId,@RequestParam(value = "towerIds[]") Long[] towerIds) {
//    public Object addCycle( XsZcCycle xsZcCycle,String currentUserId) {
		try {
			xsZcCycle.setTotalTaskNum(0);
			xsZcCycle.setCreateTime(DateUtil.dateNow());
			this.service.addCycle(xsZcCycle,currentUserId,towerIds);
//			this.service.addCycle(xsZcCycle,currentUserId);
			return WebApiResponse.success("周期新增成功");
		} catch (Exception var) {
			return WebApiResponse.erro("周期新增失败" + var.getStackTrace());
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
	public Object cycleList(Pageable pageable,XsTaskSCh xsTaskSCh,String currentUserId) {
		try {
			Object cycleList = this.service.cycleList(pageable, xsTaskSCh,currentUserId);
			return WebApiResponse.success(cycleList);
		} catch (Exception var7) {
			return WebApiResponse.erro("数据查询失败" + var7.getStackTrace());
		}
	}


	@ApiOperation(value = "查找周期",notes = "查找周期")
	@GetMapping("getCycle")
	public Object getCycle(Long id) {
		try {
			Object cycle = this.service.getCycle(id);
			return WebApiResponse.success(cycle);
		} catch (Exception var3) {
			return WebApiResponse.erro("数据查询失败" + var3.getStackTrace());
		}

	}



    @ApiOperation(value = "周期删除",notes = "周期删除")
    @DeleteMapping("deleteCycle")
    public Object deleteCycle(@RequestParam(value = "ids[]") Long[] ids) {
        try {
			this.service.logicalDelete(ids);
            return WebApiResponse.success("数据删除成功");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据删除失败" + var3.getStackTrace());
        }

    }


    @ApiOperation(value = "任务删除",notes = "任务删除")
    @DeleteMapping("deletePlan")
    public Object deletePlan(@RequestParam(value = "ids[]") Long[] ids) {
        try {
			this.service.logicalDeletePlan(ids);
            return WebApiResponse.success("数据删除成功");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据删除失败" + var3.getStackTrace());
        }

    }

    @ApiOperation(value = "周期更新",notes = "周期更新")
    @PatchMapping("updateCycle")
		public Object updateCycle(Long id,Integer cycle,Integer inUse,Integer planXsNum,String planStartTime,String planEndTime,Integer isKt,String userId) {
        try {
            this.service.updateCycle(id,cycle,inUse,planXsNum,planStartTime,planEndTime,isKt,userId);
            return WebApiResponse.success("数据保存成功");
        } catch (Exception var3) {
            return WebApiResponse.erro("数据保存失败" + var3.getStackTrace());
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
	public Object listPlan(Pageable pageable, XsTaskSCh xsTaskSch,String currentUserId) {
		try {
			return this.service.listPlan(pageable,xsTaskSch,currentUserId);
		} catch (Exception var7) {
			return WebApiResponse.erro("数据查询失败" + var7.getMessage());
		}

	}

	/**
	 * @Method listPlanForMap
	 * @Description  任务派发 生成任务和人员信息
	 * @param
	 * @return java.lang.Object
	 * @date 2017/12/7 17:57
	 * @author nwz
	 */
	@ApiOperation(value = "pc端人员位置展示的巡视任务列表接口",notes = "pc端人员位置展示的巡视任务列表接口")
	@GetMapping("listPlanForMap")
	public Object listPlanForMap( Pageable pageable,XsTaskSCh xsTaskSch,String currentUserId) {
		try {
			return this.service.listPlanForMap(pageable,xsTaskSch,currentUserId);
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
	public Object listPictureById(Long taskId,Integer zj) {
		try {
			return WebApiResponse.success(this.service.listPictureById(taskId,zj));
		} catch (Exception var) {
			return WebApiResponse.erro("图片查找失败" + var.getStackTrace());
		}
	}


	/**
	 * @Method listExecByTaskid
	 * @Description  轮数据
	 * @param
	 * @return java.lang.Object
	 * @date 2017/12/7 17:57
	 * @author nwz
	 */
	@ApiOperation(value = "轮数据",notes = "轮数据")
	@GetMapping("listExecByTaskid")
	public Object listExecByTaskid(Long taskId) {
		try {
			return WebApiResponse.success(this.service.listExecByTaskid(taskId));
		} catch (Exception var) {
			return WebApiResponse.erro("图片查找失败" + var.getStackTrace());
		}
	}

	/**
	 * @Method listExecByTaskid
	 * @Description  轮详情数据
	 * @param
	 * @return java.lang.Object
	 * @date 2017/12/7 17:57
	 * @author nwz
	 */
	@ApiOperation(value = "轮详情数据",notes = "轮详情数据")
	@GetMapping("listExecDetail")
	public Object listExecDetail(Long execId) {
		try {
			return WebApiResponse.success(this.service.listExecDetail(execId));
		} catch (Exception var) {
			return WebApiResponse.erro("图片查找失败" + var.getStackTrace());
		}
	}


	@GetMapping("fuck")
	public Object fuck(Long execId) {
		try {
			return WebApiResponse.success(this.service.listExecDetail(execId));
		} catch (Exception var) {
			return WebApiResponse.erro("图片查找失败" + var.getStackTrace());
		}
	}


	@GetMapping("importCycle")
	public Object importCycle(String execlPath) {
		try {
			this.service.importCycle(execlPath);
			return WebApiResponse.success("成功了...");
		} catch (Exception var) {
			return WebApiResponse.erro("图片查找失败" + var.getStackTrace());
		}
	}

	@GetMapping("bornTask")
	public Object bornTask() {
		try {
			this.service.bornTask();
			return WebApiResponse.success("成功了...");
		} catch (Exception var) {
			return WebApiResponse.erro("图片查找失败" + var.getStackTrace());
		}
	}


	@GetMapping("redisInfo")
	public Object redisInfo() {
		try {
			this.service.zhengwanshuijiao();
			return WebApiResponse.success("成功了...");
		} catch (Exception var) {
			return WebApiResponse.erro("图片查找失败" + var.getStackTrace());
		}
	}



	@GetMapping("gaipinin")
	public Object gaipinin() {
		try {
//			this.service.gaipinin();
			this.service.gaipinin2();
			return WebApiResponse.success("成功了...");
		} catch (Exception var) {
			return WebApiResponse.erro("图片查找失败" + var.getStackTrace());
		}
	}

	@GetMapping("insertCycleTower")
	public Object insertCycleTower(String sql,Long xsZcCycleId) {
		try {
			this.service.insertCycleTower(sql,xsZcCycleId);
			return WebApiResponse.success("成功了...");
		} catch (Exception var) {
			return WebApiResponse.erro("图片查找失败" + var.getStackTrace());
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