package com.thomas.dao.bill;

import com.thomas.pojo.Bill;

import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

public interface BillDao {

    /**
     * @Author GeekThomas
     * @Date 2023/2/6 23:08
     * @Description 根据供应商名，供应商ID，是否付款查询订单
     * @Param connection
     * @Param proName
     * @Param proId
     * @Param isPayment
     * @Return {@link List< Bill>}
     * @Since version-1.0
     */
    public List<Bill> getBillList(Connection connection, String proName, int proId, int isPayment) throws SQLException;

    /**
     * @Author GeekThomas
     * @Date 2023/2/6 23:51
     * @Description 增加订单
     * @Param connection
     * @Param bill
     * @Return {@link int}
     * @Since version-1.0
     */
    public int addBill(Connection connection, Bill bill) throws SQLException;

    /**
     * @Author GeekThomas
     * @Date 2023/2/8 22:41
     * @Description 根据订单ID，删除订单
     * @Param connection
     * @Param billId
     * @Return {@link int}
     * @Since version-1.0
     */
    public int deleteBill(Connection connection, int billId) throws SQLException;

    /**
     * @Author GeekThomas
     * @Date 2023/2/8 23:44
     * @Description 通过账单ID获取账单
     * @Param connection
     * @Param billId
     * @Return {@link Bill}
     * @Since version-1.0
     */
    public Bill getBillById(Connection connection, int billId) throws SQLException;

    /**
     * @Author GeekThomas
     * @Date 2023/2/8 23:07
     * @Description 修改订单
     * @Param connection
     * @Param bill
     * @Return {@link int}
     * @Since version-1.0
     */
    public int modifyBill(Connection connection, Bill bill) throws SQLException;
}
