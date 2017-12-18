/**
 * 文件名：RztSysOperateController
 * 版本信息：
 * 日期：2017/10/12 10:25:31
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.rzt.controller.CurdController;
import com.rzt.entity.RztSysOperate;
import com.rzt.entity.RztSysOperate;
import com.rzt.service.RztSysOperateService;
import com.rzt.util.WebApiResponse;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类名称：RztSysOperateController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/10/12 10:25:31
 * 修改人：张虎成
 * 修改时间：2017/10/12 10:25:31
 * 修改备注：
 */
@RestController
@RequestMapping("RztSysOperate")
public class RztSysOperateController extends
        CurdController<RztSysOperate, RztSysOperateService> {
    /**
     * 查询操作表 提示前端是否选中
     * String menuid
     *
     * @return
     */
    @GetMapping("findAllRztSysOperate")
    public WebApiResponse findAllRztSysOperate(String menuid) {
        try {
            return WebApiResponse.success(this.service.findAllRztSysOperate(menuid));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据请求错误");
        }
    }

    /**
     * 添加菜单列表是否有权限增删改
     *
     * @param menuid    菜单id
     * @param operateid 操作id
     * @return
     */
    @PostMapping("insertRztmenuprivilege")
    public WebApiResponse insertRztmenuprivilege(String menuid, String operateid) {
        return this.service.insertRztmenuprivilege(menuid, operateid);
    }
}