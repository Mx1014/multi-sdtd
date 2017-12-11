/**
 * 文件名：CMINSTALLController
 * 版本信息：
 * 日期：2017/12/11 15:58:59
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.rzt.entity.CMINSTALL;
import com.rzt.service.CMINSTALLService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 类名称：CMINSTALLController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/11 15:58:59
 * 修改人：张虎成
 * 修改时间：2017/12/11 15:58:59
 * 修改备注：
 */
@RestController
@EnableSwagger2
@RequestMapping("INSTALL")
@Api(value = "字典表")
public class CMINSTALLController extends
        CurdController<CMINSTALL, CMINSTALLService> {
    /**
     * 查询字典所有数据
     *
     * @return
     */
    @RequestMapping("cminstallQuery")
    @ApiOperation(value = "查询字典表数据", notes = "查询字典表数据")
    public WebApiResponse cminstallQuery() {
        try {
            return WebApiResponse.success(this.service.cminstallQuery());
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据请求失败");
        }
    }

    /**
     * 修改字典表数据
     *
     * @param id  字典表ID
     * @param key 数值
     * @return
     */
    @RequestMapping("cminstallUpdate")
    @ApiOperation(value = "修改字典表数据", notes = "修改字典表数据")
    public Object cminstallUpdate(Long id, int key) {
        try {
            int one = 1;
            int zero = 0;
            int cminstallUpdate = this.service.cminstallUpdate(key, id);
            if (cminstallUpdate == one) {
                return WebApiResponse.success("修改成功");
            } else if (cminstallUpdate == zero) {
                return WebApiResponse.erro("保存失败");
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("错误");
        }
    }
}