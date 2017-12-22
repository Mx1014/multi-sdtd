/**
 * 文件名：CHECKLIVETASKDETAIL
 * 版本信息：
 * 日期：2017/12/13 17:32:33
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.entity;
import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
/**
 * 类名称：CHECKLIVETASKDETAIL
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/13 17:32:33
 * 修改人：张虎成
 * 修改时间：2017/12/13 17:32:33
 * 修改备注：
 * @version
 */
@Entity
@Table(name="CHECK_LIVE_TASK_DETAIL")
public class CheckLiveTaskDetail  implements Serializable{
	//字段描述:
   	 @Id
     private Long id;
    	//字段描述: 稽查父任务id(check_live_task_exec表id)
   	 @Column(name = "EXEC_ID")
     private Long execId;
    	//字段描述: 稽查任务id（check_live_task表）
   	 @Column(name = "TASK_ID")
     private Long taskId;
    	//字段描述: 计划开始时间
   	 @Column(name = "PLAN_START_TIME")
     private Date planStartTime;
    	//字段描述: 计划结束时间
   	 @Column(name = "PLAN_END_TIME")
     private Date planEndTime;
    	//字段描述: 实际开始时间
   	 @Column(name = "REAL_START_TIME")
     private Date realStartTime;
    	//字段描述: 实际结束时间
   	 @Column(name = "REAL_END_TIME")
     private Date realEndTime;
    	//字段描述: 执行情况 0未开始 1进行中 2已完成 3已超期
   	 @Column(name = "STATUS")
     private Integer status;
    	//字段描述: 达到现场时间
   	 @Column(name = "DDXC_TIME")
     private Date ddxcTime;
    	//字段描述: 周期内第多少次任务
   	 @Column(name = "COUNT")
     private Long count;
    	//字段描述: 是否在岗(0是 1否)
   	 @Column(name = "SFZG")
     private Integer sfzg;
    	//字段描述: 现场APP人员是否一直（0是 1否）
   	 @Column(name = "RYYZ")
     private Integer ryyz;
    	//字段描述: 是否清楚段落电压等级等（0是 1否）
   	 @Column(name = "DYDJ")
     private Integer dydj;
    	//字段描述: 是否掌握现场隐患信息（0是 1否）
   	 @Column(name = "YHXX")
     private Integer yhxx;
    	//字段描述: 是否清楚隐患处置方案（0 是 1否）
   	 @Column(name = "CZFA")
     private Integer czfa;
    	//字段描述: 是否存在其他问题（0 是 1否）
   	 @Column(name = "QTWT")
     private Integer qtwt;
    	//字段描述: 创建时间
   	 @Column(name = "CREATE_TIME")
     private Date createTime;

	public void setId(){
		this.id =   Long.valueOf(new SnowflakeIdWorker(0,0).nextId());
	}
	@ExcelResources(title="",order=1)
	public Long getId(){
		return this.id;
	}

	public void setExecId(Long execId){
		this.execId = execId;
	}
	@ExcelResources(title="稽查父任务id(check_live_task_exec表id)",order=2)
	public Long getExecId(){
		return this.execId;
	}

	public void setTaskId(Long taskId){
		this.taskId = taskId;
	}
	@ExcelResources(title="稽查任务id（check_live_task表）",order=3)
	public Long getTaskId(){
		return this.taskId;
	}

	public void setPlanStartTime(Date planStartTime){
		this.planStartTime = planStartTime;
	}
	@ExcelResources(title="计划开始时间",order=4)
	public Date getPlanStartTime(){
		return this.planStartTime;
	}

	public void setPlanEndTime(Date planEndTime){
		this.planEndTime = planEndTime;
	}
	@ExcelResources(title="计划结束时间",order=5)
	public Date getPlanEndTime(){
		return this.planEndTime;
	}

	public void setRealStartTime(Date realStartTime){
		this.realStartTime = realStartTime;
	}
	@ExcelResources(title="实际开始时间",order=6)
	public Date getRealStartTime(){
		return this.realStartTime;
	}

	public void setRealEndTime(Date realEndTime){
		this.realEndTime = realEndTime;
	}
	@ExcelResources(title="实际结束时间",order=7)
	public Date getRealEndTime(){
		return this.realEndTime;
	}

	public void setStatus(Integer status){
		this.status = status;
	}
	@ExcelResources(title="执行情况 0未开始 1进行中 2已完成 3已超期",order=8)
	public Integer getStatus(){
		return this.status;
	}

	public void setDdxcTime(Date ddxcTime){
		this.ddxcTime = ddxcTime;
	}
	@ExcelResources(title="达到现场时间",order=9)
	public Date getDdxcTime(){
		return this.ddxcTime;
	}

	public void setCount(Long count){
		this.count = count;
	}
	@ExcelResources(title="周期内第多少次任务",order=10)
	public Long getCount(){
		return this.count;
	}

	public void setSfzg(Integer sfzg){
		this.sfzg = sfzg;
	}
	@ExcelResources(title="是否在岗(0是 1否)",order=11)
	public Integer getSfzg(){
		return this.sfzg;
	}

	public void setRyyz(Integer ryyz){
		this.ryyz = ryyz;
	}
	@ExcelResources(title="现场APP人员是否一直（0是 1否）",order=12)
	public Integer getRyyz(){
		return this.ryyz;
	}

	public void setDydj(Integer dydj){
		this.dydj = dydj;
	}
	@ExcelResources(title="是否清楚段落电压等级等（0是 1否）",order=13)
	public Integer getDydj(){
		return this.dydj;
	}

	public void setYhxx(Integer yhxx){
		this.yhxx = yhxx;
	}
	@ExcelResources(title="是否掌握现场隐患信息（0是 1否）",order=14)
	public Integer getYhxx(){
		return this.yhxx;
	}

	public void setCzfa(Integer czfa){
		this.czfa = czfa;
	}
	@ExcelResources(title="是否清楚隐患处置方案（0 是 1否）",order=15)
	public Integer getCzfa(){
		return this.czfa;
	}

	public void setQtwt(Integer qtwt){
		this.qtwt = qtwt;
	}
	@ExcelResources(title="是否存在其他问题（0 是 1否）",order=16)
	public Integer getQtwt(){
		return this.qtwt;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	@ExcelResources(title="创建时间",order=17)
	public Date getCreateTime(){
		return this.createTime;
	}

}