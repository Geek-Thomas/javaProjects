package com.thomas.dao.provider;

import com.mysql.cj.util.StringUtils;
import com.thomas.dao.BaseDao;
import com.thomas.pojo.Provider;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProviderDaoImpl implements ProviderDao {
    public List<Provider> getProviderList(Connection connection, String proName, String proCode) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Provider> providers = new ArrayList<Provider>();
        StringBuffer sql = new StringBuffer();

        if (connection != null) {
            sql.append("select * from smbms_provider where 1 = 1");
            List<Object> paramList = new ArrayList<Object>();
            if (!StringUtils.isNullOrEmpty(proName)) {
                sql.append(" and proName like ?");
                paramList.add("%" + proName + "%");
            }
            if (!StringUtils.isNullOrEmpty(proCode)) {
                sql.append(" and proCode like ?");
                paramList.add("%" + proCode + "%");
            }

            System.out.println(sql.toString());
            Object[] params = paramList.toArray();
            rs = BaseDao.execute(connection, ps, rs, sql.toString(), params);

            while (rs.next()) {
                Provider _provider = new Provider();
                _provider.setId(rs.getInt("id"));
                _provider.setProCode(rs.getString("proCode"));
                _provider.setProName(rs.getString("proName"));
                _provider.setProDesc(rs.getString("proDesc"));
                _provider.setProContact(rs.getString("proContact"));
                _provider.setProPhone(rs.getString("proPhone"));
                _provider.setProAddress(rs.getString("proAddress"));
                _provider.setProFax(rs.getString("proFax"));
                _provider.setCreationDate(rs.getDate("creationDate"));
                providers.add(_provider);
            }

            BaseDao.closeResources(null, ps, rs);
        }

        return providers;
    }

    public int addProvider(Connection connection, Provider provider) throws SQLException {
        PreparedStatement ps = null;
        int updatedRows = 0;

        if (connection != null) {
            String sql = "insert into smbms_provider(proCode, proName, proContact, proPhone, proAddress, proFax, proDesc, " +
                    "createdBy, creationDate)" +
                    " values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Object[] params = {provider.getProCode(), provider.getProName(), provider.getProContact(), provider.getProPhone(),
                    provider.getProAddress(), provider.getProFax(), provider.getProDesc(), provider.getCreatedBy(),
                    provider.getCreationDate()};
            updatedRows = BaseDao.execute(connection, ps, sql, params);
            BaseDao.closeResources(null, ps, null);
        }

        return updatedRows;
    }

    public int delProvider(Connection connection, int proId) throws SQLException {
        PreparedStatement ps = null;
        int updatedRows = 0;

        if (connection != null) {
            String sql = "delete from smbms_provider where id = ?";
            Object[] params = {proId};
            updatedRows = BaseDao.execute(connection, ps, sql, params);
            BaseDao.closeResources(null, ps, null);
        }
        return updatedRows;
    }

    public Provider getProviderById(Connection connection, int proId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Provider provider = null;

        if (connection != null) {
            String sql = "select * from smbms_provider where id = ?";
            Object[] params = {proId};
            rs = BaseDao.execute(connection, ps, rs, sql, params);
            if (rs.next()) {
                provider = new Provider();
                provider.setId(proId);
                provider.setProCode(rs.getString("proCode"));
                provider.setProName(rs.getString("proName"));
                provider.setProDesc(rs.getString("proDesc"));
                provider.setProContact(rs.getString("proContact"));
                provider.setProPhone(rs.getString("proPhone"));
                provider.setProAddress(rs.getString("proAddress"));
                provider.setProFax(rs.getString("proFax"));
            }

            BaseDao.closeResources(null, ps, rs);
        }

        return provider;
    }

    public int modifyProvider(Connection connection, Provider provider) throws SQLException {
        PreparedStatement ps = null;
        int updatedRows = 0;

        if (connection != null) {
            String sql = "update smbms_provider set proCode=?, proName=?, proContact=?, proPhone=?, proAddress=?," +
                    " proFax=?, proDesc=? where id=?";
            Object[] params = {provider.getProCode(), provider.getProName(), provider.getProContact(),
                    provider.getProPhone(), provider.getProAddress(), provider.getProFax(), provider.getProDesc(),
                    provider.getId()};
            updatedRows = BaseDao.execute(connection, ps, sql, params);
            BaseDao.closeResources(null, ps, null);
        }

        return updatedRows;
    }
}
