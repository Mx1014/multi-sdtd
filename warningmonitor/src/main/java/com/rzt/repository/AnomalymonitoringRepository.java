/**
 * 文件名：ANOMALYMONITORINGRepository
 * 版本信息：
 * 日期：2017/12/31 16:25:17
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository;

import com.rzt.entity.Anomalymonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 类名称：ANOMALYMONITORINGRepository
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/31 16:25:17
 * 修改人：张虎成
 * 修改时间：2017/12/31 16:25:17
 * 修改备注：
 */
@Repository
public interface AnomalymonitoringRepository extends JpaRepository<Anomalymonitoring, String> {
    /**
     * 二级 未处理告警处理
     */
    @Modifying
    @Query(value = " INSERT INTO ANOMALY_MONITORING (ID, TASKID, TWOCHECK_STATUS, TWOCHECK_EXPLAINZ,TWOCHECK_APP_INFO,TASK_TYPE, ANOMALY_TYPE) VALUES (?1, ?2, ?3, ?4, ?5,?6,?7)  ", nativeQuery = true)
    int ejAnomalyIns(Long id, Long taskId, Integer s, String explain, String explainApp, Integer tasktype, Integer anomalytype);

    /**
     * 一级单位处理  未处理告警处理
     */
    @Modifying
    @Query(value = " INSERT INTO ANOMALY_MONITORING (ID, TASKID, ONECHECK_STATUS, ONECHECK_EXPLAINZ,ONECHECK_APP_INFO, TASK_TYPE, ANOMALY_TYPE) VALUES (?1, ?2, ?3, ?4, ?5,?6,?7) ", nativeQuery = true)
    int yiAnomalyIns(Long id, Long taskId, Integer s, String explain, String explainApp, Integer tasktype, Integer anomalytype);

    /**
     * 已完成处理
     */
    @Modifying
    @Query(value = " UPDATE ANOMALY_MONITORING set TWOCHECK_STATUS = 0,TWOCHECK_EXPLAINO = ?1 where TASKID = ?2 and ANOMALY_TYPE = ?3", nativeQuery = true)
    int ejAnomalyInsO(String explain, Long taskId, Integer anomalytype);

    @Modifying
    @Query(value = " UPDATE ANOMALY_MONITORING set ONECHECK_STATUS = 0,ONECHECK_EXPLAINO = ?1 where TASKID = ?2 and ANOMALY_TYPE = ?3 ", nativeQuery = true)
    int yjAnomalyInsO(String explain, Long taskId, Integer anomalytype);


}
