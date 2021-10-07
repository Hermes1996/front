<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>报表-Shopee专用</title>
    <app:include type="css" />
    <style type="text/css">
        .d-top {
            bottom: 78px !important;
        }

        .d-bottom {
            bottom: 36px !important;
            display: block !important
        }

        .d-bottom a {
            transform: rotate(180deg);
            -webkit-transform: rotate(180deg);
        }
    </style>
    <link rel="stylesheet" type="text/css" href="${CUSTOM_THEME_PATH }assets/plugins/bootstrap-editable/bootstrap-editable/css/bootstrap-editable.css" />
    <link rel="stylesheet" type="text/css" href="${CUSTOM_THEME_PATH }assets/plugins/bootstrap-switch/static/stylesheets/bootstrap-switch-metro.css" />
</head>
<body>
<app:header active="10000000" fixed="false" />

<div class="container-fluid mb50">
    <app:nav active="12141900" ancestor="12140000"></app:nav>

    <app:searchform id="search_form" name="searchForm" action="${CONTEXT_PATH}stable/order/tracks/statistics/shopee/logistics/complate">
        <app:searchitem label="发货时间">
            <div class="input-group input-large">
                <app:datetime id="fromDeliverDateId" name="query.fromDeliverDate" startDate="%y-%M-%d 00:00:00" dateFmt="yyyy-MM-dd HH:mm:ss" readonly="true" alwaysUseStartDate="true" required="true" />
                <span class="input-group-addon">到</span>
                <app:datetime id="toDeliverDateId" name="query.toDeliverDate" startDate="%y-%M-%d 23:59:59" dateFmt="yyyy-MM-dd HH:mm:ss" readonly="true" alwaysUseStartDate="true" required="true" />
            </div>
        </app:searchitem>
        <app:searchitem label="运输方式物流代码">
            <app:select select2="true" multiple="true" name="query.logisticsTypes" id="logistics-type" classes="input-medium" items="${domain.transportations }" listkey="transportationModeId" listvalue="transportationModeDisplayForScan" headkey="" headvalue=""></app:select>
            <span class="control-inline">&nbsp;</span>
            <app:checkbox name="query.isLogisticsTypeNegation" classes="">
                <app:item key="true" value="取反"></app:item>
            </app:checkbox>
        </app:searchitem>
        <app:searchitem label="目的国家">
            <app:country name="query.countryCodeStr" classes="input-large" multiple="true" />
            <span class="control-inline">&nbsp;</span>
            <app:checkbox name="query.isCountryCodeStrNegation" classes="">
                <app:item key="true" value="取反"></app:item>
            </app:checkbox>
        </app:searchitem>
    </app:searchform>

    <div class="mt10 mb10"></div>

    <div>
        <table class="table table-striped table-bordered table-hover table-condensed" id="data-table">
            <thead>
            <tr>
                <th>运输方式</th>
                <th>发货量</th>
                <th>已签收</th>
                <th>总签收率</th>
                <th>进单时效(付款-创建)</th>
                <th>打单时效(创建-待合并)</th>
                <th>仓库包装时效(待合并-发货)</th>
                <th>收包时效(发货-收包)</th>
                <th>物流操作时效(收包-上网)</th>
                <th>APT时效(付款-上网)</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${domain.shopeeTrackStatisticCompletedRates}" var="logisticsCompletedRate" varStatus="loopStatus">
                <tr>
                    <c:set var="tran" value="${app:j2cache('J2CACHE_TRANSPORTATION_MODE', logisticsCompletedRate.logisticType)}" />
                    <td>${tran.transportationModeDisplayForScan}</td>
                    <td>${logisticsCompletedRate.deliveredCount}</td>
                    <td>${logisticsCompletedRate.signedCount}</td>
                    <td>
                        <fmt:formatNumber type="number" value="${logisticsCompletedRate.signedCount / logisticsCompletedRate.deliveredCount * 100 }" pattern="0.00" maxFractionDigits="2" />
                        %
                    </td>
                    <td>
                        <fmt:formatNumber type="number" value="${logisticsCompletedRate.orderinAgeing / logisticsCompletedRate.deliveredCount}" pattern="0.00" maxFractionDigits="2" />
                        H
                    </td>
                    <td>
                        <fmt:formatNumber type="number" value="${logisticsCompletedRate.printAgeing / logisticsCompletedRate.deliveredCount}" pattern="0.00" maxFractionDigits="2" />
                        H
                    </td>
                    <td>
                        <fmt:formatNumber type="number" value="${logisticsCompletedRate.packageAgeing / logisticsCompletedRate.deliveredCount}" pattern="0.00" maxFractionDigits="2" />
                        H
                    </td>
                    <td>
                        <fmt:formatNumber type="number" value="${logisticsCompletedRate.pickupAgeing / logisticsCompletedRate.deliveredCount}" pattern="0.00" maxFractionDigits="2" />
                        H
                    </td>
                    <td>
                        <fmt:formatNumber type="number" value="${logisticsCompletedRate.shippingOperationAgeing / logisticsCompletedRate.deliveredCount}" pattern="0.00" maxFractionDigits="2" />
                        H
                    </td>
                    <td>
                        <fmt:formatNumber type="number" value="${logisticsCompletedRate.aptAgeing / logisticsCompletedRate.deliveredCount }" pattern="0.00" maxFractionDigits="2" />
                        H (
                        <fmt:formatNumber type="number" value="${logisticsCompletedRate.aptAgeing / logisticsCompletedRate.deliveredCount / 24}" pattern="0.0" maxFractionDigits="1" />
                        天 )
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <td>渠道AVG</td>
                <c:forEach items="${domain.shopeeTrackStatisticCompletedRates}" var="logisticsCompletedRate">
                    <c:set var="deliveredCount" value="${deliveredCount + logisticsCompletedRate.deliveredCount}"/>
<%--                  c:set标签用于设置变量值和对象属性。--%>
                    <c:set var="signedCount" value="${signedCount + logisticsCompletedRate.signedCount}"/>
                    <c:set var="orderinAgeing" value="${orderinAgeing + logisticsCompletedRate.orderinAgeing}"/>
                    <c:set var="printAgeing" value="${printAgeing + logisticsCompletedRate.printAgeing}"/>
                    <c:set var="packageAgeing" value="${packageAgeing + logisticsCompletedRate.packageAgeing}"/>
                    <c:set var="pickupAgeing" value="${pickupAgeing + logisticsCompletedRate.pickupAgeing}"/>
                    <c:set var="shippingOperationAgeing" value="${shippingOperationAgeing + logisticsCompletedRate.shippingOperationAgeing}"/>
                    <c:set var="aptAgeing" value="${aptAgeing + logisticsCompletedRate.aptAgeing}"/>
                </c:forEach>
                <td>${deliveredCount}</td>
                <td>${signedCount}</td>
                <td>
                    <fmt:formatNumber type="number" value="${signedCount / deliveredCount * 100 }" pattern="0.00" maxFractionDigits="2"/> %
                </td>
                <td>
                    <fmt:formatNumber type="number" value="${orderinAgeing / deliveredCount}" pattern="0.00" maxFractionDigits="2"/>
                    H
                </td>
                <td>
                    <fmt:formatNumber type="number" value="${printAgeing / deliveredCount}" pattern="0.00" maxFractionDigits="2"/>
                    H
                </td>
                <td>
                    <fmt:formatNumber type="number" value="${packageAgeing / deliveredCount}" pattern="0.00" maxFractionDigits="2"/>
                    H
                </td>
                <td>
                    <fmt:formatNumber type="number" value="${pickupAgeing / deliveredCount}" pattern="0.00" maxFractionDigits="2"/>
                    H
                </td>
                <td>
                    <fmt:formatNumber type="number" value="${shippingOperationAgeing / deliveredCount}" pattern="0.00" maxFractionDigits="2"/>
                    H
                </td>
                <td>
                    <fmt:formatNumber type="number" value="${aptAgeing / deliveredCount }" pattern="0.00" maxFractionDigits="2" />
                    H (
                    <fmt:formatNumber type="number" value="${aptAgeing / deliveredCount / 24}" pattern="0.0" maxFractionDigits="1" />
                    天 )
                </td>
            </tr>

            <c:if test="${empty domain.shopeeTrackStatisticCompletedRates}">
                <tr class="tc">
                    <td colspan="99">没有记录</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

<div id="d-top" class="d-top">
    <a id="d-top-a" title="返回顶部"></a>
</div>

<div id="d-top" class="d-bottom">
    <a id="d-top-a" class="d-bottom-a" title="去底部"></a>
</div>

<app:include type="javascript" />

<script src="${CUSTOM_THEME_PATH }js/datepicker/WdatePicker.js" type="text/javascript"></script>
<script src="${CUSTOM_THEME_PATH }assets/plugins/bootstrap-editable/bootstrap-editable/js/bootstrap-editable.js" type="text/javascript"></script>
<script type="text/javascript" src="${CUSTOM_THEME_PATH}js/pages/stable.order.track.js"></script>
<script type="text/javascript" src="${CUSTOM_THEME_PATH }js/multiselect/jquery.multiSelect.js"></script>
<script type="text/javascript">
    $(".d-bottom").click(function(e) {
        var windowHeight = parseInt($("body").css("height"));
        $('body, html').animate({
            scrollTop : windowHeight
        });
    });

    var buyerCountry = $("#search_form select[name='query.countryCodeStr']");
    buyerCountry.find("option[value='']").remove();
    buyerCountry.select2({
        allowClear : true,
        placeholder : "选择国家"
    });
</script>
</body>
</html>