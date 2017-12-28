/**
 * 文件名：KHCYCLERepository
 * 版本信息：
 * 日期：2017/12/25 21:46:38
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository;
import com.rzt.entity.KhCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
/**
 * 类名称：KHCYCLERepository
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/25 21:46:38
 * 修改人：张虎成
 * 修改时间：2017/12/25 21:46:38
 * 修改备注：
 * @version
 */
@Repository
public interface KhCycleRepository extends JpaRepository<KhCycle,String> {
    @Query(value ="SELECT * FROM KH_CYCLE  where id=?1",nativeQuery = true)
        //SELECT id,VTYPE,LINE_NAME,SECTION,status,line_id,KH_RANGE,to_date(KHXQ_TIME,'yyyy-mm-dd hh24:mi:ss'),to_date(create_time,'yyyy-mm-dd hh24:mi:ss'),KHFZR_ID1,KHFZR_ID2,KHDY_ID1,KHDY_ID2,TDYW_ORG,YH_ID,TASK_NAME,COUNT FROM KH_SITE  where id=?1
    KhCycle findSite(long id);
}
