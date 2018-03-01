package com.rzt.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;

/**
 * 李成阳
 * 2018/2/5
 * 路由过滤器
 */
public class AccessTokenFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenFilter.class);
    @Autowired
    private JedisPool redisTemplate;

    @Override
    //pre 证明是前置过滤器
    public String filterType() {
        return "pre";
    }

    @Override
    //执行顺序   数字越大 优先级越低
    public int filterOrder() {
        return 0;
    }

    @Override
    //为true时证明开启了过滤器
    public boolean shouldFilter() {
        return true;
    }
    //逻辑
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String tokenUserId = request.getParameter("tokenUserId");
        String token = request.getParameter("token");
        //没有token的放行 例如pc端接口 app登陆接口和图片会仙的接口
        if(StringUtils.isEmpty(tokenUserId) && StringUtils.isEmpty(token)) {
            return null;
        } else {
            Jedis resource = redisTemplate.getResource();
            try {
                String usertoken = resource.hget("USERTOKEN", tokenUserId);
                if(StringUtils.isEmpty(usertoken)) {
                    //空的 此时应该终止route 返回错误信息
                    ctx.setSendZuulResponse(false);
                    //异常
                    ctx.setResponseStatusCode(401);
                    // 返回错误内容
                    ctx.setResponseBody("{\"flag\":\"0\",\"success\":true}");
                    return null;
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                resource.close();
                return null;
            }
        }
    }
}
