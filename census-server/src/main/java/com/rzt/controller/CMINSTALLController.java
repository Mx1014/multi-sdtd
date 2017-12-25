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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    private static ConcurrentHashMap<String, Integer> offLine_map = new ConcurrentHashMap<String, Integer>();
    private static ConcurrentHashMap<String, Integer> onLine_map = new ConcurrentHashMap<String, Integer>();
    private ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();

    @PostMapping("getCount")
    public Map<String, Integer> lineSize(String ID, Integer LOGINSTATUS, Integer WORKTYPE) {
        if (!StringUtils.isEmpty(LOGINSTATUS) && !StringUtils.isEmpty(WORKTYPE)) {
            //离线状态(0表示不在线  1表示在线)
            if (LOGINSTATUS == 0) {
                offLine_map.put(ID + "_" + WORKTYPE, 0);
            } else if (LOGINSTATUS == 1) {
                onLine_map.put(ID + "_" + WORKTYPE, 1);
            }
            map.put("offLine", offLine_map.size());
            map.put("onLine", onLine_map.size());
        }
        return map;
    }
}