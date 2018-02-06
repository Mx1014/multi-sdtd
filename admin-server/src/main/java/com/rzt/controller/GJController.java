package com.rzt.controller;

import com.rzt.entity.TimedTask;
import com.rzt.service.GJService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 李成阳
 * 2018/1/31
 * 管理app告警数据统计
 */
@RestController
@RequestMapping("/GJ")
public class GJController extends CurdController<TimedTask,GJService>  {
    @Autowired
    private GJService gjService;

    /**
     * 一级页面使用
     * 告警统计信息
     * @return
     */
    @GetMapping("/findGJSum")
    public WebApiResponse findGJ(){
        return gjService.GJ();
    }

    /**
     * 二级页面使用  按照部门分组 返回所有部门
     * @return
     */
    @GetMapping("/findGJTwo")
    public WebApiResponse findGJTwo(){
        return gjService.GJTwo();
    }

    /**
     * 三级页面使用 按照部门查询
     * @param deptId
     * @return
     */
    @GetMapping("/findGJThree")
    public WebApiResponse findGJThree(String deptId){
        return gjService.GJThree(deptId);
    }

}
