/**    
 * 文件名：CMLINETOWERRepository           
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.rzt.entity.CMLINETOWER;
/**      
 * 类名称：CMLINETOWERRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface CMLINETOWERRepository extends JpaRepository<CMLINETOWER,String> {

     @Modifying
     @Query(value = "update cm_tower set LONGITUDE=?2 , LATITUDE = ?3 where id=?1",nativeQuery = true)
    void updatetowerPosition(Long id, String lon, String lat);
}
