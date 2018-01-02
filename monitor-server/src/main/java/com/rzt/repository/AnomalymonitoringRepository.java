/**
 * 文件名：ANOMALYMONITORINGRepository
 * 版本信息：
 * 日期：2017/12/31 16:25:17
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository;

import com.rzt.entity.Anomalymonitoring;
import io.swagger.models.auth.In;
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
     * 二级添加
     *
     * @param id
     * @param explain     告警说明
     * @param status
     * @param tasktype
     * @param anomalytype
     * @return
     */
    @Modifying
    @Query(value = " INSERT INTO ANOMALY_MONITORING (ID, TASKID, TWOCHECK_STATUS, TWOCHECK_EXPLAINZ,TASK_TYPE, ANOMALY_TYPE) VALUES (?1, ?2, 1, ?3, ?4, ?5)  ", nativeQuery = true)
    int ejAnomalyIns(Long id, String explain, Integer status, Integer tasktype, Integer anomalytype);

    /**
     * 一级单位处理
     *
     * @param id
     * @param explain
     * @param status
     * @param tasktype
     * @param anomalytype
     * @return
     */
    @Modifying
    @Query(value = " INSERT INTO ANOMALY_MONITORING (ID, TASKID, ONECHECK_STATUS, ONECHECK_EXPLAINZ, TASK_TYPE, ANOMALY_TYPE) VALUES (?1, ?2, 1, ?3, ?4, ?5) ", nativeQuery = true)
    int yiAnomalyIns(Long id, String explain, Integer status, Integer tasktype, Integer anomalytype);

    /**
     * 已完成处理
     *
     * @param id
     * @param explain
     * @param status
     * @param tasktype
     * @param anomalytype
     * @return
     */
    @Modifying
    @Query(value = " INSERT INTO ANOMALY_MONITORING (ID, TASKID,  TWOCHECK_STATUS, TWOCHECK_EXPLAINO,TASK_TYPE, ANOMALY_TYPE,TWOASSESSMENT) VALUES (?1, ?2, 0, ?3, ?4, ?5,?6) ", nativeQuery = true)
    int ejAnomalyInsO(Long id, String explain, Integer status, Integer tasktype, Integer anomalytype, Integer assessment);

    @Modifying
    @Query(value = " INSERT INTO ANOMALY_MONITORING (ID, TASKID, ONECHECK_STATUS, ONECHECK_EXPLAINZ, TASK_TYPE, ANOMALY_TYPE,ONEASSESSMENT) VALUES (?1, ?2, 0, ?3, ?4, ?5,?6); ", nativeQuery = true)
    int yjAnomalyInsO(Long id, String explain, Integer status, Integer tasktype, Integer anomalytype, Integer assessment);
}
