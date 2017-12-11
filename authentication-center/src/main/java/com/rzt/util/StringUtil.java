package com.rzt.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by 张虎成 on 2017/1/13.
 */
public class StringUtil {
    /**
     * 根据传入的JSONArray拼装sql in字符串
     * @param jsonArray
     * @return
     */
    public static String getSqlInStringByJsonArray(JSONArray jsonArray){
        String sql="";
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = (JSONObject) jsonArray.get(i);
            if (i == jsonArray.size() - 1) {
                sql += obj.getLong("id") + ")";
            } else {
                sql += obj.getLong("id") + ",";
            }
        }
        return sql;
    }

    /**
     * 根据传入的Long数组sql in字符串
     * @param roleIds
     * @return
     */
    public  static String getSqlInStringByLongArray(Long[] roleIds){
        String sql="";
        for (int i = 0; i < roleIds.length; i++) {
            if (i == roleIds.length - 1) {
                sql += roleIds[i] + ")";
            } else {
                sql +=roleIds[i]+ ",";
            }
        }
        return sql;
    }
}
