/**    
 * 文件名：RztsyscompanyRepository           
 * 版本信息：    
 * 日期：2017/12/08 16:40:23    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rzt.entity.Rztsyscompany;
/**      
 * 类名称：RztsyscompanyRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/08 16:40:23 
 * 修改人：张虎成    
 * 修改时间：2017/12/08 16:40:23    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface RztsyscompanyRepository extends JpaRepository<Rztsyscompany,String> {
}
