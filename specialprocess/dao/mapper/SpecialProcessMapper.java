package com.bessky.erp.warehouse.specialprocessapproval.dao.mapper;

import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcess;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpecialProcessMapper implements RowMapper<SpecialProcess>
{
    public SpecialProcess mapRow(ResultSet rs, int rowNum) throws SQLException
    {
        SpecialProcess entity = new SpecialProcess();
        entity.setId(rs.getInt("id"));
        entity.setPurchaseOrder(rs.getString("purchase_order"));
        entity.setWarehouseId(rs.getInt("warehouse_id"));
        entity.setState(rs.getInt("state"));
        entity.setSku(rs.getString("sku"));
        entity.setProcessQuantity(rs.getInt("process_quantity"));
        entity.setProcessHour(rs.getInt("process_hour"));
        entity.setProcessAmount(rs.getDouble("process_amount"));
        entity.setApplyReason(rs.getString("apply_reason"));
        entity.setSupplierPayNumber(rs.getString("supplier_pay_number"));
        entity.setRemark(rs.getString("remark"));
        entity.setCreateTime(rs.getTimestamp("create_time"));
        entity.setCreateBy(rs.getInt("create_by"));
        entity.setUpdateTime(rs.getTimestamp("update_time"));
        entity.setUpdateBy(rs.getInt("update_by"));
        entity.setCompleteTime(rs.getTimestamp("complete_time"));
        return entity;
    }
}