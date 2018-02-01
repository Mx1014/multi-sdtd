/**    
 * 文件名：CMLINERepository           
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;

import com.rzt.entity.CMSETTING;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 类名称：CMLINERepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface CMSETTINGRepository extends JpaRepository<CMSETTING,String> {
    @Query(value = "update CM_LINE_SECTION set line_name=?2 where id=?1 ",nativeQuery = true)
    @Modifying
    @Transactional
    void updateLineName(Long id, String linename);

    @Modifying
    @Query(value = "update cm_line set SECTION=?2 where id=?1 ",nativeQuery = true)
    void updateLineSection(Long lineId, String section);
}
