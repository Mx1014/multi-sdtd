/**
 * 文件名：RztSysMenuController
 * 版本信息：
 * 日期：2017/09/25 09:58:09
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.rzt.controller.CurdController;
import com.rzt.entity.RztSysMenu;
import com.rzt.service.RztSysMenuService;
import com.rzt.util.WebApiResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 类名称：RztSysMenuController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/09/25 09:58:09
 * 修改人：张虎成
 * 修改时间：2017/09/25 09:58:09
 * 修改备注：
 *
 * @version 菜单表
 */
@RestController
@RequestMapping("RztSysMenu")
public class RztSysMenuController extends
        CurdController<RztSysMenu, RztSysMenuService> {

    //新增子节点
    @ResponseBody
    @PostMapping("addSonNode")
    public RztSysMenu addSonNode(@RequestParam(required = false) String id, @ModelAttribute RztSysMenu rztSysMenu) {
        if (StringUtils.isEmpty(id))
            id = this.service.getRootId();
        return this.service.addSonNode(id, rztSysMenu);
    }

    //新增同级节点
    @ResponseBody
    @PostMapping("addNode")
    public RztSysMenu addNode(String id, @ModelAttribute RztSysMenu rztSysMenu) {
        return this.service.addNode(id, rztSysMenu);
    }

    //新增同级节点
    @ResponseBody
    @PatchMapping("updateNode/{id}")
    public WebApiResponse updateNode(@PathVariable String id, @ModelAttribute RztSysMenu rztSysMenu) {
        RztSysMenu menu = this.service.findOne(id);
        menu.setMenuname(rztSysMenu.getMenuname());
        menu.setMenuurl(rztSysMenu.getMenuurl());
        try {
            this.service.update(menu, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return WebApiResponse.success("更新成功！");
    }

    /**
     * 修改菜单
     *
     * @param menuname
     * @param id
     * @return
     */
    @PatchMapping("updateNodeById")
    public WebApiResponse updateNodeById(String menuname, String id) {
        return this.service.updateNodeById(menuname, id);
    }


    //删除节点
    @DeleteMapping("deleteNode")
    public void deleteNode(String id) {
        this.service.deleteNode(id);
    }

    //根据父节点的id查询子孙节点
    @ResponseBody
    @GetMapping("findMenuListByPid/{page}/{size}")
    public List<Map<String, Object>> findMenuListByPid(@PathVariable int page, @PathVariable int size,
                                                       @RequestParam(required = false) String id) {
        if (StringUtils.isEmpty(id))
            id = this.service.getRootId();
        return this.service.findMenuListByPid(page, size, id);
    }

    //根据父节点的id查询子孙节点
    @ResponseBody
    @GetMapping("findAllMenuByPid")
    public List<Map<String, Object>> findAllMenuByPid(@RequestParam(required = false) String id) {
        if (StringUtils.isEmpty(id))
            id = this.service.getRootId();
        return this.service.findMenuListByPid(0, 0, id);
    }

    @GetMapping("findAllMenu")
    public List<Map<String, Object>> findAllMenu() {
        return this.service.findAllMenu();
    }

    /**
     * pc 菜单列表
     *
     * @return
     */
    @GetMapping("queryMenuPc")
    public WebApiResponse queryMenuPc(String roleid) {
        try {
            return WebApiResponse.success(this.service.queryMenuPc(roleid));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据返回失败");
        }
    }

    /**
     * App 菜单列表
     *
     * @return
     */
    @GetMapping("queryMenuApp")
    public WebApiResponse queryMenuApp(String roleid) {
        try {
            return WebApiResponse.success(this.service.queryMenuApp(roleid));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据返回失败");
        }
    }

    /**
     * 按钮查询
     *
     * @param id 菜单表ID
     * @return
     */
    @GetMapping("queryListMenu")
    public WebApiResponse queryListMenu(String id, String roleid) {
        return this.service.queryListMenu(id, roleid);
    }

    /**
     * 公共选择任务
     *
     * @param id
     * @return
     */
//    @GetMapping("treeQuery")
//    public List<Map<String, Object>> treeQuery(String id, Integer orgtype) {
//        return this.service.treeQuery(id, orgtype);
//    }

    /**
     * 公共接口
     * @param id deptID
     * @param orgtype 专业类型
     * @return
     */
    @GetMapping("treeQuery")
    public List<Map<String, Object>> treeQuery(String id, Integer orgtype) {
        return this.service.treeQuery(id, orgtype);
    }

    /**
     * 菜单数据中间表
     *
     * @param menuid 菜单表ID
     * @param roleid 角色ID
     * @return
     */
    @PostMapping("insertRztmenuprivilege")
    public WebApiResponse insertRztmenuprivilege(String menuid, String roleid) {
        return this.service.insertRztmenuprivilege(menuid, roleid);
    }

    @PostMapping("insertRztsysbutton")
    public WebApiResponse insertRztsysbutton(String roleid, String menuid, String buttonid) {
        return this.service.insertRztsysbutton(roleid, menuid, buttonid);
    }

    @PostMapping("deleteRztmenuprivilege")
    public WebApiResponse deleteRztmenuprivilege(String roleid, String menuid) {
        return this.service.deleteRztmenuprivilege(roleid, menuid);
    }

    @PostMapping("deleteRztsysbutton")
    public WebApiResponse deleteRztsysbutton(String roleid, String menuid, String buttonid) {
        return this.service.deleteRztsysbutton(roleid, menuid, buttonid);
    }

    @PostMapping("insertApp")
    public WebApiResponse insertApp(String menuid, String roleid) {
        return this.service.insertApp(menuid, roleid);
    }

    @PostMapping("deleteApp")
    public WebApiResponse deleteApp(String menuid, String roleid) {
        return this.service.deleteApp(menuid, roleid);
    }

    @PostMapping("insertRztsysdata")
    public WebApiResponse insertRztsysdata(String type, String roleid) {
        return this.service.insertRztsysdata(type, roleid);
    }

    /**
     * 查询角色数据权限
     *
     * @param roleid
     * @return
     */
    @GetMapping("qDataQx")
    public WebApiResponse qDataQx(String roleid) {
        return this.service.qDataQx(roleid);
    }

    /**
     * 添加角色数据权限
     *
     * @param type
     * @param roleid
     * @return
     */
    @PostMapping("dataByDAndi")
    public WebApiResponse dataByDAndi(String type, String roleid) {
        return this.service.dataByDAndi(type, roleid);
    }
}