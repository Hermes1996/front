package com.bessky.erp.warehouse.specialprocessapproval.dao.impl;

import com.bessky.erp.core.common.bean.Pager;
import com.bessky.erp.core.common.util.CommonUtils;
import com.bessky.erp.foundation.sqler.SqlerRequest;
import com.bessky.erp.foundation.sqler.analyzer.DataType;
import com.bessky.erp.foundation.sqler.template.SqlerTemplate;
import com.bessky.erp.starter.core.constants.AssertConstants;
import com.bessky.erp.starter.core.context.DataContextHolder;
import com.bessky.erp.warehouse.specialprocessapproval.dao.SpecialProcessDao;
import com.bessky.erp.warehouse.specialprocessapproval.dao.mapper.SpecialProcessMapper;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcess;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcessQueryCondition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.List;

/**
 *
 * 特殊加工审批数据层实现
 *
 * @author 丁光辉
 * @version Bessky V100R001 2021年10月18日
 * @since Bessky V100R001C00
 */
@Repository("specialProcessDao")
public class SpecialProcessDaoImpl implements SpecialProcessDao
{
    @Override
    public SpecialProcess querySpecialProcess(Integer primaryKey)
    {
        Assert.notNull(primaryKey, AssertConstants.NOT_NULL_MSG);
        SqlerRequest request = new SqlerRequest("querySpecialProcessByPrimaryKey");
        request.addDataParam("id", DataType.INT, primaryKey);
        return SqlerTemplate.queryForObject(request, new SpecialProcessMapper());
    }

    @Override
    public Integer querySpecialProcessCount(SpecialProcessQueryCondition query)
    {
        SqlerRequest request = new SqlerRequest("querySpecialProcessCount");
        setQueryCondition(request, query);
        return SqlerTemplate.queryForInt(request);
    }

    @Override
    public List<SpecialProcess> querySpecialProcessList()
    {
        SqlerRequest request = new SqlerRequest("querySpecialProcessList");
        return SqlerTemplate.query(request, new SpecialProcessMapper());
    }

    @Override
    public List<SpecialProcess> querySpecialProcessList(SpecialProcessQueryCondition query, Pager pager)
    {
        SqlerRequest request = new SqlerRequest("querySpecialProcessList");
        setQueryCondition(request, query);
        if (pager != null)
        {
            request.addFetch(pager.getPageNo(), pager.getPageSize());
        }
        return SqlerTemplate.query(request, new SpecialProcessMapper());
    }

    @Override
    public void createSpecialProcess(SpecialProcess entity)
    {
        SqlerRequest request = new SqlerRequest("createSpecialProcess");
        setCondition(request,entity);
        request.addDataParam("create_by", DataType.INT, entity.getCreateBy() == null ? DataContextHolder.getUserId() : entity.getCreateBy());
        request.addDataParam("update_by", DataType.INT, entity.getUpdateBy() == null ? DataContextHolder.getUserId() : entity.getUpdateBy());
        request.addDataParam("complete_time", DataType.TIMESTAMP, entity.getCompleteTime());
        request.addDataParam("create_time", DataType.TIMESTAMP, new Timestamp(System.currentTimeMillis()));
        request.addDataParam("update_time", DataType.TIMESTAMP, new Timestamp(System.currentTimeMillis()));
        Integer autoIncrementId = SqlerTemplate.executeAndReturn(request);
        entity.setId(autoIncrementId);
    }

    @Override
    public void updateSpecialProcess(SpecialProcess entity)
    {
        Assert.notNull(entity.getId(), AssertConstants.NOT_NULL_MSG);
        SqlerRequest request = new SqlerRequest("updateSpecialProcess");
        setCondition(request,entity);
        request.addDataParam("id", DataType.INT, entity.getId());
        request.addDataParam("update_by", DataType.INT, entity.getUpdateBy() == null ? DataContextHolder.getUserId() : entity.getUpdateBy());
        request.addDataParam("complete_time", DataType.TIMESTAMP, entity.getCompleteTime());
        request.addDataParam("update_time", DataType.TIMESTAMP, new Timestamp(System.currentTimeMillis()));
        SqlerTemplate.execute(request);
    }

    @Override
    public void batchUpdateSpecialProcess(List<SpecialProcess> entityList)
    {
        if (entityList != null && !entityList.isEmpty())
        {
            SqlerRequest request = new SqlerRequest("updateSpecialProcess");
            for (SpecialProcess entity : entityList)
            {
                setBatchCondition(request, entity);
                request.addBatchDataParam("id", DataType.INT, entity.getId());
                request.addBatchDataParam("update_by", DataType.INT, entity.getUpdateBy() == null ? DataContextHolder.getUserId() : entity.getUpdateBy());
                request.addBatchDataParam("complete_time", DataType.TIMESTAMP, entity.getCompleteTime());
                request.addBatchDataParam("update_time", DataType.TIMESTAMP, new Timestamp(System.currentTimeMillis()));
                request.addBatch();
            }
            SqlerTemplate.batchUpdate(request);
        }
    }

    // 公共字段设置
    private void setCondition(SqlerRequest request, SpecialProcess query){
        request.addDataParam("purchase_order", DataType.STRING, query.getPurchaseOrder());
        request.addDataParam("warehouse_id", DataType.INT, query.getWarehouseId());
        request.addDataParam("state", DataType.INT, query.getState());
        request.addDataParam("sku", DataType.STRING, query.getSku());
        request.addDataParam("process_quantity", DataType.INT, query.getProcessQuantity());
        request.addDataParam("process_hour", DataType.INT, query.getProcessHour());
        request.addDataParam("process_amount", DataType.DOUBLE, query.getProcessAmount());
        request.addDataParam("apply_reason", DataType.STRING, query.getApplyReason());
        request.addDataParam("supplier_pay_number", DataType.STRING, query.getSupplierPayNumber());
        request.addDataParam("remark", DataType.STRING, query.getRemark());
    }

    // 公共字段批量参数
    private void setBatchCondition(SqlerRequest request, SpecialProcess query){
        request.addBatchDataParam("purchase_order", DataType.STRING, query.getPurchaseOrder());
        request.addBatchDataParam("warehouse_id", DataType.INT, query.getWarehouseId());
        request.addBatchDataParam("state", DataType.INT, query.getState());
        request.addBatchDataParam("sku", DataType.STRING, query.getSku());
        request.addBatchDataParam("process_quantity", DataType.INT, query.getProcessQuantity());
        request.addBatchDataParam("process_hour", DataType.INT, query.getProcessHour());
        request.addBatchDataParam("process_amount", DataType.DOUBLE, query.getProcessAmount());
        request.addBatchDataParam("apply_reason", DataType.STRING, query.getApplyReason());
        request.addBatchDataParam("supplier_pay_number", DataType.STRING, query.getSupplierPayNumber());
        request.addBatchDataParam("remark", DataType.STRING, query.getRemark());
    }

    // 查询字段设置
    private void setQueryCondition(SqlerRequest request, SpecialProcessQueryCondition query)
    {
        if (query == null)
        {
            return;
        }

        // 查询条件不允许出现空字符
        request.setAllowBlank(false);

        // 实体类字段
        setCondition(request,query);
        request.addDataParam("id", DataType.INT, query.getId());
        request.addDataParam("create_by", DataType.INT, query.getCreateBy());
        request.addDataParam("update_by", DataType.INT, query.getUpdateBy());
        request.addDataParam("from_create_time", DataType.STRING, query.getFromCreateTime());
        request.addDataParam("to_create_time", DataType.STRING, query.getToCreateTime());
        request.addDataParam("from_update_time", DataType.STRING, query.getFromCreateTime());
        request.addDataParam("to_update_time", DataType.STRING, query.getToCreateTime());
        request.addDataParam("from_complete_time", DataType.STRING, query.getFromCompleteTime());
        request.addDataParam("to_complete_time", DataType.STRING, query.getToCompleteTime());

        // 搜索字段
        if (query.getSupplierNumber() != null)
        {
            request.addDataParam("supplier_number", DataType.STRING, query.getSupplierNumber());
        }

        if (StringUtils.isNotBlank(query.getPurchaseOrders()))
        {
            List<String> list = CommonUtils.tokenizeToStringList(query.getPurchaseOrders());
            request.addDataParam("purchase_orders", DataType.STRING, list);
        }

        if (StringUtils.isNotBlank(query.getWarehouseIds()))
        {
            List<String> list = CommonUtils.tokenizeToStringList(query.getWarehouseIds());
            request.addDataParam("warehouse_ids", DataType.INT, list);
        }

        if (StringUtils.isNotBlank(query.getStates()))
        {
            List<String> list = CommonUtils.tokenizeToStringList(query.getStates());
            request.addDataParam("states", DataType.INT, list);
        }

        if (StringUtils.isNotBlank(query.getSupplierPayNumbers()))
        {
            List<String> list = CommonUtils.tokenizeToStringList(query.getSupplierPayNumbers());
            request.addDataParam("supplier_pay_numbers", DataType.STRING, list);
        }

        if (StringUtils.isNotBlank(query.getSkus()))
        {
            List<String> list = CommonUtils.tokenizeToStringList(query.getSkus());
            request.addDataParam("skus", DataType.STRING, list);
        }

        if (StringUtils.isNotBlank(query.getLikeApplyReason()))
        {
            request.addFuzzyDataParam("like_apply_reason", query.getLikeApplyReason());
        }

        if (StringUtils.isNotBlank(query.getLikeRemark()))
        {
            request.addFuzzyDataParam("like_remark", query.getLikeRemark());
        }

        // 权限字段
        if (CollectionUtils.isNotEmpty(query.getIds()))
        {
            request.addDataParam("ids", DataType.INT, query.getIds());
        }

        if (CollectionUtils.isNotEmpty(query.getWarehouseIdList()))
        {
            request.addDataParam("warehouse_id_list", DataType.INT, query.getWarehouseIdList());
        }

        if (CollectionUtils.isNotEmpty(query.getSubUsers()))
        {
            request.addDataParam("user_list", DataType.INT, query.getSubUsers());
        }
    }
}