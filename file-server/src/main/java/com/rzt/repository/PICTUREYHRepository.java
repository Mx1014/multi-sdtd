/**    
 * 文件名：PICTUREYHRepository           
 * 版本信息：    
 * 日期：2017/12/19 15:31:04    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import com.rzt.entity.PICTUREYH;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**      
 * 类名称：PICTUREYHRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/19 15:31:04 
 * 修改人：张虎成    
 * 修改时间：2017/12/19 15:31:04    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface PICTUREYHRepository extends JpaRepository<PICTUREYH,String> {
    List<PICTUREYH> findBytaskId(Long taskId);

    PICTUREYH findById(Long id);

    void deleteById(Long id);
}
