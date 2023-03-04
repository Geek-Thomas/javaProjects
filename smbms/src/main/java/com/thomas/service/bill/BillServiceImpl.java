package com.thomas.service.bill;

import com.thomas.dao.BaseDao;
import com.thomas.dao.bill.BillDao;
import com.thomas.dao.bill.BillDaoImpl;
import com.thomas.pojo.Bill;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BillServiceImpl implements BillService {
    private BillDao billDao;

    public BillServiceImpl() {
        billDao = new BillDaoImpl();
    }
    public List<Bill> getBillList(String proName, int proId, int isPayment) {
        Connection connection = null;
        List<Bill> bills = new ArrayList<Bill>();

        try {
            connection = BaseDao.getConnection();
            bills = billDao.getBillList(connection, proName, proId, isPayment);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return bills;
    }

    public boolean addBill(Bill bill) {
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false); //开启jdbc事务
            int updatedRows = billDao.addBill(connection, bill);
            connection.commit();
            if (updatedRows > 0) {
                flag = true;
                System.out.println("add bill succeeded");
            } else {
                System.out.println("add bill failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                System.out.println("rollback------------------>");
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return flag;
    }

    public boolean deleteBill(int billId) {
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            int updatedRows = billDao.deleteBill(connection, billId);
            if (updatedRows > 0) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return flag;
    }

    public Bill getBillById(int billId) {
        Connection connection = null;
        Bill bill = null;

        try {
            connection = BaseDao.getConnection();
            bill = billDao.getBillById(connection, billId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return bill;
    }

    public boolean modifyBill(Bill bill) {
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            int updatedRows = billDao.modifyBill(connection, bill);
            if (updatedRows > 0) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return flag;
    }
}
