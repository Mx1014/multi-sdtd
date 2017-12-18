/**
 * 文件名：RztSysMenuService
 * 版本信息：
 * 日期：2017/09/25 09:58:09
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.service.CurdService;
import com.rzt.entity.RztSysMenu;
import com.rzt.repository.RztSysMenuRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DbUtil;
import com.rzt.utils.PageUtil;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * shiguodong
 */
@Service
@Transactional
public class RztSysMenuService extends CurdService<RztSysMenu, RztSysMenuRepository> {

    @PersistenceContext
    private EntityManager entityManager;

    //添加子节点
    public RztSysMenu addSonNode(String id, RztSysMenu rztSysMenu) {
        rztSysMenu.setCreatetime(new Date());
        int lft = this.reposiotry.getLftById(id).getLft();
        this.reposiotry.updateLft(lft);
        this.reposiotry.updateRgt(lft);
        rztSysMenu.setLft(lft + 1);
        rztSysMenu.setRgt(lft + 2);
        rztSysMenu.setMenupid(id);
        this.reposiotry.save(rztSysMenu);
        return rztSysMenu;
    }

    //添加节点
    public RztSysMenu addNode(String id, RztSysMenu rztSysMenu) {
        rztSysMenu.setCreatetime(new Date());
        RztSysMenu tongji = this.reposiotry.getRgtById(id);
        int rgt = tongji.getRgt();
        this.reposiotry.updateLft(rgt);
        this.reposiotry.updateRgt(rgt);
        rztSysMenu.setLft(rgt + 1);
        rztSysMenu.setRgt(rgt + 2);
        rztSysMenu.setMenupid(tongji.getMenupid());
        this.reposiotry.save(rztSysMenu);
        return rztSysMenu;
    }

    //根据父节点的左右值查询子孙节点
    public List<Map<String, Object>> findMenuListByPid(int page, int size, String id) {
        RztSysMenu rztSysMenu = this.reposiotry.findOne(id);
        StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT node.id ,node.menuName ,node.lft,node.rgt,node.menuUrl ,node.menuPid ");
        buffer.append("FROM RztSysMenu node,RztSysMenu parent WHERE node.lft BETWEEN parent.lft AND parent.rgt ");
        buffer.append("AND node.lft > " + rztSysMenu.getLft());
        buffer.append(" AND node.rgt <" + rztSysMenu.getRgt() + " ");
        buffer.append("GROUP BY node.id,node.lft,node.menuName,node.rgt,node.menuUrl,node.menuPid ");
        buffer.append("ORDER BY node.rgt");
        if (size != 0)
            buffer.append(PageUtil.getLimit(page, size));
//		Query queryentityManager.createNativeQuery(buffer.toString());
        List<Map<String, Object>> list = DbUtil.list(entityManager, buffer.toString());
        return list;
    }

    public String getRootId() {
        return this.reposiotry.getRootId();
    }

    //根据父节点id查询所有子节点
    public List<RztSysMenu> findByMenuPid(String menuPid) {
        return this.reposiotry.findByMenupid(menuPid);
    }

    //获取本节点的直系祖先（到根的路径）
    public List<RztSysMenu> findByLftLessThanAndRgtGreaterThan(String id) {
        RztSysMenu rztSysMenu = this.reposiotry.getOne(id);
        return this.reposiotry.findByLftLessThanAndRgtGreaterThan(rztSysMenu.getLft(), rztSysMenu.getRgt());
    }

    public void deleteNode(String id) {
        RztSysMenu rztSysMenu = this.reposiotry.getOne(id);
        int width = rztSysMenu.getRgt() - rztSysMenu.getLft() + 1;
        this.reposiotry.deleteByLftBetween(rztSysMenu.getLft(), rztSysMenu.getRgt());
        this.reposiotry.updateNodeRgt(rztSysMenu.getRgt(), width);
        this.reposiotry.updateNodeLft(rztSysMenu.getLft(), width);
    }

    public List<Map<String, Object>> findAllMenu() {
        return this.execSql(" SELECT * FROM RZTSYSMENU ");
    }

    /**
     * pc权限
     * Type 1PC 2App
     *
     * @return
     */
    public List<Map<String, Object>> queryMenuPc() {
        return this.execSql(" SELECT * FROM RZTSYSMENU WHERE TYPE = 1 AND MENUTYPE!=3 ");
    }

    /**
     * App权限
     * Type 1PC 2App
     *
     * @return
     */
    public List<Map<String, Object>> queryMenuApp() {
        return this.execSql(" SELECT * FROM RZTSYSMENU WHERE TYPE = 2 ");
    }

    /**
     * 按钮查询
     *
     * @param id 菜单表ID
     * @return
     */
    public WebApiResponse queryListMenu(String id) {
        String sql = "SELECT * FROM RZTSYSMENU WHERE TYPE = 1 AND MENUTYPE = 3 AND MENUPID = ?1 ";
        try {
            return WebApiResponse.success(this.execSql(sql, id));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据返回错误");
        }
    }

    public List<Map<String, Object>> treeQuery(String deptpid) {
        String sql = " select id as \"value\",DEPTNAME as \"label\",DEPTPID,ID from RZTSYSDEPARTMENT start with deptpid= ?1 CONNECT by prior id=  DEPTPID ";
        List<Map<String, Object>> list = this.execSql(sql, deptpid);
        List list1 = treeOrgList(list, list.get(0).get("ID").toString());
        return list1;

    }

    public List treeOrgList(List<Map<String, Object>> orgList, String parentId) {
        List childOrg = new ArrayList<>();
        for (Map<String, Object> map : orgList) {
            String menuId = String.valueOf(map.get("ID"));
            String pid = String.valueOf(map.get("DEPTPID"));
            if (parentId.equals(pid)) {
                List c_node = treeOrgList(orgList, menuId);
                map.put("children", c_node);
                childOrg.add(map);
            }
        }
        return childOrg;
    }


}
