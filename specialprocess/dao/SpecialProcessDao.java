package com.bessky.erp.warehouse.specialprocessapproval.dao;

import com.bessky.erp.core.common.bean.Pager;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcess;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcessQueryCondition;

import java.util.List;

/**
 *
 * 特殊加工审批数据层
 *
 * @author 丁光辉
 * @version Bessky V100R001 2021年10月18日
 * @since Bessky V100R001C00
 */
public interface SpecialProcessDao
{
    /**
     * This method corresponds to the database table t_special_process
     */
    SpecialProcess querySpecialProcess(Integer primaryKey);

    Integer querySpecialProcessCount(SpecialProcessQueryCondition query);
    
    List<SpecialProcess> querySpecialProcessList();
    
    List<SpecialProcess> querySpecialProcessList(SpecialProcessQueryCondition query, Pager pager);
    
    void createSpecialProcess(SpecialProcess entity);

    void updateSpecialProcess(SpecialProcess entity);

    void batchUpdateSpecialProcess(List<SpecialProcess> entityList);
}