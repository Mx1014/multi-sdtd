/**
 * 文件名：ANOMALYMONITORINGController
 * 版本信息：
 * 日期：2017/12/31 16:25:17
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.rzt.entity.Anomalymonitoring;
import com.rzt.service.AnomalymonitoringService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名称：ANOMALYMONITORINGController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/31 16:25:17
 * 修改人：张虎成
 * 修改时间：2017/12/31 16:25:17
 * 修改备注：
 */
@RestController
@RequestMapping("ANOMALYMONITORING")
public class AnomalymonitoringController extends
        CurdController<Anomalymonitoring, AnomalymonitoringService> {

    @Autowired
    private AnomalymonitoringService service;

    /**
     * 查询所有
     *
     * @Author hyn
     * @Method XSGJ
     * @Params [type, page, size]
     * @Date 2018/1/1 11:09
     */
    @GetMapping("XSGJ")
    public Object XSGJ(Integer orgtype, Integer page, Integer size, String date, String orgid, String type) {
        try {
            return WebApiResponse.success(service.XSGJ(orgtype, page, size, date, orgid, type));
        } catch (Exception e) {
            return WebApiResponse.erro("查询失败" + e.getMessage());
        }
    }

    /**
     * 查询已处理
     *
     * @param type
     * @param page
     * @param size
     * @return
     */
    @GetMapping("XSGJC")
    public Object XSGJC(Integer orgtype, Integer page, Integer size, String date, String orgid, String type) {
        try {
            return WebApiResponse.success(service.XSGJC(orgtype, page, size, date, orgid, type));
        } catch (Exception e) {
            return WebApiResponse.erro("查询失败" + e.getMessage());
        }
    }

    @GetMapping("XSGJCZ")
    public Object XSGJCZ(Integer orgtype, Integer page, Integer size, String date, String orgid, String type) {
        try {
            return WebApiResponse.success(service.XSGJCZ(orgtype, page, size, date, orgid, type));
        } catch (Exception e) {
            return WebApiResponse.erro("查询失败" + e.getMessage());
        }
    }


    @GetMapping("khGj")
    public WebApiResponse khGj(Integer orgtype, Integer page, Integer size, String date, String orgid, String type) {
        return this.service.khGj(orgtype, page, size, date, orgid, type);
    }

    @GetMapping("khGjC")
    public WebApiResponse khGjC(Integer orgtype, Integer page, Integer size, String date, String orgid, String type) {
        return this.service.khGjC(orgtype, page, size, date, orgid, type);
    }

    @GetMapping("khGjCZ")
    public WebApiResponse khGjCZ(Integer orgtype, Integer page, Integer size, String date, String orgid, String type) {
        return this.service.khGjCZ(orgtype, page, size, date, orgid, type);
    }

    /**
     * 处理中
     *
     * @param orgtype     一级还是二级单位
     * @param explain
     * @param status
     * @param tasktype
     * @param anomalytype
     * @return
     */
    @PostMapping("anomalyIns")
    public WebApiResponse anomalyIns(String orgtype, String explain, Integer status, Integer tasktype, Integer anomalytype) {
        return this.service.anomalyIns(orgtype, explain, status, tasktype, anomalytype);
    }

    /**
     * 已完成处理
     *
     * @param orgtype
     * @param explain
     * @param status
     * @param tasktype
     * @param anomalytype
     * @return
     */
    @PostMapping("anomalyInsO")
    public WebApiResponse anomalyInsO(String orgtype, String explain, Integer status, Integer tasktype, Integer anomalytype, Integer assessment) {
        return this.service.anomalyInsO(orgtype, explain, status, tasktype, anomalytype, assessment);
    }
}