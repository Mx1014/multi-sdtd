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
}
