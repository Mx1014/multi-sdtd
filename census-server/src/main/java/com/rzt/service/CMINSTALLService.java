/**
 * 文件名：CMINSTALLService
 * 版本信息：
 * 日期：2017/12/11 15:58:59
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.entity.CMINSTALL;
import com.rzt.repository.CMINSTALLRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 类名称：CMINSTALLService
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/11 15:58:59
 * 修改人：张虎成
 * 修改时间：2017/12/11 15:58:59
 * 修改备注：
 */
@Service
public class CMINSTALLService extends CurdService<CMINSTALL, CMINSTALLRepository> {
    /**
     * 查询字典全部数据
     *
     * @return
     */
    public List<Map<String, Object>> cminstallQuery() {
        return this.execSql(" SELECT ID,ZYXX_NAME,ZYLX,SZ_NUM,KEY_NUM FROM CM_INSTALL ");
    }

    /**
     * 修改配置表
     *
     * @param id
     * @return
     */
    public int cminstallUpdate(int key, Long id) {
        return this.reposiotry.cminstallUpdate(key, id);
    }
}