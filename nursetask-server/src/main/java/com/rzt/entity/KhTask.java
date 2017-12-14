/**    
 * 文件名：KhTask           
 * 版本信息：    
 * 日期：2017/11/28 14:43:44    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;
import com.rzt.entity.BaseEntity;
import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**      
 * 类名称：KhTask    
 * 类描述：InnoDB free: 536576 kB    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 14:43:44 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 14:43:44    
 * 修改备注：    
 * @version        
 */
@Entity
public class KhTask implements Serializable{
	@Id
	private Long id;
	//字段描述: 计划开始时间
	@Column(name = "PLAN_START_TIME")
	private Date planStartTime;
	//字段描述: 计划结束时间
	@Column(name = "PLAN_END_TIME")
	private Date planEndTime;
	//字段描述: 第几次执行该任务
	@Column(name = "COUNT")
	private int count;
	//字段描述: 通道运维单位
	@Column(name = "TDYW_ORG")
	private String tdywOrg;
	//字段描述: 实际开始时间
	@Column(name = "REAL_START_TIME")
	private String realStartTime;
	//字段描述: 实际结束时间
	@Column(name = "REAL_END_TIME")
	private String realEndTime;
	//字段描述: 身份确认时间
	@Column(name = "SFQR_TIME")
	private String sfqrTime;
	//字段描述: 到达现场时间
	@Column(name = "DDXC_TIME")
	private String ddxcTime;
	//字段描述: 周期内第多少次任务
	@Column(name = "TASK_NUM_IN_CYCLE")
	private int taskNumInCycle;
	//字段描述: 外协单位
	@Column(name = "WX_ORG")
	private String wxOrg;
	//字段描述: 看护点（主任务）ID
	@Column(name = "SITE_ID")
	private Long siteId;
	//字段描述: 看护人ID
	@Column(name = "USER_ID")
	private String userId;
	//字段描述: 是否为负责人  0为队长 1为队员
	@Column(name = "CAPTAIN")
	private String captain;
	//字段描述: 队伍标识  一样的为一队
	@Column(name = "GROUP_FLAG")
	private String groupFlag;
	//字段描述: 隐患id
	@Column(name = "YH_ID")
	private Long yhId;
	//字段描述: 看护任务名称
	@Column(name = "TASK_NAME")
	private String taskName;
	//字段描述: 看护任务派发时间
	@Column(name = "CREATE_TIME")
	private Date createTime;
	//字段描述: 物品确认时间
	@Column(name = "WPQR_TIME")
	private String wpqrTime;
	//字段描述: 任务状态 0未开始 1进行中 2已完成
	@Column(name = "STATUS")
	private String status;
	public void setId(){
		this.id =   Long.valueOf(new SnowflakeIdWorker(0,0).nextId());
	}
	@ExcelResources(title="",order=1)
	public Long getId(){
		return this.id;
	}

	public void setPlanStartTime(Date planStartTime){
		this.planStartTime = planStartTime;
	}
	@ExcelResources(title="计划开始时间",order=2)
	public Date getPlanStartTime(){
		return this.planStartTime;
	}

	public void setPlanEndTime(Date planEndTime){
		this.planEndTime = planEndTime;
	}
	@ExcelResources(title="计划结束时间",order=3)
	public Date getPlanEndTime(){
		return this.planEndTime;
	}

	public void setCount(int count){
		this.count = count;
	}
	@ExcelResources(title="第几次执行该任务",order=4)
	public int getCount(){
		return this.count;
	}

	public void setTdywOrg(String tdywOrg){
		this.tdywOrg = tdywOrg;
	}
	@ExcelResources(title="通道运维单位",order=5)
	public String getTdywOrg(){
		return this.tdywOrg;
	}

	public void setRealStartTime(String realStartTime){
		this.realStartTime = realStartTime;
	}
	@ExcelResources(title="实际开始时间",order=6)
	public String getRealStartTime(){
		return this.realStartTime;
	}

	public void setRealEndTime(String realEndTime){
		this.realEndTime = realEndTime;
	}
	@ExcelResources(title="实际结束时间",order=7)
	public String getRealEndTime(){
		return this.realEndTime;
	}

	public void setSfqrTime(String sfqrTime){
		this.sfqrTime = sfqrTime;
	}
	@ExcelResources(title="身份确认时间",order=8)
	public String getSfqrTime(){
		return this.sfqrTime;
	}

	public void setDdxcTime(String ddxcTime){
		this.ddxcTime = ddxcTime;
	}
	@ExcelResources(title="到达现场时间",order=9)
	public String getDdxcTime(){
		return this.ddxcTime;
	}

	public void setTaskNumInCycle(int taskNumInCycle){
		this.taskNumInCycle = taskNumInCycle;
	}
	@ExcelResources(title="周期内第多少次任务",order=10)
	public int getTaskNumInCycle(){
		return this.taskNumInCycle;
	}

	public void setWxOrg(String wxOrg){
		this.wxOrg = wxOrg;
	}
	@ExcelResources(title="外协单位",order=11)
	public String getWxOrg(){
		return this.wxOrg;
	}

	public void setSiteId(Long siteId){
		this.siteId = siteId;
	}
	@ExcelResources(title="看护点（主任务）ID",order=12)
	public Long getSiteId(){
		return this.siteId;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}
	@ExcelResources(title="看护人ID",order=13)
	public String getUserId(){
		return this.userId;
	}

	public void setCaptain(String captain){
		this.captain = captain;
	}
	@ExcelResources(title="是否为负责人  0为队长 1为队员",order=14)
	public String getCaptain(){
		return this.captain;
	}

	public void setGroupFlag(String groupFlag){
		this.groupFlag = groupFlag;
	}
	@ExcelResources(title="队伍标识  一样的为一队",order=15)
	public String getGroupFlag(){
		return this.groupFlag;
	}

	public void setYhId(Long yhId){
		this.yhId = yhId;
	}
	@ExcelResources(title="隐患id",order=16)
	public Long getYhId(){
		return this.yhId;
	}

	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
	@ExcelResources(title="看护任务名称",order=17)
	public String getTaskName(){
		return this.taskName;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	@ExcelResources(title="看护任务派发时间",order=18)
	public Date  getCreateTime(){
		return this.createTime;
	}

	public void setWpqrTime(String wpqrTime){
		this.wpqrTime = wpqrTime;
	}
	@ExcelResources(title="物品确认时间",order=19)
	public String getWpqrTime(){
		return this.wpqrTime;
	}

	public void setStatus(String status){
		this.status = status;
	}
	@ExcelResources(title="任务状态 0未开始 1进行中 2已完成 ",order=20)
	public String getStatus(){
		return this.status;
	}

}