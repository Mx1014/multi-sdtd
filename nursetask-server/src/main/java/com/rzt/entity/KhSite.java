/**    
 * 文件名：KhSite
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
public class KhSite extends BaseEntity implements Serializable{
	//字段描述: 主任务id
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;
	//字段描述: 电压等级 0 1 2 3
	@Column(name = "VTYPE")
	private String vtype;
	//字段描述: 线路名称
	@Column(name = "LINE_NAME")
	private String lineName;
	//字段描述: 段落
	@Column(name = "SECTION")
	private String section;
	//字段描述: 隐患状态（0未消除 1消除）
	@Column(name = "STATUS")
	private String status;
	//字段描述: 任务名称
	@Column(name = "TASK_NAME")
	private String taskName;
	//字段描述: 看护负责人1
	@Column(name = "KHFZR_ID1")
	private String khfzrId1;
	//字段描述: 通道运维单位
	@Column(name = "TDYW_ORG")
	private String tdywOrg;
	//字段描述: 是否停用（消缺） 0 不停用 1停用
	@Column(name = "IN_USE")
	private String inUse;
	//字段描述: 生成任务总次数
	@Column(name = "TASK_TIMES")
	private String taskTimes;
	//字段描述: 线路id
	@Column(name = "LINE_ID")
	private String lineId;
	//字段描述: 隐患id
	@Column(name = "YH_ID")
	private String yhId;
	//字段描述: 看护范围
	@Column(name = "KH_RANGE")
	private String khRange;
	//字段描述: 看护队员1
	@Column(name = "KHDY_ID1")
	private String khdyId1;
	//字段描述: 看护负责人2
	@Column(name = "KHFZR_ID2")
	private String khfzrId2;
	//字段描述: 看护队员2
	@Column(name = "KHDY_ID2")
	private String khdyId2;
	//字段描述: 任务消缺时间
	@Column(name = "KHXQ_TIME")
	private String khxqTime;
	@Column(name = "CREATE_TIME")
	private Date createTime;

	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="主任务id",order=1)
	public String getId(){
		return this.id;
	}

	public void setVtype(String vtype){
		this.vtype = vtype;
	}
	@ExcelResources(title="电压等级 0 1 2 3",order=2)
	public String getVtype(){
		return this.vtype;
	}

	public void setLineName(String lineName){
		this.lineName = lineName;
	}
	@ExcelResources(title="线路名称",order=3)
	public String getLineName(){
		return this.lineName;
	}

	public void setSection(String section){
		this.section = section;
	}
	@ExcelResources(title="段落",order=4)
	public String getSection(){
		return this.section;
	}

	public void setStatus(String status){
		this.status = status;
	}
	@ExcelResources(title="隐患状态（0未消除 1消除）",order=5)
	public String getStatus(){
		return this.status;
	}

	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
	@ExcelResources(title="任务名称",order=8)
	public String getTaskName(){
		return this.taskName;
	}

	public void setKhfzrId1(String khfzrId1){
		this.khfzrId1 = khfzrId1;
	}
	@ExcelResources(title="看护负责人1",order=9)
	public String getKhfzrId1(){
		return this.khfzrId1;
	}

	public void setTdywOrg(String tdywOrg){
		this.tdywOrg = tdywOrg;
	}
	@ExcelResources(title="通道运维单位",order=10)
	public String getTdywOrg(){
		return this.tdywOrg;
	}

	public void setInUse(String inUse){
		this.inUse = inUse;
	}
	@ExcelResources(title="是否停用（消缺） 0 不停用 1停用",order=11)
	public String getInUse(){
		return this.inUse;
	}

	public void setTaskTimes(String taskTimes){
		this.taskTimes = taskTimes;
	}
	@ExcelResources(title="生成任务总次数",order=12)
	public String getTaskTimes(){
		return this.taskTimes;
	}

	public void setLineId(String lineId){
		this.lineId = lineId;
	}
	@ExcelResources(title="线路id",order=14)
	public String getLineId(){
		return this.lineId;
	}

	public void setYhId(String yhId){
		this.yhId = yhId;
	}
	@ExcelResources(title="隐患id",order=15)
	public String getYhId(){
		return this.yhId;
	}

	public void setKhRange(String khRange){
		this.khRange = khRange;
	}
	@ExcelResources(title="看护范围",order=16)
	public String getKhRange(){
		return this.khRange;
	}

	public void setKhdyId1(String khdyId1){
		this.khdyId1 = khdyId1;
	}
	@ExcelResources(title="看护队员1",order=17)
	public String getKhdyId1(){
		return this.khdyId1;
	}

	public void setKhfzrId2(String khfzrId2){
		this.khfzrId2 = khfzrId2;
	}
	@ExcelResources(title="看护负责人2",order=18)
	public String getKhfzrId2(){
		return this.khfzrId2;
	}

	public void setKhdyId2(String khdyId2){
		this.khdyId2 = khdyId2;
	}
	@ExcelResources(title="看护队员2",order=19)
	public String getKhdyId2(){
		return this.khdyId2;
	}

	public void setKhxqTime(String khxqTime){
		this.khxqTime = khxqTime;
	}
	@ExcelResources(title="任务消缺时间",order=20)
	public String getKhxqTime(){
		return this.khxqTime;
	}
	@ExcelResources(title="任务创建时间",order=21)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}