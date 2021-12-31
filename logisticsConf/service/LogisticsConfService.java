package com.bessky.logistic.conf.core.service;

import com.bessky.common.model.core.PageDTO;
import com.bessky.logistic.conf.core.entity.LogisticsConf;
import com.bessky.logistic.conf.core.entity.LogisticsConfCustom;
import com.bessky.logistic.conf.core.entity.LogisticsConfQuery;

import java.util.List;

/**
 * 物流配置表服务层接口
 *
 * @author liunancun
 * @date 2020/04/11
 */
public interface LogisticsConfService
{
    /**
     * 根据条件分页获取物流配置列表
     *
     * @param query
     * @return
     */
    PageDTO<LogisticsConf> getLogisticsConfPageList(LogisticsConfQuery query);

    /**
     * 根据物流公司获取物流配置列表
     *
     * @param logisticsCompany
     * @return
     */
    List<LogisticsConf> getLogisticsConfList(String logisticsCompany);

    /**
     * 根据物流公司获取物流配置列表缓存
     *
     * @param logisticsCompany
     * @return
     */
    List<LogisticsConf> getLogisticsConfListFromCache(String logisticsCompany);

    /**
     * 根据主键获取物流配置对象
     *
     * @param id
     * @return
     */
    LogisticsConf getLogisticsConf(Integer id);

    /**
     * 创建物流配置对象
     *
     * @param logisticsConf
     * @return
     */
    LogisticsConf createLogisticsConf(LogisticsConfCustom logisticsConf);

    /**
     * 修改物流配置对象
     *
     * @param logisticsConf
     * @return
     */
    LogisticsConf updateLogisticsConf(LogisticsConfCustom logisticsConf);
}