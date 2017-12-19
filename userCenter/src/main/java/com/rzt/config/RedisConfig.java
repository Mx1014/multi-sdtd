package com.rzt.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by Administrator on 2017/12/12.
 */
@Configuration
public class RedisConfig {

	@Value("${spring.redis.host}")
	private String redisHost;

	@Value("${spring.redis.port}")
	private int redisPort;

	@Bean
	@Scope("prototype")
	JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory jedis = new JedisConnectionFactory();
		jedis.setHostName(redisHost);
		jedis.setPort(redisPort);
		return jedis;
	}

	@Bean
	public RedisTemplate<String, Object> redisHashTemplate(){
		RedisTemplate<String,Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new FastJsonRedisSerializer<>(Object.class));
		return template;
	}
}
