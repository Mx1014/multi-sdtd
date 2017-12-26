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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class RztSysUserService extends CurdService<RztSysUser, RztSysUserRepository> {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Page<RztSysUser> findByName(String name, Pageable pageable) {
        if (StringUtils.isEmpty(name))
            return this.reposiotry.findAll(pageable);
        else {
            name = "%" + name + "%";
            return this.reposiotry.findByRealnameLike(name, pageable);
        }
    }

    public Page<Map<String, Object>> findUserList(Integer page, Integer size, String DEPTID, String REALNAME, String CLASSNAME, String WORKTYPE) {
        ArrayList<String> arrayList = new ArrayList<>();
        Pageable pageable = new PageRequest(page, size);
        String s = "";
        if (!StringUtils.isEmpty(DEPTID)) {
            arrayList.add(DEPTID);
            s += " AND DEPTID = ?" + arrayList.size();
        }
        if (!StringUtils.isEmpty(REALNAME)) {
            arrayList.add(REALNAME + "%");
            s += " AND REALNAME LIKE ?" + arrayList.size();
        }
        if (!StringUtils.isEmpty(CLASSNAME)) {
            arrayList.add(CLASSNAME);
            s += " AND CLASSNAME = ?" + arrayList.size();
        }
        if (!StringUtils.isEmpty(WORKTYPE)) {
            arrayList.add(WORKTYPE);
            s += " AND WORKTYPE = ?" + arrayList.size();
        }
//        String sql = "SELECT " +
//                "  wm_concat(r.ID) AS roleid,u.id," +
//                "  wm_concat(R.roleName) AS roleName," +
//                "  U.REALNAME, " +
//                "  U.USERNAME, " +
//                "  U.EMAIL, " +
//                "  U.PHONE, " +
//                "  U.DEPTID, " +
//                "  U.CLASSNAME, " +
//                "  U.CERTIFICATE, " +
//                "  U.WORKYEAR, " +
//                "  U.WORKTYPE, " +
//                "  U.SERIALNUMBER, " +
//                "  U.AGE, " +
//                "  U.USERTYPE, " +
//                "  U.AVATAR  " +
//                " FROM rztsysuser u LEFT JOIN rztsysuserrole l ON u.id = l.userId " +
//                "  LEFT JOIN rztsysrole r ON l.roleId = r.id WHERE USERDELETE = 1  " + s +
//                " GROUP BY u.REALNAME, U.REALNAME,U.USERNAME,U.EMAIL,U.PHONE,U.DEPTID,U.CLASSNAME,U.CERTIFICATE,U.WORKYEAR,U.WORKTYPE,U.SERIALNUMBER,U.AGE,U.USERTYPE,U.AVATAR,u.id";
        String sql = "SELECT " +
                "  u.id, " +
                "  R.roleName AS roleName, " +
                "  U.REALNAME, " +
                "  U.USERNAME, " +
                "  U.EMAIL, " +
                "  U.PHONE, " +
                "  U.DEPTID, " +
                "  U.CERTIFICATE, " +
                "  U.WORKYEAR, " +
                "  U.WORKTYPE, " +
                "  U.SERIALNUMBER, " +
                "  U.AGE, " +
                "  U.USERTYPE, " +
                "  U.AVATAR, " +
                "  y.DEPTNAME, " +
                "  m.COMPANYNAME , " +
                "  t.DEPTNAME as classname " +
                "FROM rztsysuser u LEFT JOIN rztsysuserrole l ON u.id = l.userId " +
                "  LEFT JOIN rztsysrole r ON l.roleId = r.id " +
                "  LEFT JOIN RZTSYSDEPARTMENT y ON u.DEPTID = y.ID " +
                "  LEFT JOIN RZTSYSDEPARTMENT t ON u.CLASSNAME = t.ID " +
                "  LEFT JOIN RZTSYSCOMPANY m ON m.ID = u.COMPANYID " +
                "WHERE USERDELETE = 1" + s;
        return this.execSqlPage(pageable, sql, arrayList.toArray());
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
    @Transactional
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
    @Transactional
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
            this.reposiotry.updateUser(age, certificate, deptid, email, phone, realname, serialnumber, userType, username, worktype, workyear, id);
            /**
             * 修改Redis人员信息
             */
            String sql1 = " SELECT * FROM USERINFO where id=?1 and USERDELETE = 1 ";
            Map<String, Object> stringObjectMap = this.execSqlSingleResult(sql1, id);
            HashOperations hashOperations = redisTemplate.opsForHash();
            hashOperations.put("UserInformation", id, stringObjectMap);
            return WebApiResponse.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.success("修改失败");
        }
    }

    /**
     * 人员查询
     *
     * @return
     */
    public List<Map<String, Object>> userQuery(String classname, String realname) {
        ArrayList arrayList = new ArrayList();
        String s = "";
        arrayList.add(classname);
        if (!StringUtils.isEmpty(realname)) {
            arrayList.add("%" + realname + "%");
            s += " AND REALNAME LIKE  ?" + arrayList.size();
        }
        String sql = " SELECT ID,REALNAME FROM RZTSYSUSER WHERE CLASSNAME = ?1 AND USERDELETE = 1 " + s;
        return this.execSql(sql, arrayList.toArray());
    }

    public Map<String, Object> getUserinfoByUserId(String userid) {
        String sql = "select * from userinfo where id = ?1";
        Map<String, Object> map = null;
        try {
            map = this.execSqlSingleResult(sql, userid);
        } catch (Exception e) {
            e.printStackTrace();
            map = new HashMap<>();
        }
        return map;
    }

    /**
     * 人员添加角色
     *
     * @param roleid 登陆人角色ID
     * @return
     */
    public List<Map<String, Object>> treeRztsysroleQuery(String roleid) {
        String sql = " SELECT ID as \"value\" ,ROLENAME as \"label\",ROLENODEID,ID FROM RZTSYSROLE START WITH ID=?1 CONNECT BY PRIOR ID=ROLENODEID ";
        List<Map<String, Object>> list = this.execSql(sql, roleid);
        List list1 = treeOrgRztsysroleList(list, list.get(0).get("ID").toString());
        return list1;

    }

    public List treeOrgRztsysroleList(List<Map<String, Object>> orgList, String parentId) {
        List childOrg = new ArrayList<>();
        for (Map<String, Object> map : orgList) {
            String menuId = String.valueOf(map.get("ID"));
            String pid = String.valueOf(map.get("ROLENODEID"));
            if (parentId.equals(pid)) {
                List c_node = treeOrgRztsysroleList(orgList, menuId);
                map.put("children", c_node);
                childOrg.add(map);
            }
        }
        return childOrg;
    }

    @Transactional
    public WebApiResponse userLogin(String password, String account, String loginType, HttpServletRequest request) {
        String auth = "SELECT USERID FROM RZTSYSUSERAUTH WHERE IDENTITYTYPE = ?1 AND PASSWORD =?2 ";
        String user = "SELECT * FROM RZTSYSUSER  WHERE id=?1 AND USERDELETE = 1 AND USERTYPE = ?2 ";
        String userAccout = "SELECT * FROM USERINFO where id=?1";
        try {
            Map<String, Object> stringObjectMap = this.execSqlSingleResult(auth, account, password);
            if (!StringUtils.isEmpty(stringObjectMap.get("USERID"))) {
                List<Map<String, Object>> userid = this.execSql(user, String.valueOf(stringObjectMap.get("USERID")), loginType);
                if (userid.size() == 1) {
                    this.reposiotry.updateUserLOGINSTATUS(String.valueOf(stringObjectMap.get("USERID")));
                    List<Map<String, Object>> userid1 = this.execSql(userAccout, String.valueOf(stringObjectMap.get("USERID")));
                    HashOperations hashOperations = redisTemplate.opsForHash();
                    hashOperations.put("UserInformation", stringObjectMap.get("USERID"), userid1);
                    request.getSession().setAttribute("user", userid1);
//                    usercenter.lineSize(String.valueOf(userid1.get(0).get("ID")), Integer.valueOf(userid1.get(0).get("LOGINSTATUS").toString()), Integer.valueOf(userid1.get(0).get("WORKTYPE").toString()));
                    return WebApiResponse.success(userid1);
                }
            }
            return WebApiResponse.erro("erro");
        } catch (Exception e) {
            return WebApiResponse.erro("erro");
        }
    }

    @Transactional
    public WebApiResponse userQuit(String id, HttpServletRequest request) {
        String userAccout = "SELECT * FROM USERINFO where id=?1";
        try {
            this.reposiotry.quitUserLOGINSTATUS(id);
            request.getSession().removeAttribute("user");
            List<Map<String, Object>> maps = this.execSql(userAccout, id);
            HashOperations hashOperations = redisTemplate.opsForHash();
            hashOperations.put("UserInformation", id, maps);
//            usercenter.lineSize(String.valueOf(maps.get(0).get("ID")), Integer.valueOf(maps.get(0).get("LOGINSTATUS").toString()), Integer.valueOf(maps.get(0).get("WORKTYPE").toString()));
            return WebApiResponse.success("");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }
}