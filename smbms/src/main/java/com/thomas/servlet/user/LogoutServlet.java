package com.thomas.servlet.user;

import com.thomas.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //注销直接移除Session中定义的变量
        Object userSession = req.getSession().getAttribute(Constants.USER_SESSION);
        if (userSession != null) {
            req.getSession().removeAttribute(Constants.USER_SESSION);
            //重定向到登录页面
            resp.sendRedirect("login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
