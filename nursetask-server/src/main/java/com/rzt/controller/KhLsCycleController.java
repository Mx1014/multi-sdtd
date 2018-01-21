package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.KhLsCycle;
import com.rzt.entity.KhYhHistory;
import com.rzt.service.KhLsCycleService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;


/**
 * Created by admin on 2018/1/20.
 */

@RestController
@RequestMapping("KhLsCycle")
public class KhLsCycleController extends
        CurdController<KhLsCycle, KhLsCycleService> {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @PostMapping("/saveLsCycle")
    public WebApiResponse saveLsCycle(String yhId){
        return this.service.saveLsCycle(yhId);
    }

    @GetMapping("/listLsNotDo")
    public WebApiResponse listLsNotDo(String taskName, String yworg, String startTime, String endTime, String userId, Pageable pageable, String yhjb){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        Object userInformation = hash.get("UserInformation", userId);
        JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());

        return this.service.listLsNotDo(taskName,yworg,startTime,endTime,jsonObject,pageable,yhjb);
    }

    @PostMapping("/paifaLsTask")
    public WebApiResponse paifaLsTask(String id, String tasks){
        return this.service.paifaLsTask(id, tasks);
    }

    @DeleteMapping("/deleteCycle/{id}")
    public WebApiResponse deleteCycle(@PathVariable("id")String id){
        return this.service.deleteCycle(id);
    }
}
