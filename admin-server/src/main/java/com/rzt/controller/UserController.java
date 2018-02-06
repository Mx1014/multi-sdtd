package com.rzt.controller;

import com.rzt.entity.TimedTask;
import com.rzt.service.YhService;
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
public class UserController extends CurdController<TimedTask, UserService>  {
    @Autowired
    private UserService userService;
    /**
     * 首页使用
     * 查询在线人数 离线人数  总人数
     * @return
     */
    @GetMapping("/findUserSum")
    public WebApiResponse findUserSum(){
        return userService.findUser();
    }

    /**
     * 二级页面用户信息查询
     * 按照部门分组
     * @return
     */
    @GetMapping("/findUserInfoTwo")
    public WebApiResponse findUserInfoTwo(){
        return userService.findUserInfoTwo();
    }
    /**
     * 三级页面用户信息查询
     * 根据部门查询
     * @return
     */
    @GetMapping("/findUserInfoThree")
    public WebApiResponse findUserInfoThree(String deptId){
        return userService.findUserInfoThree(deptId);
    }
    /**
     * 四级页面  查看当前单位所有人 关联任务
     * @param deptId       部门
     * @param loginStatus  在线状态 0不在  1在
     * @return
     */
    @GetMapping("/findUserInfoUserAndTask")
    public WebApiResponse findUserInfoUserAndTask(Integer page,Integer size,String deptId,String loginStatus ){
        return userService.findUserAndTask(page,size,deptId,loginStatus);
    }

}
