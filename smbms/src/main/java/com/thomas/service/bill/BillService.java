package com.thomas.service.bill;

import com.thomas.pojo.Bill;

import java.util.List;

public interface BillService {

    /**
     * @Author GeekThomas
     * @Date 2023/2/6 23:11
     * @Description 调用dao层代码
     * @Param proName
     * @Param proId
     * @Param isPayment
     * @Return {@link List< Bill>}
     * @Since version-1.0
     */
    public List<Bill> getBillList(String proName, int proId, int isPayment);

    /**
     * @Author GeekThomas
     * @Date 2023/2/8 22:49
     * @Description 增加订单
     * @Param bill
     * @Return {@link boolean}
     * @Since version-1.0
     */
    public boolean addBill(Bill bill);

    /**
     * @Author GeekThomas
     * @Date 2023/2/8 22:49
     * @Description 删除订单
     * @Param billId
     * @Return {@link boolean}
     * @Since version-1.0
     */
    public boolean deleteBill(int billId);

    /**
     * @Author GeekThomas
     * @Date 2023/2/8 23:55
     * @Description 根据ID获得订单
     * @Param billId
     * @Return {@link Bill}
     * @Since version-1.0
     */
    public Bill getBillById(int billId);

    /**
     * @Author GeekThomas
     * @Date 2023/2/9 0:17
     * @Description 修改订单
     * @Param bill
     * @Return {@link boolean}
     * @Since version-1.0
     */
    public boolean modifyBill(Bill bill);
}
