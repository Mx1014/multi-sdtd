/**    
 * 文件名：KhYhTowerRepository           
 * 版本信息：    
 * 日期：2018/03/14 02:22:31    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.rzt.entity.KhYhTower;
import org.springframework.transaction.annotation.Transactional;

/**
 * 类名称：KhYhTowerRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2018/03/14 02:22:31 
 * 修改人：张虎成    
 * 修改时间：2018/03/14 02:22:31    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface KhYhTowerRepository extends JpaRepository<KhYhTower,String> {
    @Modifying
    @Transactional
    @Query(value = "DELETE  FROM KH_YH_TOWER WHERE yh_id=?1", nativeQuery = true)
    void deleteByYhId(Long id);

    @Modifying
    @Transactional
    @Query(value = "insert into KH_YH_TOWER(id,YH_ID,TOWER_ID,RADIUS) VALUES (?1,?2,?3,?4)", nativeQuery = true)
    void insertKhTower(Long id, Long id1, long l, int i);
}
