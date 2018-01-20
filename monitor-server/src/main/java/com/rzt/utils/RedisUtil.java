package com.rzt.utils;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.CheckResult;
import com.rzt.repository.CheckResultRepository;
import com.rzt.service.CurdService;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * 李成阳
 * 2018/1/19
 */
public class RedisUtil  extends CurdService<CheckResult, CheckResultRepository> {
    /**
     * 根据userId  获取到当前用户的角色id  工作流权限分化
     * @param redisTemplate
     * @param userId
     * @return
     */
    public  String findRoleIdByUserId(RedisTemplate redisTemplate,String userId) throws Exception {
        /**
         * -- 管理员角色(角色管理)        yjid            公司运检部
         -- 后台稽查角色(角色管理)        jkid            公司反外力监控中心
         --  管理员角色(通道运维单位)      sdyjid          属地运检部
         -- 后台稽查角色(通道运维单位)     sdid          属地反外力监控中心
         */
        try {
            Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", userId);
            JSONObject jsonObject1 = JSONObject.parseObject(userInformation1.toString());
            String roleid = (String) jsonObject1.get("ROLEID");
            ArrayList<String> strings = new ArrayList<>();
            strings.add(roleid);
            String sql = "SELECT ROLENAME" +
                    "    FROM RZTSYSROLE WHERE ID = ?"+strings.size();
            Map<String, Object> map = this.execSqlSingleResult(sql, strings);
            if(null != map ){
                String rolename = (String) map.get("ROLENAME");
                if(null != rolename && !"".equals(rolename)){
                    if("管理员角色(角色管理)".equals(rolename)){
                        return "yjid";
                    }else if("后台稽查角色(角色管理)".equals(rolename)){
                        return "jkid";
                    }else if("管理员角色(通道运维单位)".equals(rolename)){
                        return "sdyjid";
                    }else if("后台稽查角色(通道运维单位)".equals(rolename)){
                        return "sdid";
                    }else {
                        return "0";
                    }
                }else {
                    return "0";
                }
            }

        }catch (Exception e){
            return "0";
        }


        return "0";
    }


}
