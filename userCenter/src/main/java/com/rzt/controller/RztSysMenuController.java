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

    //删除节点
    @DeleteMapping("deleteNode/{id}")
    public void deleteNode(@PathVariable String id) {
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
    public WebApiResponse queryMenuPc() {
        try {
            return WebApiResponse.success(this.service.queryMenuPc());
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
    public WebApiResponse queryMenuApp() {
        try {
            return WebApiResponse.success(this.service.queryMenuApp());
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
    public WebApiResponse queryListMenu(String id) {
        return this.service.queryListMenu(id);
    }

    @GetMapping("treeQuery")
    public List<Map<String, Object>> treeQuery(String id) {
        return this.service.treeQuery(id);
    }
}