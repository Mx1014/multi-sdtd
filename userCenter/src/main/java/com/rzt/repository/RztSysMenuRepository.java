/**
 * 文件名：RztSysMenuRepository
 * 版本信息：
 * 日期：2017/09/25 09:58:09
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.repository;

import com.rzt.entity.RztSysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类名称：RztSysMenuRepository
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/09/25 09:58:09
 * 修改人：张虎成
 * 修改时间：2017/09/25 09:58:09
 * 修改备注：
 */
@Repository
public interface RztSysMenuRepository extends JpaRepository<RztSysMenu, String> {

    //根据父节点的左右值查询子孙节点
    @Query(value = "SELECT " +
            "       node.id , " +
            "		node.menuName , " +
            "       node.lft, " +
            "       node.rgt, " +
            "       node.menuUrl , " +
            "       node.menuPid  " +
            "FROM RztSysMenu node, " +
            "       RztSysMenu parent " +
            "WHERE " +
            "        node.lft BETWEEN parent.lft AND parent.rgt " +
            "        AND node.lft > ?1 AND node.rgt <  ?2 " +
            "GROUP BY node.id,node.lft,node.menuName,node.rgt,node.menuUrl,node.menuPid " +
            "ORDER BY node.lft", nativeQuery = true
    )
    public List<Object[]> findMenuList(int lft, int rgt);

    //根据父节点id查询所有子节点
    public List<RztSysMenu> findByMenupid(String menuPid);

    //获取本节点的直系祖先（到根的路径）
    public List<RztSysMenu> findByLftLessThanAndRgtGreaterThan(int lft, int rgt);

    //根据节点id获取节点左值
    public RztSysMenu getLftById(String id);

    //根据节点id获取节点右值
    public RztSysMenu getRgtById(String id);

    @Query(value = "select id from RztSysMenu WHERE menuPid = '0'", nativeQuery = true)
    public String getRootId();

    //更新所有节点右值
    @Modifying
    @Query(value = "UPDATE RztSysMenu SET rgt = rgt + 2 WHERE rgt > ?1", nativeQuery = true)
    public void updateRgt(int rgt);

    //更新所有节点左值
    @Modifying
    @Query(value = "UPDATE RztSysMenu SET lft = lft + 2 WHERE lft > ?1 ", nativeQuery = true)
    public void updateLft(int lft);

    //更新所有节点左值
    @Modifying
    @Query(value = "UPDATE RztSysMenu SET rgt = rgt - ?2 WHERE rgt > ?1", nativeQuery = true)
    public void updateNodeRgt(int rgt, int width);

    //更新所有节点左值
    @Modifying
    @Query(value = "UPDATE RztSysMenu SET lft = lft -?2 WHERE lft > ?1", nativeQuery = true)
    public void updateNodeLft(int lft, int width);

    public void deleteByLftBetween(int lft, int rgt);

    /**
     * 菜单数据中间表
     *
     * @param menuid 菜单表ID
     * @param roleid 角色ID
     * @return
     */
    @Modifying
    @Query(value = "INSERT INTO RZTMENUPRIVILEGE (ID, MENUID, ROLEID) VALUES (sys_guid(),?1,?2)", nativeQuery = true)
    int insertRztmenuprivilege(String menuid, String roleid);

    /**
     * 添加按钮
     *
     * @param button      菜单ID
     * @param privilegeid 中间表ID
     * @return
     */
    @Modifying
    @Query(value = "INSERT INTO RZTSYSBUTTON (ID, MENUID, PRIVILEGEID) VALUES (sys_guid(),?1,?2)", nativeQuery = true)
    int insertRztsysbutton(String button, String privilegeid);

    /**
     * 删除中间表
     *
     * @param roleid
     * @param menuid
     * @return
     */
    @Modifying
    @Query(value = "DELETE FROM RZTMENUPRIVILEGE WHERE ROLEID=?1 AND MENUID=?2", nativeQuery = true)
    int deleteRztmenuprivilege(String roleid, String menuid);

    /**
     * 删除中间表按钮表ID
     *
     * @param privilegeid
     * @return
     */
    @Modifying
    @Query(value = "DELETE FROM RZTSYSBUTTON WHERE PRIVILEGEID=?1", nativeQuery = true)
    int deleteRztsysbuttonz(String privilegeid);

    /**
     * 删除按钮表ID
     *
     * @param privilegeid
     * @return
     */
    @Modifying
    @Query(value = "DELETE FROM RZTSYSBUTTON WHERE PRIVILEGEID=?1 AND MENUID=?2", nativeQuery = true)
    int deleteRztsysbutton(String privilegeid, String buttonid);

    /**
     * 添加数据权限表
     *
     * @param type   权限类型
     * @param roleid 角色ID
     * @return
     */
    @Modifying
    @Query(value = "INSERT INTO RZTSYSDATA (ID, TYPE, ROLEID) VALUES (sys_guid(), ?1, ?2)", nativeQuery = true)
    int insertRztsysdata(String type, String roleid);

    /**
     * 删除数据权限表
     *
     * @param roleid 角色ID
     * @return
     */
    @Modifying
    @Query(value = "DELETE FROM RZTSYSDATA WHERE ROLEID=?1", nativeQuery = true)
    int deleteRztsysdata(String roleid);

    /**
     * app添加
     *
     * @param menuid appID
     * @param roleid 人员ID
     * @return
     */
    @Modifying
    @Query(value = " INSERT INTO RZTMENUPRIVILEGE (ID, MENUID, ROLEID) VALUES (sys_guid(),?1,?2) ", nativeQuery = true)
    int insertApp(String menuid, String roleid);

    @Modifying
    @Query(value = " DELETE FROM RZTMENUPRIVILEGE WHERE MENUID=?1 AND ROLEID=?2 ", nativeQuery = true)
    int deleteApp(String menuid, String roleid);

    /**
     * 根据人员删除
     *
     * @param roleid 人员ID
     * @return
     */
    @Modifying
    @Query(value = "DELETE RZTSYSDATA WHERE ROLEID = ?1", nativeQuery = true)
    int deleteSysData(String roleid);

    /**
     * 添加角色数据权限
     *
     * @param type
     * @param roleid
     * @return
     */
    @Modifying
    @Query(value = "INSERT INTO RZTSYSDATA (ID, TYPE, ROLEID) VALUES (sys_guid(), ?1, ?2)", nativeQuery = true)
    int insertSysData(String type, String roleid);
}
