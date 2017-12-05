/**    
 * 文件名：CMTOWERTTBJ           
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
 * 类名称：CMTOWERTTBJ    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 18:05:13 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 18:05:13    
 * 修改备注：    
 * @version        
 */
@Entity
public class CMTOWERTTBJ extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 电压等级1
   	 @Column(name = "V_LEVEL_1")
     private String vLevel1;
    	//字段描述: 并架线路1
   	 @Column(name = "LINE1")
     private String line1;
    	//字段描述: 段落1
   	 @Column(name = "SECTION1")
     private String section1;
    	//字段描述: 电压等级2
   	 @Column(name = "V_LEVEL_2")
     private String vLevel2;
    	//字段描述: 并架线路2
   	 @Column(name = "LINE2")
     private String line2;
    	//字段描述: 段落2
   	 @Column(name = "SECTION2")
     private String section2;
    	//字段描述: 电压等级3
   	 @Column(name = "V_LEVEL_3")
     private String vLevel3;
    	//字段描述: 并架线路3
   	 @Column(name = "LINE3")
     private String line3;
    	//字段描述: 段落3
   	 @Column(name = "SECTION3")
     private String section3;
    	//字段描述: 电压等级4
   	 @Column(name = "V_LEVEL_4")
     private String vLevel4;
    	//字段描述: 并架线路4
   	 @Column(name = "LINE4")
     private String line4;
    	//字段描述: 段落4
   	 @Column(name = "SECTION4")
     private String section4;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setVLevel1(String vLevel1){
		this.vLevel1 = vLevel1;
	}
	@ExcelResources(title="电压等级1",order=2)
	public String getVLevel1(){
		return this.vLevel1;
	}

	public void setLine1(String line1){
		this.line1 = line1;
	}
	@ExcelResources(title="并架线路1",order=3)
	public String getLine1(){
		return this.line1;
	}

	public void setSection1(String section1){
		this.section1 = section1;
	}
	@ExcelResources(title="段落1",order=4)
	public String getSection1(){
		return this.section1;
	}

	public void setVLevel2(String vLevel2){
		this.vLevel2 = vLevel2;
	}
	@ExcelResources(title="电压等级2",order=5)
	public String getVLevel2(){
		return this.vLevel2;
	}

	public void setLine2(String line2){
		this.line2 = line2;
	}
	@ExcelResources(title="并架线路2",order=6)
	public String getLine2(){
		return this.line2;
	}

	public void setSection2(String section2){
		this.section2 = section2;
	}
	@ExcelResources(title="段落2",order=7)
	public String getSection2(){
		return this.section2;
	}

	public void setVLevel3(String vLevel3){
		this.vLevel3 = vLevel3;
	}
	@ExcelResources(title="电压等级3",order=8)
	public String getVLevel3(){
		return this.vLevel3;
	}

	public void setLine3(String line3){
		this.line3 = line3;
	}
	@ExcelResources(title="并架线路3",order=9)
	public String getLine3(){
		return this.line3;
	}

	public void setSection3(String section3){
		this.section3 = section3;
	}
	@ExcelResources(title="段落3",order=10)
	public String getSection3(){
		return this.section3;
	}

	public void setVLevel4(String vLevel4){
		this.vLevel4 = vLevel4;
	}
	@ExcelResources(title="电压等级4",order=11)
	public String getVLevel4(){
		return this.vLevel4;
	}

	public void setLine4(String line4){
		this.line4 = line4;
	}
	@ExcelResources(title="并架线路4",order=12)
	public String getLine4(){
		return this.line4;
	}

	public void setSection4(String section4){
		this.section4 = section4;
	}
	@ExcelResources(title="段落4",order=13)
	public String getSection4(){
		return this.section4;
	}

}