/**
 * 文件名：RztSysRoleController
 * 版本信息：
 * 日期：2017/10/11 18:51:02
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.rzt.controller.CurdController;
import com.rzt.entity.RztSysRole;
import com.rzt.entity.RztSysUser;
import com.rzt.service.RztMenuPrivilegeService;
import com.rzt.service.RztSysRoleService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 类名称：RztSysRoleController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/10/11 18:51:02
 * 修改人：张虎成
 * 修改时间：2017/10/11 18:51:02
 * 修改备注：
 */
@RestController
@RequestMapping("RztSysRole")
public class RztSysRoleController extends
        CurdController<RztSysRole, RztSysRoleService> {
    @Autowired
    private RztMenuPrivilegeService privilegeService;

    @PostMapping("addRole")
    public WebApiResponse addRole(@ModelAttribute RztSysRole sysRole) {
        sysRole.setCreatetime(new Date());
        this.service.add(sysRole);
//		privilegeService.addAll(menuPrivilege);
        return WebApiResponse.success("创建成功！");
    }

    @PatchMapping("updateRole/{id}")
    public WebApiResponse updateRole(@PathVariable String id, @ModelAttribute RztSysRole sysRole) {
        try {
            RztSysRole role = this.service.findOne(id);
            role.setRoledesc(sysRole.getRoledesc());
            role.setRolename(sysRole.getRolename());
            this.service.update(role, id);
			/*privilegeService.deleteByroleId(sysRole.getId());
			privilegeService.addAll(menuPrivilege);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return WebApiResponse.success("更新成功！");
    }

    @GetMapping("findRoleList/{page}/{size}")
    public Page<RztSysRole> findRoleList(@PathVariable int page, @PathVariable int size, @RequestParam(required = false)
            String name) {
        Pageable pageable = new PageRequest(page, size);
        Page<RztSysRole> pageList = this.service.findByName(name, pageable);
        return pageList;
    }

    @GetMapping("findAllRole")
    @ResponseBody
    public List<RztSysRole> findAllRole() {
        return this.service.findAllRole();
    }

    @DeleteMapping("deleteRole/{id}")
    public WebApiResponse deleteRole(@PathVariable String id) {
        try {
            this.service.delete(id);
            privilegeService.deleteByroleId(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return WebApiResponse.success("删除成功！");
    }

    @GetMapping("roleDistribution")
    public WebApiResponse roleDistribution(String userId) {
        return this.service.roleDistribution(userId);
    }

}