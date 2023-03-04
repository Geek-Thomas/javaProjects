package com.thomas.servlet.provider;

import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;
import com.thomas.pojo.Provider;
import com.thomas.pojo.User;
import com.thomas.service.provider.ProviderServiceImpl;
import com.thomas.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProviderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method != null && "query".equals(method)) {
            this.query(req, resp); //查询供应商
        } else if (method != null && "add".equals(method)) {
            this.addProvider(req, resp); //添加供应商
        } else if (method != null && "delprovider".equals(method)) {
            this.delProvider(req, resp);
        } else if (method != null && "modify".equals(method)) {
            this.getProviderById(req, resp);
        } else if (method != null && "modifysave".equals(method)) {
            this.modifyProvider(req, resp);
        } else if (method != null && "view".equals((method))) {
            this.view(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从前端获取参数：proCode和proName(字符串，无需进一步处理)
        String proCode = req.getParameter("queryProCode");
        String proName = req.getParameter("queryProName");

        //调用业务层
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        List<Provider> providerList = providerService.getProviderList(proName, proCode);

        req.setAttribute("providerList", providerList);

        //请求转发
        req.getRequestDispatcher("providerlist.jsp").forward(req, resp);
    }

    public void addProvider(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取前端的参数
        String proCode = req.getParameter("proCode");
        String proName = req.getParameter("proName");
        String proContact = req.getParameter("proContact");
        String proPhone = req.getParameter("proPhone");
        String proAddress = req.getParameter("proAddress");
        String proFax = req.getParameter("proFax");
        String proDesc = req.getParameter("proDesc");

        //新建一个供应商
        Provider provider = new Provider();
        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProAddress(proAddress);
        provider.setProFax(proFax);
        provider.setProDesc(proDesc);

        //增加创建人和创建时间
        provider.setCreationDate(new Date());
        provider.setCreatedBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());

        ProviderServiceImpl providerService = new ProviderServiceImpl();
        boolean flag = providerService.addProvider(provider);

        if (flag) {
            resp.sendRedirect(req.getContextPath() + "/jsp/provider.do?method=query");
        } else {
            req.getRequestDispatcher("provideradd.jsp").forward(req, resp);
        }
    }

    public void delProvider(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取前端传递的参数
        String proid = req.getParameter("proid");
        int proId = 0;

        if (!StringUtils.isNullOrEmpty(proid)) {
            proId = Integer.parseInt(proid);
        }

        Map<String, String> resultMap = new HashMap<String, String>();

        if (proId <= 0) {
            resultMap.put("delResult", "nonexist"); //供应商不存在
        } else {
            ProviderServiceImpl providerService = new ProviderServiceImpl();
            boolean flag = providerService.delProvider(proId);
            if (flag) {
                resultMap.put("delResult", "true"); //删除成功
            } else {
                resultMap.put("delResult", "false"); // 删除失败
            }
        }

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }

    public void getProviderById(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String proid = req.getParameter("proid");
        int proId = 0;

        if (!StringUtils.isNullOrEmpty(proid)) {
            proId = Integer.parseInt(proid);
        }

        ProviderServiceImpl providerService = new ProviderServiceImpl();
        Provider provider = providerService.getProviderById(proId);

        if (provider != null) {
            req.setAttribute("provider", provider);
            req.getRequestDispatcher("providermodify.jsp").forward(req, resp);
        }
    }

    public void modifyProvider(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取前端参数
        String proId = req.getParameter("id");
        String proCode = req.getParameter("proCode");
        String proName = req.getParameter("proName");
        String proContact = req.getParameter("proContact");
        String proPhone = req.getParameter("proPhone");
        String proAddress = req.getParameter("proAddress");
        String proFax = req.getParameter("proFax");
        String proDesc = req.getParameter("proDesc");

        int id = 0;

        if (!StringUtils.isNullOrEmpty(proId)) {
            id = Integer.parseInt(proId);
        }
        //创建一个新的用户
        Provider provider = new Provider();
        provider.setId(id);
        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProAddress(proAddress);
        provider.setProFax(proFax);
        provider.setProDesc(proDesc);

        //增加修改用户和修改时间
        provider.setModifyBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        provider.setModifyDate(new Date());

        ProviderServiceImpl providerService = new ProviderServiceImpl();
        boolean flag = providerService.modifyProvider(provider);

        if (flag) {
            resp.sendRedirect(req.getContextPath() + "/jsp/provider.do?method=query");
        } else {
            req.getRequestDispatcher("providermodify.jsp").forward(req, resp);
        }
    }

    public void view(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String proId = req.getParameter("proid");
        int id = 0;

        if (!StringUtils.isNullOrEmpty(proId)) {
            id = Integer.parseInt(proId);
        }

        ProviderServiceImpl providerService = new ProviderServiceImpl();
        Provider provider = providerService.getProviderById(id);

        if (provider != null) {
            req.setAttribute("provider", provider);
            req.getRequestDispatcher("providerview.jsp").forward(req, resp);
        }
    }
}
