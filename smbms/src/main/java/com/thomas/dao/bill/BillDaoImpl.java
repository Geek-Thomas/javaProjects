package com.thomas.dao.bill;

import com.mysql.cj.util.StringUtils;
import com.thomas.dao.BaseDao;
import com.thomas.pojo.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BillDaoImpl implements BillDao {
    public List<Bill> getBillList(Connection connection, String proName, int proId, int isPayment) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Bill> bills = new ArrayList<Bill>();
        List<Object> paramList = new ArrayList<Object>();

        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select a.*, b.proName from smbms_bill a, smbms_provider b\n" +
                    "where a.providerId = b.id");
            if(!StringUtils.isNullOrEmpty(proName)) {
                sql.append(" and a.productName like ?");
                paramList.add("%" + proName + "%");
            }
            if (proId > 0) {
                sql.append(" and b.id = ?");
                paramList.add(proId);
            }
            if (isPayment > 0) {
                sql.append(" and isPayment = ?");
                paramList.add(isPayment);
            }

            System.out.println(sql.toString());
            Object[] params = paramList.toArray();
            rs = BaseDao.execute(connection, ps, rs, sql.toString(), params);

            while (rs.next()) {
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillCode(rs.getString("billCode"));
                bill.setProductName(rs.getString("productName"));
                bill.setProviderName(rs.getString("proName"));
                bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                bill.setIsPayment(rs.getInt("isPayment"));
                bill.setCreationDate(rs.getDate("creationDate"));
                bills.add(bill);
            }
            BaseDao.closeResources(null, ps, rs);

        }
        return bills;
    }

    public int addBill(Connection connection, Bill bill) throws SQLException {
        PreparedStatement ps = null;
        int updatedRows = 0;

        if (connection != null) {
            String sql = "insert into smbms_bill(billCode, productName, productUnit, productCount, totalPrice, " +
                    "providerId, isPayment, creationDate, createdBy) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Object[] params = {bill.getBillCode(), bill.getProductName(), bill.getProductUnit(), bill.getProductCount(),
            bill.getTotalPrice(), bill.getProviderId(), bill.getIsPayment(), bill.getCreationDate(), bill.getCreatedBy()};
            updatedRows = BaseDao.execute(connection, ps, sql, params);

            BaseDao.closeResources(null, ps, null);
        }
        return updatedRows;
    }

    public int deleteBill(Connection connection, int billId) throws SQLException {
        PreparedStatement ps = null;
        int updatedRows = 0;

        if (connection != null) {
            String sql = "delete from smbms_bill where id = ?";
            Object[] params = {billId};
            updatedRows = BaseDao.execute(connection, ps, sql, params);
            BaseDao.closeResources(null, ps, null);
        }
        return updatedRows;
    }

    public Bill getBillById(Connection connection, int billId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Bill bill = null;

        if (connection != null) {
            String sql = "select a.*, b.proName from smbms_bill a, smbms_provider b " +
                    "where a.providerId = b.id and a.id = ?";
            Object[] params = {billId};

            rs = BaseDao.execute(connection, ps, rs, sql, params);
            if (rs.next()) {
                bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setBillCode(rs.getString("billCode"));
                bill.setProductName(rs.getString("productName"));
                bill.setProductUnit(rs.getString("productUnit"));
                bill.setProductCount(rs.getBigDecimal("productCount"));
                bill.setTotalPrice(rs.getBigDecimal("totalPrice"));
                bill.setProviderId(rs.getInt("providerId"));
                bill.setProviderName(rs.getString("proName"));
                bill.setIsPayment(rs.getInt("isPayment"));
            }
            BaseDao.closeResources(null, ps, rs);
        }
        return bill;
    }

    public int modifyBill(Connection connection, Bill bill) throws SQLException {
        PreparedStatement ps = null;
        int updatedRows = 0;

        if (connection != null) {
            String sql = "update smbms_bill set productName = ?, productUnit = ?, productCount = ?," +
                    "totalPrice = ?, providerId = ?, isPayment = ?, modifyBy = ?, modifyDate = ? where id = ?";
            Object[] params = {bill.getProductName(), bill.getProductUnit(), bill.getProductCount(),
            bill.getTotalPrice(), bill.getProviderId(), bill.getIsPayment(), bill.getModifyBy(), bill.getModifyDate(),
                    bill.getId()};
            System.out.println(sql);
            updatedRows = BaseDao.execute(connection, ps, sql, params);
            BaseDao.closeResources(null, ps, null);
        }
        return updatedRows;
    }
}
