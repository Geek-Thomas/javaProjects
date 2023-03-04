package com.thomas.filter;

import com.thomas.util.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SysFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //将req和resp转成HttpServletRequest和HttpServletResponse
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Object userSession = request.getSession().getAttribute(Constants.USER_SESSION);
        //如果用户未登录或者已注销
        if (userSession == null) {
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }

        filterChain.doFilter(request, response);
    }

    public void destroy() {
    }
}
