<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>特殊加工审批列表</title>
    <app:include type="css" />
    <link rel="stylesheet" href="${CUSTOM_THEME_PATH}js/ztree/zTreeStyle.css" type="text/css" />
    <link rel="stylesheet" type="text/css" href="${CUSTOM_THEME_PATH}assets/plugins/bootstrap-editable/bootstrap-editable/css/bootstrap-editable.css" />
</head>

<body>
<app:header active="11000000" fixed="false" />
<div class="mb50">
    <app:nav active="11161300" ancestor="11160000" />

    <app:searchform id="search-form" action="${CONTEXT_PATH}special/process/approvals/search" onsubmit="search(this);" pageno="${domain.page.pageNo}" pagesize="${domain.page.pageSize}" totalcount="${domain.page.totalCount}">
        <app:searchitem label="供应商编号">
            <app:input name="query.supplierNumber" classes="input-large" maxlength="50" placeholder="请输入单个供应商编号进行查询" />
        </app:searchitem>

        <app:searchitem label="采购单号">
            <app:input name="query.purchaseOrders" classes="input-large" maxLen="1000" placeholder="多个采购单号查询逗号隔开" />
        </app:searchitem>

        <app:searchitem label="仓库">
            <app:select id="warehouseId" name="query.warehouseIds" classes="input-medium" multiple="true" items="${domain.warehouses}" listkey="warehouseId" listvalue="warehouseName" headkey="" headvalue="" />
        </app:searchitem>

        <app:searchitem label="状态">
            <app:select id="state" name="query.states" dictkey="SpecialProcessStatus" classes="input-medium" order="false" multiple="true" headkey="" headvalue="" />
        </app:searchitem>

        <app:searchitem label="交易流水号">
            <app:input name="query.supplierPayNumbers" classes="input-large" maxLen="1000" placeholder="多个交易流水号查询逗号隔开" />
        </app:searchitem>

        <app:searchitem label="SKU">
            <app:input name="query.skus" classes="input-large" maxLen="1000" placeholder="多个SKU查询逗号隔开" />
        </app:searchitem>

        <app:searchitem label="申请人">
            <app:select name="query.createdBy" classes="input-medium" items="${domain.createByList}" listkey="userId" listvalue="display" headkey="" headvalue="" select2="true" />
        </app:searchitem>

        <app:searchitem label="申请原因">
            <app:input name="query.likeApplyReason" classes="input-large" maxLen="100" placeholder="申请原因支持模糊查询" />
        </app:searchitem>

        <app:searchitem label="备注">
            <app:input name="query.likeRemark" classes="input-large" maxLen="500" placeholder="备注支持模糊查询" />
        </app:searchitem>

        <app:searchitem label="创建时间">
            <div class="input-group input-large">
                <app:datetime name="query.fromCreateTime" dateFmt="yyyy-MM-dd 00:00:00" readonly="true" alwaysUseStartDate="true" />
                <span class="input-group-addon">到</span>
                <app:datetime name="query.toCreateTime" dateFmt="yyyy-MM-dd 23:59:59" readonly="true" alwaysUseStartDate="true" />
            </div>
        </app:searchitem>

        <app:searchitem label="完成时间">
            <div class="input-group input-large">
                <app:datetime name="query.fromCompleteTime" dateFmt="yyyy-MM-dd 00:00:00" readonly="true" alwaysUseStartDate="true" />
                <span class="input-group-addon">到</span>
                <app:datetime name="query.toCompleteTime" dateFmt="yyyy-MM-dd 23:59:59" readonly="true" alwaysUseStartDate="true" />
            </div>
        </app:searchitem>
    </app:searchform>

    <!-- 移采购经理审批-->
    <c:set var="specialProcessPeople" value="${app:auth('11161305')}" />
    <!-- 采购经理确认-->
    <c:set var="purchasingManagerConfirm" value="${app:auth('11161306')}" />
    <!-- 仓库经理确认-->
    <c:set var="warehouseManagerConfirm" value="${app:auth('11161307')}" />
    <!-- 批量废弃 -->
    <c:set var="authDiscard" value="${app:auth('11161308') }" />
    <!-- 批量驳回 -->
    <c:set var="authReject" value="${app:auth('11161309') }" />
    <!-- 导出 -->
    <c:set var="export" value="${app:auth('11161304') }" />
    <!-- 新增 -->
    <c:set var="add" value="${app:auth('11161302') }" />
    <!-- 编辑 -->
    <c:set var="edit" value="${app:auth('11161303') }" />

    <div class="mt10 mb10">
        <button type="button" class="btn btn-sm btn-default" onclick="checkAllItems();">全选</button>
        <button type="button" class="btn btn-sm btn-default" onclick="checkNegative();">反选</button>
        <c:if test="${add}">
            <button type="button" class="btn btn-sm green" onclick="location.href='${CONTEXT_PATH}special/process/approvals/create'">
                <i class="icon-plus"></i>新建审批单
            </button>
        </c:if>
        <c:if test="${export}">
            <button type="button" class="btn btn-sm btn-default" id="export">
                <i class="icon-download"></i>导出审批单
            </button>
        </c:if>
        <c:if test="${authDiscard}">
            <button type="button" data-clipboard-text="" class="btn btn-sm btn-default" onclick="batchDiscard()">批量废弃</button>
        </c:if>
        <c:if test="${authReject}">
            <button type="button" data-clipboard-text="" class="btn btn-sm btn-default" onclick="batchReject(-1)">批量驳回</button>
        </c:if>
    </div>

    <form name="mainForm" action="" id="submit-form" method="post">
        <table class="table table-striped table-bordered table-condensed"  id="data-table">
            <thead>
            <tr class="">
                <th style="width: 20px;"></th>
                <th data-sort="string">供应商编号</th>
                <th>采购单号</th>
                <th>仓库</th>
                <th>状态</th>
                <th>SKU</th>
                <th>申请人</th>
                <th>申请加工数量</th>
                <th>申请工时</th>
                <th>申请金额</th>
                <th>申请原因</th>
                <th>供应商交易流水号</th>
                <th>缺货数量</th>
                <th>备注</th>
                <th>创建时间</th>
                <th>完成时间</th>
                <th max-width="200">操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${domain.specialProcessList}" var="specialProcess" varStatus="status">
                <tr id="tr-product-${specialProcess.id}">
                    <td>
                        <label>
                            <input type="checkbox" name="specialProcessIds" value="${specialProcess.id}" autocomplete="off" />
                        </label>
                    </td>
                    <td>${specialProcess.supplierNumber}</td>
                    <td>${specialProcess.purchaseOrder}</td>
                    <td>${app:j2cache('J2CACHE_WAREHOUSE',specialProcess.warehouseId).warehouseName}</td>
                    <td>${app:dict('SpecialProcessStatus', specialProcess.state) }</td>
                    <td>${specialProcess.sku}</td>
                    <td>${app:fullname(specialProcess.createBy)}</td>
                    <td>${specialProcess.processQuantity}</td>
                    <td>${specialProcess.processHour}</td>
                    <td>${specialProcess.processAmount}</td>
                    <td style="white-space:nowrap;overflow:hidden;text-overflow: ellipsis;" title="${specialProcess.applyReason}">
                        <div style="width:85px">${specialProcess.applyReason}</div>
                    </td>
                    <td>${specialProcess.supplierPayNumber}</td>
                    <td>${specialProcess.stockout}</td>
                    <td>
                        <c:choose>
                            <c:when test="${edit}">
                                <div class="font-small word-wrap" style="width: 100px;"><a data-original-title="输入备注" data-pk="${specialProcess.id}" data-type="textarea" href="#" class="location-remark">${specialProcess.remark}</a></div>
                            </c:when>
                            <c:otherwise>${specialProcess.remark}</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <fmt:formatDate value="${specialProcess.createTime}" pattern="yyyy-MM-dd HH:mm:ss" />
                    </td>
                    <td>
                        <fmt:formatDate value="${specialProcess.completeTime}" pattern="yyyy-MM-dd HH:mm:ss" />
                    </td>

                    <td style="width: 283px;" nowrap="nowrap">
                        <c:choose>
                            <c:when test="${specialProcess.state == SpecialProcessStatus.TO_BE_CONFIRMED}">
                                <c:if test="${specialProcessPeople}">
                                    <c:if test="${edit}">
                                        <a class="btn btn-xs btn-default" href="${CONTEXT_PATH}special/process/approvals/update?specialProcessId=${specialProcess.id}" target="_blank">
                                            <span class="icon-edit"></span>编辑
                                        </a>
                                    </c:if>
                                    <a class="btn btn-xs btn-default" onclick="updateState(${specialProcess.id}, 2)" title="移采购经理审批">
                                        <i class="icon-ok"></i>移采购经理审批
                                    </a>
                                    <c:if test="${authDiscard}">
                                        <a class="btn btn-xs btn-default" onclick="updateState(${specialProcess.id}, 5" title="废弃">
                                            <i class="icon-ok"></i>废弃
                                        </a>
                                    </c:if>
                                </c:if>
                            </c:when>

                            <c:when test="${specialProcess.state == SpecialProcessStatus.PURCHASING_MANAGER_CONFIRM}">
                                <c:if test="${purchasingManagerConfirm }">
                                    <c:if test="${edit}">
                                        <a class="btn btn-xs btn-default" href="${CONTEXT_PATH}special/process/approvals/update?specialProcessId=${specialProcess.id}" target="_blank">
                                            <span class="icon-edit"></span>编辑
                                        </a>
                                    </c:if>
                                    <a class="btn btn-xs btn-default" onclick="updateState(${specialProcess.id}, 3)" title="移仓库经理审批">
                                        <i class="icon-ok"></i>移仓库经理审批
                                    </a>
                                    <c:if test="${authReject}">
                                        <a class="btn btn-xs btn-default" onclick="batchReject(${specialProcess.id})" title="驳回">
                                            <i class="icon-ok"></i>驳回
                                        </a>
                                    </c:if>
                                    <c:if test="${authDiscard}">
                                        <a class="btn btn-xs btn-default" onclick="updateState(${specialProcess.id}, 5)" title="废弃">
                                            <i class="icon-ok"></i>废弃
                                        </a>
                                    </c:if>
                                </c:if>
                            </c:when>

                            <c:when test="${specialProcess.state == SpecialProcessStatus.WAREHOUSE_MANAGER_CONFIRMED}">
                                <c:if test="${warehouseManagerConfirm}">
                                    <a class="btn btn-xs btn-default" onclick="updateState(${specialProcess.id}, 4)" title="移已完成">
                                        <i class="icon-ok"></i>移已完成
                                    </a>
                                </c:if>
                            </c:when>
                        </c:choose>

                        <a class="btn btn-xs btn-default" onclick="viewLog(${specialProcess.id})" title="日志" href="javascript:void(0);">
                            <i class="icon-ok"></i>日志
                        </a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty domain.specialProcessList}">
                <tr class="tc">
                    <td colspan="99">没有记录</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </form>
</div>

<form id="toStockform" pageno="1" pagesize="10" totalcount="1" >
</form >

<div class="fixed-page"></div>

<div id="d-top" style="display: block;">
    <a id="d-top-a" title="返回顶部"></a>
</div>

<app:include type="javascript" />

<script type="text/javascript" src="${CUSTOM_THEME_PATH}js/jquery.export.excel.js"></script>

<script type="text/javascript" src="${CUSTOM_THEME_PATH}js/ztree/jquery.ztree.min.js"></script>

<script type="text/javascript" src="${CUSTOM_THEME_PATH}assets/plugins/bootstrap-editable/bootstrap-editable/js/bootstrap-editable.js" type="text/javascript"></script>

<script src="${CUSTOM_THEME_PATH}js/pages/special.process.js" type="text/javascript"></script>

<script src="${CUSTOM_THEME_PATH}js/datepicker/WdatePicker.js" type="text/javascript"></script>

<script type="text/javascript" src="${CUSTOM_THEME_PATH}js/dist/clipboard.min.js"></script>

<app:ready method="initList" />

</body>
</html>