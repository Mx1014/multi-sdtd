package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 张虎成 on 2017/5/23.
 */
public class AccessFilter extends ZuulFilter {
    @Value("${auth.url}")
    private String auth_url;
    @Autowired
    private RestTemplate restTemplate;
    @Override
    //filterType 过滤器类型 决定过滤器在请求的哪个周期中执行
    public String filterType() {
        return "pre";
    }

    @Override
    //过滤器的执行顺序
    public int filterOrder() {
        return 0;
    }

    @Override
    //判断该过滤器是否需要执行
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        HttpHeaders requestHeaders = new HttpHeaders();
        String accessToken = request.getHeader("Authorization");
        requestHeaders.add("Authorization", accessToken);
        HttpEntity<String> requestEntity = new HttpEntity<String>(accessToken,requestHeaders);
        String resultStr = this.restTemplate.exchange(auth_url, HttpMethod.POST,requestEntity, String.class).getBody();
        JSONObject resultObj = JSONObject.parseObject(resultStr);
        if(Boolean.parseBoolean(resultObj.get("success").toString())||request.getRequestURI().contains("login")||request.getRequestURI().contains("export")) {// 如果请求的参数不为空，且值为chhliu时，则通过
            ctx.setSendZuulResponse(true);// 对该请求进行路由
            ctx.setResponseStatusCode(200);
            ctx.set("isSuccess", true);// 设值，让下一个Filter看到上一个Filter的状态
            return null;
        }else{
            ctx.getResponse().setCharacterEncoding("utf-8");
            ctx.setSendZuulResponse(false);// 过滤该请求，不对其进行路由
            ctx.setResponseStatusCode(401);// 返回错误码
            ctx.setResponseBody(resultStr);// 返回错误内容
            ctx.set("isSuccess", false);
            return null;
        }

    }
}
