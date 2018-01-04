/**
 * 文件名：RztSysRoleService
 * 版本信息：
 * 日期：2017/10/11 18:51:02
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysRole;
import com.rzt.repository.RztSysRoleRepository;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 类名称：RztSysRoleService
 * 类描述：InnoDB free: 537600 kB
 * 创建人：张虎成
 * 创建时间：2017/10/11 18:51:02
 * 修改人：张虎成
 * 修改时间：2017/10/11 18:51:02
 * 修改备注：
 */
@Service
public class RztSysRoleService extends CurdService<RztSysRole, RztSysRoleRepository> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public List<RztSysRole> findAllRole() {
        return this.reposiotry.findAll();
    }

    public Page<RztSysRole> findByName(String name, Pageable pageable) {
        if (StringUtils.isEmpty(name))
            return this.reposiotry.findAll(pageable);
        else {
            name = "%" + name + "%";
            return this.reposiotry.findByRolenameLike(name, pageable);
        }
    }

    /**
     * 角色分配
     *
     * @return
     */
    public WebApiResponse roleDistribution(String userID) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", userID).toString());
        Integer roletype = Integer.valueOf(jsonObject.get("ROLETYPE").toString());
        if (roletype == 0) {
            String sql = " SELECT ID,ROLENAME FROM RZTSYSROLE  WHERE ROLENODEID != '0'  ";
            try {
                return WebApiResponse.success(this.execSql(sql));
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("ERRO");
            }
        }
        String roleid = (String) jsonObject.get("ROLEID");
        if (!StringUtils.isEmpty(roleid)) {
            String roleID = " SELECT ROLENODEID FROM RZTSYSROLE WHERE ID = ?1 ";
            try {
                String id = this.execSqlSingleResult(roleID, roleid).get("ROLENODEID").toString();
                String sql = " SELECT * from (SELECT " +
                        "  ID, " +
                        "  ROLENAME, " +
                        "  ROLENODEID " +
                        "FROM RZTSYSROLE " +
                        "START WITH id = ?1 CONNECT BY PRIOR id = ROLENODEID) WHERE ROLENODEID !='0' ";
                return WebApiResponse.success(this.execSql(sql, id));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return WebApiResponse.erro("erro");
    }
}