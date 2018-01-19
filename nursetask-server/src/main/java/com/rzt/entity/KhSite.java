/**    
 * 文件名：KhSite
 * 版本信息：    
 * 日期：2017/11/28 14:43:44    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.rzt.entity.BaseEntity;
import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 类名称：KhSite
 * 类描述：InnoDB free: 536576 kB    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 14:43:44 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 14:43:44    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name = "KH_SITE")
public class KhSite  implements Serializable{
	//字段描述: 主任务id
	@Id
	private Long id;
	//字段描述: 电压等级 0 1 2 3
	@Column(name = "VTYPE")
	private String vtype;
	//字段描述: 线路名称
	@Column(name = "LINE_NAME")
	private String lineName;
	//字段描述: 段落
	@Column(name = "SECTION")
	private String section;
	//字段描述: 看护点状态（0未派发 1已派发 2停用）
	@Column(name = "STATUS")
	private int status;
	//字段描述: 任务名称
	@Column(name = "TASK_NAME")
	private String taskName;
	//字段描述: 看护人
	@Column(name = "USER_ID")
	private String userid;
	//字段描述: 通道运维单位
	@Column(name = "TDYW_ORG")
	private String tdywOrg;
	//字段描述: 外协单位
	@Column(name = "WX_ORG")
	private String wxOrg;
	//字段描述: 生成任务总次数
	@Column(name = "COUNT")
	private int count;
	//字段描述: 线路id
	@Column(name = "LINE_ID")
	private long lineId;
	//字段描述: 隐患id
	@Column(name = "YH_ID")
	private long yhId;
	//字段描述: 看护范围
	@Column(name = "KH_RANGE")
	private String khRange;
	//字段描述: 任务消缺时间
	@Column(name = "KHXQ_TIME")
	private Date khxqTime;
	//字段描述: 看护点创建时间
	@Column(name = "CREATE_TIME")
	private Date createTime;
	//字段描述: 几班倒
	@Column(name = "JBD")
	private String jbd;
	//字段描述: 队伍标识
	@Column(name = "GROUP_FLAG")
	private String groupFlag;
	//字段描述: 是否为队长（1是 0否）
	@Column(name = "CAPATAIN")
	private int capatain;
	//字段描述: 计划开始时段
	@Column(name = "PLAN_START_TIME")
	private Date planStartTime;
	//字段描述: 计划结束时段
	@Column(name = "PLAN_END_TIME")
	private Date planEndTime;
	//字段描述: 计划开始时段
	@Column(name = "WX_ORGID")
	private String wxOrgId;
	//字段描述: 计划结束时段
	@Column(name = "TDYW_ORGID")
	private String tdywOrgId;
	//字段描述: 一轮任务总时长
	@Column(name = "CYCLE")
	private double cycle;
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
		this.id = new SnowflakeIdWorker(1,1).nextId();
	}
	@ExcelResources(title="看护点id",order=1)
	public Long getId(){
		return this.id;
	}



	public void setVtype(String vtype){
		this.vtype = vtype;
	}
	@ExcelResources(title="电压等级",order=3)
	public String getVtype(){
		return this.vtype;
	}



	public void setLineName(String lineName){
		this.lineName = lineName;
	}
	@ExcelResources(title="线路名称",order=5)
	public String getLineName(){
		return this.lineName;
	}



	public void setSection(String section){
		this.section = section;
	}
	@ExcelResources(title="区段",order=4)
	public String getSection(){
		return this.section;
	}


	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
	@ExcelResources(title="任务名",order=2)
	public String getTaskName(){
		return this.taskName;
	}



	public void setUserid(String userid){
		this.userid = userid;
	}
	@ExcelResources(title="看护人id",order=6)
	public String getUserid(){
		return this.userid;
	}



	public void setTdywOrg(String tdywOrg){
		this.tdywOrg = tdywOrg;
	}
	@ExcelResources(title="通道运维单位",order=8)
	public String getTdywOrg(){
		return this.tdywOrg;
	}





	public void setKhRange(String khRange){
		this.khRange = khRange;
	}
	public String getKhRange(){
		return this.khRange;
	}



	public void setKhxqTime(Date khxqTime){
		this.khxqTime = khxqTime;
	}
	public Date getKhxqTime(){
		return this.khxqTime;
	}



	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	@ExcelResources(title="创建时间",order=9)
	public Date getCreateTime(){
		return this.createTime;
	}




	public void setGroupFlag(String groupFlag){
		this.groupFlag = groupFlag;
	}
	public String getGroupFlag(){
		return this.groupFlag;
	}




	public void setPlanStartTime(Date planStartTime){
		this.planStartTime = planStartTime;
	}
	public Date getPlanStartTime(){
		return this.planStartTime;
	}



	public void setPlanEndTime(Date planEndTime){
		this.planEndTime = planEndTime;
	}
	public Date getPlanEndTime(){
		return this.planEndTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getLineId() {
		return lineId;
	}

	public void setLineId(long lineId) {
		this.lineId = lineId;
	}
	@ExcelResources(title="隐患id",order=7)
	public long getYhId() {
		return yhId;
	}

	public void setYhId(long yhId) {
		this.yhId = yhId;
	}
	@ExcelResources(title="几班倒",order=10)
	public String getJbd() {
		return jbd;
	}

	public void setJbd(String jbd) {
		this.jbd = jbd;
	}

	public int getCapatain() {
		return capatain;
	}

	public void setCapatain(int capatain) {
		this.capatain = capatain;
	}

	public void setWxOrg(String wxOrg) {
		this.wxOrg = wxOrg;
	}

	public String getWxOrg() {
		return wxOrg;
	}

	public String getWxOrgId() {
		return wxOrgId;
	}

	public void setWxOrgId(String wxOrgId) {
		this.wxOrgId = wxOrgId;
	}

	public String getTdywOrgId() {
		return tdywOrgId;
	}

	public void setTdywOrgId(String tdywOrgId) {
		this.tdywOrgId = tdywOrgId;
	}

	public double getCycle() {
		return cycle;
	}

	public void setCycle(double cycle) {
		this.cycle = cycle;
	}
}