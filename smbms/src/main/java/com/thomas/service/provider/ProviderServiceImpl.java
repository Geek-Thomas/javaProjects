package com.thomas.service.provider;

import com.mysql.cj.util.StringUtils;
import com.thomas.dao.BaseDao;
import com.thomas.dao.provider.ProviderDao;
import com.thomas.dao.provider.ProviderDaoImpl;
import com.thomas.pojo.Provider;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProviderServiceImpl implements ProviderService {
    private ProviderDao providerDao;

    public ProviderServiceImpl() {
        providerDao = new ProviderDaoImpl();
    }

    public List<Provider> getProviderList(String proName, String proCode) {
        Connection connection = null;
        List<Provider> providers = new ArrayList<Provider>();
        System.out.println("queryProName ---> " + (StringUtils.isNullOrEmpty(proName) ? "all" : proName));
        System.out.println("queryProCode ---> " + (StringUtils.isNullOrEmpty(proCode) ? "all" : proCode));

        try {
            connection = BaseDao.getConnection();
            providers = providerDao.getProviderList(connection, proName, proCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return providers;
    }

    public boolean addProvider(Provider provider) {
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false); //开启事务
            int updatedRows = providerDao.addProvider(connection, provider);
            connection.commit();
            if (updatedRows > 0) {
                flag = true;
                System.out.println("add provider succeeded");
            } else {
                System.out.println("add provider failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //事务回滚
            try {
                System.out.println("rollback-------------->");
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return flag;
    }

    public boolean delProvider(int proId) {
        Connection connection = null;
        boolean flag = false;


        try {
            connection = BaseDao.getConnection();
            int updatedRows = providerDao.delProvider(connection, proId);
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

    public Provider getProviderById(int proId) {
        Connection connection = null;
        Provider provider = null;


        try {
            connection = BaseDao.getConnection();
            provider = providerDao.getProviderById(connection, proId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(connection, null, null);
        }
        return provider;
    }

    public boolean modifyProvider(Provider provider) {
        Connection connection = null;
        boolean flag = false;

        try {
            connection = BaseDao.getConnection();
            int updatedRows = providerDao.modifyProvider(connection, provider);
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

    @Test
    public void getProvidersListTest() {
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        List<Provider> providers = providerService.getProviderList("", "");
        for (Provider provider : providers) {
            System.out.println(provider.getProName());
        }
    }
}
