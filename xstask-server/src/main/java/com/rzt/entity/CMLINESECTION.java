/**    
 * 文件名：CMLINESECTION           
 * 版本信息：    
 * 日期：2017/11/28 18:05:13    
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
import java.util.UUID;

/**
 * 类名称：CMLINESECTION    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 18:05:13 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 18:05:13    
 * 修改备注：    
 * @version        
 */
@Entity
public class CMLINESECTION extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 线路名称
   	 @Column(name = "LINE_NAME")
     private String lineName;
    	//字段描述: 线路id
   	 @Column(name = "LINE_ID")
     private String lineId;
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
     private String startSort;
    	//字段描述: 终止杆塔序号
   	 @Column(name = "END_SORT")
     private String endSort;
    	//字段描述: 通道单位id
   	 @Column(name = "TD_ORG")
     private String tdOrg;
    	//字段描述: 本体单位id
   	 @Column(name = "BT_ORG")
     private String btOrg;
    	//字段描述: 外协维护单位id
   	 @Column(name = "WX_ORG")
     private String wxOrg;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setLineName(String lineName){
		this.lineName = lineName;
	}
	@ExcelResources(title="线路名称",order=2)
	public String getLineName(){
		return this.lineName;
	}

	public void setLineId(String lineId){
		this.lineId = lineId;
	}
	@ExcelResources(title="线路id",order=3)
	public String getLineId(){
		return this.lineId;
	}

	public void setSection(String section){
		this.section = section;
	}
	@ExcelResources(title="段落",order=4)
	public String getSection(){
		return this.section;
	}

	public void setLength(String length){
		this.length = length;
	}
	@ExcelResources(title="长度",order=5)
	public String getLength(){
		return this.length;
	}

	public void setLineDx(String lineDx){
		this.lineDx = lineDx;
	}
	@ExcelResources(title="线路地形",order=6)
	public String getLineDx(){
		return this.lineDx;
	}

	public void setVLevel(String vLevel){
		this.vLevel = vLevel;
	}
	@ExcelResources(title="电压等级",order=7)
	public String getVLevel(){
		return this.vLevel;
	}

	public void setLineJb(String lineJb){
		this.lineJb = lineJb;
	}
	@ExcelResources(title="线路级别",order=8)
	public String getLineJb(){
		return this.lineJb;
	}

	public void setStartSort(String startSort){
		this.startSort = startSort;
	}
	@ExcelResources(title="起始杆塔序号",order=9)
	public String getStartSort(){
		return this.startSort;
	}

	public void setEndSort(String endSort){
		this.endSort = endSort;
	}
	@ExcelResources(title="终止杆塔序号",order=10)
	public String getEndSort(){
		return this.endSort;
	}

	public void setTdOrg(String tdOrg){
		this.tdOrg = tdOrg;
	}
	@ExcelResources(title="通道单位id",order=11)
	public String getTdOrg(){
		return this.tdOrg;
	}

	public void setBtOrg(String btOrg){
		this.btOrg = btOrg;
	}
	@ExcelResources(title="本体单位id",order=12)
	public String getBtOrg(){
		return this.btOrg;
	}

	public void setWxOrg(String wxOrg){
		this.wxOrg = wxOrg;
	}
	@ExcelResources(title="外协维护单位id",order=13)
	public String getWxOrg(){
		return this.wxOrg;
	}

}