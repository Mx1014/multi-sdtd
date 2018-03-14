package com.rzt.utils;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.CheckResult;
import com.rzt.repository.CheckResultRepository;
import com.rzt.service.CurdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * 李成阳
 * 2018/1/19
 */
@Component
public class RedisUtil  extends CurdService<CheckResult, CheckResultRepository> {
    /**
     * 根据userId  获取到当前用户的角色id  工作流权限分化
     * @param redisTemplate
     * @param userId
     * @return
     */
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public  String findRoleIdByUserId(String userId) {
        /**
         * -- 管理员角色(角色管理)        yjid            公司运检部
         -- 后台稽查角色(角色管理)        jkid            公司反外力监控中心
         --  管理员角色(通道运维单位)      sdyjid          属地运检部
         -- 后台稽查角色(通道运维单位)     sdid          属地反外力监控中心
         */
        try {
            Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", userId);

//            Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", userId);
            if (null != userInformation1) {
                JSONObject jsonObject2 = JSONObject.parseObject(userInformation1.toString());
                if (null != jsonObject2) {
                    String roleId = (String) jsonObject2.get("ROLEID");

                    if (null != roleId && !"".equals(roleId)) {
                        if ("606DE762BD183D21E0501AAC38EF5184".equals(roleId)) {
                            return "yjid";
                        } else if ("606DE762BD1A3D21E0501AAC38EF5184".equals(roleId)) {
                            return "jkid";
                        } else if ("606DE762BD213D21E0501AAC38EF5184".equals(roleId)) {
                            return "sdyjid";
                        } else if ("606DE762BD233D21E0501AAC38EF5184".equals(roleId)) {
                            return "sdid";
                        }
                    } else {
                        return null;
                    }
                }
            }


        } catch (Exception e) {

            return null;
        }

        return null;
    }




    /**
     * 根据用户id  获取当前通道公司汉字名
     * @param userId
     * @return
     */
   public  String findTDByUserId(String userId) {
        try {

            Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", userId);
            JSONObject jsonObject2 = JSONObject.parseObject(userInformation1.toString());

            /*resource = jedisPool.getResource();
            String userInformation = resource.hget("UserInformation", userId);
            JSONObject jsonObject1 = JSONObject.parseObject(userInformation);*/
            String DEPT = (String) jsonObject2.get("DEPT");
            if (null != DEPT && !"".equals(DEPT)) {
                return DEPT;
            }

        } catch (Exception e) {
            return null;

        }
        return null;

    }


    /**
     * 根据用户id  获取当前通道公司ID
     * @param userId
     * @return
     */
    public  String findTDIDByUserId(String userId) {
        //Jedis resource = null;
        try {
            Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", userId);
            JSONObject jsonObject2 = JSONObject.parseObject(userInformation1.toString());
            String DEPT = (String) jsonObject2.get("DEPTID");
            if (null != DEPT && !"".equals(DEPT)) {
                return DEPT;
            }

        } catch (Exception e) {
            return null;
        }
        return null;

    }

}
