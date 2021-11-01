package com.bessky.erp.warehouse.specialprocessapproval.facade.impl;

import com.bessky.erp.core.common.bean.Pager;
import com.bessky.erp.core.common.dto.QueryListDTO;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcess;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcessQueryCondition;
import com.bessky.erp.warehouse.specialprocessapproval.facade.SpecialProcessApprovalFacade;
import com.bessky.erp.warehouse.specialprocessapproval.service.SpecialProcessService;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;

@Service(version = "1.0.0", retries = 0, timeout = 60000)
public class SpecialProcessApprovalFacadeImpl implements SpecialProcessApprovalFacade
{
    @Resource
    private SpecialProcessService specialProcessService;

    @Override
    public SpecialProcess getSpecialProcess(Integer id)
    {
        return specialProcessService.getSpecialProcess(id);
    }

    @Override
    public List<SpecialProcess> querySpecialProcessList()
    {
        return specialProcessService.querySpecialProcessList();
    }

    @Override
    public List<SpecialProcess> querySpecialProcessList(SpecialProcessQueryCondition query, Pager pager)
    {
       return specialProcessService.querySpecialProcessList(query, pager);
    }

    @Override
    public QueryListDTO<SpecialProcess> querySpecialProcessListDTO(SpecialProcessQueryCondition query, Pager pager)
    {
        List<SpecialProcess> list = specialProcessService.querySpecialProcessList(query, pager);
        return new QueryListDTO<>(list, pager);
    }

    @Override
    public void createSpecialProcess(SpecialProcess specialProcess)
    {
        specialProcessService.createSpecialProcess(specialProcess);
    }

    @Override
    public void updateSpecialProcess(SpecialProcess specialProcess)
    {
        specialProcessService.updateSpecialProcess(specialProcess);
    }

    @Override
    public void batchUpdateSpecialProcess(List<SpecialProcess> specialProcessList)
    {
        specialProcessService.batchUpdateSpecialProcess(specialProcessList);
    }
}