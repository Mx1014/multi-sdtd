/**    
 * 文件名：CMLINESECTIONRepository           
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import com.rzt.entity.CMLINESECTION;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
/**
 * 类名称：CMLINESECTIONRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface CMLINESECTIONRepository extends JpaRepository<CMLINESECTION,String> {

    @Modifying
    @Query(value = "delete from cm_line_tower where LINE_ID= ?1",nativeQuery = true)
    void deleteCmLineTower(Long lineId);

    @Modifying
    @Query(value = "insert into cm_line_tower (id,line_id,tower_id,tower_name,line_name,sort) select seq.nextval,a.* from (select l.id lid,t.id tid,t.name,l.line_name,t.id from cm_tower t,cm_line l where l.id=?1 and l.id=t.line_id order by t.id) a",nativeQuery = true)
    void addCmLineTower(Long lineId);

    @Modifying
    @Query(value = "update CM_line_TOWER set sort=to_number(regexp_substr(TOWER_NAME,'[0-9]*[0-9]',1)) where LINE_ID= ?1",nativeQuery = true)
    void updateTowerSort(Long lineId);

    void deleteById(Long id);
}
