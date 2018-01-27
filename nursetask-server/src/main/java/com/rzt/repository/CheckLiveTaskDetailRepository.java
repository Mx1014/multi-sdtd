package com.rzt.repository;

import com.rzt.entity.CheckLiveTaskDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by admin on 2017/12/5.
 */
@Repository
public interface CheckLiveTaskDetailRepository extends JpaRepository<CheckLiveTaskDetail,String> {
    @Modifying
    @Query(value = "update check_live_taskxs set WPTS=?2,status=1 where id =?1",nativeQuery = true)
    void updateWptsById(Long id, String str);

    CheckLiveTaskDetail findById(Long id);

    @Modifying
    @Query(value = "UPDATE check_live_task_detail SET sfzg=?2,  ryyz=?3, sjt=?4, dzwl=?5,status=1 where status!=2 AND id= ?1",nativeQuery = true)
    void checkDgdwUpdate(Long id, String sfzg, String ryyz,String sjt, String dzwl);
    @Modifying
    @Query(value = "UPDATE check_live_task_detailxs SET sfzg=?2,  ryyz=?3,  dzwl=?4,status=1 where status!=2 AND id= ?1",nativeQuery = true)
    void checkDgdwUpdateXs(Long id, String sfzg, String ryyz, String dzwl);

    @Modifying
    @Query(value = "UPDATE KH_YH_HISTORY SET jd=?2,wd=?3,RADIUS=?4 where id=?1",nativeQuery = true)
    void updateDzwl(String yhId, String lon, String lat, String radius);

    @Modifying
    @Query(value = "UPDATE check_live_task_detail SET dydj=?2,  yhxx=?3,  czfa=?4 ,qtwt=?5 , dxjx=?6,status=2 where status!=2 AND id= ?1",nativeQuery = true)
    void checkQuestionUpdate(String detailId, String dydj, String yhxx, String czfa, String qtwt,String dxjx);
    @Modifying
    @Query(value = "UPDATE check_live_task_detailxs SET  dydj=?2,  yhxx=?3,  czfa=?4 ,qtwt=?5,status=2 where status!=2 AND id= ?1",nativeQuery = true)
    void checkQuestionUpdateXs(String detailId, String dydj, String yhxx, String czfa, String qtwt);


}
