/**    
 * 文件名：XSZCCYCLELINETOWER           
 * 版本信息：    
 * 日期：2017/12/09 10:31:43    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity.pc;

import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 类名称：XSZCCYCLELINETOWER    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/09 10:31:43 
 * 修改人：张虎成    
 * 修改时间：2017/12/09 10:31:43    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="XS_ZC_CYCLE_LINE_TOWER")
public class XsZcCycleLineTower implements Serializable{
	//字段描述: id
   	 @Id
     private Long id;
    	//字段描述: 正常巡视周期id
   	 @Column(name = "XS_ZC_CYCLE_ID")
     private Long xsZcCycleId;
    	//字段描述: 线路杆塔id
   	 @Column(name = "CM_LINE_TOWER_ID")
     private Long cmLineTowerId;
    
    public void setId(Long id){
        this.id = Long.valueOf(new SnowflakeIdWorker(0,0).nextId());
    }

	@ExcelResources(title="id",order=1)
	public Long getId(){
		return this.id;
	}

	public void setXsZcCycleId(Long xsZcCycleId){
		this.xsZcCycleId = xsZcCycleId;
	}
	@ExcelResources(title="正常巡视周期id",order=2)
	public Long getXsZcCycleId(){
		return this.xsZcCycleId;
	}

	public void setCmLineTowerId(Long cmLineTowerId){
		this.cmLineTowerId = cmLineTowerId;
	}
	@ExcelResources(title="线路杆塔id",order=3)
	public Long getCmLineTowerId(){
		return this.cmLineTowerId;
	}


}