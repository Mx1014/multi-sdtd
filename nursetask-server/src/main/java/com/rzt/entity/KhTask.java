/**    
 * 文件名：KhTask           
 * 版本信息：    
 * 日期：2017/11/28 14:43:44    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;
import com.rzt.entity.BaseEntity;
import com.rzt.util.excelUtil.ExcelResources;
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
public class KhTask extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 巡视频率
   	 @Column(name = "TOUR_FREQ")
     private String tourFreq;
    	//字段描述: 通道运维单位
   	 @Column(name = "TDYW_ORG")
     private String tdywOrg;
    	//字段描述: 看护点id
   	 @Column(name = "TOUR_ID")
     private String tourId;
    	//字段描述: 看护人
   	 @Column(name = "USER_ID")
     private String userId;
    	//字段描述: 是否队长 0为队长
   	 @Column(name = "CAPTAIN")
     private int captain;
    	//字段描述: 队伍标识
   	 @Column(name = "GROUP_FLAG")
     private int groupFlag;
    	//字段描述: 任务名称
   	 @Column(name = "TASK_NAME")
     private String taskName;
    	//字段描述: 隐患id
   	 @Column(name = "YH_ID")
     private String yhId;
    	//字段描述: 创建时间
   	 @Column(name = "CREATE_TIME")
     private String createTime;
    	//字段描述: 派发时间
   	 @Column(name = "PF_TIME")
     private String pfTime;
    	//字段描述: 看护任务状态
   	 @Column(name = "STATUS")
     private String status;
    	//字段描述: 计划开始时间
   	 @Column(name = "PLAN_START_TIME")
     private String planStartTime;
    	//字段描述: 计划结束时间
   	 @Column(name = "PLAN_END_TIME")
     private String planEndTime;
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
    	//字段描述: 周期内第几次任务
   	 @Column(name = "TASK_NUM_IN_CYCLE")
     private String taskNumInCycle;
    	//字段描述: 看护消缺时间
   	 @Column(name = "KH_QX_TIME")
     private String khQxTime;
    	//字段描述: 外协单位
   	 @Column(name = "WX_ORG")
     private String wxOrg;
    	//字段描述: 物品确认时间
   	 @Column(name = "WPQR_TIME")
     private String wpqrTime;
	{
		this.id = UUID.randomUUID().toString();
	}
	public void setId(String id){
		this.id = id;
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setTourFreq(String tourFreq){
		this.tourFreq = tourFreq;
	}
	@ExcelResources(title="",order=2)
	public String getTourFreq(){
		return this.tourFreq;
	}

	public void setTdywOrg(String tdywOrg){
		this.tdywOrg = tdywOrg;
	}
	@ExcelResources(title="",order=3)
	public String getTdywOrg(){
		return this.tdywOrg;
	}

	public void setTourId(String tourId){
		this.tourId = tourId;
	}
	@ExcelResources(title="",order=4)
	public String getTourId(){
		return this.tourId;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}
	@ExcelResources(title="",order=5)
	public String getUserId(){
		return this.userId;
	}

	public void setCaptain(int captain){
		this.captain = captain;
	}
	@ExcelResources(title="",order=6)
	public int getCaptain(){
		return this.captain;
	}

	public void setGroupFlag(int groupFlag){
		this.groupFlag = groupFlag;
	}
	@ExcelResources(title="",order=7)
	public int getGroupFlag(){
		return this.groupFlag;
	}

	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
	@ExcelResources(title="",order=8)
	public String getTaskName(){
		return this.taskName;
	}

	public void setYhId(String yhId){
		this.yhId = yhId;
	}
	@ExcelResources(title="",order=9)
	public String getYhId(){
		return this.yhId;
	}

	public void setCreateTime(String createTime){
		this.createTime = createTime;
	}
	@ExcelResources(title="",order=10)
	public String getCreateTime(){
		return this.createTime;
	}

	public void setPfTime(String pfTime){
		this.pfTime = pfTime;
	}
	@ExcelResources(title="",order=11)
	public String getPfTime(){
		return this.pfTime;
	}

	public void setStatus(String status){
		this.status = status;
	}
	@ExcelResources(title="",order=12)
	public String getStatus(){
		return this.status;
	}

	public void setPlanStartTime(String planStartTime){
		this.planStartTime = planStartTime;
	}
	@ExcelResources(title="",order=13)
	public String getPlanStartTime(){
		return this.planStartTime;
	}

	public void setPlanEndTime(String planEndTime){
		this.planEndTime = planEndTime;
	}
	@ExcelResources(title="",order=14)
	public String getPlanEndTime(){
		return this.planEndTime;
	}

	public void setRealStartTime(String realStartTime){
		this.realStartTime = realStartTime;
	}
	@ExcelResources(title="",order=15)
	public String getRealStartTime(){
		return this.realStartTime;
	}

	public void setRealEndTime(String realEndTime){
		this.realEndTime = realEndTime;
	}
	@ExcelResources(title="",order=16)
	public String getRealEndTime(){
		return this.realEndTime;
	}

	public void setSfqrTime(String sfqrTime){
		this.sfqrTime = sfqrTime;
	}
	@ExcelResources(title="",order=17)
	public String getSfqrTime(){
		return this.sfqrTime;
	}

	public void setDdxcTime(String ddxcTime){
		this.ddxcTime = ddxcTime;
	}
	@ExcelResources(title="",order=18)
	public String getDdxcTime(){
		return this.ddxcTime;
	}

	public void setTaskNumInCycle(String taskNumInCycle){
		this.taskNumInCycle = taskNumInCycle;
	}
	@ExcelResources(title="",order=19)
	public String getTaskNumInCycle(){
		return this.taskNumInCycle;
	}

	public void setKhQxTime(String khQxTime){
		this.khQxTime = khQxTime;
	}
	@ExcelResources(title="",order=20)
	public String getKhQxTime(){
		return this.khQxTime;
	}

	public void setWxOrg(String wxOrg){
		this.wxOrg = wxOrg;
	}
	@ExcelResources(title="",order=21)
	public String getWxOrg(){
		return this.wxOrg;
	}

	public void setWpqrTime(String wpqrTime){
		this.wpqrTime = wpqrTime;
	}
	@ExcelResources(title="",order=22)
	public String getWpqrTime(){
		return this.wpqrTime;
	}

}