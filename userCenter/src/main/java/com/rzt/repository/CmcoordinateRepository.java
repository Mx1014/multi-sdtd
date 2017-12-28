/**    
 * 文件名：CMCOORDINATERepository           
 * 版本信息：    
 * 日期：2017/12/20 15:22:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import com.rzt.entity.Cmcoordinate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 类名称：CMCOORDINATERepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/20 15:22:15 
 * 修改人：张虎成    
 * 修改时间：2017/12/20 15:22:15    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface CmcoordinateRepository extends JpaRepository<Cmcoordinate,String> {
}
