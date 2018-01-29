package com.rzt.controller;

import com.rzt.entity.WarningOneKey;
import com.rzt.service.WarningOneKeyservice;
import com.rzt.util.WebApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("WarningOneKey")
public class WarningOneKeyController  extends
        CurdController<WarningOneKey, WarningOneKeyservice> {

    /**
     * 一键告警展示未处理
     * currentUserId 当前登录用户id
     * deptId 条件查询
     * startDate 开始时间
     * endDate 结束时间
     */
    @GetMapping("oneKeyW")
    public WebApiResponse oneKeyW(String currentUserId,String deptId,String startDate,String endDate,Integer page, Integer size){
        try {
            return WebApiResponse.success(service.oneKeyW(currentUserId,deptId,startDate,endDate,page,size));
        }catch (Exception e){
            return WebApiResponse.erro("查询失败"+e.getMessage());
        }
    }

    /**
     * 一键告警展示处理中
     */
    @GetMapping("oneKeyZ")
    public WebApiResponse oneKeyZ(String currentUserId,String deptId,String startDate,String endDate,Integer page, Integer size){
        try {
            return WebApiResponse.success(service.oneKeyZ(currentUserId,deptId,startDate,endDate,page,size));
        }catch (Exception e){
            return WebApiResponse.erro("查询失败"+e.getMessage());
        }
    }

    /**
     * 一键告警展示已处理
     */
    @GetMapping("oneKeyY")
    public WebApiResponse oneKeyY(String currentUserId,String deptId,String startDate,String endDate,Integer page, Integer size){
        try {
            return WebApiResponse.success(service.oneKeyY(currentUserId,deptId,startDate,endDate,page,size));
        }catch (Exception e){
            return WebApiResponse.erro("查询失败"+e.getMessage());
        }
    }

    /**
     * 获取告警图片、音频、视频
     * @param taskId
     * @return
     */
    @GetMapping("GJPhoto")
    public WebApiResponse GJPhoto(Long taskId) {
        try {
            return WebApiResponse.success(service.GJPhoto(taskId));
        }catch (Exception e){
            return WebApiResponse.erro("查询失败"+e.getMessage());
        }
    }

    /**
     *
     * @param taskId
     * @return
     */
    @GetMapping("GJcl")
    public WebApiResponse GJcl(Long taskId,String checkInfo) {
        return WebApiResponse.success(service.GJcl(taskId,checkInfo));
    }
    @GetMapping("GJclc")
    public WebApiResponse GJclc(Long taskId,String checkInfo) {
        return WebApiResponse.success(service.GJclc(taskId,checkInfo));
    }




}
