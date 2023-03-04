package com.thomas.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @Author GeekThomas
 * @Date 2023/1/12 14:29
 * @Description 处理乱码过滤器
 * @Since version-1.0
 */

public class CharacterEncodingFilter implements Filter {
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("进入过滤器----------");
        servletRequest.setCharacterEncoding("utf-8");
        servletResponse.setCharacterEncoding("utf-8");

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {
    }
}
