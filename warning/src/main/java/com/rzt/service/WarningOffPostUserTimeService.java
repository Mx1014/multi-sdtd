/**    
 * 文件名：WarningOffPostUserTimeService           
 * 版本信息：    
 * 日期：2017/12/27 03:58:05    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.entity.OffPostUserTime;
import com.rzt.repository.OffPostUserTimeRepository;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**      
 * 类名称：WarningOffPostUserTimeService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/27 03:58:05 
 * 修改人：张虎成    
 * 修改时间：2017/12/27 03:58:05    
 * 修改备注：    
 * @version        
 */
@Service
public class WarningOffPostUserTimeService extends CurdService<OffPostUserTime, OffPostUserTimeRepository> {

    @Autowired
    private OffPostUserTimeRepository repository;

    /**
     * 更新脱岗结束时间
     * @param offPostUserTime
     */
    @Transactional
    public void updateOffUserEndTime(OffPostUserTime  offPostUserTime){
        repository.saveAndFlush(offPostUserTime);
    }

    /**
     * 查询该人员是否已经脱岗
     * @param userId
     * @return
     */
    public OffPostUserTime findByUserIdAndDateisNull(String userId){
        return repository.findByUserIdAndDateisNull(userId);
    }

    /**
     * 添加一条新的脱岗时间记录
     * @param userId
     */
    @Transactional
    public void addOffUserTime(String userId){
        OffPostUserTime offPostUserTime = new OffPostUserTime();
        offPostUserTime.setId(new SnowflakeIdWorker(0,0).nextId());
        offPostUserTime.setFkUserId(userId);
        offPostUserTime.setStartTime(new Date());
        repository.save(offPostUserTime);
    }


}