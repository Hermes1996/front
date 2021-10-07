<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<jsp:useBean id="now" class="java.util.Date"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <meta content="text/html; charset=utf-8" http-equiv="Content-type">
    <title>包裹统计打印</title>
</head>

<body style="padding: 0px; margin: 0px">
<c:if test="${not empty domain.logisticsPackages }">
    <%-- 计算打印页数 --%>
    <c:set value="${fn:length(domain.logisticsPackages)}" var="printPageSize"></c:set>
<%-- 错误写法   <fmt:formatNumber pattern="0" value=" ${printPageSize/40}" var="printPageNo"/>比例大于0.5会进1--%>
    <fmt:formatNumber type="number" value="${(printPageSize-printPageSize % 40) / 40}" var="printPageNo"/>
    <%--    40以下为0页；减取模40使结果为整数--%>
    <%--    type="number"无进制--%>
    <c:set var="printPageNo" value="${printPageNo + (printPageSize % 40 == 0 ? 0: 1)}"></c:set>

    <%-- 每页40条数据 --%>
    <c:set var="beginNo" value="0"></c:set>
    <c:set var="endNo" value="39"></c:set>

    <c:forEach begin="1" end="${printPageNo}" varStatus="status">
        <div id="print_content">
            <div style="width: 200mm; height: 287mm; margin-left: 1.5mm; margin-top: 1mm; border: 2px solid;">
                <div style="width: 200mm; height: 5mm;font-size: 32px;font-weight: bolder;text-align: center;margin-top: 4mm">
                    大岭山仓库交接单
                </div>
                <c:forEach items="${domain.logisticsPackages[0].logisticsIdList }" var="logisticsId">
                    <c:set value="${app:j2cache('J2CACHE_TRANSPORTATION_MODE', logisticsId)}" var="tran"></c:set>
                    <c:set value="${app:dict('LogisticsCompany', tran.logisticsCompany)}"
                           var="logisticsCompany"></c:set>
                    <c:set value="${tran.logisticsCompanyCode }" var="logisticsCompanyCode"></c:set>
                </c:forEach>
                    <%--  发货日期取最近的发货日期--%>
                <c:set value="${domain.logisticsPackages[0].lastUpdateDate }" var="lastUpdateDate"></c:set>

                <div style="width: 200mm; height: 5mm;font-size: 18px;font-weight: bolder;text-align: left;margin-top: 8mm;margin-left: 13px">
                    <span>物流公司名称：<u>${logisticsCompany}</u></span>
                    <span style=" margin-left: 4mm;">物流代码：<u>${logisticsCompanyCode} </u></span>
                    <span style=" margin-left: 4mm;">发货日期：<u><fmt:formatDate value="${lastUpdateDate}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate></u></span><br>
                </div>
                <div style="width: 200mm; height: 5mm;font-size: 18px;font-weight: bolder;text-align: left;margin-top: 6mm;margin-bottom: 25px;margin-left: 13px">
                    <c:set var="orderTotalCount" value=""></c:set>
                    <c:set var="parcelTotalWeigh" value=""></c:set>
                    <c:forEach items="${domain.logisticsPackages}" var="logisticsPackage" varStatus="itemStatus">
                        <c:set var="orderTotalCount" value="${itemStatus.index + 1 }"></c:set>
                        <c:set var="parcelTotalWeigh"
                               value="${parcelTotalWeigh + logisticsPackage.parcelWeigh }"></c:set>
                    </c:forEach>
                    <span>总袋数：<u>${orderTotalCount}</u> </span>
                    <span style=" margin-left: 4mm;">总重量：<u><fmt:formatNumber pattern="0.00" value="${parcelTotalWeigh}"/> kg</u> </span>
                </div>

                <table style="width: 180mm; margin-top: 20px;margin-left: 35px; margin-bottom: 5px; height: 10mm; text-align: center; border-collapse: collapse; ">
                    <tbody>
                    <tr style="font-size: 4mm">
                        <th style="border: 1px solid ; text-align: center;width: 5%">序号</th>
                        <th style="border-top: 1px solid ;border-right: 1px solid ; border-bottom: 1px solid; text-align: center;width: 50%">
                            序列号
                        </th>
                        <th style="border-top: 1px solid ;border-right: 1px solid ; border-bottom: 1px solid ;text-align: center;width: 10%">
                            渠道代码
                        </th>
                        <th style="border-top: 1px solid ;border-right: 1px solid ; border-bottom: 1px solid;text-align: center; width: 10%">
                            票数
                        </th>
                        <th style="border-top: 1px solid ;border-right: 1px solid ; border-bottom: 1px solid; text-align: center;width: 10%">
                            重量(kg)
                        </th>
                        <th style="border-top: 1px solid ;border-right: 1px solid ; border-bottom: 1px solid; text-align: center;width: 25%">
                            备注
                        </th>
                    </tr>
                    <c:forEach items="${domain.logisticsPackages}" var="logisticsPackage" varStatus="itemStatus"
                               begin="${beginNo}" end="${endNo}">
                        <tr style="font-size: 1mm">
                            <td style="border: 1px solid ;width: 5%">${itemStatus.index + 1 }</td>
                            <td style="border-top: 1px solid ;border-right: 1px solid ; border-bottom: 1px solid; width: 50%">${logisticsPackage.serialNumber}</td>
                            <c:set var="logisticsCode" value=""></c:set>
                            <c:forEach items="${logisticsPackage.logisticsIdList }" var="logisticsId">
                                <c:set value="${app:j2cache('J2CACHE_TRANSPORTATION_MODE', logisticsId)}" var="tran"></c:set>
                                <c:if test="${!fn:contains(logisticsCode, tran.logisticsCode) }">
                                    <c:set value="${tran.logisticsCode },${logisticsCode }" var="logisticsCode"></c:set>
                                </c:if>
                            </c:forEach>
                            <td style="border-top: 1px solid ;border-right: 1px solid ; border-bottom: 1px solid ;width: 10%">${logisticsCode}</td>
                            <td style="border-top: 1px solid ;border-right: 1px solid ; border-bottom: 1px solid; width: 10%">${logisticsPackage.orderCount }</td>
                            <td style="border-top: 1px solid ;border-right: 1px solid ; border-bottom: 1px solid; width: 10%">${logisticsPackage.parcelWeigh }</td>
                            <td style="border-top: 1px solid ;border-right: 1px solid ; border-bottom: 1px solid; width: 25%">${logisticsPackage.remark }</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <c:if test="${status.last}">
                    <div style="width: 200mm; height: 5mm;font-size: 12px;font-weight: bolder;text-align: center;margin-top: 8mm;">
                        <span>联系人：蒋祖翔</span>
                        <span style=" margin-left: 4mm;">电话：15820491639</span>
                        <span style=" margin-left: 4mm;">地址：广东省东莞市大岭山镇杨屋大兴路248号鑫龙科创港一楼</span><br>
                    </div>
                    <div style="width: 200mm; height: 5mm;font-size: 20px;font-weight: bolder;text-align: left;margin-top: 8mm;">
                        <span style="margin-left: 30mm">发货方签名：</span>
                        <span style=" margin-left: 60mm;">提货方签名：</span>
                    </div>
                </c:if>
                <div style="height:284px;margin-left: 350px;margin-top:4mm;font-weight: bolder">${status.index}/${printPageNo}</div>
            </div>
        </div>
        <c:set var="beginNo" value="${beginNo + 40 }"></c:set>
        <c:set var="endNo" value="${endNo + 40}"></c:set>
    </c:forEach>
</c:if>
</body>
</html>