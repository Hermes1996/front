package com.bessky.pss.portal.business.specialprocess.action;

import com.bessky.erp.core.common.bean.Pager;
import com.bessky.erp.core.common.dto.QueryListDTO;
import com.bessky.erp.warehouse.manager.bean.Warehouse;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcess;
import com.bessky.erp.warehouse.specialprocessapproval.bean.SpecialProcessQueryCondition;
import com.bessky.erp.warehouse.specialprocessapproval.facade.SpecialProcessApprovalFacade;
import com.bessky.platform.action.BaseController;
import com.bessky.platform.component.StatusCode;
import com.bessky.platform.context.DataContextHolder;
import com.bessky.platform.json.ResponseJson;
import com.bessky.platform.rbac.permission.PermissionAnnotation;
import com.bessky.pss.common.cache.SystemCacheQuerier;
import com.bessky.pss.common.cache.utils.SystemParamCacheUtils;
import com.bessky.pss.common.dict.ExportType;
import com.bessky.pss.common.log.PortalLog;
import com.bessky.pss.common.log.PortalLogFactory;
import com.bessky.pss.common.resouces.ModuleName;
import com.bessky.pss.common.resouces.PermissionCode;
import com.bessky.pss.common.resouces.SessionKeyConstants;
import com.bessky.pss.common.util.CommonUtils;
import com.bessky.pss.common.util.ValidationUtils;
import com.bessky.pss.portal.business.approval.domain.ExceptionFeedbackApprovalDo;
import com.bessky.pss.portal.business.purchase.bean.PurchaseOrder;
import com.bessky.pss.portal.business.purchase.bean.PurchaseOrderQueryCondition;
import com.bessky.pss.portal.business.purchase.service.PurchaseOrderService;
import com.bessky.pss.portal.business.specialprocess.common.SpecialProcessStatus;
import com.bessky.pss.portal.business.specialprocess.domain.SpecialProcessDo;
import com.bessky.pss.portal.business.specialprocess.util.SpecialProcessExportUtils;
import com.bessky.pss.portal.business.stock.bean.Stock;
import com.bessky.pss.portal.business.stock.service.StockService;
import com.bessky.pss.portal.business.warehouse.util.WarehouseUtils;
import com.bessky.pss.portal.system.log.bean.OperationLog;
import com.bessky.pss.portal.system.log.util.OperationLogUtil;
import com.bessky.pss.portal.system.user.bean.PortalUser;
import com.bessky.pss.portal.system.user.service.UserService;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * 特殊加工审批控制层
 *
 * @author 丁光辉
 * @version Bessky V100R001 2021年10月18日
 * @since Bessky V100R001C00
 */
@Controller
@RequestMapping(value = "special/process/approvals")
public class SpecialProcessApprovalController extends BaseController
{
    @Resource
    private SpecialProcessApprovalFacade specialProcessApprovalFacade;

    @Resource
    private UserService userService;

    @Resource
    private PurchaseOrderService purchaseOrderService;

    @Resource
    private StockService stockService;

    /**
     * 特殊加工日志模块
     */
    private static final PortalLog portalLogger = PortalLogFactory.getLog(ModuleName.SPECIAL_PROCESS_APPROVAL);

    private void initFormData(@ModelAttribute("domain") SpecialProcessDo domain)
    {
        SpecialProcessQueryCondition query = domain.getQuery();
        query = Optional.ofNullable(query).orElse(new SpecialProcessQueryCondition());

        // 仓库映射
        List<Warehouse> warehouses = WarehouseUtils.queryAuthWarehouses();
        domain.setWarehouses(warehouses);

        // 采购人员
        List<PortalUser> purchaseUsers = SystemCacheQuerier.getPurchaseDepartment();

        // 仓库人员
        List<PortalUser> warehouseUsers = SystemCacheQuerier.getWarehouseDepartment();

        // 申请人映射
        List<PortalUser> createByList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(purchaseUsers) && CollectionUtils.isNotEmpty(warehouseUsers))
        {
            createByList.addAll(purchaseUsers);
            createByList.addAll(warehouseUsers);
            domain.setCreateByList(createByList);
        }

        // 授权仓库列表
        List<Integer> authedWarehouses = new ArrayList<>();
        warehouses.forEach(warehouse -> authedWarehouses.add(warehouse.getWarehouseId()));

        // 权限控制
        if (!ValidationUtils.isSuperUser() && !ValidationUtils.auth(PermissionCode.GLOBAL_SPECIAL_PROCESS))
        {
            query.setWarehouseIdList(authedWarehouses);
            List<Integer> groupIds = SystemParamCacheUtils.getIntegerList("GROUP_DEFAULT_DEPARTMENT.PURCHASE");

            PortalUser user = SystemCacheQuerier.getUser(DataContextHolder.getUserId());
            Integer departmentId = Optional.ofNullable(user).map(u -> u.getDepartment()).map(d -> d.getDepartmentId()).orElse(0);

            // 采购部可以查看自己和下级的单子
            if (groupIds.contains(departmentId))
            {
                query.setSubUsers(userService.querySubUsers(DataContextHolder.getUserId()));
            }

        }
        domain.setQuery(query);
    }

    private void querySpecialProcess(@ModelAttribute("domain") SpecialProcessDo domain)
    {
        SpecialProcessQueryCondition query = domain.getQuery();
        Pager page = domain.getPage();
        if (query == null)
        {
            query = new SpecialProcessQueryCondition();
            domain.setQuery(query);
        }
        QueryListDTO<SpecialProcess> specialProcessList = specialProcessApprovalFacade.querySpecialProcessListDTO(domain.getQuery(), page);
        List<SpecialProcess> list = specialProcessList.getList();

        // 设置额外数据
        setAdditionalData(list);

        domain.setSpecialProcessList(list);
        domain.setPage(specialProcessList.getPager());
    }

    /**
     *
     * 特殊加工审批初始化页面
     *
     * @author 丁光辉
     * @param domain
     * @return
     */
    @PermissionAnnotation(permission = "11161300")
    @RequestMapping(method = { RequestMethod.GET })
    public String init(@ModelAttribute("domain") SpecialProcessDo domain)
    {
        // 初始化数据
        initFormData(domain);

        // 条件查询
        querySpecialProcess(domain);

        return "specialprocess/specialprocess_approval_list";
    }

    /**
     *
     * 特殊加工审批查询
     *
     * @author 丁光辉
     * @param domain
     * @return
     */
    @PermissionAnnotation(permission = "11161300")
    @RequestMapping(value = "search", method = {RequestMethod.POST})
    public String search(@ModelAttribute("domain") SpecialProcessDo domain)
    {
        initFormData(domain);

        querySpecialProcess(domain);

        return "specialprocess/specialprocess_approval_list";
    }

    /**
     *
     * 新增特殊加工审批页面
     *
     * @author 丁光辉
     * @param domain
     * @return
     */
    @PermissionAnnotation(permission = "11161302")
    @RequestMapping(value = "create", method = { RequestMethod.GET })
    public String toCreateSpecialProcess(@ModelAttribute("domain") SpecialProcessDo domain)
    {
        return "specialprocess/specialprocess_approval_add";
    }

    /**
     * 新增特殊加工审批
     *
     * @param domain
     * @param session
     * @return
     */
    @PermissionAnnotation(permission = "11161302")
    @RequestMapping(value = "create", method = { RequestMethod.POST })
    public String createSpecialProcess(@ModelAttribute("domain") SpecialProcessDo domain, HttpSession session)
    {
        SpecialProcess specialProcess = domain.getSpecialProcess();

        // 创建默认为待确认
        specialProcess.setState(SpecialProcessStatus.TO_BE_CONFIRMED.intCode());
        specialProcess.setCreateBy(DataContextHolder.getUserId());
        specialProcessApprovalFacade.createSpecialProcess(specialProcess);
        session.setAttribute(SessionKeyConstants.TIPS, SessionKeyConstants.TIPS_ADD);

        return "redirect:/special/process/approvals?query.state=1";
    }

    /**
     *
     * 编辑特殊加工审批页面
     *
     * @author 丁光辉
     * @param domain
     * @return
     */
    @PermissionAnnotation(permission = "11161303")
    @RequestMapping(value = "update", method = { RequestMethod.GET })
    public String toUpdateSpecialProcess(@ModelAttribute("domain") SpecialProcessDo domain, @RequestParam("specialProcessId") Integer specialProcessId)
    {
        SpecialProcess specialProcess = specialProcessApprovalFacade.getSpecialProcess(specialProcessId);
        domain.setSpecialProcess(specialProcess);
        return "specialprocess/specialprocess_approval_edit";
    }

    /**
     * 编辑特殊加工审批
     *
     * @param domain
     * @param session
     * @return
     */
    @PermissionAnnotation(permission = "11161303")
    @RequestMapping(value = "update", method = { RequestMethod.POST })
    public String updateSpecialProcess(@ModelAttribute("domain") SpecialProcessDo domain, HttpSession session)
    {
        SpecialProcess specialProcess = domain.getSpecialProcess();
        specialProcess.setUpdateBy(DataContextHolder.getUserId());
        specialProcessApprovalFacade.updateSpecialProcess(specialProcess);

        portalLogger.log(specialProcess.getId(), "修改特殊加工审批");
        session.setAttribute(SessionKeyConstants.TIPS, SessionKeyConstants.TIPS_UPDATE);

        return "redirect:/special/process/approvals";
    }

    /**
     * 批量驳回
     *
     * @author 丁光辉
     * @param domain
     * @param specialProcessIds
     * @param rejectReason
     * @return
     */
    @PermissionAnnotation(permission = "11161309")
    @PostMapping("reject/batch")
    @ResponseBody
    public ResponseJson batchReject(@ModelAttribute("domain") ExceptionFeedbackApprovalDo domain, @RequestParam("specialProcessIds") List<Integer> specialProcessIds,
                                    @RequestParam("rejectReason") String rejectReason)
    {
        ResponseJson response = new ResponseJson();

        if (CollectionUtils.isEmpty(specialProcessIds) || StringUtils.isBlank(rejectReason))
        {
            response.setStatus(StatusCode.FAIL);
            response.setMessage("未选择审批单或未填写反馈内容");
            return response;
        }

        SpecialProcessQueryCondition query = new SpecialProcessQueryCondition();
        query.setIds(specialProcessIds);
        List<SpecialProcess> specialProcessList = specialProcessApprovalFacade.querySpecialProcessList(query, null);

        for (SpecialProcess specialProcess : specialProcessList)
        {
            // 待确定、已完成、废弃状态下不能驳回
            if (specialProcess.getState() == SpecialProcessStatus.TO_BE_CONFIRMED.intCode() || specialProcess.getState() == SpecialProcessStatus.COMPLETED.intCode() || specialProcess.getState() == SpecialProcessStatus.DISCARD.intCode())
            {
                response.setStatus(StatusCode.FAIL);
                response.setMessage("修改失败，[待确定]、[已完成]、[废弃]状态下不能驳回");
                return response;
            }
        }
        List<SpecialProcess> list = specialProcessList.stream().map(s -> {
            SpecialProcess specialProcess = new SpecialProcess();
            specialProcess.setId(s.getId());
            Integer state = s.getState();
            specialProcess.setState(state == 1 ? 1 : state - 1);
            return specialProcess;
        }).collect(Collectors.toList());

        specialProcessApprovalFacade.batchUpdateSpecialProcess(list);

        for (SpecialProcess specialProcess : list)
        {
            portalLogger.log(specialProcess.getId(), "批量驳回，驳回原因:" + rejectReason);
        }

        return response;
    }

    /**
     * 批量废弃
     *
     * @author 丁光辉
     * @param domain
     * @param specialProcessIds
     * @param rejectReason
     * @return
     */
    @PermissionAnnotation(permission = "11161308")
    @PostMapping("discards/batch")
    @ResponseBody
    public ResponseJson batchMoveDiscards(@ModelAttribute("domain") SpecialProcessDo domain, @RequestParam("specialProcessIds") List<Integer> specialProcessIds, @RequestParam("rejectReason") String rejectReason)
    {
        ResponseJson response = new ResponseJson();

        if (CollectionUtils.isEmpty(specialProcessIds) || StringUtils.isBlank(rejectReason))
        {
            response.setStatus(StatusCode.FAIL);
            response.setMessage("未选择审批单或未填写反馈内容");
            return response;
        }

        SpecialProcessQueryCondition query = new SpecialProcessQueryCondition();
        query.setIds(specialProcessIds);
        List<SpecialProcess> specialProcessList = specialProcessApprovalFacade.querySpecialProcessList(query, null);

        for (SpecialProcess specialProcess : specialProcessList)
        {
            // 已完成、废弃状态下不能废弃
            if (specialProcess.getState() == SpecialProcessStatus.COMPLETED.intCode() || specialProcess.getState() == SpecialProcessStatus.DISCARD.intCode())
            {
                response.setStatus(StatusCode.FAIL);
                response.setMessage("修改失败，[已完成]、[废弃]状态下不能废弃");
                return response;
            }
        }

        List<SpecialProcess> list = specialProcessIds.stream().map(id -> {
            SpecialProcess specialProcess = new SpecialProcess();
            specialProcess.setId(id);
            specialProcess.setState(SpecialProcessStatus.DISCARD.intCode());
            return specialProcess;
        }).collect(Collectors.toList());

        specialProcessApprovalFacade.batchUpdateSpecialProcess(list);

        for (SpecialProcess specialProcess : list)
        {
            portalLogger.log(specialProcess.getId(), "批量废弃，废弃原因:" + rejectReason);
        }

        return response;
    }

    /**
     *
     * 特殊加工状态更新
     *
     * @author 丁光辉
     * @param domain
     * @param session
     * @param specialProcessId
     * @param targetState
     * @param reason
     * @return
     */
    @RequestMapping(value = "updateState", method = { RequestMethod.POST })
    @ResponseBody
    public ResponseJson updateState(@ModelAttribute("domain") SpecialProcessDo domain, HttpSession session, @RequestParam("specialProcessId") Integer specialProcessId,
                                    @RequestParam("targetState") Integer targetState, @RequestParam(value = "reason", required = false) String reason)
    {
        String info = "";
        ResponseJson response = new ResponseJson();
        SpecialProcess specialProcess = specialProcessApprovalFacade.getSpecialProcess(specialProcessId);

        if (targetState == SpecialProcessStatus.TO_BE_CONFIRMED.intCode())
        {
            response.setStatus(StatusCode.FAIL);
            response.setMessage("目标状态不能为待确定");
            return response;
        }

        Integer state = specialProcess.getState();
        if (targetState - state > 1)
        {
            response.setStatus(StatusCode.FAIL);
            response.setMessage("目标状态不能越流程");
            return response;
        }

        switch (targetState)
        {
            case 2:
                specialProcess.setState(SpecialProcessStatus.PURCHASING_MANAGER_CONFIRM.intCode());
                info = SpecialProcessStatus.PURCHASING_MANAGER_CONFIRM.display();
                break;
            case 3:
                specialProcess.setState(SpecialProcessStatus.WAREHOUSE_MANAGER_CONFIRMED.intCode());
                info = SpecialProcessStatus.WAREHOUSE_MANAGER_CONFIRMED.display();
                break;
            case 4:
                specialProcess.setState(SpecialProcessStatus.COMPLETED.intCode());
                specialProcess.setCompleteTime(new Timestamp(System.currentTimeMillis()));
                info = SpecialProcessStatus.COMPLETED.display();
                break;
            case 5:
                specialProcess.setState(SpecialProcessStatus.DISCARD.intCode());
                info = StringUtils.join(SpecialProcessStatus.DISCARD.display(),"，废弃原因：", reason);
                break;
            default:
                break;
        }

        if (StringUtils.isNotBlank(info))
        {
            specialProcessApprovalFacade.updateSpecialProcess(specialProcess);
            portalLogger.log(specialProcessId, info);
        }
        session.setAttribute(SessionKeyConstants.TIPS, SessionKeyConstants.TIPS_UPDATE);
        return response;
    }

    /**
     *
     * 快速修改备注
     *
     * @author 丁光辉
     * @param domain
     * @return
     */
    @PermissionAnnotation(permission = "11161303")
    @RequestMapping(value = "quick/update", method = { RequestMethod.POST })
    @ResponseBody
    public ResponseJson quickUpdateRemark(@ModelAttribute("domain") SpecialProcessDo domain)
    {
        ResponseJson response = new ResponseJson();

        SpecialProcess specialProcess = domain.getSpecialProcess();

        portalLogger.log(specialProcess.getId(), "修改备注：" + specialProcess.getRemark());

        specialProcessApprovalFacade.updateSpecialProcess(specialProcess);
        return response;
    }

    /**
     *
     * 导出特殊加工审批单
     *
     * @author 丁光辉
     * @param domain
     * @param exportType
     * @param response
     */
    @PermissionAnnotation(permission = "11161304")
    @RequestMapping(value = "export", method = { RequestMethod.POST })
    public void exportExcel(@ModelAttribute("domain") SpecialProcessDo domain, @RequestParam("exportType") String exportType, @RequestParam(value = "specialProcessIds", required = false) List<Integer> specialProcessIds, HttpServletResponse response)
    {
        SpecialProcessQueryCondition query = domain.getQuery();
        query = Optional.ofNullable(query).orElse(new SpecialProcessQueryCondition());
        List<SpecialProcess> specialProcessList = new ArrayList<>();
        ExportType exportEnum = ExportType.build(exportType);

        switch (exportEnum)
        {
            case ALL:
                specialProcessList = specialProcessApprovalFacade.querySpecialProcessList(query, null);
                break;
            case PAGE:
                Pager pager = domain.getPage();
                specialProcessList = specialProcessApprovalFacade.querySpecialProcessList(query, pager);
                break;
            case CHECKED:
                if (CollectionUtils.isNotEmpty(specialProcessIds))
                {
                    query.setIds(specialProcessIds);
                    specialProcessList = specialProcessApprovalFacade.querySpecialProcessList(query, null);
                }
                break;
            default:
                break;
        }

        String filename = "特殊加工审批单.xls";

        // 格式化新的文件名，防止文件覆盖
        String newFilename = CommonUtils.newFileNameByTime(filename);
        response.setHeader("Content-Disposition", "attachment; filename=" + newFilename);
        response.setContentType("application/vnd.ms-excel; charset=utf-8");

        OutputStream output = null;
        try
        {
            output = response.getOutputStream();
            SpecialProcessExportUtils.exportExcel(output, specialProcessList);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            IOUtils.closeQuietly(output);
        }
    }

    /**
     * 特殊加工审批日志
     *
     * @param domain
     * @param specialProcessId
     * @return
     */
    @RequestMapping(value = "log", method = { RequestMethod.GET })
    public String viewLog(@ModelAttribute("domain") SpecialProcessDo domain, @RequestParam("specialProcessId") Integer specialProcessId)
    {
        List<OperationLog> logs = OperationLogUtil.queryOperationLogList(specialProcessId, ModuleName.SPECIAL_PROCESS_APPROVAL);
        domain.setLogs(logs);

        return "log/log";
    }

    /**
     * 设置额外数据
     *
     * @author 丁光辉
     * @param specialProcessList
     */
    public void setAdditionalData(List<SpecialProcess> specialProcessList)
    {
        if (CollectionUtils.isEmpty(specialProcessList))
        {
            return;
        }

        // 采购单号列表
        List<String> purchaseOrderList = specialProcessList.stream().map(s -> s.getPurchaseOrder()).distinct()
                .filter(Objects::nonNull).collect(Collectors.toList());

        // 采购单映射
        Map<String, PurchaseOrder> purchaseOrderMap = Maps.newHashMap();

        if (CollectionUtils.isNotEmpty(purchaseOrderList))
        {
            // 获取对应采购单
            PurchaseOrderQueryCondition query = new PurchaseOrderQueryCondition();
            query.setPurchaseOrderList(purchaseOrderList);
            List<PurchaseOrder> purchaseOrders = purchaseOrderService.queryPurchaseOrderAndSupplier(query);
            purchaseOrderList.clear();

            if (CollectionUtils.isNotEmpty(purchaseOrders))
            {
                purchaseOrderMap = purchaseOrders.stream().collect(HashMap::new, (m, v) -> m.put(v.getPurchaseOrder(), v), HashMap::putAll);
                purchaseOrders.clear();
            }
        }

        // SKU列表
        List<String> skuList = specialProcessList.stream().map(approval -> approval.getSku()).distinct()
                .filter(Objects::nonNull).collect(Collectors.toList());

        // 库存映射
        Map<String, Stock> stockOutMap = Maps.newHashMap();

        if (CollectionUtils.isNotEmpty(skuList))
        {
            // 获取库存
            List<Stock> stockOutBySku = stockService.getStockOutBySku(skuList);

            if (CollectionUtils.isNotEmpty(stockOutBySku))
            {
                stockOutMap = stockOutBySku.stream().collect(HashMap::new, (m, v) -> m.put(v.getSku(), v), HashMap::putAll);
                stockOutBySku.clear();
            }
        }

        for (SpecialProcess specialProcess : specialProcessList)
        {
            // 设置供应商编号
            String purchaseOrderStr = specialProcess.getPurchaseOrder();
            PurchaseOrder purchaseOrder = purchaseOrderMap.get(purchaseOrderStr);

            String supplier = Optional.ofNullable(purchaseOrder).map(p -> p.getSupplier()).map(s -> s.getSupplierNumber()).orElse(null);
            if (org.apache.commons.lang.StringUtils.isNotEmpty(supplier))
            {
                specialProcess.setSupplierNumber(supplier);
            }

            // 设置缺货数量
            Stock stock = stockOutMap.get(specialProcess.getSku());
            if (stock != null && stock.getStockout() != null)
            {
                specialProcess.setStockout(stock.getStockout());
            }
        }
        purchaseOrderMap.clear();
        stockOutMap.clear();
    }
}