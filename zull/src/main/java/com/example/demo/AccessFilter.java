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
        return null;
    }
}
