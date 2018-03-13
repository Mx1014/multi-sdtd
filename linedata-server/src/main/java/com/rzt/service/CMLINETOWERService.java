/**    
 * 文件名：CMLINETOWERService           
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.alibaba.fastjson.JSON;
import com.rzt.entity.CMLINETOWER;
import com.rzt.repository.CMLINETOWERRepository;
import com.rzt.util.WebApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：CMLINETOWERService
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
@Service
public class CMLINETOWERService extends CurdService<CMLINETOWER,CMLINETOWERRepository> {

    protected static Logger LOGGER = LoggerFactory.getLogger(CMLINETOWERService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public WebApiResponse getLineTowerPosition(Pageable pageable, String tdOrg, String kv, String lineId,String currentUserId) {
        List<String> list = new ArrayList<>();
        Object[] objects = list.toArray();
        String sql = "select t.id,s.v_level,s.line_name,s.section,t.name tower_name,t.longitude,t.latitude from cm_line_section s " +
                " left join cm_line l on s.line_id=l.id " +
                " left join cm_line_tower lt on lt.line_id=l.id " +
                " left join cm_tower t on lt.tower_id=t.id  " +
                " where to_number(regexp_substr(name,'[0-9]*[0-9]',1)) between to_number(regexp_substr(s.start_sort,'[0-9]*[0-9]',1)) and to_number(regexp_substr(s.end_sort,'[0-9]*[0-9]',1)) " ;
        if(StringUtils.isNotEmpty(currentUserId)){
            Map<String, Object> map = userInfoFromRedis(currentUserId);
            Integer roletype = Integer.parseInt(map.get("ROLETYPE").toString());
            String deptid  = map.get("DEPTID").toString();
            switch (roletype) {
                case 0:
                    break;
                case 1:
                    tdOrg = deptid;
                    break;
                case 2:
                    tdOrg = deptid;
                    break;
                case 3:
                    //外协角色
                    break;
                case 4:
                    //班组角色
                    break;
                case 5:
                    //个人角色
                    break;
            }

        }

        if(tdOrg!=null&&!"".equals(tdOrg.trim())){
            list.add(tdOrg);
            sql += " and s.td_org=?" + list.size();
        }

        if(kv!=null&&!"".equals(kv.trim())){
            list.add(kv);
            sql += " and s.v_level=?" + list.size();
        }
        if(lineId!=null&&!"".equals(lineId.trim())){
            list.add(lineId);
            sql += " and s.line_id=?" + list.size();
        }
        sql += " order by lt.line_name,s.section,lt.sort ";
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql,list.toArray());
        return WebApiResponse.success(maps);
    }


    @Transactional
    public WebApiResponse updateTowerPosition(String id, String lon, String lat) {
        reposiotry.updatetowerPosition(Long.valueOf(id),lon,lat);
        return WebApiResponse.success("杆塔坐标更新成功!");
    }


    public Map<String, Object> userInfoFromRedis(String userId) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();

        Map<String,Object> jsonObject = null;
        Object userInformation = hashOperations.get("UserInformation", userId);
        if(userInformation == null) {
            String sql = "select * from userinfo where id = ?";
            try {
                jsonObject = this.execSqlSingleResult(sql, userId);
            } catch (Exception e) {
                LOGGER.error("currentUserId未获取到唯一数据!",e);
            }
            hashOperations.put("UserInformation",userId,jsonObject);
        } else {
            jsonObject = JSON.parseObject(userInformation.toString(),Map.class);
        }
        return jsonObject;
    }


}