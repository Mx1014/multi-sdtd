/**
 * 文件名：ANOMALYMONITORINGService
 * 版本信息：
 * 日期：2017/12/31 16:25:17
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.entity.Anomalymonitoring;
import com.rzt.repository.AnomalymonitoringRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 类名称：ANOMALYMONITORINGService    
 * 类描述：${table.comment}
 * 创建人：张虎成   
 * 创建时间：2017/12/31 16:25:17 
 * 修改人：张虎成    
 * 修改时间：2017/12/31 16:25:17    
 * 修改备注：    
 * @version
 */
@Service
@Transactional
public class AnomalymonitoringService extends CurdService<Anomalymonitoring, AnomalymonitoringRepository> {


}