/**    
 * 文件名：CmFileRepository           
 * 版本信息：    
 * 日期：2017/12/08 11:06:32    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import com.rzt.entity.CmFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名称：CmFileRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/08 11:06:32 
 * 修改人：张虎成    
 * 修改时间：2017/12/08 11:06:32    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface CmFileRepository extends JpaRepository<CmFile,String> {
    List<CmFile> findByFkId(Long fkid);

    CmFile findById(Long id);

    void deleteById(Long id);

    List<CmFile> findByFkIdStr(String fkidStr);
}
