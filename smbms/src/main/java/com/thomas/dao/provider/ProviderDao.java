package com.thomas.dao.provider;

import com.thomas.pojo.Provider;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ProviderDao {
    /**
     * @Author GeekThomas
     * @Date 2023/2/1 22:33
     * @Description 根据供应商编码或供应商名称获得供应商列表
     * @Param proCode
     * @Param proName
     * @Return {@link List< Provider>}
     * @Since version-1.0
     */
    public List<Provider> getProviderList(Connection connection, String proName, String proCode) throws SQLException;

    /**
     * @Author GeekThomas
     * @Date 2023/2/3 0:10
     * @Description 新增供应商
     * @Param connection
     * @Param provider
     * @Return {@link int}
     * @Since version-1.0
     */
    public int addProvider(Connection connection, Provider provider) throws SQLException;

    /**
     * @Author GeekThomas
     * @Date 2023/2/5 0:41
     * @Description 根据供应商ID删除用户
     * @Param connection
     * @Param proId
     * @Return {@link int}
     * @Since version-1.0
     */
    public int delProvider(Connection connection, int proId) throws SQLException;

    /**
     * @Author GeekThomas
     * @Date 2023/2/5 23:00
     * @Description 通过供应商ID获得供应商
     * @Param connection
     * @Param proId
     * @Return {@link Provider}
     * @Since version-1.0
     */
    public Provider getProviderById(Connection connection, int proId) throws SQLException;

    /**
     * @Author GeekThomas
     * @Date 2023/2/5 23:35
     * @Description 修改供应商
     * @Param connection
     * @Param provider
     * @Return {@link int}
     * @Since version-1.0
     */
    public int modifyProvider(Connection connection, Provider provider) throws SQLException;
}
