/**    
 * 文件名：PermissionController
 * 版本信息：    
 * 日期：2017/01/11 11:39:43    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.util.WebApiResponse;
import com.rzt.entity.Resource;
import com.rzt.security.Audience;
import com.rzt.security.JwtHelper;
import com.rzt.security.ResultMsg;
import com.rzt.security.ResultStatusCode;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**      
 * 类名称：PermissionController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/01/11 11:39:43 
 * 修改人：张虎成    
 * 修改时间：2017/01/11 11:39:43    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("/authentication")
public class AuthenticationController{
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
	@RequestMapping(value = "/auth")
	public
	@ResponseBody
	WebApiResponse<ResultMsg> authentication(HttpServletRequest request) throws IOException, ServletException {
		ResultMsg resultMsg;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String auth = httpRequest.getHeader("access_token");
		httpRequest.getHeaderNames();
		String authUrl = httpRequest.getHeader("access_url");
		if ((auth != null) && (auth.length() > 7)) {
			String HeadStr = auth.substring(0, 6).toLowerCase();
			if (HeadStr.compareTo("bearer") == 0) {
				auth = auth.substring(6, auth.length());
				try {
					if (JwtHelper.parseJWT(auth) != null) {
						Audience audience = JwtHelper.parseJWT(auth);
						if (stringRedisTemplate.opsForValue().get(audience.getUser().getUsername() + audience.getUser().getPassword()) != null && stringRedisTemplate.opsForValue().get(audience.getUser().getUsername() + audience.getUser().getPassword()).equals(auth)) {
							if(redisTemplate.opsForValue().get(audience.getUser().getUsername() + audience.getUser().getPassword())!=null){
								List<Resource> rolePermission = (List)redisTemplate.opsForValue().get(audience.getUser().getUsername() + audience.getUser().getPassword());
								for (Resource obj : rolePermission) {
									Pattern p = Pattern.compile(obj.getUrl());
									Matcher m = p.matcher(authUrl);
									if (m.find()) {
										resultMsg = new ResultMsg(ResultStatusCode.REACT_TOKEN.getErrocode(), ResultStatusCode.REACT_TOKEN.getErrmsg(), null);
										return WebApiResponse.success(resultMsg);
									}
								}
							}
							resultMsg = new ResultMsg(ResultStatusCode.NO_AUTH.getErrocode(), ResultStatusCode.NO_AUTH.getErrmsg(), null);
							return WebApiResponse.success(resultMsg);
						} else {
							resultMsg = new ResultMsg(ResultStatusCode.TOKEN_EXPIRES.getErrocode(), ResultStatusCode.TOKEN_EXPIRES.getErrmsg(), null);
							return WebApiResponse.success(resultMsg);
						}
					}
				} catch (ExpiredJwtException e) {
					resultMsg = new ResultMsg(ResultStatusCode.TOKEN_EXPIRES.getErrocode(), ResultStatusCode.TOKEN_EXPIRES.getErrmsg(), null);
					return WebApiResponse.success(resultMsg);
				} catch (Exception e) {
					e.printStackTrace();
					resultMsg = new ResultMsg(ResultStatusCode.INVALID_TOKEN.getErrocode(), ResultStatusCode.INVALID_TOKEN.getErrmsg(), null);
					return WebApiResponse.success(resultMsg);
				}

			}
		}
		resultMsg = new ResultMsg(ResultStatusCode.NO_TOKEN.getErrocode(), ResultStatusCode.NO_TOKEN.getErrmsg(), null);
		return WebApiResponse.success(resultMsg);
	}
	
}
