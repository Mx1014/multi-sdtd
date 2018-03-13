/**    
 * 文件名：GUZHANG           
 * 版本信息：    
 * 日期：2017/12/13 14:37:30    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 类名称：GUZHANG    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/13 14:37:30 
 * 修改人：张虎成    
 * 修改时间：2017/12/13 14:37:30    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="GUZHANG")
public class GUZHANG implements Serializable{
	//字段描述: 追损金额
   	 @Column(name = "ZS_MONEY")
     private Integer zsMoney;
    	//字段描述: 是否追损
   	 @Column(name = "IS_ZS")
     private String isZs;
    	//字段描述: 施工性质
   	 @Column(name = "SGXZ")
     private String sgxz;
    	//字段描述: 异物材质
   	 @Column(name = "MATTER")
     private String matter;
    	//字段描述: 雷击双跳
   	 @Column(name = "THUNDER")
     private String thunder;
    	//字段描述: 巡视是否超期
   	 @Column(name = "XS_IS_LATE")
     private String xsIsLate;
    	//字段描述: PMS查询情况
   	 @Column(name = "PMS")
     private String pms;
    	//字段描述: 报告报送质量
   	 @Column(name = "REPORT_ZL")
     private String reportZl;
    	//字段描述: 报告报送时间（以OA为准）
   	 @Column(name = "REPORT_TIME")
     private String reportTime;
    	//字段描述: 快报报送时间（以OA为准）
   	 @Column(name = "QUICK_TIME")
     private String quickTime;
    	//字段描述: 故障结果短信（电话）报送时间
   	 @Column(name = "RESULT_TIME")
     private String resultTime;
    	//字段描述: 跳闸短信（电话）报送时间
   	 @Column(name = "MSG_TIME")
     private String msgTime;
    	//字段描述: 故障杆塔号
   	 @Column(name = "GZ_TOWER")
     private String gzTower;
    	//字段描述: 测距指示杆塔号
   	 @Column(name = "RANGE_TOWER")
     private String rangeTower;
    	//字段描述: 可靠性
   	 @Column(name = "KKX")
     private Integer kkx;
    	//字段描述: 备注2
   	 @Column(name = "REMARK2")
     private String remark2;
    	//字段描述: 备注1
   	 @Column(name = "REMARK1")
     private String remark1;
    	//字段描述: 是否重合出
   	 @Column(name = "IS_REOUT")
     private String isReout;
    	//字段描述: 跳闸及非计停简况
   	 @Column(name = "DESCRIPTION")
     private String description;
    	//字段描述: 故障原因小类
   	 @Column(name = "GZ_REASON1")
     private String gzReason1;
    	//字段描述: 故障原因
   	 @Column(name = "GZ_REASON")
     private String gzReason;
    	//字段描述: 通道运维单位uuid
   	 @Column(name = "TD_ORG")
     private String tdOrg;
    	//字段描述: 设备运维单位
   	 @Column(name = "SB_ORG")
     private String sbOrg;
    	//字段描述: 故障线路
   	 @Column(name = "LINE_NAME")
     private String lineName;
    	//字段描述: 电压等级
   	 @Column(name = "V_LEVEL")
     private String vLevel;
    	//字段描述: 故障时间
   	 @Column(name = "CREATE_TIME")
     private String createTime;
    	//字段描述: 故障日期
   	 @Column(name = "CREATE_DATA")
     private Date createData;
    	//字段描述: 月份
   	 @Column(name = "MONTH")
     private String month;
    	//字段描述: 序号
   	 @Id
     private Long id;
   	 @Column(name = "td_org_id")
   	 private String tdOrgId;
   	 @Column(name="line_id")
   	 private Long lineId;

	public void setVLevel(String vLevel){
		this.vLevel = vLevel;
	}
	public String getVLevel(){
		return this.vLevel;
	}

	public Long getLineId() {
		return lineId;
	}

	public void setLineId(Long lineId) {
		this.lineId = lineId;
	}

	public String getTdOrgId() {
		return tdOrgId;
	}

	public void setTdOrgId(String tdOrgId) {
		this.tdOrgId = tdOrgId;
	}

	public void setZsMoney(Integer zsMoney){
		this.zsMoney = zsMoney;
	}
    public Integer getZsMoney(){
		return this.zsMoney;
	}
	
	

	public void setIsZs(String isZs){
		this.isZs = isZs;
	}
    public String getIsZs(){
		return this.isZs;
	}
	
	

	public void setSgxz(String sgxz){
		this.sgxz = sgxz;
	}
    public String getSgxz(){
		return this.sgxz;
	}
	
	

	public void setMatter(String matter){
		this.matter = matter;
	}
    public String getMatter(){
		return this.matter;
	}
	
	

	public void setThunder(String thunder){
		this.thunder = thunder;
	}
    public String getThunder(){
		return this.thunder;
	}
	
	

	public void setXsIsLate(String xsIsLate){
		this.xsIsLate = xsIsLate;
	}
    public String getXsIsLate(){
		return this.xsIsLate;
	}
	
	

	public void setPms(String pms){
		this.pms = pms;
	}
    public String getPms(){
		return this.pms;
	}
	
	

	public void setReportZl(String reportZl){
		this.reportZl = reportZl;
	}
    public String getReportZl(){
		return this.reportZl;
	}
	
	

	public void setReportTime(String reportTime){
		this.reportTime = reportTime;
	}
    public String getReportTime(){
		return this.reportTime;
	}
	
	

	public void setQuickTime(String quickTime){
		this.quickTime = quickTime;
	}
    public String getQuickTime(){
		return this.quickTime;
	}
	
	

	public void setResultTime(String resultTime){
		this.resultTime = resultTime;
	}
    public String getResultTime(){
		return this.resultTime;
	}
	
	

	public void setMsgTime(String msgTime){
		this.msgTime = msgTime;
	}
    public String getMsgTime(){
		return this.msgTime;
	}
	
	

	public void setGzTower(String gzTower){
		this.gzTower = gzTower;
	}
    public String getGzTower(){
		return this.gzTower;
	}
	
	

	public void setRangeTower(String rangeTower){
		this.rangeTower = rangeTower;
	}
    public String getRangeTower(){
		return this.rangeTower;
	}
	
	

	public void setKkx(Integer kkx){
		this.kkx = kkx;
	}
    public Integer getKkx(){
		return this.kkx;
	}
	
	

	public void setRemark2(String remark2){
		this.remark2 = remark2;
	}
    public String getRemark2(){
		return this.remark2;
	}
	
	

	public void setRemark1(String remark1){
		this.remark1 = remark1;
	}
    public String getRemark1(){
		return this.remark1;
	}
	
	

	public void setIsReout(String isReout){
		this.isReout = isReout;
	}
    public String getIsReout(){
		return this.isReout;
	}
	
	

	public void setDescription(String description){
		this.description = description;
	}
    public String getDescription(){
		return this.description;
	}
	
	

	public void setGzReason1(String gzReason1){
		this.gzReason1 = gzReason1;
	}
    public String getGzReason1(){
		return this.gzReason1;
	}
	
	

	public void setGzReason(String gzReason){
		this.gzReason = gzReason;
	}
    public String getGzReason(){
		return this.gzReason;
	}
	
	

	public void setTdOrg(String tdOrg){
		this.tdOrg = tdOrg;
	}
    public String getTdOrg(){
		return this.tdOrg;
	}
	
	

	public void setSbOrg(String sbOrg){
		this.sbOrg = sbOrg;
	}
    public String getSbOrg(){
		return this.sbOrg;
	}
	
	

	public void setLineName(String lineName){
		this.lineName = lineName;
	}
    public String getLineName(){
		return this.lineName;
	}
	
	

	public void setCreateTime(String createTime){
		this.createTime = createTime;
	}
    public String getCreateTime(){
		return this.createTime;
	}
	
	

	public void setCreateData(Date createData){
		this.createData = createData;
	}
    public Date getCreateData(){
		return this.createData;
	}
	
	

	public void setMonth(String month){
		this.month = month;
	}
    public String getMonth(){
		return this.month;
	}



	public void setId(Long id){
		if(id==null||id==0){
			this.id = SnowflakeIdWorker.getInstance(9,8).nextId();
		}else{
			this.id = id;
		}
	}
    public Long getId(){
		return this.id;
	}
	
	

}
