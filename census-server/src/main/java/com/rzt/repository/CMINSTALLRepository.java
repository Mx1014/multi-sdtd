/**
 * 文件名：CMINSTALLRepository
 * 版本信息：
 * 日期：2017/12/11 15:58:59
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository;

import com.rzt.entity.CMINSTALL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 类名称：CMINSTALLRepository
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/11 15:58:59
 * 修改人：张虎成
 * 修改时间：2017/12/11 15:58:59
 * 修改备注：
 */
@Repository
public interface CMINSTALLRepository extends JpaRepository<CMINSTALL, String> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE CM_INSTALL SET KEY_NUM=?1 WHERE ID = ?2 ", nativeQuery = true)
    int cminstallUpdate(int key, Long id);
}
