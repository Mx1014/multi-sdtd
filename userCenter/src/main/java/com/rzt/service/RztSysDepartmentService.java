/**
 * 文件名：RztSysDepartmentService
 * 版本信息：
 * 日期：2017/10/10 10:26:33
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.service.CurdService;
import com.rzt.entity.RztSysDepartment;
import com.rzt.repository.RztSysDepartmentRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import com.rzt.utils.DbUtil;
import com.rzt.utils.PageUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类名称：RztSysDepartmentService
 * 类描述：InnoDB free: 537600 kB
 * 创建人：张虎成
 * 创建时间：2017/10/10 10:26:33
 * 修改人：张虎成
 * 修改时间：2017/10/10 10:26:33
 * 修改备注：
 */
@Service

public class RztSysDepartmentService extends CurdService<RztSysDepartment, RztSysDepartmentRepository> {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired

    private RedisTemplate<String, Object> redisTemplate;

    //添加子节点
    @Transactional
    public RztSysDepartment addSonNode(String id, RztSysDepartment rztSysDepartment) {
        rztSysDepartment.setCreatetime(DateUtil.dateNow());
        int lft = this.reposiotry.getLftById(id).getLft();
        this.reposiotry.updateLft(lft);
        this.reposiotry.updateRgt(lft);
        rztSysDepartment.setLft(lft + 1);
        rztSysDepartment.setRgt(lft + 2);
        rztSysDepartment.setDeptpid(id);
        this.reposiotry.save(rztSysDepartment);
//        createtime, deptdesc, depticon, deptname, deptpid, lastnode, lft, rgt, roleid, id
//        this.reposiotry.insRztsysdepartment(rztSysDepartment.getCreatetime(), rztSysDepartment.getDeptdesc(), rztSysDepartment.getDepticon(), rztSysDepartment.getDeptname(), rztSysDepartment.getDeptpid(), rztSysDepartment.getLastnode(), rztSysDepartment.getLft(), rztSysDepartment.getRgt(), rztSysDepartment.getRoleid(), rztSysDepartment.getId());
        return rztSysDepartment;
    }

    public List<RztSysDepartment> findAll() {
        return this.reposiotry.findAll();
    }

    public String getRootId() {
        return this.reposiotry.getRootId();
    }

    //添加节点
    @Transactional
    public RztSysDepartment addNode(String id, RztSysDepartment rztSysDepartment) {
        rztSysDepartment.setCreatetime(DateUtil.dateNow());
        RztSysDepartment tongji = this.reposiotry.getRgtById(id);
        int rgt = tongji.getRgt();
        this.reposiotry.updateLft(rgt);
        this.reposiotry.updateRgt(rgt);
        rztSysDepartment.setLft(rgt + 1);
        rztSysDepartment.setRgt(rgt + 2);
        rztSysDepartment.setDeptpid(tongji.getDeptpid());
        rztSysDepartment.setLastnode(tongji.getLastnode());
        this.add(rztSysDepartment);
        return rztSysDepartment;
    }

    //根据父节点的左右值查询子孙节点
    public List<Map<String, Object>> findDeptListByPid(int page, int size, String id) {
        RztSysDepartment rztSysDepartment = this.reposiotry.getOne(id);
        StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT node.id ,node.deptName ,node.lft,node.rgt,node.deptPid,node.lastnode,node.ORGTYPE  ");
        buffer.append("FROM RztSysDepartment node,RztSysDepartment parent WHERE node.lft BETWEEN parent.lft AND parent.rgt ");
        buffer.append("AND node.lft > " + rztSysDepartment.getLft());
        buffer.append(" AND node.rgt <" + rztSysDepartment.getRgt() + " ");
        buffer.append("GROUP BY node.id,node.lft,node.deptName,node.rgt,node.deptPid,node.lastnode,node.ORGTYPE ");
        buffer.append("ORDER BY node.lft");
        if (size != 0) {
            buffer.append(PageUtil.getLimit(page, size));
        }
//		Query queryentityManager.createNativeQuery(buffer.toString());
        List<Map<String, Object>> list = DbUtil.list(entityManager, buffer.toString());
        return list;
    }

    /**
     * 根据ID查询子父节点
     *
     * @return
     */
    public List<Map<String, Object>> findDeptListByPidZiFu(String id) {
        String sql = "   SELECT *" +
                "  FROM RZTSYSDEPARTMENT" +
                "  START WITH ID = ?1" +
                "  CONNECT BY NOCYCLE PRIOR DEPTPID = ID AND DEPTPID!='0'" +
                "  UNION ALL" +
                "  SELECT *" +
                "  FROM RZTSYSDEPARTMENT" +
                "  START WITH DEPTPID = ?2" +
                "  CONNECT BY NOCYCLE PRIOR ID = DEPTPID AND DEPTPID!='0' ";
        return this.execSql(sql, id, id);
    }

    //根据父节点id查询所有子节点
    public List<RztSysDepartment> findByDeptPid(String menuPid) {
        return this.reposiotry.findByDeptpid(menuPid);
    }

    //获取本节点的直系祖先（到根的路径）
    public List<RztSysDepartment> findByLftLessThanAndRgtGreaterThan(String id) {
        RztSysDepartment rztSysDepartment = this.reposiotry.getOne(id);
        return this.reposiotry.findByLftLessThanAndRgtGreaterThan(rztSysDepartment.getLft(), rztSysDepartment.getRgt());
    }

    @Transactional
    public void deleteNode(String id) {
        RztSysDepartment rztSysDepartment = this.reposiotry.getOne(id);
        int width = rztSysDepartment.getRgt() - rztSysDepartment.getLft() + 1;
        this.reposiotry.deleteByLftBetween(rztSysDepartment.getLft(), rztSysDepartment.getRgt());
        this.reposiotry.updateNodergt(rztSysDepartment.getRgt(), width);
        this.reposiotry.updateNodelft(rztSysDepartment.getLft(), width);
    }

    /**
     * 查询单位队伍
     *
     * @return
     */
    public List<Map<String, Object>> departmentQuery(String deptpid) {
        String sql = "SELECT ID,DEPTNAME,LASTNODE FROM RZTSYSDEPARTMENT WHERE DEPTPID = ?1 ";
//        String sql = "SELECT ID,DEPTNAME,LASTNODE FROM RZTSYSDEPARTMENT WHERE DEPTPID = '402881e6603a69b801603a6ab1d70000'";
        return this.execSql(sql, deptpid);
    }

    /**
     * 修改单位班组名称
     *
     * @param deptname 单位名
     * @param id       单位ID
     * @return
     */
    @Transactional
    public WebApiResponse updateByDeptName(String deptname, String id) {
        try {
            return WebApiResponse.success(this.reposiotry.updateByDeptName(deptname, id));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据修改失败");
        }
    }

    /**
     * 查询通道运维单位
     *
     * @returnString userId
     */
    public WebApiResponse queryOrgName() {
//        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
//        JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", userId).toString());
//        Integer roletype = Integer.valueOf(jsonObject.get("ROLETYPE").toString());
//        String deptid = String.valueOf(jsonObject.get("DEPTID"));
//        if (roletype == 0) {
        String sql = "SELECT * FROM RZTSYSDEPARTMENT WHERE DEPTPID='402881e6603a69b801603a6ab1d70000' ";
//            try {
        return WebApiResponse.success(this.execSql(sql));
//            } catch (Exception e) {
//                e.printStackTrace();
//                return WebApiResponse.erro("数据查询失败");
//            }
//        } else if (roletype == 1 || roletype == 2) {
//            String sql = "SELECT * FROM RZTSYSDEPARTMENT WHERE DEPTPID=?1 ";
//            try {
//                return WebApiResponse.success(this.execSql(sql, deptid));
//            } catch (Exception e) {
//                e.printStackTrace();
//                return WebApiResponse.erro("数据查询失败");
//            }
//        }
//        return WebApiResponse.erro("");
    }
}