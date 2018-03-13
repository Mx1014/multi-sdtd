/**    
 * 文件名：GUZHANGRepository           
 * 版本信息：    
 * 日期：2017/12/13 14:37:30    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.rzt.entity.GUZHANG;
/**      
 * 类名称：GUZHANGRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/13 14:37:30 
 * 修改人：张虎成    
 * 修改时间：2017/12/13 14:37:30    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface GUZHANGRepository extends JpaRepository<GUZHANG,String> {
     @Modifying
     @Query(value ="update index_pm set score=? where td_org_name like '%'||?||'%'" ,nativeQuery = true)
    void updatePm(String s, String s1);
}
