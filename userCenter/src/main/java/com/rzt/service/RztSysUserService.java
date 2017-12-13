/**
 * 文件名：RztSysUserService
 * 版本信息：
 * 日期：2017/10/10 17:28:27
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.entity.RztSysUser;
import com.rzt.repository.RztSysUserRepository;
import com.rzt.util.WebApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Map;

/**
 * 类名称：RztSysUserService
 * 类描述：InnoDB free: 537600 kB
 * 创建人：张虎成
 * 创建时间：2017/10/10 17:28:27
 * 修改人：张虎成
 * 修改时间：2017/10/10 17:28:27
 * 修改备注：
 */
@Service
@Transactional
public class RztSysUserService extends CurdService<RztSysUser, RztSysUserRepository> {
    @PersistenceContext
    private EntityManager entityManager;

    public Page<RztSysUser> findByName(String name, Pageable pageable) {
        if (StringUtils.isEmpty(name))
            return this.reposiotry.findAll(pageable);
        else {
            name = "%" + name + "%";
            return this.reposiotry.findByRealnameLike(name, pageable);
        }
    }

    public Page<Map<String, Object>> findUserList(Integer page, Integer size) {
//		StringBuilder builder = new StringBuilder();
//		builder.append("SELECT GROUP_CONCAT(r.id) roleid,GROUP_CONCAT(r.roleName) roleName,u.* from " +
//				"rztsysuser u LEFT JOIN rztsysuserrole l ON u.id = " +
//				"l.userId LEFT JOIN rztsysrole r on l.roleId = r.id GROUP BY u.id");
//		builder.append(PageUtil.getLimit(page,size));
//		return DbUtil.list(entityManager,builder.toString());
        Pageable pageable = new PageRequest(page, size);
        String sql = "SELECT " +
                "  wm_concat(r.ID) AS roleid,u.id," +
                "  wm_concat(R.roleName) AS roleName," +
                "  U.REALNAME, " +
                "  U.USERNAME, " +
                "  U.EMAIL, " +
                "  U.PHONE, " +
                "  U.DEPTID, " +
                "  U.CLASSNAME, " +
                "  U.CERTIFICATE, " +
                "  U.WORKYEAR, " +
                "  U.WORKTYPE, " +
                "  U.SERIALNUMBER, " +
                "  U.AGE, " +
                "  U.USERTYPE, " +
                "  U.AVATAR  " +
                " FROM rztsysuser u LEFT JOIN rztsysuserrole l ON u.id = l.userId " +
                "  LEFT JOIN rztsysrole r ON l.roleId = r.id WHERE USERDELETE = 1  " +
                "GROUP BY u.REALNAME, U.REALNAME,U.USERNAME,U.EMAIL,U.PHONE,U.DEPTID,U.CLASSNAME,U.CERTIFICATE,U.WORKYEAR,U.WORKTYPE,U.SERIALNUMBER,U.AGE,U.USERTYPE,U.AVATAR,u.id";
        return this.execSqlPage(pageable, sql);
    }

    public RztSysUser findByUsernameAndDeptid(String username, String deptid) {
        return this.reposiotry.findByUsernameAndDeptid(username, deptid);
    }

    /**
     * 伪删除和批量伪删除人员
     *
     * @param id 人员ID
     * @return
     */
    public int logicUser(String id) {
        return this.reposiotry.logicUser(id);
    }

    /**
     * 修改人员
     *
     * @param id
     * @param user
     * @return
     */
    public WebApiResponse updateUser(String id, RztSysUser user) {
        int age = user.getAge();
        String certificate = user.getCertificate();
        String deptid = user.getDeptid();
        String email = user.getEmail();
        String phone = user.getPhone();
        String realname = user.getRealname();
        String serialnumber = user.getSerialnumber();
        int userType = user.getUserType();
        String username = user.getUsername();
        int worktype = user.getWorktype();
        int workyear = user.getWorkyear();
        try {
            return WebApiResponse.success(this.reposiotry.updateUser(age, certificate, deptid, email, phone, realname, serialnumber, userType, username, worktype, workyear, id));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.success("修改失败");
        }
    }

}