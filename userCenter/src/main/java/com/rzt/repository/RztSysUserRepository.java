/**
 * 文件名：RztSysUserRepository
 * 版本信息：
 * 日期：2017/10/10 17:28:27
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository;

import com.rzt.entity.RztSysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 类名称：RztSysUserRepository
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/10/10 17:28:27
 * 修改人：张虎成
 * 修改时间：2017/10/10 17:28:27
 * 修改备注：
 */
@Repository
public interface RztSysUserRepository extends JpaRepository<RztSysUser, String> {

    Page<RztSysUser> findByRealnameLike(String name, Pageable pageable);

    RztSysUser findByUsernameAndDeptid(String username, String deptid);

    @Modifying
    @Query(value = " UPDATE RZTSYSUSER SET USERDELETE = 0 WHERE id=?1 ", nativeQuery = true)
    int logicUser(String id);

    @Modifying
    @Query(value = " UPDATE RZTSYSUSER " +
            "SET AGE    = ?1, CERTIFICATE = ?2, DEPTID = ?3, PHONE = ?4, REALNAME = ?5, SERIALNUMBER = ?6, USERTYPE = ?7, " +
            "  USERNAME = ?8, WORKTYPE = ?9, WORKYEAR = ?10,AVATAR=?11,CLASSNAME = ?12,COMPANYID=?13 " +
            "WHERE ID = ?14 ", nativeQuery = true)
    int updateUser(int age, String certificate, String deptid, String phone, String realname, String serialnumber, int userType, String username, int worktype, int workyear, String AVATAR, String classname, String companyid, String id);

    @Modifying
    @Transactional
    @Query(value = " UPDATE RZTSYSUSER SET LOGINSTATUS = 1 WHERE id=?1 ", nativeQuery = true)
    int updateUserLOGINSTATUS(String id);

    @Modifying
    @Query(value = " UPDATE RZTSYSUSER SET LOGINSTATUS = 0 WHERE id=?1 ", nativeQuery = true)
    int quitUserLOGINSTATUS(String id);
}
