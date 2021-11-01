package com.bessky.pss.portal.business.specialprocess.domain;

import com.bessky.erp.warehouse.manager.bean.Warehouse;
import com.bessky.erp.core.common.bean.Pager;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcess;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcessQueryCondition;
import com.bessky.pss.portal.system.log.bean.OperationLog;
import com.bessky.pss.portal.system.user.bean.PortalUser;
import java.util.List;

public class SpecialProcessDo
{
    /**
     * 特殊加工查询
     */
    private SpecialProcessQueryCondition query;

    /**
     * 分页
     */
    private Pager page = new Pager();

    /**
     * 日志
     */
    private List<OperationLog> logs;

    /**
     * 特殊加工
     */
    private SpecialProcess specialProcess;

    /**
     * 特殊加工列表
     */
    private List<SpecialProcess> specialProcessList;

    /**
     * 仓库列表
     */
    private List<Warehouse> warehouses;

    /**
     * 申请人列表
     */
    private List<PortalUser> createByList;

    public SpecialProcessQueryCondition getQuery()
    {
        return query;
    }

    public void setQuery(SpecialProcessQueryCondition query)
    {
        this.query = query;
    }

    public Pager getPage()
    {
        return page;
    }

    public void setPage(Pager page)
    {
        this.page = page;
    }

    public List<OperationLog> getLogs()
    {
        return logs;
    }

    public void setLogs(List<OperationLog> logs)
    {
        this.logs = logs;
    }

    public SpecialProcess getSpecialProcess()
    {
        return specialProcess;
    }

    public void setSpecialProcess(SpecialProcess specialProcess)
    {
        this.specialProcess = specialProcess;
    }

    public List<SpecialProcess> getSpecialProcessList()
    {
        return specialProcessList;
    }

    public void setSpecialProcessList(List<SpecialProcess> specialProcessList)
    {
        this.specialProcessList = specialProcessList;
    }

    public List<Warehouse> getWarehouses()
    {
        return warehouses;
    }

    public void setWarehouses(List<Warehouse> warehouses)
    {
        this.warehouses = warehouses;
    }

    public List<PortalUser> getCreateByList()
    {
        return createByList;
    }

    public void setCreateByList(List<PortalUser> createByList)
    {
        this.createByList = createByList;
    }
}