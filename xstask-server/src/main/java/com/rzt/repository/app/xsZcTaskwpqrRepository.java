package com.rzt.repository.app;

import com.rzt.entity.appentity.xsZcTaskwpqr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.repository.app
 * @Author: liuze
 * @date: 2017-12-7 19:42
 */
@Repository
public interface xsZcTaskwpqrRepository extends JpaRepository<xsZcTaskwpqr, String> {
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

    /**
     * 巡视开始 每轮巡视表
     *
     * @param id
     * @param taskId 任务ID
     * @param num    次数
     * @param status 状态本轮是否完成
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "insert into xs_zc_task_exec " +
            "  (id, xs_zc_task_id, xs_start_time,  xs_repeat_num, xs_status) " +
            "values " +
            "  (?1, ?2,sysdate, ?3, ?4,?5)", nativeQuery = true)
    int zcXsPatrollingTowerStart(String id, String taskId, int num, int status);

    @Modifying
    @Transactional
    @Query(value = "update xs_zc_task" +
            "   set real_xs_num=?1" +
            " where id = ?2", nativeQuery = true)
    int zcXsPatrollingTowerXsCs(int num, String id);

    /**
     * 正常巡视物品提醒添加
     *
     * @param id     ID
     * @param taskId 任务ID
     * @param wpZt   物品状态
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO XS_ZC_TASKWPQR(ID,TASKID,WP_ZT) VALUES (?1,?2,?3)", nativeQuery = true)
    int zcXsArticlesInsert(String id, String taskId, String wpZt);

    /**
     * 正常巡视如果有这条修改这条物品修改
     *
     * @param rwZt 物品状态
     * @param id   ID
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE XS_ZC_TASKWPQR SET WP_ZT =?1 WHERE ID =?2", nativeQuery = true)
    int zcXsArticlesUpdate(String rwZt, String id);

    /**
     * 保电特寻巡视物品提醒添加
     *
     * @param id     ID
     * @param taskId 任务ID
     * @param wpZt   物品状态
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO XS_TXBD_TASKWPQR(ID,TASKID,WP_ZT) VALUES (?1,?2,?3)", nativeQuery = true)
    int bdXsArticlesInsert(String id, String taskId, String wpZt);

    /**
     * 保电特寻如果有这条修改这条物品修改
     *
     * @param rwZt 物品状态
     * @param id   ID
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE XS_TXBD_TASKWPQR SET WP_ZT =?1 WHERE ID =?2", nativeQuery = true)
    int bdXsArticlesUpdate(String rwZt, String id);
}
