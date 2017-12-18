/**    
 * 文件名：KHTASKWPQRRepository           
 * 版本信息：    
 * 日期：2017/12/17 16:53:35    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import com.rzt.entity.KhTaskWpqr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rzt.entity.KhTaskWpqr;
/**      
 * 类名称：KHTASKWPQRRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/17 16:53:35 
 * 修改人：张虎成    
 * 修改时间：2017/12/17 16:53:35    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface KhTaskWpqrRepository extends JpaRepository<KhTaskWpqr,String> {
}
