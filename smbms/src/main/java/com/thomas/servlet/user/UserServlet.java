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
     * @Description ??????service???????????????????????????????????????????????????????????????????????????
     * @Param req
     * @Param resp
     * @Return
     * @Since version-1.0
     */

    public void modifyPassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String newPassword = req.getParameter("newpassword");
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);

        //???????????????????????????
        if (o != null && !StringUtils.isNullOrEmpty(newPassword)) {
            UserServiceImpl userService = new UserServiceImpl();
            User user = (User) o;
            Integer id = user.getId();
            boolean flag = userService.modifyPassword(id, newPassword);
            //??????????????????
            if (flag) {
                req.setAttribute(Constants.MESSAGE, "???????????????????????????????????????????????????");
                req.getSession().removeAttribute(Constants.USER_SESSION);
            } else {
                req.setAttribute(Constants.MESSAGE, "??????????????????");
            }
        } else {
            req.setAttribute(Constants.MESSAGE, "?????????????????????");
        }
        req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
    }

    /**
     * @Author GeekThomas
     * @Date 2023/1/14 19:24
     * @Description ?????????????????????Session??????????????????
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
            //???????????????
            resultMap.put("result", "sessionerror");
        } else if (StringUtils.isNullOrEmpty(oldPassword)) {
            //??????????????????
            resultMap.put("result", "error");
        } else {
            String password = ((User) o).getUserPassword();
            if (oldPassword.equals(password)) {
                //??????????????????
                resultMap.put("result", "true");
            } else {
                //??????????????????
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
     * @Description ??????????????????
     * @Param req
     * @Param resp
     * @Return
     * @Since version-1.0
     */

    public void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
        //???????????????????????????
        String queryUserName = req.getParameter("queryname"); //??????????????????
        String temp = req.getParameter("queryUserRole"); //?????????????????????
        String pageIndex = req.getParameter("pageIndex"); //?????????????????????
        int queryUserRole = 0;

        //????????????????????????
        int pageSize = 5;
        int currentPageNo = 1; //????????????????????????

        //????????????????????????????????????
        if (queryUserName == null) {
            queryUserName = "";
        }

        if (temp != null && !"".equals(temp)) {
            queryUserRole = Integer.parseInt(temp); //?????????????????????????????????
        }

        if (pageIndex != null) {
            currentPageNo = Integer.parseInt(pageIndex);
        }

        UserServiceImpl userService = new UserServiceImpl();
        List<User> userList = null;

        //??????????????????
        int totalCount = userService.getUserCount(queryUserName, queryUserRole);

        PageSupport pageSupport = new PageSupport();
        pageSupport.setPageSize(pageSize);
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setTotalCount(totalCount);

        int totalPageCount = pageSupport.getTotalPageCount();

        //?????????????????????
        if (totalPageCount < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }

        //??????????????????
        userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);

        //??????????????????
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();

        //?????????????????????
        req.setAttribute("userList", userList);
        req.setAttribute("roleList", roleList);
        req.setAttribute("currentPageNo", currentPageNo);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("totalPageCount", totalPageCount);

        //????????????
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

        //????????????????????????????????????????????????
        //1.????????????
        if (genderTemp != null && !"".equals(genderTemp)) {
            gender = Integer.parseInt(genderTemp);
        }
        //2.????????????
        if (birthdayTemp != null && !"".equals(birthdayTemp)) {
            try {
                birthday = new SimpleDateFormat("yyyy-mm-dd").parse(birthdayTemp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //3.??????????????????
        if (userRoleTemp != null && !"".equals(userRoleTemp)) {
            userRole = Integer.parseInt(userRoleTemp);
        }

        //????????????User??????????????????
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setGender(gender);
        user.setBirthday(birthday);
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(userRole);
        //????????????????????????????????????
        user.setCreatedBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        //???????????????????????????????????????
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
                resultMap.put("delResult", "true"); //????????????
            } else {
                resultMap.put("delResult", "false"); //????????????
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
     * @Description ???????????????????????????????????????????????????
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
        //???????????????????????????
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
     * @Description ????????????
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

        //????????????
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

        //?????????????????????????????????????????????
        User user = new User();
        user.setId(uid);
        user.setUserName(userName);
        user.setGender(gender);
        user.setBirthday(birthday);
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(userRole);

        //??????????????????????????????
        user.setModifyBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        user.setModifyDate(new Date());

        //????????????
        UserServiceImpl userService = new UserServiceImpl();
        boolean flag = userService.modifyUser(user);
        if (flag) {
            resp.sendRedirect(req.getContextPath() + "/jsp/user.do?method=query");
        } else {
            req.getRequestDispatcher("usermodify.jsp").forward(req, resp);
        }
    }
}
