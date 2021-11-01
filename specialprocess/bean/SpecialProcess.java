package com.bessky.erp.warehouse.specialprocessapproval.bean;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 特殊加工审批实体类
 *
 * @author 丁光辉
 * @version Bessky V100R001 2021年10月18日
 * @since Bessky V100R001C00
 */
public class SpecialProcess implements Serializable
{
    /**
     * 序列化标识
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     * This field corresponds to the database column t_special_process.id
     */
    private Integer id;

    /**
     * 采购单号
     */
    private String purchaseOrder;

    /**
     * 仓库
     */
    private Integer warehouseId;

    /**
     * 状态
     */
    private Integer state;

    /**
     * SKU
     */
    private String sku;

    /**
     * 申请加工数量
     */
    private Integer processQuantity;

    /**
     * 申请加工工时
     */
    private Integer processHour;

    /**
     * 申请加工金额
     */
    private Double processAmount;

    /**
     * 申请原因
     */
    private String applyReason;

    /**
     * 供应商支付流水号
     */
    private String supplierPayNumber;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Timestamp createTime;

    /**
     * 创建人
     */
    private Integer createBy;

    /**
     * 修改时间
     */
    private Timestamp updateTime;

    /**
     * 修改人
     */
    private Integer updateBy;

    /**
     * 完成时间
     */
    private Timestamp completeTime;

    /**
     * 供应商编号
     * This field corresponds to the database column t_supplier.supplier_number
     */
    private String supplierNumber;

    /**
     * 缺货数量
     * This field corresponds to the database column t_stock.stockout
     */
    private Integer stockout;

    public static long getSerialVersionUID()
    {
        return serialVersionUID;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getPurchaseOrder()
    {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder)
    {
        this.purchaseOrder = purchaseOrder;
    }

    public Integer getWarehouseId()
    {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId)
    {
        this.warehouseId = warehouseId;
    }

    public Integer getState()
    {
        return state;
    }

    public void setState(Integer state)
    {
        this.state = state;
    }

    public String getSku()
    {
        return sku;
    }

    public void setSku(String sku)
    {
        this.sku = sku;
    }

    public Integer getProcessQuantity()
    {
        return processQuantity;
    }

    public void setProcessQuantity(Integer processQuantity)
    {
        this.processQuantity = processQuantity;
    }

    public Integer getProcessHour()
    {
        return processHour;
    }

    public void setProcessHour(Integer processHour)
    {
        this.processHour = processHour;
    }

    public Double getProcessAmount()
    {
        return processAmount;
    }

    public void setProcessAmount(Double processAmount)
    {
        this.processAmount = processAmount;
    }

    public String getApplyReason()
    {
        return applyReason;
    }

    public void setApplyReason(String applyReason)
    {
        this.applyReason = applyReason;
    }

    public String getSupplierPayNumber()
    {
        return supplierPayNumber;
    }

    public void setSupplierPayNumber(String supplierPayNumber)
    {
        this.supplierPayNumber = supplierPayNumber;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Timestamp getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime)
    {
        this.createTime = createTime;
    }

    public Integer getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(Integer createBy)
    {
        this.createBy = createBy;
    }

    public Timestamp getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime)
    {
        this.updateTime = updateTime;
    }

    public Integer getUpdateBy()
    {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy)
    {
        this.updateBy = updateBy;
    }

    public Timestamp getCompleteTime()
    {
        return completeTime;
    }

    public void setCompleteTime(Timestamp completeTime)
    {
        this.completeTime = completeTime;
    }

    public String getSupplierNumber()
    {
        return supplierNumber;
    }

    //  此为供应商编号注意区分
    public void setSupplierNumber(String supplierNumber)
    {
        this.supplierNumber = supplierNumber;
    }

    public Integer getStockout()
    {
        return stockout;
    }

    public void setStockout(Integer stockout)
    {
        this.stockout = stockout;
    }
}
