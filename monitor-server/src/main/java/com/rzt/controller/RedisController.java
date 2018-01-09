package com.rzt.controller;

import com.rzt.service.RedisService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by huyuening on 2018/1/9.
 */
@RestController
@RequestMapping("redisController")
public class RedisController {

    @Autowired
    private RedisService redisService;

    /**
     * 修改redis定时时间 二级往一级推消息的定时时间
     *@Author hyn
     *@Method setFixTime
     *@Params [faixTime]
     *@Date 2018/1/9 9:45
     */
    @PostMapping("setFaixTime")
    public WebApiResponse setFixTime(Integer faixTime){

        redisService.setFaixTime(faixTime);
        return WebApiResponse.success("修改成功");
    }
    @GetMapping("aa")
    public void aa(){
        redisService.setex("aaa");
    }

}
