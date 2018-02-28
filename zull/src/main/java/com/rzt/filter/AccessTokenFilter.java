package com.rzt.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
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

    private final String LoginUEI = "/userCenter/RztSysUser/userLogin";
    private final String swagger = "/swagger-";
    private final String swaggerSource = "/webjars/";
    private final String swaggerSource2 = "/v2/";

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
        String tokenUser = request.getParameter("tokenUser");
        request.getparameter
        return null;
    }
}
