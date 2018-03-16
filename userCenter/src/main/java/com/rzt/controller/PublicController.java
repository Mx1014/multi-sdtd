package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.RztSysUserService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/14.
 */
@RestController
@RequestMapping("publicController")
public class PublicController extends CurdController<RztSysUser, RztSysUserService> {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /**
     * app人员电话列表查询
     *
     * @param currentUserId 登陆人ID
     * @return
     */
    @GetMapping("appUserPhone")
    @ApiOperation(value = "app人员电话列表查询", notes = "app人员电话列表查询")
    public WebApiResponse appUserPhone(String currentUserId) {
        Object userInformation = redisTemplate.opsForHash().get("UserInformation", currentUserId);
        JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
        String deptid = (String) jsonObject.get("DEPTID");
        String sql = " SELECT ID,USERNAME,PHONE  FROM RZTSYSUSER WHERE DEPTID = ?1 ";
        try {
            List<Map<String, Object>> maps = this.service.execSql(sql, deptid);
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败");
        }
    }
}
