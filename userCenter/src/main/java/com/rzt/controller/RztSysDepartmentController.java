/**
 * 文件名：RztSysDepartmentController
 * 版本信息：
 * 日期：2017/10/10 10:26:33
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.rzt.entity.RztSysDepartment;
import com.rzt.service.RztSysDepartmentService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.annotations.Parameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 类名称：RztSysDepartmentController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/10/10 10:26:33
 * 修改人：张虎成
 * 修改时间：2017/10/10 10:26:33
 * 修改备注：
 *
 * @version 部门表
 */
@RestController
@RequestMapping("RztSysDepartment")
@Api(value = "ningweize")
public class RztSysDepartmentController extends
        CurdController<RztSysDepartment, RztSysDepartmentService> {

    //新增子节点
    @ApiOperation(value = "新增子节点", notes = "新增子节点")
    @PostMapping("addSonNode")
    public RztSysDepartment addSonNode(String nodeId, RztSysDepartment rztSysDepartment) {
        return this.service.addSonNode(nodeId, rztSysDepartment);
    }

    //新增子节点
    @GetMapping("findAll")
    public List<RztSysDepartment> findAll() {
        return this.service.findAll();
    }

    //新增同级节点
    @PostMapping(value = "addNode")
    @ApiOperation(value = "新增同级节点", notes = "新增同级节点")
    public RztSysDepartment addNode(@RequestParam(required = false) String id, @ModelAttribute RztSysDepartment rztSysDepartment) {
        return this.service.addNode(id, rztSysDepartment);
    }

    //删除节点
    @RequestMapping(value = "deleteNode/{id}", method = RequestMethod.DELETE)
    public void deleteNode(@PathVariable String id) {
        this.service.deleteNode(id);
    }

    //根据父节点的id查询子孙节点
    @RequestMapping(value = "findDeptListByPid", method = RequestMethod.GET)
    @ApiOperation(value = "根据父节点的id查询子孙节点", notes = "根据父节点的id查询子孙节点")
    public List<Map<String, Object>> findDeptListByPid(@RequestParam(required = false) String id) {
        if (StringUtils.isEmpty(id))
            id = this.service.getRootId();
        return this.service.findDeptListByPid(0, 0, id);
    }

    //根据父节点id查询所有子节点
    @RequestMapping(value = "findByDeptPid/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据父节点id查询所有子节点", notes = "根据父节点id查询所有子节点")
    public List<RztSysDepartment> findByDeptPid(@PathVariable("id") String menuPid) {
        return this.service.findByDeptPid(menuPid);
    }

    /**
     * 查询班组单位传ID
     *
     * @return
     */
    @GetMapping("departmentQuery")
    public WebApiResponse departmentQuery(String deptpid) {
        try {
            return WebApiResponse.success(this.service.departmentQuery(deptpid));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("[Data Request Failed]");
        }
    }

    /**
     * 修改单位班组名称
     *
     * @param deptname 单位名
     * @param id       单位ID
     * @return
     */
    @PatchMapping("updateByDeptName")
    public WebApiResponse updateByDeptName(String deptname, String id) {
        return this.service.updateByDeptName(deptname, id);
    }

    @GetMapping("queryOrgName")
    public WebApiResponse queryOrgName() {
        return this.service.queryOrgName();
    }
}