package com.rzt.utils;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Created by Administrator on 2017/12/20.
 */
public class RedisUtil {

	/**
	 * @param key
	 * @return
	 */
	public static boolean exists(RedisTemplate redisTemplate,final String key) {
		return (boolean) redisTemplate.execute(new RedisCallback() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(key.getBytes());
			}
		});
	}
}
