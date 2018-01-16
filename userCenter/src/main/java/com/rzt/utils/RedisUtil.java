package com.rzt.utils;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

/**
 * Created by Administrator on 2017/12/20.
 */
public class RedisUtil {
	/***
	* @Method expireKeyOfAnotherDb
	* @Description redis设置别的db的key  并通过参数中的日期失效key
	* @param [redisTemplate, index, key,value expireDate]
	* @return void
	* @date 2018/1/9 14:08
	* @author nwz
	*/
	public static void setAndExpireKeyOfAnotherDb(RedisTemplate redisTemplate, int index, String key,String value, Date expireDate) {
		RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
		connection.select(index);
		connection.set(key.getBytes(),value.getBytes());
		try {
			connection.pExpireAt(key.getBytes(), expireDate.getTime());
		} catch (Exception var3) {
			connection.pExpireAt(key.getBytes(), expireDate.getTime() / 1000L);
		}
	}

}
