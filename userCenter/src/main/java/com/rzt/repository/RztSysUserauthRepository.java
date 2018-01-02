/**
 * 文件名：RztSysUserauthRepository
 * 版本信息：
 * 日期：2017/10/10 17:28:27
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository;

import com.rzt.entity.RztSysUserauth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 类名称：RztSysUserauthRepository
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/10/10 17:28:27
 * 修改人：张虎成
 * 修改时间：2017/10/10 17:28:27
 * 修改备注：
 */
@Repository
public interface RztSysUserauthRepository extends JpaRepository<RztSysUserauth, String> {

    //  public RztSysUserauth findByIdentitytype(String phone);

    /**
     * 邮箱验证
     *
     * @param email
     * @return
     */
    @Query(value = "SELECT count(id) FROM RZTSYSUSER WHERE EMAIL = ?1 AND USERDELETE = 1", nativeQuery = true)
    int VerificationEmail(String email);

    /**
     * 账号账号
     *
     * @param username
     * @return
     */
    @Query(value = "SELECT count(id) FROM RZTSYSUSER WHERE USERNAME = ?1 AND USERDELETE = 1 AND id != ?2 ", nativeQuery = true)
    int VerificationUsername(String username, String id);

    /**
     * 手机号验证
     *
     * @param phone
     * @return
     */
    @Query(value = "SELECT count(id) FROM RZTSYSUSER WHERE PHONE = ?1 AND USERDELETE = 1 AND id != ?2 ", nativeQuery = true)
    int VerificationPhone(String phone, String id);

    @Modifying
    @Query(value = "UPDATE RztSysUserauth u SET u.password=?2 WHERE u.userId=?1", nativeQuery = true)
    public void updatePasswordByUserid(String userId, String password);

    @Modifying
    @Query(value = "UPDATE RztSysUserauth u SET u.lastLoginTime=now(),u.lastLoginIp = ?1 WHERE u.userId=?2 and u.identifier = ?3", nativeQuery = true)
    public void updateLoginIp(String loginId, String userid, int identifier);

    public void deleteAuthByUserid(String userId);

    @Query(value = "DELETE from RztSysUserauth u WHERE u.userid in (?1)", nativeQuery = true)
    @Modifying
    public void deleteBatchAuthByUseiId(String ids);

    public RztSysUserauth findByIdentifierAndIdentitytypeAndPassword(int identifier, String identityType, String password);
}
