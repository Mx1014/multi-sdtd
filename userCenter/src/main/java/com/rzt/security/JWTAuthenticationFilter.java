package com.rzt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by 张虎成 on 2016/12/19.
 */
public class JWTAuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                filterConfig.getServletContext());

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ResultMsg resultMsg;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json; charset=utf-8");
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ObjectMapper mapper = new ObjectMapper();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String auth = httpRequest.getHeader("Authorization");
        httpRequest.getHeaderNames();
        if ((auth != null) && (auth.length() > 7)) {
            String HeadStr = auth.substring(0, 6).toLowerCase();
            if (HeadStr.compareTo("bearer") == 0) {

                auth = auth.substring(6, auth.length());
                try {
                    if (JwtHelper.parseJWT(auth) != null) {
                        chain.doFilter(request, response);
                        return;
                    }
                } catch (ExpiredJwtException e) {
                    resultMsg = new ResultMsg(ResultStatusCode.TOKEN_EXPIRES.getErrocode(), ResultStatusCode.TOKEN_EXPIRES.getErrmsg(), null);
                    httpResponse.getWriter().write(mapper.writeValueAsString(resultMsg));
                    return;
                } catch (Exception e) {
                    resultMsg = new ResultMsg(ResultStatusCode.INVALID_TOKEN.getErrocode(), ResultStatusCode.INVALID_TOKEN.getErrmsg(), null);
                    httpResponse.getWriter().write(mapper.writeValueAsString(resultMsg));
                    return;
                }

            }
        }
        resultMsg = new ResultMsg(ResultStatusCode.NO_TOKEN.getErrocode(), ResultStatusCode.NO_TOKEN.getErrmsg(), null);
        httpResponse.getWriter().write(mapper.writeValueAsString(resultMsg));
        return;
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
}
