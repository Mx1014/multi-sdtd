/**
 * 文件名：RztsyscompanyService
 * 版本信息：
 * 日期：2017/12/08 16:40:23
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.repository.RztsyscompanyRepository;
import com.rzt.entity.Rztsyscompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    public Page<Map<String, Object>> queryRztsyscompany(int page, int size) {
        Pageable pageable = new PageRequest(page, size);
        String sql = "SELECT " +
                "  c.ID, " +
                "  c.COMPANYNAME, " +
                "  c.CREATETIME, " +
                "  c.ORGID,c.UPDATETIME, " +
                "  wm_concat(e.FILENAME) AS FILENAME, " +
                "  wm_concat(e.FILETYPE) AS FILETYPE " +
                "FROM RZTSYSCOMPANY c LEFT JOIN RZTSYSCOMPANYFILE e ON c.ID = e.COMPANYID " +
                "GROUP BY c.ID,c.COMPANYNAME,c.CREATETIME,c.ORGID,c.UPDATETIME";
        return this.execSqlPage(pageable, sql);
    }

    @Transactional(rollbackFor = Exception.class)
    public int addRztsyscompany(String id, String filename, String filetype, String cmpanyname, String orgid) {
        int one = 1;
        int zero = 0;
        try {
            this.reposiotry.addRztsyscompany(id, cmpanyname, orgid);
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
            this.reposiotry.updateRztsyscompany(cmpanyname, orgid, id);
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
}