/**    
 * 文件名：CheckLiveTasksbRepository           
 * 版本信息：    
 * 日期：2018/01/21 08:27:36    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.rzt.entity.CheckLiveTasksb;

import java.util.Date;
/**      
 * 类名称：CheckLiveTasksbRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2018/01/21 08:27:36 
 * 修改人：张虎成    
 * 修改时间：2018/01/21 08:27:36    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface CheckLiveTasksbRepository extends JpaRepository<CheckLiveTasksb,String> {

    @Modifying
    @Query(value = "UPDATE check_live_tasksb set status=1 , USER_ID= ?2 ,PLAN_START_TIME=?3,PLAN_END_TIME=?4 where id=?1",nativeQuery = true)
    void updateCheckLiveTasksb(Long id, String userId, Date planStartTime, Date planEndTime);

    @Modifying
    @Query(value = "UPDATE check_live_tasksb set status=? where id=? ",nativeQuery = true)
    void updateCheckLiveTasksbStatus(int i,Long id);

    @Modifying
    @Query(value = "update XS_SB_YH set yhjb=?,yhjb1=?,yhlb=?,yhms=?,yhtdqx=?,YHTDXZJD=?,YHTDC=?,gkcs=?,JSP=?,YHXCYY=?,YHZRDW=?,YHZRDWLXR=?,YHZRDWDH=? where id=?",nativeQuery = true)
    void updateXsSbYh(String yhjb, String yhjb1, String yhlb, String yhms, String yhtdqx, String yhtdxzjd, String yhtdc, String gkcs, String jsp, String yhxcyy, String yhzrdw, String yhzrdwlxr, String yhzrdwdh,Long id);
}
