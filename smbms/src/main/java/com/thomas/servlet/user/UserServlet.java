package com.thomas.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;
import com.thomas.pojo.Role;
import com.thomas.pojo.User;
import com.thomas.service.role.RoleServiceImpl;
import com.thomas.service.user.UserServiceImpl;
import com.thomas.util.Constants;
import com.thomas.util.PageSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method != null && "savepwd".equals(method)) {
            this.modifyPassword(req, resp);
        } else if (method != null && "pwdmodify".equals(method)) {
            this.verifyPassword(req, resp);
        } else if (method != null && "query".equals(method)) {
            this.query(req, resp);
        } else if (method != null && "view".equals(method)) {
            this.view(req, resp);
        } else if (method != null && "add".equals(method)) {
            this.addUser(req, resp);
        } else if (method != null && "getrolelist".equals(method)) {
            this.getRoleList(req, resp);
        } else if (method != null && "ucexist".equals(method)) {
            this.userCodeExists(req, resp);
        } else if (method != null && "deluser".equals(method)) {
            this.deleteUser(req, resp);
        } else if (method != null && "modify".equals(method)) {
            this.getUserById(req, resp);
        } else if (method != null && "modifyexe".equals(method)) {
            this.modifyUser(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/14 19:20
     * @Description 调用service层的修改密码，完成密码修改，若修改成功，则重新登录
     * @Param req
     * @Param resp
     * @Return
     * @Since version-1.0
     */

    public void modifyPassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String newPassword = req.getParameter("newpassword");
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);

        //获取到用户和新密码
        if (o != null && !StringUtils.isNullOrEmpty(newPassword)) {
            UserServiceImpl userService = new UserServiceImpl();
            User user = (User) o;
            Integer id = user.getId();
            boolean flag = userService.modifyPassword(id, newPassword);
            //密码修改成功
            if (flag) {
                req.setAttribute(Constants.MESSAGE, "密码修改成功，请使用新密码重新登录");
                req.getSession().removeAttribute(Constants.USER_SESSION);
            } else {
                req.setAttribute(Constants.MESSAGE, "密码修改失败");
            }
        } else {
            req.setAttribute(Constants.MESSAGE, "新密码输入错误");
        }
        req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/14 19:24
     * @Description 验证旧密码，从Session中获取旧密码
     * @Param req
     * @Param resp
     * @Return
     * @Since version-1.0
     */

    public void verifyPassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldPassword = req.getParameter("oldpassword");

        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("method", "pwdmodify");
        if (o == null) {
            //用户未登录
            resultMap.put("result", "sessionerror");
        } else if (StringUtils.isNullOrEmpty(oldPassword)) {
            //密码输入为空
            resultMap.put("result", "error");
        } else {
            String password = ((User) o).getUserPassword();
            if (oldPassword.equals(password)) {
                //密码输入正确
                resultMap.put("result", "true");
            } else {
                //密码输入失败
                resultMap.put("result", "false");
            }
        }

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/19 21:58
     * @Description 查询用户列表
     * @Param req
     * @Param resp
     * @Return
     * @Since version-1.0
     */

    public void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
        //获取前端页面的参数
        String queryUserName = req.getParameter("queryname"); //查询的用户名
        String temp = req.getParameter("queryUserRole"); //查询的用户角色
        String pageIndex = req.getParameter("pageIndex"); //查询的当前页面
        int queryUserRole = 0;

        //与页面有关的参数
        int pageSize = 5;
        int currentPageNo = 1; //默认从第一页开始

        //对前端获取的参数进行处理
        if (queryUserName == null) {
            queryUserName = "";
        }

        if (temp != null && !"".equals(temp)) {
            queryUserRole = Integer.parseInt(temp); //使用前端查询的参数赋值
        }

        if (pageIndex != null) {
            currentPageNo = Integer.parseInt(pageIndex);
        }

        UserServiceImpl userService = new UserServiceImpl();
        List<User> userList = null;

        //查询用户总数
        int totalCount = userService.getUserCount(queryUserName, queryUserRole);

        PageSupport pageSupport = new PageSupport();
        pageSupport.setPageSize(pageSize);
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setTotalCount(totalCount);

        int totalPageCount = pageSupport.getTotalPageCount();

        //控制首页和尾页
        if (totalPageCount < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }

        //查询用户列表
        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);

        //查询角色列表
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();

        //向前端传递参数
        req.setAttribute("userList", userList);
        req.setAttribute("roleList", roleList);
        req.setAttribute("currentPageNo", currentPageNo);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("totalPageCount", totalPageCount);

        //请求转发
        req.getRequestDispatcher("userlist.jsp").forward(req, resp);
    }

    public void view(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = req.getParameter("uid");
        int userId = 0;

        if (!StringUtils.isNullOrEmpty(uid)) {
            userId = Integer.parseInt(uid);
        }

        UserServiceImpl userService = new UserServiceImpl();
        User user = userService.getUserById(userId);
        System.out.println(user.getUserName());

        if (user != null) {
            req.setAttribute("user", user);
            req.getRequestDispatcher("userview.jsp").forward(req, resp);
        }
    }

    public void addUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        String genderTemp = req.getParameter("gender");
        String birthdayTemp = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRoleTemp = req.getParameter("userRole");
        int gender = 0;
        Date birthday = null;
        int userRole = 0;

        //根据在数据库中的内容进行格式转换
        //1.转换性别
        if (genderTemp != null && !"".equals(genderTemp)) {
            gender = Integer.parseInt(genderTemp);
        }
        //2.转换日期
        if (birthdayTemp != null && !"".equals(birthdayTemp)) {
            try {
                birthday = new SimpleDateFormat("yyyy-mm-dd").parse(birthdayTemp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //3.转换用户角色
        if (userRoleTemp != null && !"".equals(userRoleTemp)) {
            userRole = Integer.parseInt(userRoleTemp);
        }

        //新建一个User，并设置属性
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setGender(gender);
        user.setBirthday(birthday);
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(userRole);
        //设置创建者，就是当前用户
        user.setCreatedBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        //设置创建日期，使用当前时间
        user.setCreationDate(new Date());

        UserServiceImpl userService = new UserServiceImpl();
        boolean flag = userService.addUser(user);
        if (flag) {
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("useradd.jsp").forward(req, resp);
        }
    }

    public void getRoleList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Role> roleList = null;
        RoleServiceImpl roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        System.out.println(req.getRequestURL());

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(roleList));
        writer.flush();
        writer.close();
    }

    private void userCodeExists(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userCode = req.getParameter("userCode");
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("method", "ucexist");
        UserServiceImpl userService = new UserServiceImpl();
        User user = userService.selectUserCodeExist(userCode);
        if (StringUtils.isNullOrEmpty(userCode)) {
            resultMap.put("userCode", "exist");
        } else {
            if (user != null) {
                resultMap.put("userCode", "exist");
            } else {
                resultMap.put("userCode", "nonExist");
            }
        }

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }

    public void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = req.getParameter("uid");
        int userId = 0;
        if (!StringUtils.isNullOrEmpty(uid)) {
            userId = Integer.parseInt(uid);
        }

        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("method", "deluser");
        if (userId <= 0) {
            resultMap.put("delResult", "notexist");
        } else {
            UserServiceImpl userService = new UserServiceImpl();
            boolean flag = userService.deleteUserById(userId);
            if (flag) {
                resultMap.put("delResult", "true"); //删除成功
            } else {
                resultMap.put("delResult", "false"); //删除失败
            }
        }

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/23 23:29
     * @Description 在对用户进行修改时，需要先获得用户
     * @Param req
     * @Param resp
     * @Return
     * @Since version-1.0
     */

    public void getUserById(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = req.getParameter("uid");
        int userId = 0;
        if (!StringUtils.isNullOrEmpty(uid)) {
            userId = Integer.parseInt(uid);
        }
        //获取需要修改的用户
        UserServiceImpl userService = new UserServiceImpl();
        User modifyUser = userService.getUserById(userId);
        if (modifyUser != null) {
            req.setAttribute("user", modifyUser);
            req.getRequestDispatcher("usermodify.jsp").forward(req, resp);
        }

    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/23 22:53
     * @Description 修改用户
     * @Param req
     * @Param resp
     * @Return
     * @Since version-1.0
     */

    public void modifyUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("uid");
        String userName = req.getParameter("userName");
        String genderTemp = req.getParameter("gender");
        String birthdayTemp = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRoleTemp = req.getParameter("userRole");

        int uid = 0;
        int gender = 0;
        Date birthday = null;
        int userRole = 0;

        //转换格式
        if (!StringUtils.isNullOrEmpty(userId)) {
            uid = Integer.parseInt(userId);
        }

        if (!StringUtils.isNullOrEmpty(genderTemp)) {
            gender = Integer.parseInt(genderTemp);
        }

        if (!StringUtils.isNullOrEmpty(birthdayTemp)) {
            try {
                birthday = new SimpleDateFormat("yyyy-mm-dd").parse(birthdayTemp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (!StringUtils.isNullOrEmpty(userRoleTemp)) {
            userRole = Integer.parseInt(userRoleTemp);
        }

        //使用前端传递的参数，创建新用户
        User user = new User();
        user.setId(uid);
        user.setUserName(userName);
        user.setGender(gender);
        user.setBirthday(birthday);
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(userRole);

        //设置修改人和修改信息
        user.setModifyBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());

        //提交请求
        UserServiceImpl userService = new UserServiceImpl();
        boolean flag = userService.modifyUser(user);
        if (flag) {
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("usermodify.jsp").forward(req, resp);
        }
    }
}
