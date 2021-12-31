package com.bessky.logistic.conf.core.entity;

import com.bessky.common.model.core.PageQuery;
import lombok.Data;

/**
 * 物流配置查询对象
 *
 * @author 丁光辉
 * @date 2021/12/07
 */
@Data
public class LogisticsConfQuery extends PageQuery
{
    /**
     * 物流公司等于查询
     */
    private String logisticCompany;

    /**
     * 创建人员等于查询
     */
    private Integer createBy;
}
