package com.bessky.pss.portal.business.specialprocess.util;

import com.bessky.erp.warehouse.manager.bean.Warehouse;
import com.bessky.platform.cache.CacheQuerier;
import com.bessky.platform.log.Log;
import com.bessky.platform.log.LogFactory;
import com.bessky.pss.common.cache.utils.WarehouseCacheUtils;
import com.bessky.pss.common.resouces.CacheName;
import com.bessky.pss.common.util.ExcelUtils;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcess;
import com.bessky.pss.portal.business.specialprocess.common.SpecialProcessStatus;
import com.bessky.pss.portal.system.user.bean.PortalUser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * 特殊加工审批单导出工具
 *
 * @author 丁光辉
 * @version Bessky V100R001 2021年10月18日
 * @since Bessky V100R001C00
 */
public class SpecialProcessExportUtils
{

    private static final Log logger = LogFactory.getLog(SpecialProcessExportUtils.class);

    public static void exportExcel(OutputStream output, List<SpecialProcess> specialProcessList)
    {

        if (CollectionUtils.isEmpty(specialProcessList))
        {
            return;
        }

        // 建立新HSSFWorkbook对象
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        XSSFRow titleRow = sheet.createRow(0);

        String[] titles = new String[]{"采购单号", "供应商编号", "仓库", "状态", "SKU", "申请加工数量", "申请加工工时", "申请加工金额", "申请原因", "供应商支付流水号", "备注", "缺货数量", "创建时间", "创建人", "完成时间"};

        // 表头
        for (int i = 0, len = titles.length; i < len; i++)
        {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(titles[i]);
            
        }

        CellStyle cs = wb.createCellStyle();  
        cs.setWrapText(true); 
        
        // 表数据
        int size = specialProcessList.size();
        for (int rowNum = 0; rowNum < size; rowNum++)
        {
            Row row = sheet.createRow(rowNum + 1);

            SpecialProcess specialProcess = specialProcessList.get(rowNum);

            int cellNum = 0;

            // 采购单号
            Cell cell = row.createCell(cellNum++);
            String purchaseOrder = specialProcess.getPurchaseOrder();
            cell.setCellValue(Optional.ofNullable(purchaseOrder).orElse(""));

            // 供应商编号
            cell = row.createCell(cellNum++);
            String supplierNumber = specialProcess.getSupplierNumber();
            cell.setCellValue(Optional.ofNullable(supplierNumber).orElse(""));

            // 仓库
            cell = row.createCell(cellNum++);
            Integer warehouseId = specialProcess.getWarehouseId();
            Warehouse warehouse = Optional.ofNullable(warehouseId).map(id -> WarehouseCacheUtils.getWarehouse(id)).orElse(null);
            cell.setCellValue(Optional.ofNullable(warehouse).map(w -> w.getWarehouseName()).orElse(""));

            // 状态
            cell = row.createCell(cellNum++);
            Integer state = specialProcess.getState();
            cell.setCellValue(Optional.ofNullable(state).map(s -> SpecialProcessStatus.build(s.toString()).display()).orElse(""));

            // SKU
            cell = row.createCell(cellNum++);
            String sku = specialProcess.getSku();
            cell.setCellValue(Optional.ofNullable(sku).orElse(""));

            // 申请加工数量
            cell = row.createCell(cellNum++);
            Integer processQuantity = specialProcess.getProcessQuantity();
            cell.setCellValue(Optional.ofNullable(processQuantity).map(p -> p.toString()).orElse(""));

            // 申请加工工时
            cell = row.createCell(cellNum++);
            Integer processHour = specialProcess.getProcessHour();
            cell.setCellValue(Optional.ofNullable(processHour).map(p -> p.toString()).orElse(""));

            // 申请加工金额
            cell = row.createCell(cellNum++);
            Double processAmount = specialProcess.getProcessAmount();
            cell.setCellValue(Optional.ofNullable(processAmount).map(p -> p.toString()).orElse(""));

            // 申请原因
            cell = row.createCell(cellNum++);
            String applyReason = specialProcess.getApplyReason();
            cell.setCellValue(Optional.ofNullable(applyReason).orElse(""));

            // 供应商支付流水号
            cell = row.createCell(cellNum++);
            String supplierPayNumber = specialProcess.getSupplierPayNumber();
            cell.setCellValue(Optional.ofNullable(supplierPayNumber).orElse(""));

            // 备注
            cell = row.createCell(cellNum++);
            String remark = specialProcess.getRemark();
            cell.setCellValue(Optional.ofNullable(remark).orElse(""));

            // 缺货数量
            cell = row.createCell(cellNum++);
            Integer stockout = specialProcess.getStockout();
            cell.setCellValue(Optional.ofNullable(stockout).map(s -> s.toString()).orElse(""));

            // 创建时间
            cell = row.createCell(cellNum++);
            Timestamp createTime = specialProcess.getCreateTime();
            ExcelUtils.setTimestampValue(cell, createTime);

            // 创建人
            cell = row.createCell(cellNum++);
            Integer createBy = specialProcess.getCreateBy();
            PortalUser user = CacheQuerier.get(CacheName.USER_CACHE, createBy.toString(), PortalUser.class);
            cell.setCellValue(Optional.ofNullable(user).map(u -> u.getName()).orElse(""));

            // 完成时间
            cell = row.createCell(cellNum++);
            Timestamp completeTime = specialProcess.getCompleteTime();
            ExcelUtils.setTimestampValue(cell, completeTime);
        }

        try
        {
            wb.write(output);
        }
        catch (IOException e)
        {
            logger.error("exportExcel(String, List<SpecialProcess>) - exception ignored", e);
        }
        finally
        {
            try
            {
                wb.close();
            }
            catch (IOException e)
            {
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("exportExcel(String, List<SpecialProcess>) - end");
        }
    }
}
