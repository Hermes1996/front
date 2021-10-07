<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>报表-平台专用</title>
    <app:include type="css"/>
    <style type="text/css">
    </style>
    <link rel="stylesheet" type="text/css"
          href="${CUSTOM_THEME_PATH }assets/plugins/bootstrap-editable/bootstrap-editable/css/bootstrap-editable.css"/>
    <link rel="stylesheet" type="text/css"
          href="${CUSTOM_THEME_PATH }assets/plugins/bootstrap-switch/static/stylesheets/bootstrap-switch-metro.css"/>
</head>
<body>
<app:header active="10000000" fixed="false"/>

<div class="container-fluid mb50">
    <app:nav active="12141900" ancestor="12140000"></app:nav>

    <app:searchform id="search_form" name="searchForm"
                    action="${CONTEXT_PATH}stable/order/tracks/statistics/platform/logistics/complate">
        <app:searchitem label="发货时间">
            <div class="input-group input-large">
                <app:datetime id="fromDeliverDateId" name="query.fromDeliverDate" startDate="%y-%M-%d 00:00:00"
                              dateFmt="yyyy-MM-dd HH:mm:ss" readonly="true" alwaysUseStartDate="true" required="true"/>
                <span class="input-group-addon">到</span>
                <app:datetime id="toDeliverDateId" name="query.toDeliverDate" startDate="%y-%M-%d 23:59:59"
                              dateFmt="yyyy-MM-dd HH:mm:ss" readonly="true" alwaysUseStartDate="true" required="true"/>
            </div>
        </app:searchitem>
    </app:searchform>
</div>

<script type="text/javascript">
    // required="true"失效，需添加提交验证
    jQuery(document).ready(function () {
        var submitForm = $('#search_form');
        submitForm.validate({
            rules: {},
        });
    })
</script>
</body>
</html>