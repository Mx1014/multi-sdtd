/**    
 * 文件名：XsZcCycleRepository           
 * 版本信息：    
 * 日期：2017/12/07 07:50:10    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository.pc;

import com.rzt.entity.pc.XsZcCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 类名称：XsZcCycleRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 07:50:10 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 07:50:10    
 * 修改备注：    
 * @version        
 */
@Repository
public interface XsZcCycleRepository extends JpaRepository<XsZcCycle,String> {
    @Modifying
    @Transactional
    @Query(value = "update xs_zc_cycle set is_delte = 0 where id= in (?1)", nativeQuery = true)
    Object logicalDelete(List<Long> longs);
}