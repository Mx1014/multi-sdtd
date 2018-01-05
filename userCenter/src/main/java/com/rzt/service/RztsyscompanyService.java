/**
 * 文件名：RztsyscompanyService
 * 版本信息：
 * 日期：2017/12/08 16:40:23
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.repository.RztsyscompanyRepository;
import com.rzt.entity.Rztsyscompany;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 类名称：RztsyscompanyService
 * 类描述：InnoDB free: 536576 kB
 * 创建人：张虎成
 * 创建时间：2017/12/08 16:40:23
 * 修改人：张虎成
 * 修改时间：2017/12/08 16:40:23
 * 修改备注：
 */
@Service
//@Transactional
public class RztsyscompanyService extends CurdService<Rztsyscompany, RztsyscompanyRepository> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Page<Map<String, Object>> queryRztsyscompany(int page, int size, String userId, String companyname, String orgid) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", userId).toString());
        Integer roletype = Integer.valueOf(jsonObject.get("ROLETYPE").toString());
        Pageable pageable = new PageRequest(page, size);
        List list = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(orgid)) {
            list.add("%" + orgid + "%");
            s += " AND c.ORGID like ?" + list.size();
        }
        if (!StringUtils.isEmpty(companyname)) {
            list.add("%" + companyname + "%");
            s += " AND c.COMPANYNAME like ?" + list.size();
        }
        if (roletype == 1 || roletype == 2) {
            list.add("%" + String.valueOf(jsonObject.get("DEPTID")) + "%");
            s += " AND c.COMPANYNAME like ?" + list.size();
        }
        String sql = "SELECT " +
                "  c.ID, " +
                "  c.COMPANYNAME, " +
                "  c.CREATETIME, " +
                "  c.ORGID,c.UPDATETIME, " +
                "  wm_concat(e.FILENAME) AS FILENAME, " +
                "  wm_concat(e.FILETYPE) AS FILETYPE,c.ORGNAME " +
                " FROM RZTSYSCOMPANY c LEFT JOIN RZTSYSCOMPANYFILE e ON c.ID = e.COMPANYID WHERE 1=1 " + s +
                " GROUP BY c.ID,c.COMPANYNAME,c.CREATETIME,c.ORGID,c.UPDATETIME,c.ORGNAME";
        return this.execSqlPage(pageable, sql);
    }

    @Transactional(rollbackFor = Exception.class)
    public int addRztsyscompany(String id, String filename, String filetype, String cmpanyname, String orgid) {
        int one = 1;
        int zero = 0;
        try {
            String str = "";
            String[] split = orgid.split(",");
            for (int i = 0; i < split.length; i++) {
                String sql = "SELECT DEPTNAME FROM RZTSYSDEPARTMENT WHERE id=?1";
                str += this.execSqlSingleResult(sql, split[i]).get("DEPTNAME") + ",";
            }
            this.reposiotry.addRztsyscompany(id, cmpanyname, orgid, str.substring(0, str.length() - 1));
            if (!StringUtils.isEmpty(filename)) {
                String[] filenam = filename.split(",");
                String[] filetyp = filetype.split(",");
                for (int i = 0; i < filetyp.length; i++) {
                    this.reposiotry.addpanyFile(UUID.randomUUID().toString().replaceAll("-", ""), id, filenam[i], filetyp[i]);
                }
            }
            return one;
        } catch (Exception e) {
            e.printStackTrace();
            return zero;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateRztsyscompany(String cmpanyname, String orgid, String id, String filetype, String filename) {
        int one = 1;
        int zero = 0;
        try {
            String str = "";
            String[] split = orgid.split(",");
            for (int i = 0; i < split.length; i++) {
                String sql = "SELECT DEPTNAME FROM RZTSYSDEPARTMENT WHERE id=?1";
                str += this.execSqlSingleResult(sql, split[i]).get("DEPTNAME") + ",";
            }
            this.reposiotry.updateRztsyscompany(cmpanyname, orgid, str.substring(0, str.length() - 1), id);
            if (!StringUtils.isEmpty(filename)) {
                this.reposiotry.deletePanyFile(id);
                String[] filenam = filename.split(",");
                String[] filetyp = filetype.split(",");
                for (int i = 0; i < filetyp.length; i++) {
                    this.reposiotry.addpanyFile(UUID.randomUUID().toString().replaceAll("-", ""), id, filenam[i], filetyp[i]);
                }
            }
            return one;
        } catch (Exception e) {
            e.printStackTrace();
            return zero;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int deleteRztsyscompany(String id) {
        int one = 1;
        int zero = 0;
        try {
            this.reposiotry.deletePanyFile(id);
            this.reposiotry.deleteRztsyscompany(id);
            return one;
        } catch (Exception e) {
            e.printStackTrace();
            return zero;
        }
    }

    /**
     * 公共的
     *
     * @return
     */
    public WebApiResponse queryCompanyname() {
        String sql = "SELECT ID,COMPANYNAME,ORGID FROM RZTSYSCOMPANY";
        try {
            return WebApiResponse.success(this.execSql(sql));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据请求错误");
        }
    }

    /**
     * 单位id 查外协权限
     *
     * @param id
     * @return
     */
    public WebApiResponse queryCompanynameById(String id) {
        String ids = "%" + id + "%";
        String sql = "SELECT ID,COMPANYNAME,ORGID FROM RZTSYSCOMPANY where orgid like ?1 ";
        try {
            return WebApiResponse.success(this.execSql(sql, ids));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("Erro");
        }
    }

    //    /**
//     * 修改查询外协
//     *
//     * @param id 外协ID
//     * @return
//     */
//    public WebApiResponse updateComQuery(String id) {
//        String sql = "SELECT " +
//                "  c.ID, " +
//                "  c.COMPANYNAME, " +
//                "  c.CREATETIME, " +
//                "  c.ORGID, " +
//                "  c.UPDATETIME, " +
//                "  c.ORGNAME " +  
//                "FROM RZTSYSCOMPANY c";
//        try {
//            return WebApiResponse.success(this.execSqlSingleResult(sql, id));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return WebApiResponse.erro("数据请求失败");
//        }
//    }
    public List<Map<String, Object>> exportXlsCompany() {
        String sql = "SELECT " +
                "  c.COMPANYNAME, " +
                "  c.CREATETIME, " +
                "  c.UPDATETIME, " +
                "  c.ORGNAME " +
                "FROM RZTSYSCOMPANY c";
        return this.execSql(sql);
    }
}