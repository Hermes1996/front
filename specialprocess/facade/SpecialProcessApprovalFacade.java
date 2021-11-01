package com.bessky.erp.warehouse.specialprocessapproval.facade;

import com.bessky.erp.core.common.bean.Pager;
import com.bessky.erp.core.common.dto.QueryListDTO;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcess;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcessQueryCondition;

import java.util.List;

/**
 * 特殊加工审批流程
 *
 * @author 丁光辉
 * @version Bessky V100R001 2021年10月18日
 * @since Bessky V100R001C00
 */
public interface SpecialProcessApprovalFacade
{
    SpecialProcess getSpecialProcess(Integer id);

    List<SpecialProcess> querySpecialProcessList();

    // 包含供应商编号，缺货数量
    List<SpecialProcess> querySpecialProcessList(SpecialProcessQueryCondition query, Pager pager);

    QueryListDTO<SpecialProcess> querySpecialProcessListDTO(SpecialProcessQueryCondition query, Pager pager);

    void createSpecialProcess(SpecialProcess specialProcess);

    void updateSpecialProcess(SpecialProcess specialProcess);

    void batchUpdateSpecialProcess(List<SpecialProcess> specialProcessList);
}