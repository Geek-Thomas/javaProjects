package com.thomas.servlet.bill;

import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.thomas.pojo.Bill;
import com.thomas.pojo.Provider;
import com.thomas.pojo.User;
import com.thomas.service.bill.BillServiceImpl;
import com.thomas.service.provider.ProviderServiceImpl;
import com.thomas.util.Constants;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method != null && "query".equals(method)) {
            this.query(req, resp);
        } else if (method != null && "getproviderlist".equals(method)) {
            this.getProviderList(req, resp);
        } else if (method != null && "add".equals(method)) {
            this.addBill(req, resp);
        } else if (method != null && "delbill".equals(method)) {
            this.deleteBill(req, resp);
        } else if (method != null && "modify".equals(method)) {
            this.getBillById(req, resp);
        } else if (method != null && "modifysave".equals(method)) {
            this.modifyBill(req, resp);
        } else if (method != null && "view".equals(method)) {
            this.view(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public void query(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取前端传递的参数
        String providerId = req.getParameter("queryProviderId");
        String proName = req.getParameter("queryProductName");
        String queryIsPayment = req.getParameter("queryIsPayment");

        ProviderServiceImpl providerService = new ProviderServiceImpl();
        List<Provider> providerList = providerService.getProviderList("", "");
        req.setAttribute("providerList", providerList);

        int proId = 0;
        int isPayment = 0;

        if (!StringUtils.isNullOrEmpty(providerId)) {
            proId = Integer.parseInt(providerId);
        }

        if (!StringUtils.isNullOrEmpty(queryIsPayment)) {
            isPayment = Integer.parseInt(queryIsPayment);
        }

        BillServiceImpl billService = new BillServiceImpl();
        List<Bill> billList = billService.getBillList(proName, proId, isPayment);

        req.setAttribute("billList", billList);
        req.setAttribute("queryProviderId", providerId);
        req.setAttribute("queryProductName", proName);
        req.setAttribute("queryIsPayment", queryIsPayment);
        req.getRequestDispatcher("billlist.jsp").forward(req, resp);
    }

    public void getProviderList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        List<Provider> providerList = providerService.getProviderList("", "");
        for (Provider provider : providerList) {
            System.out.println(provider.getProName());
        }
        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(providerList));
        writer.flush();
        writer.close();
    }

    public void addBill(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取前端的参数
        String billCode = req.getParameter("billCode");
        String productName = req.getParameter("productName");
        String productUnit = req.getParameter("productUnit");
        String productCountTemp = req.getParameter("productCount");
        String totalPriceTemp = req.getParameter("totalPrice");
        String providerIdTemp = req.getParameter("providerId");
        String isPaymentTemp = req.getParameter("isPayment");

        BigDecimal productCount = new BigDecimal("0.00");
        BigDecimal totalPrice = new BigDecimal("0.00");
        int providerId = 0;
        int isPayment = 0;

        if (!StringUtils.isNullOrEmpty(productCountTemp)) {
            productCount = new BigDecimal(productCountTemp).setScale(2, BigDecimal.ROUND_DOWN);
        }

        if (!StringUtils.isNullOrEmpty(totalPriceTemp)) {
            totalPrice = new BigDecimal(totalPriceTemp).setScale(2, BigDecimal.ROUND_DOWN);
        }

        if (!StringUtils.isNullOrEmpty(providerIdTemp)) {
            providerId = Integer.parseInt(providerIdTemp);
        }

        if (!StringUtils.isNullOrEmpty(isPaymentTemp)) {
            isPayment = Integer.parseInt(isPaymentTemp);
        }

        //包装成新bill
        Bill bill = new Bill();
        bill.setBillCode(billCode);
        bill.setProductName(productName);
        bill.setProductUnit(productUnit);
        bill.setProductCount(productCount);
        bill.setTotalPrice(totalPrice);
        bill.setProviderId(providerId);
        bill.setIsPayment(isPayment);

        //创建者
        bill.setCreationDate(new Date());
        //创建时间
        bill.setCreatedBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());

        BillServiceImpl billService = new BillServiceImpl();
        boolean flag = billService.addBill(bill);
        if (flag) {
            resp.sendRedirect(req.getContextPath() + "/jsp/bill.do?method=query");
        } else {
            req.getRequestDispatcher("billadd.jsp").forward(req, resp);
        }
    }

    public void deleteBill(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String billIdTemp = req.getParameter("billid");
        int billId = 0;

        if (!StringUtils.isNullOrEmpty(billIdTemp)) {
            billId = Integer.parseInt(billIdTemp);
        }

        Map<String, String> resultMap = new HashMap<String, String>();

        BillServiceImpl billService = new BillServiceImpl();
        boolean flag = billService.deleteBill(billId);

        if (billId <= 0) {
            resultMap.put("delResult", "nonexist");
        } else {
            if (flag) {
                resultMap.put("delResult", "true");
            } else {
                resultMap.put("delResult", "false");
            }
        }

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }

    public void getBillById(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String billIdTemp = req.getParameter("billid");
        System.out.println(billIdTemp);
        int billId = 0;

        if (!StringUtils.isNullOrEmpty(billIdTemp)) {
            billId = Integer.parseInt(billIdTemp);
        }
        System.out.println("id" + billId);
        BillServiceImpl billService = new BillServiceImpl();
        Bill bill = billService.getBillById(billId);
        System.out.println("订单ID：" + bill.getId());

        if (bill != null) {
            req.setAttribute("bill", bill);
            req.getRequestDispatcher("billmodify.jsp").forward(req, resp);
        }
    }

    public void modifyBill(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idTemp = req.getParameter("id");
        String billCode = req.getParameter("billCode");
        String productName = req.getParameter("productName");
        String productUnit = req.getParameter("productUnit");
        String productCountTemp = req.getParameter("productCount");
        String totalPriceTemp = req.getParameter("totalPrice");
        String providerIdTemp = req.getParameter("providerId");
        String isPaymentTemp = req.getParameter("isPayment");

        int id = 0;
        BigDecimal productCount = new BigDecimal("0.00");
        BigDecimal totalPrice = new BigDecimal("0.00");
        int providerId = 0;
        int isPayment = 0;

        if (!StringUtils.isNullOrEmpty(idTemp)) {
            id = Integer.parseInt(idTemp);
        }
        if (!StringUtils.isNullOrEmpty(providerIdTemp)) {
            providerId = Integer.parseInt(providerIdTemp);
        }
        if (!StringUtils.isNullOrEmpty(isPaymentTemp)) {
            isPayment = Integer.parseInt(isPaymentTemp);
        }

        if (!StringUtils.isNullOrEmpty(productCountTemp)) {
            productCount = new BigDecimal(productCountTemp).setScale(2, BigDecimal.ROUND_DOWN);
        }

        if (!StringUtils.isNullOrEmpty(totalPriceTemp)) {
            totalPrice = new BigDecimal(totalPriceTemp).setScale(2, BigDecimal.ROUND_DOWN);
        }

        Bill bill = new Bill();
        bill.setId(id);
        bill.setBillCode(billCode);
        bill.setProductName(productName);
        bill.setProductUnit(productUnit);
        bill.setProductCount(productCount);
        bill.setTotalPrice(totalPrice);
        bill.setProviderId(providerId);
        bill.setIsPayment(isPayment);

        //添加修改者
        bill.setModifyBy(((User) req.getSession().getAttribute(Constants.USER_SESSION)).getId());
        //添加修改时间
        bill.setModifyDate(new Date());
        System.out.println("编号" + bill.getBillCode() + " 商品详情：" + bill.getProductDesc());

        BillServiceImpl billService = new BillServiceImpl();
        boolean flag = billService.modifyBill(bill);
        if (flag) {
            resp.sendRedirect(req.getContextPath() + "/jsp/bill.do?method=query");
        } else {
            req.getRequestDispatcher("billmodify.jsp").forward(req, resp);
        }
    }

    public void view(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String billIdTemp = req.getParameter("billid");
        int billId = 0;

        if (!StringUtils.isNullOrEmpty(billIdTemp)) {
            billId = Integer.parseInt(billIdTemp);
        }

        BillServiceImpl billService = new BillServiceImpl();
        Bill bill = billService.getBillById(billId);
        req.setAttribute("bill", bill);
        req.getRequestDispatcher("billview.jsp").forward(req, resp);
    }

    @Test
    public void test() {
        System.out.println(new BigDecimal("1.22").setScale(2,BigDecimal.ROUND_DOWN));
    }
}
