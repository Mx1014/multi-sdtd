/**    
 * 文件名：CMTOWERTTBJ           
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
 * 类名称：CMTOWERTTBJ    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/09 16:45:15 
 * 修改人：张虎成    
 * 修改时间：2017/12/09 16:45:15    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="CM_TOWER_TTBJ")
public class CMTOWERTTBJ implements Serializable{
	//字段描述: 
   	 @Id
     private Long id;        
    	//字段描述: 电压等级1
   	 @Column(name = "V_LEVEL_1")
     private String vLevel1;
    	//字段描述: 并架线路1
   	 @Column(name = "LINE1_ID")
     private String line1Id;
    	//字段描述: 段落1
   	 @Column(name = "SECTION1")
     private String section1;
    	//字段描述: 电压等级2
   	 @Column(name = "V_LEVEL_2")
     private String vLevel2;
    	//字段描述: 并架线路2
   	 @Column(name = "LINE2_ID")
     private String line2Id;
    	//字段描述: 段落2
   	 @Column(name = "SECTION2")
     private String section2;
    	//字段描述: 电压等级3
   	 @Column(name = "V_LEVEL_3")
     private String vLevel3;
    	//字段描述: 并架线路3
   	 @Column(name = "LINE3_ID")
     private String line3Id;
    	//字段描述: 段落3
   	 @Column(name = "SECTION3")
     private String section3;
    	//字段描述: 电压等级4
   	 @Column(name = "V_LEVEL_4")
     private String vLevel4;
    	//字段描述: 并架线路4
   	 @Column(name = "LINE4_ID")
     private String line4Id;
    	//字段描述: 段落4
   	 @Column(name = "SECTION4")
     private String section4;
    
	public void setId(Long id){
		if(id==null||id==0){
			this.id = new SnowflakeIdWorker(1,5).nextId();
		}else{
			this.id = id;
		}
	}
    public Long getId(){
		return this.id;
	}
	
	

	public void setVLevel1(String vLevel1){
		this.vLevel1 = vLevel1;
	}
    public String getVLevel1(){
		return this.vLevel1;
	}
	
	

	public void setLine1Id(String line1Id){
		this.line1Id = line1Id;
	}
    public String getLine1Id(){
		return this.line1Id;
	}
	
	

	public void setSection1(String section1){
		this.section1 = section1;
	}
    public String getSection1(){
		return this.section1;
	}
	
	

	public void setVLevel2(String vLevel2){
		this.vLevel2 = vLevel2;
	}
    public String getVLevel2(){
		return this.vLevel2;
	}
	
	

	public void setLine2Id(String line2Id){
		this.line2Id = line2Id;
	}
    public String getLine2Id(){
		return this.line2Id;
	}
	
	

	public void setSection2(String section2){
		this.section2 = section2;
	}
    public String getSection2(){
		return this.section2;
	}
	
	

	public void setVLevel3(String vLevel3){
		this.vLevel3 = vLevel3;
	}
    public String getVLevel3(){
		return this.vLevel3;
	}
	
	

	public void setLine3Id(String line3Id){
		this.line3Id = line3Id;
	}
    public String getLine3Id(){
		return this.line3Id;
	}
	
	

	public void setSection3(String section3){
		this.section3 = section3;
	}
    public String getSection3(){
		return this.section3;
	}
	
	

	public void setVLevel4(String vLevel4){
		this.vLevel4 = vLevel4;
	}
    public String getVLevel4(){
		return this.vLevel4;
	}
	
	

	public void setLine4Id(String line4Id){
		this.line4Id = line4Id;
	}
    public String getLine4Id(){
		return this.line4Id;
	}
	
	

	public void setSection4(String section4){
		this.section4 = section4;
	}
    public String getSection4(){
		return this.section4;
	}
	
	

}
