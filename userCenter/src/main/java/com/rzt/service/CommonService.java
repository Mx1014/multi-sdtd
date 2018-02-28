package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.repository.RztSysUserRepository;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CommonService extends CurdService<RztSysUser, RztSysUserRepository> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public List<Map<String, Object>> checkDepartment(String userId, Integer worktype) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", userId).toString());
        Integer roletype = Integer.valueOf(jsonObject.get("ROLETYPE").toString());
        if (roletype == 0) {
            String sql = " SELECT * FROM (SELECT " +
                    "  id       AS \"value\", " +
                    "  DEPTNAME AS \"label\", " +
                    "  DEPTPID, " +
                    "  ID, " +
                    "  ORGTYPE, " +
                    "  LASTNODE " +
                    "FROM RZTSYSDEPARTMENT " +
                    "START WITH ID = '402881e6603a31e701603a48b5240000' CONNECT BY PRIOR id = DEPTPID) WHERE ORGTYPE = 0 OR ORGTYPE = ?1 ";
            List<Map<String, Object>> list = this.execSql(sql, worktype);
            List list1 = treeOrgRztsysroleList(list, list.get(0).get("ID").toString());
            return list1;
        } else if (roletype == 1 || roletype == 2) {
            List list2 = new ArrayList();
            String deptid = String.valueOf(jsonObject.get("DEPTID"));
            String s = "";
            list2.add(deptid);
            if (!StringUtils.isEmpty(worktype)) {
                list2.add(worktype);
                s += " OR ORGTYPE = ?" + list2.size();
            }
            String sql = " SELECT * FROM (SELECT " +
                    "  id       AS \"value\", " +
                    "  DEPTNAME AS \"label\", " +
                    "  DEPTPID, " +
                    "  ID, " +
                    "  ORGTYPE, " +
                    "  LASTNODE " +
                    "FROM RZTSYSDEPARTMENT " +
                    "START WITH ID = ?1 CONNECT BY PRIOR id = DEPTPID) WHERE ORGTYPE = 0  " + s;
            List<Map<String, Object>> list = this.execSql(sql, list2.toArray());
            List list1 = treeOrgRztsysroleList(list, list.get(0).get("DEPTPID").toString());
            return list1;
        }
        return null;
    }

    public List<Map<String, Object>> checkDepartmentAll(String userId, Integer worktype) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", userId).toString());
        Integer roletype = Integer.valueOf(jsonObject.get("ROLETYPE").toString());
        if (roletype == 0) {
            String sql = " SELECT ID,DEPTNAME FROM RZTSYSDEPARTMENT WHERE DEPTSORT IS NOT NULL ORDER BY DEPTSORT ";
           /* String sql = " SELECT * FROM (SELECT " +
                    "  id       AS \"value\", " +
                    "  DEPTNAME AS \"label\", " +
                    "  DEPTPID, " +
                    "  ID, " +
                    "  ORGTYPE, " +
                    "  LASTNODE " +
                    "FROM RZTSYSDEPARTMENT " +
                    "START WITH ID = '402881e6603a31e701603a48b5240000' CONNECT BY PRIOR id = DEPTPID) WHERE ORGTYPE = 0 OR ORGTYPE = ?1 ";*/
            List<Map<String, Object>> list = this.execSql(sql);
//            List list1 = treeOrgRztsysroleList(list, list.get(0).get("ID").toString());
            return list;
        } else if (roletype == 1 || roletype == 2) {
            List list2 = new ArrayList();
            String deptid = String.valueOf(jsonObject.get("DEPTID"));
            String s = "";
            list2.add(deptid);
            if (!StringUtils.isEmpty(worktype)) {
//                list2.add(worktype);
                s += " OR ORGTYPE = ?" + list2.size();
            }
            String sql = " SELECT * FROM (SELECT " +
                    "  id       AS \"value\", " +
                    "  DEPTNAME AS \"label\", " +
                    "  DEPTPID, " +
                    "  ID, " +
                    "  ORGTYPE, " +
                    "  LASTNODE " +
                    "FROM RZTSYSDEPARTMENT " +
                    "START WITH ID = ?1 CONNECT BY PRIOR id = DEPTPID) ";
            List<Map<String, Object>> list = this.execSql(sql, list2.toArray());
            List list1 = treeOrgRztsysroleList(list, list.get(0).get("DEPTPID").toString());
            return list1;
        }
        return null;
    }

    public List treeOrgRztsysroleList(List<Map<String, Object>> orgList, String parentId) {
        List childOrg = new ArrayList<>();
        for (Map<String, Object> map : orgList) {
            String menuId = String.valueOf(map.get("ID"));
            String pid = String.valueOf(map.get("DEPTPID"));
            if (parentId.equals(pid)) {
                List c_node = treeOrgRztsysroleList(orgList, menuId);
                map.put("children", c_node);
                childOrg.add(map);
            }
        }
        return childOrg;
    }

    public WebApiResponse userJcCx(String classId, String userId, Integer worktype) {
        List list = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(userId)) {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", userId).toString());
            Integer roletype = Integer.valueOf(jsonObject.get("ROLETYPE").toString());
            if (roletype == 1 || roletype == 2) {
                list.add(String.valueOf(jsonObject.get("DEPTID")));
                s += (" AND DEPTID =?" + list.size());
            }
        }
        if (!StringUtils.isEmpty(classId)) {
            list.add(classId);
            s += " AND CLASSNAME = ?" + list.size();
        }
        if (!StringUtils.isEmpty(worktype)) {
            list.add(worktype);
            s += " AND WORKTYPE = ?" + list.size();
        }
        String sql = " SELECT * FROM RZTSYSUSER WHERE USERDELETE=1    " + s;
        return WebApiResponse.success(this.execSql(sql, list.toArray()));
    }
}
