/**
 * 文件名：MONITORCHECKEJRepository
 * 版本信息：
 * 日期：2018/01/08 11:06:23
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository;
import com.rzt.entity.Monitorcheckej;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * 类名称：MONITORCHECKEJRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2018/01/08 11:06:23 
 * 修改人：张虎成    
 * 修改时间：2018/01/08 11:06:23    
 * 修改备注：    
 * @version
 */
 @Repository
public interface Monitorcheckejrepository extends JpaRepository<Monitorcheckej,String> {
}
