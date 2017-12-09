/**    
 * 文件名：PermissionController
 * 版本信息：    
 * 日期：2017/01/11 11:39:43    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.security.JwtHelper;
import com.rzt.security.ResultMsg;
import com.rzt.security.ResultStatusCode;
import com.rzt.util.WebApiResponse;

import io.jsonwebtoken.ExpiredJwtException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Map;

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
    private StringRedisTemplate stringRedisTemplate;
	@RequestMapping(value = "/auth")
	public
	@ResponseBody
	WebApiResponse<ResultMsg> authentication(HttpServletRequest request) throws IOException, ServletException {
		ResultMsg resultMsg;
		String access_token = request.getHeader("Authorization");
		if ((access_token != null) && (access_token.length() > 7)) {
			String HeadStr = access_token.substring(0, 6).toLowerCase();
			if (HeadStr.compareTo("bearer") == 0) {
				access_token = access_token.substring(6, access_token.length());
				try {
					if (JwtHelper.parseJWT(access_token) != null) {
						Map<String,String> audience = JwtHelper.parseJWT(access_token);
						String token = stringRedisTemplate.opsForValue().get(audience.get("username") + audience.get("password"));
						if ( token!= null && token.equals(access_token)) {
							resultMsg = new ResultMsg(ResultStatusCode.REACT_TOKEN.getErrocode(), ResultStatusCode.REACT_TOKEN.getErrmsg(), null);
							return WebApiResponse.success(resultMsg);
						} else {
							resultMsg = new ResultMsg(ResultStatusCode.INVALID_TOKEN.getErrocode(), ResultStatusCode.INVALID_TOKEN.getErrmsg(), null);
							return WebApiResponse.erro(resultMsg);
						}
					}
				} catch (ExpiredJwtException e) {
					resultMsg = new ResultMsg(ResultStatusCode.TOKEN_EXPIRES.getErrocode(), ResultStatusCode.TOKEN_EXPIRES.getErrmsg(), null);
					return WebApiResponse.erro(resultMsg);
				} catch (Exception e) {
					resultMsg = new ResultMsg(ResultStatusCode.INVALID_TOKEN.getErrocode(), ResultStatusCode.INVALID_TOKEN.getErrmsg(), null);
					return WebApiResponse.erro(resultMsg);
				}
			}
		}
		resultMsg = new ResultMsg(ResultStatusCode.NO_TOKEN.getErrocode(), ResultStatusCode.NO_TOKEN.getErrmsg(), null);
		return WebApiResponse.erro(resultMsg);
	}
	
}
