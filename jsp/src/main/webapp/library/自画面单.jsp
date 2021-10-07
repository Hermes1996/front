<html>
<head>
    <meta content="text/html; charset=utf-8" http-equiv="Content-type">
    <title>Print</title>
    <style>
        @media print {
            .printbtn {
                display: none;
            }
        }

        .tools {
            position: fixed;
            right: 16px;
            bottom: 16px;
            text-align: center;
        }

        .tools button {
            padding: 8px;
            border: 1px #aaa solid;
            border-radius: 4px;
            outline: none;
            font-size: 16px;
            transition: background-color 0.3s;
        }

        .tools button:hover {
            background-color: orange;
        }

        .tools button:active {
            background-color: #FF8800;
        }
    </style>
</head>
<body style="padding: 0px; margin: 0px">
<c:if test="${empty param.scan }">
    <div class="printbtn">
        <button onclick="myPreview();">打印预览</button>
        &nbsp;
        <button onclick="myPrint();">打印</button>
        &nbsp;
        <button onclick="myPrintDesign();">打印设计</button>
        &nbsp;
    </div>
</c:if>
<div id="print_content" style="background: #fff !important">

    <jsp:useBean id="now" class="java.util.Date"/>

    <c:forEach items="${domain.orders }" var="order" varStatus="loopStatus">
        <c:set value="${loopStatus.index }" var="index"></c:set>
        <c:set value="${app:j2cache('J2CACHE_TRANSPORTATION_MODE', order.logisticsType)}" var="tran"></c:set>
        <c:set value="${app:j2cache('J2CACHE_LOGISTICS_CONF', tran.logisticsCompany)[0]}" var="logisticsConf"></c:set>
        <c:choose>
            <c:when test="${tran.transportationModeChinese eq '京东中俄经济专线小包'}">
                <%-- 拣货 --%>
                <c:if test="${fn:length(order.orderItems ) > 3}">
                    <div style="margin-top: 2mm; width: 95mm; height: 95mm; font-size: 13px;">
                        <div style="width: 95mm;">
                            <div style="width: 85mm; margin-left: 5mm; border: 1px solid black">
                                <fmt:formatDate value="${now}" pattern="MM-dd"/>
                                <br>
                                <c:forEach items="${order.orderItems }" var="orderItem">
                                    <strong style="display: block;">
                                        <font style="font-size: 12px; line-height: 12px;">${orderItem.productSku }
                                            [${orderItem.relatedProduct.locationNumber }]*${orderItem.saleQuantity } </font>
                                    </strong>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                    <p style="page-break-after: always; margin: 0; padding: 0; line-height: 1px; font-size: 1px;">
                        &nbsp;</p>
                </c:if>

                <%-- 报关信息 --%>
                <div style="width: 95mm; height: 95mm; margin-left: 2mm; margin-top: 2mm; border: 1px solid black; font-weight: 300;">
                    <div style="width: 95mm; height: 16mm; float: left;margin-left:18mm">
                        <img style="display: block; margin-top: 1mm; margin-left: 5mm"
                             src="${CONTEXT_PATH}servlet/barcode128?keycode=${order.trackingNumber}&height=40">
                        <span style="font-size: 11px; margin-left: 15mm;">${order.trackingNumber}</span>
                    </div>
                    <div style="width: 95mm; height: 4mm; float: left; font-size: 12px; border-top: 1px solid; ">
                        <div style="width: 46mm;  float: left;">
                            <span style="display: block; margin-left: 8mm;">Origin:CN</span>
                        </div>

                        <div style="width: 48mm; float: left; border-left: 1px solid;">
                            <span style="display: block; margin-left: 5mm;">Destination:${order.buyerCountryCode}</span>
                        </div>

                    </div>
                    <div style="width: 95mm; height: 4mm; float: left; font-size: 12px; border-bottom: 1px solid;border-top: 1px solid; ">
                        <div style="width: 46mm;  float: left;">
                            <span style="display: block; margin-left: 2mm;">Channel Code<b style=" margin-left: 4mm;border-left: 1px solid;">  200000251</b></span>
                        </div>

                        <div style="width: 24mm; float: left; border-left: 1px solid;">
                            <div style="display: block;margin-left: 2mm;">Channel Type</div>
                        </div>
                        <div style="width: 24mm; float: left; border-left: 1px solid;">
                            <div style=" margin-left: 10mm;border-left: 1px solid;"> </b><b style=" margin-left: 5mm;border-right: 1px solid;"> </b>1/1</div>
                        </div>

                    </div>
                    <div style="width: 95mm; height: 12mm; float: left; font-size: 12px; border-bottom: 1px solid;">
                        <div style="width: 20mm; height: 12mm; font-size: 14px; float: left;margin-top: 4mm">
                            <span style="display: block; margin-left: 1mm;">Consignee</span>
                            <span style="display: block; margin-left: 4mm;">Info.</span>
                        </div>

                        <div style="width: 48mm; height: 12mm; float: left; font-size: 12px; line-height: 3mm; border-left: 1px solid;border-right: 1px solid;">
                            <span style="display: block;margin-top:1mm;margin-left:1mm;">${order.buyerCountryCode} ${order.buyerStateOrProvince } ${order.buyerCity }</span>
                            <span style="display: block;margin-top:1mm;margin-left:1mm;">${order.street }&nbsp;${order.street2 }</span>
                            <span style="display: block;margin-top:1mm;margin-left:1mm;">${order.buyerName }</span>
                            <span style="display: block;margin-top:1mm;margin-left:1mm;">${order.buyerTel }&nbsp;${order.buyerMobile }</span>
                        </div>
                        <div style="width: 18mm; height: 12mm; font-size: 12px; float: left;">
                            <span style="display: block;  height: 4mm;margin-left: 1mm;margin-top: 4mm">Consignee</span>
                            <span style="display: block;  height: 4mm;margin-left: 1mm;">Signature</span>
                            <span style="display: block;  height: 4mm;margin-left: 1mm;"></span>
                        </div>
                        <div style="width: 6mm; height: 16mm; font-size: 12px; float: left;border-left: 1px solid;">
                        </div>
                    </div>
                    <div style="width: 95mm; height: 14mm; float: left;">
                        <div style="width: 50mm;height: 14mm;float: left;">
                            <span style="font-size: 10px; margin-left: 15mm;">Sender Ref.</span>
                            <img style="display: block; margin-top: 1mm; margin-left: 5mm" src="${CONTEXT_PATH}servlet/barcode128?keycode=${order.customNumber }&height=20">
                            <span style="font-size: 11px; margin-left: 10mm;">${order.customNumber }</span>
                        </div>
                        <div style="width: 2mm;height: 16mm;float: left;border-left: 1px solid;">
                            <span style="font-size: 14px; margin-left: 2mm;">Remark:</span>
                        </div>
                    </div>

                    <div style="width: 94mm; height: 4mm; font-size: 8px;text-align: center;font-weight: bold; float: left; margin-top: 1px;border: 1px solid;border-bottom: 0">
                        <div style="width: 10mm; height: 4mm; float: left; ">No.</div>
                        <div style="width: 8mm; height: 4mm; float: left; border-left: 1px solid;">Qty
                        </div>
                        <div style="width: 35.2mm; height: 4mm; float: left; border-left: 1px solid;">
                            Description of Contents
                        </div>
                        <div style="width: 10mm; height: 4mm; float: left; border-left: 1px solid;">
                            kg.
                        </div>
                        <div style="width: 12mm; height: 4mm; float: left; border-left: 1px solid;">
                            Val(US$)
                        </div>
                        <div style="width: 12mm; height: 4mm; float: left;font-size: 10px; border-left: 1px solid; ">
                            Battery
                        </div>
                        <div style="width: 2mm; height: 4mm; float: left; border-left: 1px solid; ">
                        </div>
                    </div>

                    <c:forEach items="${order.orderItems }" var="orderItem" varStatus="itemIndex" end="0">
                        <div style="width: 94mm; height: 4mm; font-size: 10px;text-align: center;font-weight: bold; float: left;border: 1px solid;border-bottom: 0">
                            <div style="width: 10mm; height: 4mm; float: left; word-break: break-all;">
                                    ${fn:substring(orderItem.productSku ,0,5)}
                            </div>
                            <div style="width: 8mm; height: 4mm; float: left; border-left: 1px solid;">
                                    ${orderItem.saleQuantity }
                            </div>
                            <div style="width: 35.2mm; height: 4mm; float: left; border-left: 1px solid;">
                                    ${orderItem.relatedProduct.customsNameEn} ${fn:substring(orderItem.relatedProduct.customsNameCn,0,10)}
                                <br>
                            </div>
                            <div style="width: 10mm; height: 4mm; float: left; border-left: 1px solid;">

                                <span style="display: block">${orderItem.totalCustomsWeightKg }</span>

                            </div>
                            <div style="width: 12mm; height: 4mm; float: left; border-left: 1px solid;">

                                <span style="display: block">${orderItem.totalCustomsValue }</span>

                            </div>
                            <div style="width: 12mm; height: 4mm; float: left; border-left: 1px solid; ">
                                Liquid
                            </div>
                            <div style="width: 2mm; height: 4mm; float: left; border-left: 1px solid; ">
                            </div>
                        </div>
                    </c:forEach>

                    <div style="width: 94mm; height: 4mm; font-size: 10px;text-align: center;font-weight: bold; float: left;border: 1px solid;">
                        <div style="width: 45mm; height: 4mm; float: left;">
                            Toal Gross Weight
                        </div>
                        <div style="width: 30.2mm; height: 4mm; float: left; border-left: 1px solid;">
                                ${order.totalCustomsWeightKg }
                        </div>
                        <div style="width: 12mm; height: 4mm; float: left; border-left: 1px solid; ">
                            Cosmetic
                        </div>
                        <div style="width: 2mm; height: 4mm; float: left; border-left: 1px solid; ">
                        </div>
                    </div>

                    <div style="width: 95mm; height: 10mm; float: left; font-size: 11px; margin-left:1mm;margin-top:1mm;line-height: 3mm;">
                        <div style="width: 65mm; height: 12mm; float: left;">
                            I certify that the particulars given in this declaration are correct and this
                            item does not contain any dangerous articles prohibited by legislation or
                            by postal or customers regulations.
                        </div>
                        <div style="width: 20mm; height: 12mm; float: left;">
                            <img width="100px;" style="margin-top: 0.5mm;" src="${CUSTOM_THEME_PATH }images/print/jd_logistics.png">
                        </div>
                            <%-- 拣货码 --%>
                        <c:if test="${not empty tran.logisticsCode}">
                            <div style="font-size: 16px; line-height: 18px;  width: 20mm; font-weight: 500; border: 1px solid; text-align: center; float: left; margin-left: 20mm;">
                                    ${tran.logisticsCode }
                                <c:choose>
                                    <c:when test="${order.buyerCountryCode == 'US' }">-A</c:when>
                                    <c:when test="${order.buyerCountryCode == 'RU' }">-B</c:when>
                                    <c:when test="${order.buyerCountryCode == 'UK' or order.buyerCountryCode == 'GB'}">-C</c:when>
                                    <c:when test="${order.buyerCountryCode == 'FR' }">-D</c:when>
                                    <c:when test="${order.buyerCountryCode == 'DE' }">-E</c:when>
                                    <c:when test="${order.buyerCountryCode == 'IT' }">-F</c:when>
                                    <c:when test="${order.buyerCountryCode == 'ES' }">-G</c:when>
                                </c:choose>
                            </div>
                            <div style="clear: both;"></div>
                        </c:if>
                    </div>
                </div>
                <p style="page-break-after: always; margin: 0; padding: 0; line-height: 1px; font-size: 1px;">&nbsp;</p>
            </c:when>
        </c:choose>
    </c:forEach>
</div>

<object width="0" height="0" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" id="LODOP_OB">
    <embed width="0" height="0" type="application/x-print-lodop" id="LODOP_EM">
</object>

<!-- 打印插件 -->
<script type="text/javascript" src="${CUSTOM_THEME_PATH }js/LodopFuncs.js"></script>

<script language="javascript">
    var LODOP; //声明为全局变量
    function CheckIsInstall() {
        try {
            var LODOP = getLodop(document.getElementById('LODOP_OB'), document.getElementById('LODOP_EM'));
            if ((LODOP != null) && (typeof (LODOP.VERSION) != "undefined")) return LODOP.VERSION;
        } catch (err) {
            //alert("Error:本机未安装或需要升级!");
        }
        return false;
    }

    function myPrint() {
        CreatePrintPage();
        LODOP.PRINT();
    };

    function myPreview() {
        CreatePrintPage();
        LODOP.PREVIEW();
    };

    function myPrintDesign() {
        CreatePrintPage();
        LODOP.PRINT_DESIGN();
    };

    function CreatePrintPage() {
        LODOP = getLodop(document.getElementById('LODOP_OB'), document.getElementById('LODOP_EM'));
        LODOP.PRINT_INIT("打印");
        try {
            if (typeof (eval(CreatePrintPageWithImage)) == 'function') {
                return CreatePrintPageWithImage();
            }
        } catch (e) {
        }
        // 上下边距，左右边距，长度，高度，打印内容
        LODOP.ADD_PRINT_HTM(0, 0, "100mm", "100mm", document.getElementById('print_content').innerHTML);
    };

    //表格打印采购单详情处使用
    function myPrinttable() {
        CreatePrintpagetable();
        LODOP.PRINT();
    };

    function myPreviewtable() {
        CreatePrintpagetable();
        LODOP.PREVIEW();
    };

    function CreatePrintpagetable() {
        LODOP = getLodop(document.getElementById('LODOP_OB'), document.getElementById('LODOP_EM'));
        LODOP.PRINT_INIT("打印");
        try {
            if (typeof (eval(CreatePrintPageWithImage)) == 'function') {
                return CreatePrintPageWithImage();
            }
        } catch (e) {
        }
        LODOP.ADD_PRINT_HTM(0, 0, "210mm", "297mm", document.getElementById('print_content').innerHTML);
        //
        LODOP.SET_PRINT_PAGESIZE('2', "", "", '')
    }
</script>
</body>
</html>