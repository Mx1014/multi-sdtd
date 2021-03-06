/**    
 * 文件名：KhTask           
 * 版本信息：    
 * 日期：2017/11/28 14:43:44    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;
import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PLAN_START_TIME")
	private Date planStartTime;
	//字段描述: 计划结束时间
	@Temporal(TemporalType.TIMESTAMP)
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
	/*//字段描述: 周期内第多少次任务
	@Column(name = "TASK_NUM_IN_CYCLE")
	private int taskNumInCycle;*/
	//字段描述: 外协单位
	@Column(name = "WX_ORG")
	private String wxOrg;
	//字段描述: 看护点（主任务）ID
	@Column(name = "SITE_ID")
	private Long siteId;
	//字段描述: 看护人ID
	@Column(name = "USER_ID")
	private String userId;
	//字段描述: 隐患id
	@Column(name = "YH_ID")
	private Long yhId;
	//字段描述: 看护任务名称
	@Column(name = "TASK_NAME")
	private String taskName;
	//字段描述: 看护任务派发时间
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_TIME")
	private Date createTime;
	//字段描述: 物品确认时间
	@Column(name = "WPQR_TIME")
	private String wpqrTime;
	//字段描述: 任务状态 0未开始 1进行中 2已完成
	@Column(name = "STATUS")
	private int status;
	//字段描述: 未到位原因
	@Column(name = "REASON")
	private String reason;
	//字段描述: 是否到位 0到位 1未到位
	@Column(name = "IS_DW")
	private int isdw;

	@Column(name="ZXYS_NUM")
	private int zxysNum;
	//字段描述：任务类型
	@Column(name="TASK_TYPE")
	private int taskType;

	@Column(name="YWORG_ID")
	private String ywOrgId;
	//字段描述：任务类型
	@Column(name="WXORG_ID")
	private String wxOrgId;

	public int getTaskType() {
		return taskType;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	/*//字段描述：逻辑删除（0未删除 1已删除）
            @Column(name="IS_DELETE")
            private double isDelete;

            public double getIsDelete() {
                return isDelete;
            }

            public void setIsDelete(double isDelete) {
                this.isDelete = isDelete;
            }*/
	public void setId(){
		if(id==null||id==0){
			SnowflakeIdWorker instance = SnowflakeIdWorker.getInstance(8, 13);
			this.id = instance.nextId();
		}else{
			this.id = id;
		}
	}

	public String getWxOrgId() {
		return wxOrgId;
	}

	public void setWxOrgId(String wxOrgId) {
		this.wxOrgId = wxOrgId;
	}

	public String getYwOrgId() {
		return ywOrgId;
	}

	public void setYwOrgId(String ywOrgId) {
		this.ywOrgId = ywOrgId;
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

	/*public void setTaskNumInCycle(int taskNumInCycle){
		this.taskNumInCycle = taskNumInCycle;
	}
	@ExcelResources(title="周期内第多少次任务",order=10)
	public int getTaskNumInCycle(){
		return this.taskNumInCycle;
	}*/

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

	public void setStatus(int status){
		this.status = status;
	}
	@ExcelResources(title="任务状态 0未开始 1进行中 2已完成 ",order=20)
	public int getStatus(){
		return this.status;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getZxysNum() {
		return zxysNum;
	}

	public void setZxysNum(int zxysNum) {
		this.zxysNum = zxysNum;
	}

	public int getIsdw() {
		return isdw;
	}

	public String getReason() {
		return reason;
	}

	public void setIsdw(int isdw) {
		this.isdw = isdw;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}