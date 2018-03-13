/**
 * 文件名：XSZCTASKRepository
 * 版本信息：
 * 日期：2017/12/05 10:02:41
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository.app;

import com.rzt.entity.app.XSZCTASK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * 类名称：XSZCTASKRepository
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/05 10:02:41
 * 修改人：张虎成
 * 修改时间：2017/12/05 10:02:41
 * 修改备注：
 */
@Repository
public interface XSZCTASKRepository extends JpaRepository<XSZCTASK, String> {
    @Modifying
    @Transactional
    @Query(value = "update xs_zc_cycle set TOTAL_TASK_NUM = ?2 where id = ?1", nativeQuery = true)
    void updateCycleTotalBornNum(Long id, Integer total_task_num);

    @Modifying
    @Transactional
    @Query(value = "update xs_zc_task set reborn = 1 where id = ?1", nativeQuery = true)
    void updateTaskReborn(Long taskid);

    @Modifying
    @Transactional
    @Query(value = "update xs_zc_cycle set td_org = ?2,wx_org = ?3,group_id = ?4,class_id = ?5 where id = ?1", nativeQuery = true)
    void updateCycle(Object id, Object deptid, Object companyid, Object groupid, Object classname);

    @Modifying
    @Transactional
    @Query(value = "update xs_zc_task set td_org = ?2,wx_org = ?3,group_id = ?4,class_id = ?5 where id = ?1", nativeQuery = true)
    void updateTask(Object id, Object deptid, Object companyid, Object groupid, Object classname);

    @Modifying
    @Transactional
    @Query(value = "insert into QUEXIAN (id,USER_ID,DEPT_ID,TOWER_ID,LINE_ID,TASK_ID, PROCESS_ID,CREATE_TIME,QX_MS,QX_POSITION,QX_TYPE) VALUES (?,?,?,?,?,?,?,?,?,?,?)", nativeQuery = true)
    void insertGuZhang(long qxId, String cm_user_id, String td_org,Long towerID,Long lineId,Long taskId, Long processId, Date date, String qxMs, Integer qxPosition, Integer qxType);

    @Modifying
    @Transactional
    @Query(value = "update PICTURE_QX set QX_ID = ?2 where id in (?1)", nativeQuery = true)
    void updateQxId(List<Long> picIdList, long qxId);

    @Modifying
    @Transactional
    @Query(value = "update xs_zc_task set STAUTS = 2,REAL_END_TIME=sysdate where id = ?1", nativeQuery = true)
    void updateTasks(Long id);
}
