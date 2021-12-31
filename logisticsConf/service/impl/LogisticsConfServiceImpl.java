package com.bessky.logistic.conf.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bessky.common.jdbc.service.impl.MyServiceImpl;
import com.bessky.common.model.core.PageDTO;
import com.bessky.logistic.conf.core.entity.LogisticsConf;
import com.bessky.logistic.conf.core.entity.LogisticsConfCustom;
import com.bessky.logistic.conf.core.entity.LogisticsConfQuery;
import com.bessky.logistic.conf.core.mapper.LogisticsConfMapper;
import com.bessky.logistic.conf.core.service.LogisticsConfService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 物流配置表服务层实现
 *
 * @author liunancun
 * @date 2020/04/11
 */
@Service
@CacheConfig(cacheNames = "logistics_conf")
public class LogisticsConfServiceImpl extends MyServiceImpl<LogisticsConfMapper, LogisticsConf> implements LogisticsConfService
{
    @Override
    public PageDTO<LogisticsConf> getLogisticsConfPageList(LogisticsConfQuery query)
    {
        return this.page(query, this::setQueryWrapper);
    }

    @Override
    public List<LogisticsConf> getLogisticsConfList(String logisticsCompany)
    {
        Assert.notNull(logisticsCompany, "物流公司不能为空");

        return this.list(LogisticsConf::getLogisticsCompany, logisticsCompany);
    }

    @Override
    @Cacheable(key = "#logisticsCompany")
    public List<LogisticsConf> getLogisticsConfListFromCache(String logisticsCompany)
    {
        return this.getLogisticsConfList(logisticsCompany);
    }

    @Override
    public LogisticsConf getLogisticsConf(Integer id)
    {
        return this.get(id);
    }

    @Override
    @CacheEvict(key = "#result.logisticsCompany", condition = "#result != null")
    @Transactional(rollbackFor = Exception.class)
    public LogisticsConf createLogisticsConf(LogisticsConfCustom logisticsConf)
    {
        this.insert(logisticsConf);

        // 获取最新物流配置对象返回
        return this.getLogisticsConf(logisticsConf.getLogisticsId());
    }

    @Override
    @CacheEvict(key = "#result.logisticsCompany", condition = "#result != null")
    @Transactional(rollbackFor = Exception.class)
    public LogisticsConf updateLogisticsConf(LogisticsConfCustom logisticsConf)
    {
        // 根据主键更新
        this.update(logisticsConf);

        // 获取最新物流配置对象返回
        return this.getLogisticsConf(logisticsConf.getLogisticsId());
    }

    /**
     * 设置查询条件
     *
     * @param query
     * @return
     */
    private LambdaQueryWrapper<LogisticsConf> setQueryWrapper(LogisticsConfQuery query)
    {
        LambdaQueryWrapper<LogisticsConf> queryWrapper = Wrappers.lambdaQuery();

        // 默认根据主键逆序
        queryWrapper.orderByDesc(LogisticsConf::getLogisticsId);

        if (query == null)
        {
            return queryWrapper;
        }

        this.eq(queryWrapper, LogisticsConf::getLogisticsCompany, query.getLogisticCompany());
        this.eq(queryWrapper, LogisticsConf::getCreatedBy, query.getCreateBy());

        return queryWrapper;
    }
}