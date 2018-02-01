/**    
 * 文件名：CMLINEService           
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.alibaba.fastjson.JSON;
import com.rzt.entity.CMSETTING;
import com.rzt.repository.CMSETTINGRepository;
import com.rzt.util.WebApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：CMLINEService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
@Service
public class CMSETTINGService extends CurdService<CMSETTING,CMSETTINGRepository> {

    protected static Logger LOGGER = LoggerFactory.getLogger(CMSETTINGService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public WebApiResponse getLineInfo(String kv, String currentUserId) {
        List<String> list = new ArrayList<>();
        String sql = "select line_id id,line_name,SECTION,TD_ORG_NAME from cm_line_section where is_del=0 ";
        if(StringUtils.isNotEmpty(currentUserId)){
            Map<String, Object> map = userInfoFromRedis(currentUserId);
            Integer roletype = Integer.parseInt(map.get("ROLETYPE").toString());
            String deptid  = map.get("DEPTID").toString();
            switch (roletype) {
                case 0:
                    break;
                case 1:
                    list.add(deptid);
                    sql += " and td_org= ?" + list.size();
                    break;
                case 2:
                    list.add(deptid);
                    sql += " and td_org= ?" + list.size();
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

        if(kv!=null&&!"".equals(kv.trim())){
            list.add(kv);
            sql += " and v_level= ?" + list.size();
        }
        sql += " ORDER BY NLSSORT(line_name,'NLS_SORT = SCHINESE_PINYIN_M')";
        List<Map<String, Object>> maps = execSql(sql,list.toArray());
        return WebApiResponse.success(maps);
    }

    @Transactional
    public void saveOrUpdateSetting(List<CMSETTING> list) {
        for (CMSETTING cmsetting:list){
            if(cmsetting.getId()==null){
                cmsetting.setId(null);
            }
            reposiotry.save(cmsetting);
        }
    }

    public List<Map<String,Object>> listSetting(CMSETTING cmsetting) {
        List<String> list = new ArrayList<>();
        String sql = "select * from cm_setting where 1=1 ";
        if(StringUtils.isNotEmpty(cmsetting.getSettingKey())){
            list.add(cmsetting.getSettingKey());
            sql += " and setting_key=?";
        }
        if(StringUtils.isNotEmpty(cmsetting.getSettingType())){
            list.add(cmsetting.getSettingType());
            sql += " and setting_type=?";
        }
        if(StringUtils.isNotEmpty(cmsetting.getSettingModel())){
            list.add(cmsetting.getSettingModel());
            sql += " and setting_model=?";
        }
        return  execSql(sql, list.toArray());
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