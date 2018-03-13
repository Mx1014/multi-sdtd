package com.rzt.controller;

import com.rzt.entity.KHYHHISTORY;
import com.rzt.service.UserService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 李成阳
 * 2018/1/31
 * 管理app用户数据统计
 */
@RestController
@RequestMapping("/users")
public class UserController extends CurdController<KHYHHISTORY, UserService> {
    @Autowired
    private UserService userService;

    /**
     * 单位分组查询
     * 查询在线人数 离线人数  总人数
     *
     * @return
     */
    @GetMapping("/findUserInfoTwo")
    public WebApiResponse findUserSum(String tokenUserId) {
        return userService.findUser(tokenUserId);
    }

    /**
     * 首页统计
     *
     * @return
     */
    @GetMapping("/findUserSum")
    public WebApiResponse findUserInfoTwo(String tokenUserId) {
        return userService.findUserInfoOne(tokenUserId);
    }
    /**
     * 按照部门查询
     *
     * @return
     */
    @GetMapping("/findUserInByDept")
    public WebApiResponse findUserInByDept(String deptId) {
        return userService.findUserInByDept(deptId);
    }


    /**
     * 三级页面  查看当前单位所有人 关联任务
     * @param deptId      部门
     * @param loginStatus 在线状态 0不在  1在
     * @return
     */
    @GetMapping("/findUserInfoUserAndTask")
    public WebApiResponse findUserInfoUserAndTask(Integer page, Integer size, String deptId, String loginStatus,String condition) {
        return userService.findUserAndTask(page, size, deptId, loginStatus,condition);
    }

    /**
     * 四级页面 查询用户详细信息
     * @return
     */
    @GetMapping("/findUserInfo")
    public WebApiResponse findUserInfo(String userId) {
        return userService.findUserInfo(userId);
    }

    /**
     * 获取用户头像 根据userId
     * @param userId
     * @return
     */
    @GetMapping("/findUserPic")
    public WebApiResponse findUserPic(String userId){
        return userService.findUserPic(userId);
    }

    /**
     * 获取今日应执行任务总人数
     * @param deptId
     * @return
     */
    @GetMapping("/getDayUser")
    public WebApiResponse getDayUser(String deptId){
        return userService.getDayUser(deptId);
    }

}

