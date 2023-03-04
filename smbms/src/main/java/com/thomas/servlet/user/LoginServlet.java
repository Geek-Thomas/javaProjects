package com.thomas.servlet.user;

import com.thomas.pojo.User;
import com.thomas.service.user.UserServiceImpl;
import com.thomas.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userCode = req.getParameter("userCode");
        String password = req.getParameter("userPassword");

        UserServiceImpl userService = new UserServiceImpl();
        User user = userService.login(userCode, password);

        if (user != null) {
            //将用户保存到Session
            req.getSession().setAttribute(Constants.USER_SESSION, user);
            //重定向到后台主页
            resp.sendRedirect("jsp/frame.jsp");
        } else {
            //通过请求转发携带参数
            req.setAttribute("error", "用户名或密码错误");
            //请求转发到登录页面
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
