/**
 * 文件名：XSZCTASKRepository
 * 版本信息：
 * 日期：2017/12/05 10:02:41
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository.apprepository;

import com.rzt.entity.appentity.XSZCTASK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    /**
     * 实际开始时间 ,巡视开始时间 ,身份确认时间 更改时间 正常巡视
     *
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update xs_zc_task set real_start_time = sysdate,xsks_time = sysdate,sfqr_time = sysdate where id=?1", nativeQuery = true)
    int zxXsSfqrTime(String id);

    /**
     * 实际开始时间 ,巡视开始时间 ,身份确认时间 更改时间 保电巡视
     *
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update xs_txbd_task set xsks_time = sysdate,real_start_time = sysdate,sfqr_time  =sysdate where id=?1", nativeQuery = true)
    int bdXsSfqrTime(String id);

    /**
     * 保电巡视 到达现场时间修改
     *
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update xs_txbd_task set ddxc_time = sysdate where id =?1", nativeQuery = true)
    int bdXsDdxcTime(String id);

    /**
     * 正常巡视 到达现场时间更改
     *
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update xs_zc_task set ddxc_time  = sysdate where id =?1", nativeQuery = true)
    int zcXsDdxcTime(String id);

    @Modifying
    @Transactional
    @Query(value = "insert into xs_zc_task_exec " +
            "  (id, xs_zc_task_id, xs_start_time,  xs_repeat_num, xs_status) " +
            "values " +
            "  (?1, sysdate, v_xs_end_time, ?2, ?3)", nativeQuery = true)
    int zcXsPatrollingTowerStart();
}
