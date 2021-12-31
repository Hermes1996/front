package com.bessky.logistic.conf.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物流配置表定制类
 *
 * @author liunancun
 * @date 2020/04/11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LogisticsConfCustom extends LogisticsConf
{
    /**
     * 序列化标识
     */
    private static final long serialVersionUID = 1L;

    /**
     * 创建人名
     */
    private String createByName;
}