package com.rzt.repository.app;

import com.rzt.entity.app.XsZcTaskwpqr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.repository.app
 * @Author: liuze
 * @date: 2017-12-7 19:42
 */
@Repository
public interface XsZcTaskwpqrRepository extends JpaRepository<XsZcTaskwpqr, String> {

    /**
     * 实际开始时间 (即接单时间)更改时间 正常巡视
     *
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update xs_zc_task set real_start_time = sysdate where id=?1", nativeQuery = true)
    int zcXsJiedan(Long id);

    /**
     * 实际开始时间 (即接单时间)更改时间 保电巡视
     *
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update xs_txbd_task set real_start_time  =sysdate where id=?1", nativeQuery = true)
    int bdtxJiedan(Long id);


    /**
     * 物品提醒时间 更改时间 正常巡视
     *
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update xs_zc_task set wptx_time = sysdate where id=?1", nativeQuery = true)
    int zcxsWptsTimeUpdate(Long id);

    /**
     * 物品提醒时间  更改时间 保电巡视
     *
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update xs_txbd_task set wptx_time  =sysdate where id=?1", nativeQuery = true)
    int bdxsWptsTimeUpdate(Long id);


    /**
     * 实际开始时间 (即接单时间)更改时间 保电巡视
     *
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update xs_zc_task set stauts = 1 where id=?1", nativeQuery = true)
    void zcXsUpdateTaskStatus(Long id);

    /**
     * 实际开始时间 (即接单时间)更改时间 保电巡视
     *
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update xs_txbd_task set stauts = 1 where id=?1", nativeQuery = true)
    void bdtxUpdateTaskStatus(Long id);





    
    /**
     * 身份确认时间 更改时间 正常巡视
     *
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update xs_zc_task set sfqr_time = sysdate where id=?1", nativeQuery = true)
    int zxXsSfqrTime(Long id);

    /**
     * 身份确认时间 更改时间 保电巡视
     *
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update xs_txbd_task set sfqr_time  =sysdate where id=?1", nativeQuery = true)
    int bdXsSfqrTime(Long id);

    /**
     * 保电巡视 到达现场时间修改
     *
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update xs_txbd_task set ddxc_time = sysdate,zxys_num = ?2,xscs_num = 1 where id =?1", nativeQuery = true)
    int bdXsDdxcTime(Long id,Integer zxys);

    /**
     * 正常巡视 到达现场时间更改
     *
     * @param id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "update xs_zc_task set ddxc_time  = sysdate,zxys_num = ?2,xscs_num = 1  where id =?1", nativeQuery = true)
    int zcXsDdxcTime(Long id,Integer zxys);

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
    int insertZcxsArticles(Long id, Long taskId, String wpZt);

    /**
     * 正常巡视如果有这条修改这条物品修改
     *
     * @param rwZt   物品状态
     * @param taskId 任务ID
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE XS_ZC_TASKWPQR SET WP_ZT =?1 WHERE TASKID =?2", nativeQuery = true)
    int updateZcxsArticles(String rwZt, Long taskId);

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
    int insertBdtxArticles(Long id, Long taskId, String wpZt);

    /**
     * 保电特寻如果有这条修改这条物品修改
     *
     * @param rwZt   物品状态
     * @param taskId 任务ID
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE XS_TXBD_TASKWPQR SET WP_ZT =?1 WHERE TASKID =?2", nativeQuery = true)
    int updateBdtxArticles(String rwZt, Long taskId);

    /**
     * 正常巡视新增轮数据
     *
     * @param id     ID
     * @param taskId 任务ID
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO xs_zc_task_exec(ID,xs_zc_task_id,xs_create_time,xs_repeat_num) VALUES (?1,?2,sysdate,?3)", nativeQuery = true)
    int insertZcXsTaskExec(Long id, Long taskId, Integer repeat);


    /**
     * 正常巡视新增轮数据
     *
     * @param id     ID
     * @param taskId 任务ID
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO xs_txbd_task_exec(ID,xs_zc_task_id,xs_create_time,xs_repeat_num) VALUES (?1,?2,sysdate,?3)", nativeQuery = true)
    int insertdtxTaskExec(Long id, Long taskId, Integer repeat);


    /**
     * 正常巡视 修改当前任务巡视到的页数
     *
     * @param zxys   执行页数
     * @param id 任务ID
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE XS_ZC_TASK SET zxys_num =?1 WHERE id =?2", nativeQuery = true)
    int updateXszcTaskZxys(Integer zxys, Long id);

    /**
     * 正常巡视 修改当前任务巡视到的页数
     *
     * @param zxys   执行页数
     * @param id 任务ID
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE XS_txbd_TASK SET zxys_num =?1 WHERE id =?2", nativeQuery = true)
    int updateTxbdTaskZxys(Integer zxys, Long id);

    /**
     * 保电特巡新增轮详情数据
     *
     *
     * @param nextId
     * @param aLong
     * @param gznr
     * @param id 主键
     * @param execId     轮ID
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO xs_txbd_task_exec_detail(ID,XS_TXBD_TASK_EXEC_ID,start_time,operate_name,start_tower_id,end_tower_id) VALUES (?1,?2,sysdate,?3,?4,?5)", nativeQuery = true)
    void insertTxbdExecDetail(long nextId, Long execID, String gznr, Long startTowerId, Long endTowerID);



    /**
     * 正常巡视新增轮详情数据
     *
     *
     * @param nextId
     * @param aLong
     * @param gznr
     * @param id 主键
     * @param execId     轮ID
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO xs_zc_task_exec_detail(ID,XS_ZC_TASK_EXEC_ID,START_TIME,operate_name,start_tower_id,end_tower_id) VALUES (?1,?2,sysdate,?3,?4,?5)", nativeQuery = true)
    void insertZcxsExecDetail(long nextId, Long execId, String gznr, Long startTowerId, Long endTowerId);


    /**
     * 正常巡视 修改当前任务巡视到的页数
     *
     * @param sfdw   是否到位
     * @param reason 不到位原因
     * @param execDetailId 轮详情id
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE xs_txbd_task_exec_detail SET is_dw =?1,reason = ?2,end_time = sysdate WHERE id =?3", nativeQuery = true)
    void updateTxbdExecDetail(Integer sfdw, String reason, Long execDetailId);

    /**
     * 正常巡视 修改当前任务巡视到的页数
     *
     * @param sfdw   是否到位
     * @param reason 不到位原因
     * @param execDetailId 轮详情id
     * @param longtitude
     * @param latitude
     * @return
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE xs_zc_task_exec_detail SET is_dw =?1,reason = ?2,end_time = sysdate,REALLONGITUDE = ?4,REALLATITUDE = ?5 WHERE id =?3", nativeQuery = true)
    void updateZcxsExecDetail(Integer sfdw, String reason, Long execDetailId, String longtitude, String latitude);

    /***
    * @Method updateTxbdExecOn
    * @Description  特巡保电更改轮状态 到进行中
    * @param [execId]
    * @return void
    * @date 2017/12/19 13:51
    * @author nwz
    */
    @Modifying
    @Transactional
    @Query(value = "UPDATE xs_txbd_task_exec SET xs_start_time = sysdate,xs_status = 1 WHERE id =?1", nativeQuery = true)
    void updateTxbdExecOn(Long execId);

    /***
    * @Method updateTxbdExecOff
    * @Description   特巡保电更改论状态 结束
    * @param [execId]
    * @return void
    * @date 2017/12/19 13:55
    * @author nwz
    */
    @Modifying
    @Transactional
    @Query(value = "UPDATE xs_txbd_task_exec SET xs_end_time = sysdate,xs_status = 2 WHERE id =?1", nativeQuery = true)
    void updateTxbdExecOff(Long execId);

    /***
    * @Method updateZcxsExecOn
    * @Description
    * @param [execId]
    * @return void
    * @date 2017/12/19 13:57
    * @author nwz
    */
    @Modifying
    @Transactional
    @Query(value = "UPDATE xs_zc_task_exec SET xs_start_time = sysdate,xs_status = 1 WHERE id =?1", nativeQuery = true)
    void updateZcxsExecOn(Long execId);

    /***
    * @Method updateZcxsExecOff
    * @Description
    * @param [execId]
    * @return void
    * @date 2017/12/19 13:57
    * @author nwz
    */
    @Modifying
    @Transactional
    @Query(value = "UPDATE xs_zc_task_exec SET xs_end_time = sysdate,xs_status = 2 WHERE id =?1", nativeQuery = true)
    void updateZcxsExecOff(Long execId);


    /***
     * @Method updateZcxsExecOff
     * @Description
     * @param [execId]
     * @return void
     * @date 2017/12/19 13:57
     * @author nwz
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE xs_txbd_task SET xscs_num = ?2 WHERE id =?1", nativeQuery = true)
    void updateTxbdTaskXsRepeatNum(Long taskId, Integer repeatNum);


    /***
     * @Method updateZcxsExecOff
     * @Description
     * @param [execId]
     * @return void
     * @date 2017/12/19 13:57
     * @author nwz
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE xs_zc_task SET xscs_num = ?2 WHERE id =?1", nativeQuery = true)
    void updateZcxsTaskXsRepeatNum(Long taskId, Integer repeatNum);



    /***
     * @Method updateTxbdTaskToOff
     * @Description
     * @param id
     * @return void
     * @date 2017/12/19 13:57
     * @author nwz
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE xs_txbd_task SET stauts = 2,real_end_time = sysdate WHERE id =?1", nativeQuery = true)
    void updateTxbdTaskToOff(Long id);

    /***
     * @Method updateZcxsExecOff
     * @Description
     * @param id
     * @return void
     * @date 2017/12/19 13:57
     * @author nwz
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE xs_zc_task SET stauts = 2,real_end_time = sysdate WHERE id =?1", nativeQuery = true)
    void updateZcxsTaskToOff(Long id);
}
