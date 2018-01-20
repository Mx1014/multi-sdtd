package com.rzt.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;

/**
 *
 * 李成阳
 * 2018/1/19
 */
public class RedisUtil {
    /**
     * 根据userId  获取到当前用户的角色id  工作流权限分化
     * @param redisTemplate
     * @param userId
     * @return
     */
    public static String findRoleIdByUserId(RedisTemplate redisTemplate,String userId){
        Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", userId);
        JSONObject jsonObject1 = JSONObject.parseObject(userInformation1.toString());
        String roleid = (String) jsonObject1.get("ROLEID");
        return roleid;
    }


}
