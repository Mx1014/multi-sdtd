/**
 * 文件名：RztSysUserauthService
 * 版本信息：
 * 日期：2017/10/10 17:28:27
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.service.CurdService;
import com.rzt.entity.RztSysUser;
import com.rzt.entity.RztSysUserauth;
import com.rzt.repository.RztSysUserauthRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类名称：RztSysUserauthService
 * 类描述：InnoDB free: 537600 kB
 * 创建人：张虎成
 * 创建时间：2017/10/10 17:28:27
 * 修改人：张虎成
 * 修改时间：2017/10/10 17:28:27
 * 修改备注：
 */
@Service
public class RztSysUserauthService extends CurdService<RztSysUserauth, RztSysUserauthRepository> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public String findByUserName(RztSysUser user) {
        String flag = "1";
//        int userauth = this.reposiotry.VerificationEmail(user.getEmail());
//        if (userauth != 0)
//            flag = "该邮箱已存在";
        int userauth1 = this.reposiotry.VerificationUsername(user.getUsername(), user.getId());
        if (userauth1 != 0)
            flag = "该账号已存在";
        int userauth2 = this.reposiotry.VerificationPhone(user.getPhone(), user.getId());
        if (userauth2 != 0)
            flag = "该手机号已存在";
        return flag;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addUserAuth(RztSysUser user, String password) {
        RztSysUserauth userauth = new RztSysUserauth();
        userauth.setCreatetime(DateUtil.dateNow());
        userauth.setIdentifier(0);
        userauth.setIdentitytype(user.getUsername());
        userauth.setPassword(password);
        userauth.setUserid(user.getId());
        this.reposiotry.save(userauth);
        if (!StringUtils.isEmpty(user.getPhone())) {
            RztSysUserauth userauth1 = new RztSysUserauth();
            userauth1.setCreatetime(DateUtil.dateNow());
            userauth1.setIdentifier(1);
            userauth1.setIdentitytype(user.getPhone());
            userauth1.setPassword(password);
            userauth1.setUserid(user.getId());
            this.reposiotry.save(userauth1);
        }
        if (!StringUtils.isEmpty(user.getEmail())) {
            RztSysUserauth userauth2 = new RztSysUserauth();
            userauth2.setCreatetime(DateUtil.dateNow());
            userauth2.setIdentifier(2);
            userauth2.setIdentitytype(user.getEmail());
            userauth2.setPassword(password);
            userauth2.setUserid(user.getId());
            this.reposiotry.save(userauth2);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateLoginIp(String loginId, String userid, int identifier) {
        this.reposiotry.updateLoginIp(loginId, userid, identifier);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserAuth(String userid, String password) {
        this.reposiotry.updatePasswordByUserid(userid, password);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAuthByUserId(String userId) {
        this.reposiotry.deleteAuthByUserid(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchAuthByUserId(String ids) {
        this.reposiotry.deleteBatchAuthByUseiId(ids);
    }

    public RztSysUserauth findByIdentifierAndIdentityTypeAndPassword(int identifier, String identityType, String password) {
        return this.reposiotry.findByIdentifierAndIdentitytypeAndPassword(identifier, identityType, password);
    }
}