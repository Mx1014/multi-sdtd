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
        boolean flag = false;
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        String requestURI = request.getRequestURI();
       try {

           if(LoginUEI.equals(requestURI)){
               //登录时
               ctx.setSendZuulResponse(true);
               //正常的状态码
               ctx.setResponseStatusCode(200);
               ctx.setResponseBody("{\"flag\":\"1\"}");// 返回错误内容
               //如果有下个过滤器  能够使用的参数
               ctx.set("isSuccess",true);
               return null;
           }
           LOGGER.info(String.format("send %s request to %s", request.getMethod(), request.getRequestURL().toString()));
           String token = request.getParameter("token");
           String tokenUserId = request.getParameter("tokenUserId");
           if(null != token  && !"".equals(token)){
               token = token.replace("\"","");
               //在redis中获取token 并比较
               Jedis resource = redisTemplate.getResource();
               String redisToken = resource.hget("USERTOKEN", tokenUserId);
               if(null != redisToken && !"".equals(redisToken)){
                   redisToken = redisToken.replace("\"","");
               }
               if(token.equals(redisToken)){
                   //存在时
                   flag = true;
               }
               resource.close();
           }
           if(flag){
               //属性存在时正常路由
               ctx.setSendZuulResponse(true);
               //正常的状态码
               ctx.setResponseStatusCode(200);
               ctx.setResponseBody("{\"flag\":\"1\"}");// 返回错误内容
               //如果有下个过滤器  能够使用的参数
               ctx.set("isSuccess",true);
               return null;

           } else{
               //属性不存在时取消路由
               ctx.setSendZuulResponse(false);
               //正常的状态码
               ctx.setResponseStatusCode(401);
               ctx.setResponseBody("{\"flag\":\"0\"}");// 返回错误内容
               //如果有下个过滤器  能够使用的参数
               ctx.set("isSuccess",false);
               return null;
           }

       }catch (Exception e){
           LOGGER.error("zull过滤器过滤失败 "+e.getMessage());
       }


        return null;

    }
}
