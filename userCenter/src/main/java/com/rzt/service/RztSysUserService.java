/**
 * 文件名：RztSysUserService
 * 版本信息：
 * 日期：2017/10/10 17:28:27
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.eureka.StaffLine;
import com.rzt.repository.RztSysUserRepository;
import com.rzt.security.JwtHelper;
import com.rzt.security.TokenProp;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

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
    @Autowired
    private TokenProp tokenProp;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    StaffLine staffLine;

    public Page<RztSysUser> findByName(String name, Pageable pageable) {
        if (StringUtils.isEmpty(name))
            return this.reposiotry.findAll(pageable);
        else {
            name = "%" + name + "%";
            return this.reposiotry.findByRealnameLike(name, pageable);
        }
    }

    public Page<Map<String, Object>> findUserList(String userId, Integer page, Integer size, String id, String REALNAME, String COMPANYID, String WORKTYPE) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", userId).toString());
        ArrayList<String> arrayList = new ArrayList<>();
        String s = "";
        if (Integer.parseInt(jsonObject.get("ROLETYPE").toString()) == 1 || Integer.parseInt(jsonObject.get("ROLETYPE").toString()) == 2) {
            arrayList.add(jsonObject.get("DEPTID").toString());
            s += " AND DEPTID = ?" + arrayList.size();
        } else if (Integer.parseInt(jsonObject.get("ROLETYPE").toString()) == 3) {
            arrayList.add(jsonObject.get("COMPANYID").toString());
            s += " AND COMPANYID = ?" + arrayList.size();
        } else if (Integer.parseInt(jsonObject.get("ROLETYPE").toString()) == 4) {
            arrayList.add(jsonObject.get("GROUPID").toString());
            s += " AND GROUPID=?" + arrayList.size();
        }
        Pageable pageable = new PageRequest(page, size);
        if (!StringUtils.isEmpty(id)) {
            arrayList.add(id);
            s += " AND DEPTID = ?" + arrayList.size();
        }
        if (!StringUtils.isEmpty(REALNAME)) {
            arrayList.add("%" + REALNAME + "%");
            s += " AND REALNAME LIKE ?" + arrayList.size();
        }
        if (!StringUtils.isEmpty(COMPANYID)) {
            arrayList.add(COMPANYID);
            s += " AND COMPANYID = ?" + arrayList.size();
        }
        if (!StringUtils.isEmpty(WORKTYPE)) {
            arrayList.add(WORKTYPE);
            s += " AND WORKTYPE = ?" + arrayList.size();
        }
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
                "  t.DEPTNAME as classname,u.roleId,u.alleyway,u.COMPANYID,u.CLASSNAME as classid,u.GROUPID " +
                "  FROM rztsysuser u " +
                "  LEFT JOIN rztsysrole r ON u.roleId = r.id " +
                "  LEFT JOIN RZTSYSDEPARTMENT y ON u.DEPTID = y.ID " +
                "  LEFT JOIN RZTSYSDEPARTMENT t ON u.CLASSNAME = t.ID " +
                "  LEFT JOIN RZTSYSCOMPANY m ON m.ID = u.COMPANYID " +
                "WHERE USERDELETE = 1" + s + " order by u.CREATETIME desc ";
        return this.execSqlPage(pageable, sql, arrayList.toArray());
    }

    /**
     * 人员条件查询下拉框
     *
     * @return
     */
    public WebApiResponse userQuertDeptZero(String userId) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", userId).toString());
        if (Integer.parseInt(jsonObject.get("ROLETYPE").toString()) == 0) {
            String sql = " SELECT ID,DEPTNAME FROM RZTSYSDEPARTMENT WHERE DEPTSORT IS NOT NULL ORDER BY DEPTSORT ";
            try {
                return WebApiResponse.success(this.execSql(sql));
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("erro");
            }
        }
        return WebApiResponse.success("");
    }

    /**
     * 下拉框外协单位
     *
     * @return
     */
    public WebApiResponse companyPage(String userId) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", userId).toString());
        if (Integer.parseInt(jsonObject.get("ROLETYPE").toString()) == 0) {
            String sql = " SELECT COMPANYNAME,ID FROM RZTSYSCOMPANY ";
            try {
                return WebApiResponse.success(this.execSql(sql));
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("erro");
            }
        } else if (Integer.parseInt(jsonObject.get("ROLETYPE").toString()) == 2 || Integer.parseInt(jsonObject.get("ROLETYPE").toString()) == 1) {
            String sql = " SELECT COMPANYNAME,ID FROM RZTSYSCOMPANY WHERE ORGID LIKE ?1";
            Object deptid = jsonObject.get("DEPTID");
            try {
                return WebApiResponse.success(this.execSql(sql, "%" + deptid + "%"));
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("");
            }
        }
        return WebApiResponse.success("");
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
    public void logicUser(String id) {
        this.reposiotry.logicUsers(id);
        this.reposiotry.logicUser(id);
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
//        String AVATAR = null;
        if (StringUtils.isEmpty(user.getAvatar())) {
            String sql = " SELECT AVATAR FROM RZTSYSUSER where id = ?1 ";
            user.setAvatar(String.valueOf(this.execSql(sql, user.getId()).get(0).get("AVATAR")));
        } /*else {
            AVATAR = user.getAvatar();
        }*/
        try {
            user.setUserdelete(1);
            this.update(user, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        int age = user.getAge();
//        String certificate = user.getCertificate();
//        String deptid = user.getDeptid();
//        String phone = user.getPhone();
//        String realname = user.getRealname();
//        String serialnumber = user.getSerialnumber();
//        int userType = user.getUserType();
//        String username = user.getUsername();
//        int worktype = user.getWorktype();
//        int workyear = user.getWorkyear();
//        String classname = user.getClassName();
//        String companyid = user.getCompanyid();
//        String roleid = user.getRoleid();
//            this.reposiotry.updateUser(age, certificate, deptid, phone, realname, serialnumber, userType, username, worktype, workyear, AVATAR, classname, companyid, roleid, id);
        try {
            /**
             * 修改Redis人员信息
             */
            String sql1 = " SELECT * FROM USERINFO where id=?1 ";
            Map<String, Object> stringObjectMap = this.execSqlSingleResult(sql1, id);
            HashOperations hashOperations = redisTemplate.opsForHash();
            hashOperations.put("UserInformation", id, stringObjectMap);
            return WebApiResponse.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
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
        List<Map<String, Object>> maps = this.execSql(sql, arrayList.toArray());
        return maps;
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

    /**
     * 人员登录
     *
     * @param password
     * @param account
     * @param loginType
     * @return
     */
    public WebApiResponse userLogin(String password, String account, String loginType) {
        String auth = "SELECT USERID FROM RZTSYSUSERAUTH WHERE IDENTITYTYPE = ?1 AND PASSWORD =?2 and USERDELETE=1 ";
        String user = "SELECT * FROM RZTSYSUSER  WHERE id=?1 AND USERDELETE = 1 AND USERTYPE = ?2 ";
        String userAccout = "SELECT * FROM USERINFO where id=?1";
        String access_token = null;
        try {
            Map<String, Object> stringObjectMap = this.execSqlSingleResult(auth, account, password);
            if (!StringUtils.isEmpty(stringObjectMap.get("USERID"))) {
                List<Map<String, Object>> userid = this.execSql(user, String.valueOf(stringObjectMap.get("USERID")), loginType);
                if (userid.size() == 1) {
                    HashOperations hashOperations = redisTemplate.opsForHash();
                    Object roleid1 = userid.get(0).get("ROLEID");
                    Object rztsysdata = hashOperations.get("RZTSYSDATA", roleid1);
                    if (StringUtils.isEmpty(rztsysdata)) {
                        String sql = " SELECT * FROM  RZTSYSDATA WHERE ROLEID = ?1 ";
                        Map<String, Object> map2 = this.execSqlSingleResult(sql, roleid1);
                        hashOperations.put("RZTSYSDATA", roleid1, map2);
                    }
                    this.reposiotry.updateUserLOGINSTATUS(String.valueOf(stringObjectMap.get("USERID")));
                    Map userid1 = this.execSqlSingleResult(userAccout, String.valueOf(stringObjectMap.get("USERID")));

                    hashOperations.put("UserInformation", stringObjectMap.get("USERID"), userid1);
                    access_token = JwtHelper.createJWT(userid1,
                            tokenProp.getExpireTime()).getAccess_token();
                    hashOperations.put("USERTOKEN", userid1.get("ID"), access_token);
                    userid1.put("TOKEN", access_token);
                    Integer typee = Integer.valueOf(userid.get(0).get("WORKTYPE").toString());
                    String roleid2 = userid1.get("ROLEID").toString();
                    /**
                     606DE762BD183D21E0501AAC38EF5184 一级运检部
                     606DE762BD1A3D21E0501AAC38EF5184 一级反外力中心
                     606DE762BD213D21E0501AAC38EF5184 二级运检部
                     606DE762BD233D21E0501AAC38EF5184 二级反外力中心
                     */
                    if (Integer.parseInt(loginType) == 1) {
                        String LOOKTYPE = "0";
                        if (roleid2.equals("606DE762BD183D21E0501AAC38EF5184")) {
                            LOOKTYPE = "1_1";
                        } else if (roleid2.equals("606DE762BD1A3D21E0501AAC38EF5184")) {
                            LOOKTYPE = "1_2";
                        } else if (roleid2.equals("606DE762BD213D21E0501AAC38EF5184")) {
                            LOOKTYPE = "2_1";
                        } else if (roleid2.equals("606DE762BD233D21E0501AAC38EF5184")) {
                            LOOKTYPE = "2_2";
                        }
                        userid1.put("LOOKTYPE", LOOKTYPE);
                    }
                    if (Integer.parseInt(loginType) == 0) {
                        if (typee == 1) {
                            typee = 2;
                        } else if (typee == 2) {
                            typee = 1;
                        }
                        try {
                            //人员登陆时间添加
                            this.reposiotry.insRztuserLoginTypeTime(userid.get(0).get("ID").toString(), 1);
                            KHSX(String.valueOf(userid.get(0).get("ID")), typee);
                            removeLiXianRedis(String.valueOf(userid.get(0).get("ID")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    this.reposiotry.updateMonitorCheckEjUserLoginType(1, userid.get(0).get("ID").toString());
                    return WebApiResponse.success(userid1);
                }
            }
            return WebApiResponse.erro("erro");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return WebApiResponse.erro("erro");
        }
    }

    @Transactional
    public WebApiResponse userQuit(String id) {
        String userAccout = "SELECT * FROM USERINFO where id=?1";
        try {
            this.reposiotry.quitUserLOGINSTATUS(id);
            //人员登陆时间添加
            this.reposiotry.insRztuserLoginTypeTime(id, 0);
            this.reposiotry.updateMonitorCheckEjUserLoginType(0, id);
            Map<String, Object> stringObjectMap = this.execSqlSingleResult(userAccout, id);
            HashOperations hashOperations = redisTemplate.opsForHash();
            hashOperations.put("UserInformation", id, stringObjectMap);
            hashOperations.delete("USERTOKEN", stringObjectMap.get("ID"));
            return WebApiResponse.success("");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }


    /**
     * 人员登录，删除redis中的键
     */
    public void KHSX(String userId, Integer taskType) {
        String sql = "";
        if (taskType == 2) {
            sql = " SELECT kh.ID,d.ID as DEPTID,kh.PLAN_START_TIME,kh.PLAN_END_TIME, kh.TASK_NAME,kh.USER_ID FROM  KH_TASK kh   LEFT JOIN RZTSYSDEPARTMENT d " +
                    " ON kh.TDYW_ORG = d.DEPTNAME  WHERE trunc(kh.PLAN_START_TIME) = trunc(sysdate) AND kh.USER_ID =?1 AND kh.STATUS !=2 ";
        } else if (taskType == 1) {
            sql = "SELECT ID,TD_ORG as DEPTID,PLAN_START_TIME,TASK_NAME,CM_USER_ID,PLAN_END_TIME  " +
                    "FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND CM_USER_ID=?1 AND STAUTS !=2";
        } else if (taskType == 3) {
            sql = " SELECT t.ID,t.USER_ID,t.TASK_NAME,t.PLAN_START_TIME,t.PLAN_END_TIME,u.DEPTID FROM CHECK_LIVE_TASK t " +
                    "   LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID " +
                    "  WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND STATUS!=2 " +
                    " UNION ALL " +
                    " SELECT t.ID,t.USER_ID,t.TASK_NAME,t.PLAN_START_TIME,t.PLAN_END_TIME,u.DEPTID FROM CHECK_LIVE_TASKSB t " +
                    "      LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID " +
                    "      WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND STATUS!=2 " +
                    " UNION ALL  " +
                    " SELECT t.ID,t.USER_ID,t.TASK_NAME,d.PLAN_START_TIME,d.PLAN_END_TIME,u.DEPTID FROM CHECK_LIVE_TASKXS t " +
                    "      LEFT JOIN RZTSYSUSER u ON t.USER_ID=u.ID LEFT JOIN CHECK_LIVE_TASK_DETAILXS d ON d.TASK_ID=t.ID " +
                    "      WHERE trunc(t.CREATE_TIME)=trunc(sysdate) AND t.STATUS!=2";
        }

        List<Map<String, Object>> maps = execSql(sql, userId);
        //如果查询结果为0，证明这个人当天没有任务
        if (maps.size() == 0) {
            return;
        }
        for (Map<String, Object> map : maps) {
            //开始时间
            Date plan_start_time = (Date) map.get("PLAN_START_TIME");
            //结束时间
            Date plan_end_time = (Date) map.get("PLAN_END_TIME");
            try {
                Long startDate = plan_start_time.getTime();
                Long endDate = plan_end_time.getTime();
                Long currentDate = new Date().getTime();
                if (currentDate < endDate) {
                    //主要是删除定时拉取数据，存放在redis中的key
                    String key = "";
                    if (taskType == 2) {
                        key = "TWO+*+2+8+" + map.get("USER_ID") + "+" + map.get("DEPTID") + "+*";
                    } else if (taskType == 1) {
                        key = "TWO+*+1+2+" + map.get("CM_USER_ID") + "+" + map.get("DEPTID") + "+*";
                    } else if (taskType == 3) {
                        key = "TWO+*+3+13+" + map.get("USER_ID") + "+" + map.get("DEPTID") + "+*";
                    }
                    removeKey(key);
                }
                this.reposiotry.updateOnlineTime(userId, Long.parseLong(map.get("ID").toString()));
            } catch (Exception e) {

            }
        }
    }

    public void removeKey(String s) {
        //String s = "TWO+*+2+8+*";
        RedisConnection connection = null;
        try {
            connection = redisTemplate.getConnectionFactory().getConnection();
            connection.select(1);
            Set<byte[]> keys = connection.keys(s.getBytes());
            byte[][] ts = keys.toArray(new byte[][]{});
            if (ts.length > 0) {
                connection.del(ts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    public void removeLiXianRedis(String s) {
        RedisConnection connection = null;
        try {
            connection = redisTemplate.getConnectionFactory().getConnection();
            connection.select(5);
            byte[] bytes = "lixian".getBytes();
            connection.hDel(bytes,s.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    public WebApiResponse getAddressList(String userId, JSONObject jsonObject) {
        try {
            String userSql = "select u.*,d.deptname from rztsysuser u left join rztsysdepartment d on d.id= u.deptid where u.id='" + userId + "'";
            Map<String, Object> map = this.execSqlSingleResult(userSql);
            String sql ="";
            if (map.get("DEPTNAME").toString().equals("公司本部")) {
                 sql = "select u.*,d.deptname from rztsysuser u left join rztsysdepartment d on d.id= u.deptid where userdelete=1 order by realname desc";
            } else {
                 sql = "select u.*,d.deptname from rztsysuser u left join rztsysdepartment d on d.id= u.deptid where userdelete=1 and deptid= '" + map.get("DEPTID").toString() + "' order by realname";
            }
            List<Map<String, Object>> maps = this.execSql(sql);
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }
}