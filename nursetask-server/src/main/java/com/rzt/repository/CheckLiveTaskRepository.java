/**    
 * 文件名：CHECKLIVETASKRepository           
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import com.rzt.entity.CheckLiveTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 类名称：CHECKLIVETASKRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/04 15:13:15 
 * 修改人：张虎成    
 * 修改时间：2017/12/04 15:13:15    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface CheckLiveTaskRepository extends JpaRepository<CheckLiveTask,String> {
  @Query(value ="SELECT * FROM Check_Live_Task  where id=?1",nativeQuery = true)
  CheckLiveTask findLiveTask(long id);

 @Modifying
 @Query(value ="update check_live_task set status=?1,CYCLE_ID=?2 where id=?3",nativeQuery = true)
 void updateLiveTask(String status, Long cycleId, long id);

 @Modifying
 @Query(value ="DELETE FROM check_live_task  where id=?1",nativeQuery = true)
 void deleteById(long id);

 @Modifying
 @Query(value = "update check_live_task set WPTS=?2 where id =?1",nativeQuery = true)
 void updateWptsById(Long id, String str);

    CheckLiveTask findById(Long id);

    @Modifying
    @Query(value = "insert into CHECK_LIVE_SITE (id,TASK_ID,TASK_TYPE,CREATE_TIME,TASK_NAME,STATUS,line_id,TDYW_ORGID,TDWX_ORGID,yh_id) " +
            " select id,id as taskid,0,sysdate,TASK_NAME,0,LINE_ID,TDYW_ORGID,WX_ORGID,YH_ID from KH_CYCLE",nativeQuery = true)
    void generalKhSite();
}
