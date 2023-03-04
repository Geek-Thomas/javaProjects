package com.thomas.service.provider;

import com.thomas.pojo.Provider;

import java.util.List;

public interface ProviderService {
    /**
     * @Author GeekThomas
     * @Date 2023/2/2 22:44
     * @Description 调用provider dao层，实现查询供应商列表业务
     * @Param proName
     * @Param proCode
     * @Return {@link List< Provider>}
     * @Since version-1.0
     */
    public List<Provider> getProviderList(String proName, String proCode);

    /**
     * @Author GeekThomas
     * @Date 2023/2/4 21:26
     * @Description 调用dao层，实现增加用户功能
     * @Param provider
     * @Return {@link boolean}
     * @Since version-1.0
     */
    public boolean addProvider(Provider provider);

    /**
     * @Author GeekThomas
     * @Date 2023/2/5 23:18
     * @Description 根据供应商ID删除对应的供应商
     * @Param proId
     * @Return {@link boolean}
     * @Since version-1.0
     */
    public boolean delProvider(int proId);

    /**
     * @Author GeekThomas
     * @Date 2023/2/5 23:18
     * @Description 根据供应商ID，获得对应的供应商
     * @Param proId
     * @Return {@link Provider}
     * @Since version-1.0
     */
    public Provider getProviderById(int proId);

    /**
     * @Author GeekThomas
     * @Date 2023/2/6 0:16
     * @Description 修改供应商信息
     * @Param provider
     * @Return {@link boolean}
     * @Since version-1.0
     */
    public boolean modifyProvider(Provider provider);
 }
