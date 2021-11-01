<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>添加特殊加工审批</title>
<app:include type="css" />
</head>

<body class="page-header-fixed page-full-width">
<app:header active="11000000">
    <a>审批流程</a>
    <a href="${CONTEXT_PATH}special/process/approval">特殊加工审批列表</a>
    <a>添加审批单</a>
</app:header>
<div class="page-container">
    <div class="page-content">
        <div class="row" id="fee-list">
            <div class="col-md-12">
                <form class="form-horizontal" role="form" id="submit-form" action="${CONTEXT_PATH}special/process/approval/create" role="form" method="post">
                    <table class="table table-bordered table-condensed">
                        <colgroup><col width="10%" /></colgroup>
                        <tbody>
                        <tr>
                            <td class="form-label"><label class="control-label">采购单号<span class="required">*</span></label></td>
                            <td><app:input name="specialProcess.purchaseOrder" classes="input-large" required="true" /></td>
                        </tr>
                        <tr>
                            <td class="form-label"><label class="control-label">仓库<span class="required">*</span></label></td>
                            <td><app:select name="specialProcess.warehouseId" classes="input-medium" required="true"
                                            items="${app:j2caches('J2CACHE_WAREHOUSE')}" listkey="warehouseId" listvalue="warehouseName" headkey="" headvalue=""/>
                            </td>
                        </tr>
                        <tr>
                            <td class="form-label"><label class="control-label">SKU<span class="required">*</span></label></td>
                            <td><app:input name="specialProcess.sku" classes="input-large" required="true" placeholder="请输入一个SKU" maxLength="32" /></td>
                        </tr>
                        <tr>
                            <td class="form-label"><label class="control-label">申请加工数量<span class="required">*</span></label></td>
                            <td><app:input name="specialProcess.processQuantity" classes="form-control input-large not-empty edit-quantity" required="true" digits="true" min="1" />
                            </td>
                        </tr>
                        <tr>
                            <td class="form-label"><label class="control-label">申请工时<span class="required">*</span></label></td>
                            <td><app:input name="specialProcess.processHour" classes="form-control input-large not-empty edit-quantity" required="true" digits="true" min="1" />
                            </td>
                        </tr>
                        <tr>
                            <td class="form-label"><label class="control-label">申请金额<span class="required">*</span></label></td>
                            <td><app:input name="specialProcess.processAmount" classes="form-control input-large not-empty edit-price" required="true" positiveDouble="true" /></td>
                        </tr>
                        <tr>
                            <td class="form-label"><label class="control-label">申请原因<span class="required">*</span></label></td>
                            <td><app:input name="specialProcess.applyReason" classes="input-large" required="true" maxLength="100" /></td>
                        </tr>
                        <tr>
                            <td class="form-label"><label class="control-label">供应商交易流水号<span class="required">*</span></label></td>
                            <td><app:input name="specialProcess.supplierPayNumber" classes="input-large" required="true" maxLength="100" /></td>
                        </tr>
                        </tbody>
                    </table>
                    <div id="fixed-bottom" style="margin-left: -15px;" align="right">
                        <div class="col-md-offset-3 col-md-9">
                            <button type="submit" class="btn green"><i class="icon-save"></i>保存</button>
                            <button type="button" class="btn btn-default" onclick="location.href='${CONTEXT_PATH}special/process/approval'">
                                <i class="m-icon-swapleft"></i>返回
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<app:include type="javascript"/>

<script src="${CUSTOM_THEME_PATH }js/pages/supplier.single-selectable.js" type="text/javascript"></script>

</body>
</html>