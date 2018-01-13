/**
 * 文件名：MONITORCHECKYJService
 * 版本信息：
 * 日期：2018/01/08 11:06:23
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.entity.Monitorcheckyj;
import com.rzt.repository.Monitorcheckyjrepository;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类名称：MONITORCHECKYJService
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2018/01/08 11:06:23
 * 修改人：张虎成
 * 修改时间：2018/01/08 11:06:23
 * 修改备注：
 */
@Service
public class Monitorcheckyjservice extends CurdService<Monitorcheckyj, Monitorcheckyjrepository> {

    @Autowired
    private Monitorcheckyjrepository repo;


    public void saveCheckYj(String[] messages) {
        //保存到一级
        repo.saveCheckYj(new SnowflakeIdWorker(0,0).nextId(),Long.valueOf(messages[1]),Integer.valueOf(messages[2]),Integer.valueOf(messages[3]),messages[4],messages[5],messages[6]);
    }
}