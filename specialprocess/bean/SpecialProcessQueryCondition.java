package com.bessky.erp.warehouse.specialprocessapproval.bean;

import java.util.List;

/**
 * 特殊加工审批查询扩展类
 *
 * @author 丁光辉
 * @version Bessky V100R001 2021年10月18日
 * @since Bessky V100R001C00
 */
public class SpecialProcessQueryCondition extends SpecialProcess
{
    private static final long serialVersionUID = 1L;

    /**
     * 供应商编号查询
     */
    private String supplierNumber;

    /**
     * 多个采购单号查询逗号隔开
     */
    private String purchaseOrders;

    /**
     * 多个仓库号查询逗号隔开
     */
    private String warehouseIds;

    /**
     * 多个采购单状态查询逗号隔开
     */
    private String states;

    /**
     * 多个交易流水号查询逗号隔开
     */
    private String supplierPayNumbers;

    /**
     * SKU多个交易流水号查询逗号隔开
     */
    private String skus;

    /**
     * 申请原因模糊查询
     */
    private String likeApplyReason;

    /**
     * 备注模糊查询
     */
    private String likeRemark;

    /**
     * 创建时间区间查询
     */
    private String fromCreateTime;

    private String toCreateTime;

    /**
     * 完成时间区间查询
     */
    private String fromCompleteTime;

    private String toCompleteTime;

    /**
     * 主键列表
     */
    private List<Integer> ids;

    /**
     * 仓库号列表
     */
    private List<Integer> warehouseIdList;

    /**
     * 下级用户包括自己
     */
    private List<Integer> subUsers;

    public static long getSerialVersionUID()
    {
        return serialVersionUID;
    }

    public String getSupplierNumber()
    {
        return supplierNumber;
    }

    public void setSupplierNumber(String supplierNumber)
    {
        this.supplierNumber = supplierNumber;
    }

    public String getPurchaseOrders()
    {
        return purchaseOrders;
    }

    public void setPurchaseOrders(String purchaseOrders)
    {
        this.purchaseOrders = purchaseOrders;
    }

    public String getWarehouseIds()
    {
        return warehouseIds;
    }

    public void setWarehouseIds(String warehouseIds)
    {
        this.warehouseIds = warehouseIds;
    }

    public String getStates()
    {
        return states;
    }

    public void setStates(String states)
    {
        this.states = states;
    }

    public String getSupplierPayNumbers()
    {
        return supplierPayNumbers;
    }

    public void setSupplierPayNumbers(String supplierPayNumbers)
    {
        this.supplierPayNumbers = supplierPayNumbers;
    }

    public String getSkus()
    {
        return skus;
    }

    public void setSkus(String skus)
    {
        this.skus = skus;
    }

    public String getLikeApplyReason()
    {
        return likeApplyReason;
    }

    public void setLikeApplyReason(String likeApplyReason)
    {
        this.likeApplyReason = likeApplyReason;
    }

    public String getLikeRemark()
    {
        return likeRemark;
    }

    public void setLikeRemark(String likeRemark)
    {
        this.likeRemark = likeRemark;
    }

    public String getFromCreateTime()
    {
        return fromCreateTime;
    }

    public void setFromCreateTime(String fromCreateTime)
    {
        this.fromCreateTime = fromCreateTime;
    }

    public String getToCreateTime()
    {
        return toCreateTime;
    }

    public void setToCreateTime(String toCreateTime)
    {
        this.toCreateTime = toCreateTime;
    }

    public String getFromCompleteTime()
    {
        return fromCompleteTime;
    }

    public void setFromCompleteTime(String fromCompleteTime)
    {
        this.fromCompleteTime = fromCompleteTime;
    }

    public String getToCompleteTime()
    {
        return toCompleteTime;
    }

    public void setToCompleteTime(String toCompleteTime)
    {
        this.toCompleteTime = toCompleteTime;
    }

    public List<Integer> getIds()
    {
        return ids;
    }

    public void setIds(List<Integer> ids)
    {
        this.ids = ids;
    }

    public List<Integer> getWarehouseIdList()
    {
        return warehouseIdList;
    }

    public void setWarehouseIdList(List<Integer> warehouseIdList)
    {
        this.warehouseIdList = warehouseIdList;
    }

    public List<Integer> getSubUsers()
    {
        return subUsers;
    }

    public void setSubUsers(List<Integer> subUsers)
    {
        this.subUsers = subUsers;
    }
}