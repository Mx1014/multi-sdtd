package com.rzt.servlet;

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * 类名称：DruidStatViewServlet
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/09/21 17:14:13
 * 修改人：张虎成
 * 修改时间：2017/09/21 17:14:13
 * 修改备注：
 * @version
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/druid/*",
    initParams={
        @WebInitParam(name="allow",value="127.0.0.1"),// IP白名单 (没有配置或者为空，则允许所有访问)
        @WebInitParam(name="deny",value="192.168.16.111"),// IP黑名单 (存在共同时，deny优先于allow)
        @WebInitParam(name="loginUsername",value="admin"),// 用户名
        @WebInitParam(name="loginPassword",value="123456"),// 密码
        @WebInitParam(name="resetEnable",value="false")// 禁用HTML页面上的“Reset All”功能
    })
public class DruidStatViewServlet extends StatViewServlet {

}