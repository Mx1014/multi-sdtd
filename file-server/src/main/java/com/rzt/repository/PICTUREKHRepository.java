/**    
 * 文件名：PICTUREKHRepository           
 * 版本信息：    
 * 日期：2017/12/19 15:31:04    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import com.rzt.entity.PICTUREKH;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**      
 * 类名称：PICTUREKHRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/19 15:31:04 
 * 修改人：张虎成    
 * 修改时间：2017/12/19 15:31:04    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface PICTUREKHRepository extends JpaRepository<PICTUREKH,String> {
    List<PICTUREKH> findBytaskId(Long taskId);

    PICTUREKH findById(Long id);

    void deleteById(Long id);
}
