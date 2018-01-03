/**    
 * 文件名：CMLINESECTION           
 * 版本信息：    
 * 日期：2017/12/09 16:45:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
/**      
 * 类名称：CMLINESECTION    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/09 16:45:15 
 * 修改人：张虎成    
 * 修改时间：2017/12/09 16:45:15    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="CM_LINE_SECTION")
public class CMLINESECTION implements Serializable{
	//字段描述: 
   	 @Id
     private Long id;        
    	//字段描述: 线路名称拼音
   	 @Column(name = "LINE_NAME")
     private String lineName;
	//字段描述: 线路名称汉子
	@Column(name = "LINE_NAME1")
	private String lineName1;
    	//字段描述: 线路id
   	 @Column(name = "LINE_ID")
     private Long lineId;
    	//字段描述: 段落
   	 @Column(name = "SECTION")
     private String section;
    	//字段描述: 长度
   	 @Column(name = "LENGTH")
     private String length;
    	//字段描述: 线路地形
   	 @Column(name = "LINE_DX")
     private String lineDx;
    	//字段描述: 电压等级
   	 @Column(name = "V_LEVEL")
     private String vLevel;
    	//字段描述: 线路级别
   	 @Column(name = "LINE_JB")
     private String lineJb;
    	//字段描述: 起始杆塔序号
   	 @Column(name = "START_SORT")
     private Integer startSort;
    	//字段描述: 终止杆塔序号
   	 @Column(name = "END_SORT")
     private Integer endSort;
    	//字段描述: 通道单位id
   	 @Column(name = "TD_ORG")
     private String tdOrg;
    	//字段描述: 本体单位id
   	 @Column(name = "BT_ORG")
     private String btOrg;
    	//字段描述: 外协维护单位id
   	 @Column(name = "WX_ORG")
     private String wxOrg;
    	//字段描述: 是否删除 0未删除 1已删除
   	 @Column(name = "IS_DEL")
     private String isDel;
    	//字段描述: 
   	 @Column(name = "TD_ORG_NAME")
     private String tdOrgName;
   	 @Column(name = "FJ_TOWER")
   	 private Integer fjTower;

	public String getLineName1() {
		return lineName1;
	}

	public void setLineName1(String lineName1) {
		this.lineName1 = lineName1;
	}

	public Integer getFjTower() {
		return fjTower;
	}

	public void setFjTower(Integer fjTower) {
		this.fjTower = fjTower;
	}

	public void setId(Long id){
		if(id==null||id==0){
			this.id = new SnowflakeIdWorker(1,2).nextId();
		}else{
			this.id = id;
		}
	}
    public Long getId(){
		return this.id;
	}
	
	

	public void setLineName(String lineName){
		this.lineName = lineName;
	}
    public String getLineName(){
		return this.lineName;
	}
	
	

	public void setLineId(Long lineId){
		this.lineId = lineId;
	}
    public Long getLineId(){
		return this.lineId;
	}
	
	

	public void setSection(String section){
		this.section = section;
	}
    public String getSection(){
		return this.section;
	}
	
	

	public void setLength(String length){
		this.length = length;
	}
    public String getLength(){
		return this.length;
	}
	
	

	public void setLineDx(String lineDx){
		this.lineDx = lineDx;
	}
    public String getLineDx(){
		return this.lineDx;
	}
	
	

	public void setVLevel(String vLevel){
		this.vLevel = vLevel;
	}
    public String getVLevel(){
		return this.vLevel;
	}
	
	

	public void setLineJb(String lineJb){
		this.lineJb = lineJb;
	}
    public String getLineJb(){
		return this.lineJb;
	}
	
	

	public void setStartSort(Integer startSort){
		this.startSort = startSort;
	}
    public Integer getStartSort(){
		return this.startSort;
	}
	
	

	public void setEndSort(Integer endSort){
		this.endSort = endSort;
	}
    public Integer getEndSort(){
		return this.endSort;
	}
	
	

	public void setTdOrg(String tdOrg){
		this.tdOrg = tdOrg;
	}
    public String getTdOrg(){
		return this.tdOrg;
	}
	
	

	public void setBtOrg(String btOrg){
		this.btOrg = btOrg;
	}
    public String getBtOrg(){
		return this.btOrg;
	}
	
	

	public void setWxOrg(String wxOrg){
		this.wxOrg = wxOrg;
	}
    public String getWxOrg(){
		return this.wxOrg;
	}
	
	

	public void setIsDel(String isDel){
		this.isDel = isDel;
	}
    public String getIsDel(){
		return this.isDel;
	}
	
	

	public void setTdOrgName(String tdOrgName){
		this.tdOrgName = tdOrgName;
	}
    public String getTdOrgName(){
		return this.tdOrgName;
	}
	
	

}
