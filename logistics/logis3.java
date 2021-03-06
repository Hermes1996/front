package com.bessky.erp.logistics.api.yw;

import java.io.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.bessky.erp.logistics.utils.*;
import com.bessky.erp.starter.core.context.SpringContextUtils;
import com.bessky.erp.starter.fastdfs.FastdfsClientManager;
import com.bessky.erp.starter.fastdfs.StorePath;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bessky.erp.core.common.constants.CountryConstants;
import com.bessky.erp.core.common.type.LogisticsCompany;
import com.bessky.erp.core.logistics.LogisticsConstants;
import com.bessky.erp.logistics.auto.AbstractLogisticsCall;
import com.bessky.erp.logistics.auto.config.LogisticsCallContext;
import com.bessky.erp.order.manager.bean.Order;
import com.bessky.erp.order.manager.bean.OrderItem;
import com.bessky.erp.order.manager.bean.RelatedProduct;
import com.bessky.erp.starter.core.component.StatusCode;
import com.bessky.erp.starter.core.json.ResponseJson;
import com.bessky.erp.system.expressdeclarevalue.bean.ExpressDeclareValueConf;
import com.bessky.erp.system.logistics.bean.LogisticsConf;
import com.bessky.erp.system.logistics.bean.TransportationMode;

public class YanWenCall extends AbstractLogisticsCall
{
    private static final Logger LOGGER = LoggerFactory.getLogger(YanWenCall.class);

    private final static String END_POINT = "http://online.yw56.com.cn/service";

    private static final String MAILNO_QUERY_URL = "http://trackapi.yanwentech.com/api/tracking";

    private final static String CHARSET_UTF8 = "utf-8";

    // ???????????????????????????????????? ??????DHL(?????????); ????????????-IP;
    private final static List<String> HANGZHOU_CODE = Arrays.asList("5", "733");

    // ??????????????????
    private final static Integer WAREHOUSE_ID = 33;

    private static final YanWenCall SINGLE = new YanWenCall();

    public static YanWenCall getInstance()
    {
        return SINGLE;
    }

    /**
     * ??????????????????
     *
     * @param order
     * @param transportationMode
     * @param logisticsConf
     * @return
     * @author ??????
     */
    public ResponseJson getYWPackage(Order order, TransportationMode transportationMode, LogisticsConf logisticsConf, ExpressDeclareValueConf expressDeclareValueConf, Boolean isDebugMode)
    {
        CloseableHttpClient httpClient = getHttpClient();

        ResponseJson response = new ResponseJson(StatusCode.FAIL);
        String fullStreet = OrderUtils.getFullStreet(order);
        if (fullStreet.length() > 64)
        {
            response.setMessage("??????1????????????2??????????????????64?????????");
            return response;
        }

        String url = MessageFormat.format("{0}/Users/{1}/Expresses", END_POINT, logisticsConf.getUserId());

        HttpPost httpPost = new HttpPost(url.toString());

        httpPost.addHeader("Authorization", "basic " + logisticsConf.getToken());
        httpPost.addHeader("Accept", "application/xml");
        httpPost.addHeader("Content-Type", "text/xml; charset=utf-8");

        // ?????? 2014-02-07T00:00:00
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        String itemBody = "ITEM_BODY";

        // ??????
        String memo = "YANWEN_MENO";

        // ??????
        String country = order.getBuyerCountry();

        // ???????????????
        if (StringUtils.isNotBlank(order.getBuyerCountryCode()) && order.getBuyerCountryCode()
                .length() == 2 && !StringUtils.equals(order.getBuyerCountryCode(), CountryConstants.PF))
        {
            country = order.getBuyerCountryCode();

            // ??????UK?????????GB
            if ("UK".equals(country))
            {
                country = "GB";
            }

            // ??????????????????????????????CD
            if (StringUtils.equals("ZR", country))
            {
                country = "CD";
            }
        }

        String buyerTel = StringUtils.isEmpty(order.getBuyerTel()) ? order.getBuyerMobile() : order.getBuyerTel();
        String buyerMobile = StringUtils.isEmpty(order.getBuyerMobile()) ? buyerTel : order.getBuyerMobile();

        // ????????????????????????????????????????????????????????????
        if (StringUtils.equals("UA", country) && (order.getLogisticsType() == 417 || order.getLogisticsType() == 418))
        {
            buyerTel = MyStringUtils.getFilter(buyerTel);
            buyerMobile = MyStringUtils.getFilter(buyerMobile);
        }

        if (StringUtils.contains(buyerTel, "ext.") || StringUtils.contains(buyerMobile, "ext."))
        {
            buyerMobile = StringUtils.substringAfter(buyerTel, "ext.");
            buyerTel = StringUtils.substringBefore(buyerTel, "ext.");
        }

        //?????????-????????????????????????
        String buyerName = order.getBuyerName();
        buyerName = MyStringUtils.replaceSymbol(buyerName);
        buyerName = MyStringUtils.getFilterSymbol(buyerName);
        if (buyerName.length() < 2)
        {
            buyerName = "Dear " + buyerName;
        }
        if (buyerName.length() > 31)
        {
            buyerName = buyerName.substring(0, 30);
        }

        //?????????-????????????????????????
        String street = StringUtils.isEmpty(order.getStreet()) ? (StringUtils.isEmpty(order.getStreet2()) ? "" : order
                .getStreet2()) : order.getStreet();
        String street2 = StringUtils.isEmpty(order.getStreet2()) ? "" : order.getStreet2();
        street = MyStringUtils.replaceSymbol(street);
        street = MyStringUtils.getFilterSymbol(street);
        if (street.length() > 35 && street.length() < 6)
        {
            response.setMessage("??????1????????????35?????????????????????6?????????");
            return response;
        }

        street2 = MyStringUtils.replaceSymbol(street2);
        street2 = MyStringUtils.getFilterSymbol(street2);

        String customerNumber = OrderUtils.getCustomerNum(order, logisticsConf.getOrderNoPrefix());
        String taxId = StringUtils.substringBetween(order.getRemark(), "(taxId:", ")");
        taxId = StringUtils.isBlank(taxId) ? "" : taxId;
        String requestBody = "<ExpressType>" + "<Epcode></Epcode>" // ????????????????????????????????????
                + "<Userid>" + logisticsConf.getUserId() + "</Userid>" // ?????? ?????????
                + "<Channel>" + transportationMode.getShippingServiceCode() + "</Channel>" // ?????? ???????????? ????????????E??????(??????)
                + "<UserOrderNumber>" + customerNumber + "</UserOrderNumber>" // ?????? ???????????????
                + "<SendDate>" + sdf.format(new Date()) + "</SendDate>" // ?????? ????????????
                + "<Receiver>" + " <Userid>" + logisticsConf.getUserId() + "</Userid>" // ?????? ?????????
                + " <NationalId>" + taxId + "</NationalId>" // ???????????????
                + " <Name>" + StringEscapeUtils.escapeXml(buyerName) + "</Name>" // ?????? ?????????-??????
                + " <Phone>" + StringEscapeUtils.escapeXml(buyerTel) + "</Phone>" // ?????????-?????????????????????????????????????????????
                + " <Mobile>" + buyerMobile + "</Mobile>" // ?????????-?????????????????????????????????????????????
                + " <Email>" + StringEscapeUtils.escapeXml(order.getBuyerEmail()) + "</Email>" // ?????????-??????
                + " <Company></Company>" // ?????????-??????
                + " <Country>" + (country) + "</Country>" // ?????????-??????
                + " <Postcode>" + order.getBuyerPostalCode() + "</Postcode>" // ?????????-??????
                + " <State>" + StringEscapeUtils.escapeXml(order.getBuyerStateOrProvince() == null ? "" : order
                .getBuyerStateOrProvince()) + "</State>" // ?????????-???
                + " <City>" + StringEscapeUtils.escapeXml(order.getBuyerCity()) + "</City>" // ?????????-??????
                + " <Address1>" + StringEscapeUtils.escapeXml(street) + "</Address1>" // ?????????-??????1
                + " <Address2>" + StringEscapeUtils.escapeXml(street2) + "</Address2>" // ?????????-??????2
                + "</Receiver>" + "<Sender>" + "<TaxNumber>" + OrderUtils.getIossId(order) + "</TaxNumber>" +"</Sender>"
                +"<Memo>" + memo + "</Memo>"// ?????????????????????????????????
                + itemBody + "</ExpressType>";

        StringBuffer itemAll = new StringBuffer();

        // ????????????
        StringBuffer memoAll = new StringBuffer();
        SimpleDateFormat sdfMemo = new SimpleDateFormat("MMdd");
        String date = sdfMemo.format(new Date());

        // ?????? ??????
        memoAll.append("-" + date + "\n");

        // ???????????????????????????????????????????????????
        boolean flag = false;

        // ???????????????shengbaoPH ????????????????????? ?????????????????????????????????
        String shippingServiceShengbao = transportationMode.getShippingServiceShengbao();

        if (StringUtils.isNotBlank(shippingServiceShengbao) && StringUtils
                .equals(shippingServiceShengbao, "shengbaoPH"))
        {
            // shengbaoPH
            flag = true;
        }

        // ??????????????????
        double totalCustomsValue = OrderUtils.getTotalCustomsValue(order);
        OrderUtils.avgTotalCustomsValue(order, totalCustomsValue);

        List<OrderItem> orderItems = order.getOrderItems();

        // ????????????
        String peihuo = "??????:";

        if (flag)
        {
            int i = 0;
            for (OrderItem orderItem : orderItems)
            {
                // ????????????????????? todo: ?????????,????????????
                int YW_DEM_PACKET = 122; // SystemParamCacheUtils.getIntValue("TRANSPORTATION_MODE.DEM");
                int YW_DEM_PACKET_REGISTERED = 159; // SystemParamCacheUtils.getIntValue("TRANSPORTATION_MODE.DEMG");
                if (i >= 3 && order.getLogisticsType() != null && (order.getLogisticsType() == YW_DEM_PACKET || order
                        .getLogisticsType() == YW_DEM_PACKET_REGISTERED))
                {
                    continue;
                }

                peihuo = peihuo + orderItem.getProductSku() + "[" + orderItem.getRelatedProduct()
                        .getLocationNumber() + "]X" + orderItem.getSaleQuantity() + "\n";
                i++;
            }
        }

        // ????????????????????????????????????
        if (orderItems.size() > 1 && (order.getLogisticsType() == 1425 || order.getLogisticsType() == 1426))
        {
            int skuQuantity = 0;
            Double itemDeclareValue = 0.0;
            double totalCustomsWeight = OrderUtils.getTotalCustomsWeight(orderItems);
            for (OrderItem orderItem : orderItems)
            {
                skuQuantity = skuQuantity + orderItem.getSaleQuantity();
                itemDeclareValue = itemDeclareValue + getItemDeclareValue(orderItem, order, expressDeclareValueConf, transportationMode);
            }

            // ??????????????????
            itemDeclareValue = itemDeclareValue < 3 ? 3 : itemDeclareValue;
            if (itemDeclareValue <= 3 && CountryConstants.EURO_COUNTRYS.contains(order.getBuyerCountryCode()))
            {
                // ????????????????????????
                itemDeclareValue = 6D;
            }

            itemDeclareValue = itemDeclareValue > 75 ? 75 : itemDeclareValue;
            OrderItem orderItem = orderItems.get(0);
            // ??????????????????
            String customsNameEn = orderItem.getRelatedProduct().getCustomsNameEn();

            String item = "<Quantity>" + skuQuantity + "</Quantity>" // ?????? ????????????
                    + "<GoodsName>" + " <Userid>" + logisticsConf.getUserId() + "</Userid>" // ?????? ?????????
                    + "<NameCh>" + StringEscapeUtils
                    .escapeXml(orderItem.getRelatedProduct().getCustomsNameCn()) + "</NameCh>" // ?????? ??????????????????
                    + "<NameEn><![CDATA[" + customsNameEn + "]]></NameEn>" // ?????? ??????????????????
                    + "<Weight>" + Double.valueOf(totalCustomsWeight).intValue() + "</Weight>" // ?????? ???????????? ?????? ???
                    + "<DeclaredValue>" + itemDeclareValue + "</DeclaredValue>" // ?????? ????????????
                    + "<DeclaredCurrency>" + "USD" + "</DeclaredCurrency>" // ?????? ??????????????????????????????USD,EUR,GBP,CNY???
                    + "<MoreGoodsName>" + StringEscapeUtils
                    .escapeXml(orderItem.getRelatedProduct().getCustomsNameEn()) + "</MoreGoodsName>" // ?????????
                    + "<HsCode>" + StringEscapeUtils
                    .escapeXml(orderItem.getRelatedProduct().getCustomsCode()) + "</HsCode>" // ????????????
                    + "</GoodsName>";

            itemAll.append(item);

            // ?????? SKU[??????]????????
            String memoDetails = StringEscapeUtils.escapeXml(orderItem.getProductSku()) + "[" + StringEscapeUtils
                    .escapeXml(orderItem.getRelatedProduct().getLocationNumber()) + "]??" + orderItem.getSaleQuantity();
            memoAll.append(memoDetails);
        }
        else
        {
            for (OrderItem orderItem : orderItems)
            {
                // ??????????????????
                String customsNameEn = orderItem.getRelatedProduct().getCustomsNameEn();
                Double totalDeclareValue = getItemDeclareValue(orderItem, order, expressDeclareValueConf, transportationMode);

                // ??????????????????
                totalDeclareValue = totalDeclareValue < 3 ? 3 : totalDeclareValue;
                if (totalDeclareValue <= 3 && CountryConstants.EURO_COUNTRYS.contains(order.getBuyerCountryCode()))
                {
                    // ????????????????????????
                    totalDeclareValue = 6D;
                }

                totalDeclareValue = totalDeclareValue > 75 ? 75 : totalDeclareValue;

                String item = "<Quantity>" + orderItem.getSaleQuantity() + "</Quantity>" // ?????? ????????????
                        + "<GoodsName>" + " <Userid>" + logisticsConf.getUserId() + "</Userid>" // ?????? ?????????
                        + "<NameCh>" + StringEscapeUtils
                        .escapeXml(orderItem.getRelatedProduct().getCustomsNameCn()) + "</NameCh>" // ?????? ??????????????????
                        + "<NameEn><![CDATA[" + customsNameEn + "]]></NameEn>" // ?????? ??????????????????
                        + "<Weight>" + formatWeight(OrderUtils
                        .getTotalCustomsWeight(orderItem)) + "</Weight>" // ?????? ???????????? ?????? ???
                        + "<DeclaredValue>" + totalDeclareValue + "</DeclaredValue>" // ?????? ????????????
                        + "<DeclaredCurrency>" + "USD" + "</DeclaredCurrency>" // ?????? ??????????????????????????????USD,EUR,GBP,CNY???
                        + "<MoreGoodsName>" + StringEscapeUtils
                        .escapeXml(orderItem.getRelatedProduct().getCustomsNameEn()) + "</MoreGoodsName>" // ?????????
                        + "<HsCode>" + StringEscapeUtils
                        .escapeXml(orderItem.getRelatedProduct().getCustomsCode()) + "</HsCode>" // ????????????
                        + "</GoodsName>";

                itemAll.append(item);

                // ?????? SKU[??????]????????
                String memoDetails = StringEscapeUtils.escapeXml(orderItem.getProductSku()) + "[" + StringEscapeUtils
                        .escapeXml(orderItem.getRelatedProduct().getLocationNumber()) + "]??" + orderItem
                        .getSaleQuantity() + "\n";
                memoAll.append(memoDetails);
            }
        }

        requestBody = StringUtils.replace(requestBody, itemBody, itemAll.toString());

        requestBody = StringUtils.replace(requestBody, memo, memoAll.toString());

        requestBody = StringUtils.replace(requestBody, "", "");

        requestBody = requestBody.replaceAll("(?i)packstation", "");

        HttpEntity requestEntity = new StringEntity(requestBody, CHARSET_UTF8);
        httpPost.setEntity(requestEntity);

        try
        {
            CloseableHttpResponse rsp = null;

            int retryTimes = 0;

            while (retryTimes < 5)
            {
                retryTimes++;

                try
                {
                    // ??????????????????
                    rsp = httpClient.execute(httpPost);

                    HttpEntity entity = rsp.getEntity();

                    if (rsp.getStatusLine().getStatusCode() == 200 && entity != null)
                    {
                        String responseText = EntityUtils.toString(entity);

                        if (BooleanUtils.isTrue(isDebugMode))
                        {
                            response.getBody().put(LogisticsConstants.REQUEST_JSON, requestBody);
                            response.getBody().put(LogisticsConstants.RESPONSE_JSON, responseText);
                        }
                        response.getBody().put(LogisticsConstants.CUSTOM_NUMBER, customerNumber);

                        Document doc = DocumentHelper.parseText(responseText);
                        Element root = doc.getRootElement();
                        Element ack = root.element("Response");

                        if (ack != null)
                        {
                            String success = ack.elementText("Success");
                            if (StringUtils.equalsIgnoreCase(success, "true"))
                            {
                                Element createdExpress = root.element("CreatedExpress");
                                String epcode = createdExpress.elementText("Epcode");
                                String yanwenNumber = createdExpress.elementText("YanwenNumber");
                                response.setStatus(StatusCode.SUCCESS);
                                // ??????-??????DHL??????????????????????????????????????????????????????????????????
                                if (HANGZHOU_CODE.contains(transportationMode.getShippingServiceCode()))
                                {
                                    response.setMessage(epcode);
                                }
                                else
                                {
                                    response.getBody().put(LogisticsConstants.AGENT_MAILNO, yanwenNumber);
                                    if (StringUtils.isBlank(yanwenNumber) && (transportationMode.getTransportationModeId() == 1008
                                            || transportationMode.getTransportationModeId() == 1714 || transportationMode.getTransportationModeId() == 2058))
                                    {
                                        response.getBody().put(LogisticsConstants.AGENT_MAILNO, epcode);
                                    }

                                    response.setMessage(epcode);
                                }
                            }
                            else
                            {
                                String message = ack.elementText("ReasonMessage");
                                String reason = ack.elementText("Reason");

                                // ????????????
                                if (StringUtils.contains(reason, "V121") || StringUtils.contains(reason, "V104"))
                                {
                                    // ?????????????????????????????????
                                    ResponseJson epxress = getEpxress(httpClient, customerNumber, logisticsConf);

                                    String shippingMethodCode = (String) epxress.getBody()
                                            .get(LogisticsConstants.SHIPPING_METHOD);
                                    Object agentMailno = epxress.getBody().get(LogisticsConstants.AGENT_MAILNO);

                                    if (StatusCode.SUCCESS.equals(epxress.getStatus()))
                                    {
                                        response.setStatus(StatusCode.SUCCESS);
                                        // ??????-??????DHL???????????????-IP??????????????????????????????????????????????????????????????????
                                        if (HANGZHOU_CODE.contains(transportationMode.getShippingServiceCode()))
                                        {
                                            response.setMessage(epxress.getMessage());
                                        }
                                        else
                                        {
                                            response.getBody().put(LogisticsConstants.AGENT_MAILNO, agentMailno);
                                            if ((agentMailno == null || StringUtils.isBlank(agentMailno.toString())) && (transportationMode.getTransportationModeId() == 1008
                                                    || transportationMode.getTransportationModeId() == 1714 || transportationMode.getTransportationModeId() == 2058))
                                            {
                                                response.getBody().put(LogisticsConstants.AGENT_MAILNO, epxress.getMessage());
                                            }
                                            response.setMessage(epxress.getMessage());
                                        }

                                        return response;
                                    }
                                    else
                                    {
                                        response.setMessage("???????????????" + "???????????????=" + shippingMethodCode + "????????????=" + epxress
                                                .getMessage() + "????????????=" + agentMailno);
                                        response.setStatus(StatusCode.FAIL);
                                        return response;
                                    }
                                }

                                // ???????????????????????????
                                if ("D2".equals(reason))
                                {
                                    message = "?????????????????????????????????????????????";
                                }

                                // ????????????
                                response.setMessage(order.getOrderId() + "," + reason + ", " + message);

                                if (StringUtils.contains(message, "???????????????"))
                                {
                                    LOGGER.debug("?????????????????????????????????");
                                    continue;
                                }
                            }
                        }
                    }
                    else
                    {
                        response.setStatus(StatusCode.FAIL);
                        response.setMessage(rsp.getStatusLine().toString());
                    }

                    break;
                }
                catch (Exception e)
                {
                    if (StringUtils.containsIgnoreCase(e.getMessage(), "timed out"))
                    {
                        continue;
                    }

                    LOGGER.error(e.getMessage(), e);

                    response.setStatus(StatusCode.FAIL);
                    response.setMessage(e.getMessage());
                    return response;
                }
                finally
                {
                    HttpClientUtils.closeQuietly(rsp);
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);

            response.setStatus(StatusCode.FAIL);
            response.setMessage(e.getMessage());

            return response;
        }

        return response;
    }

    /**
     *
     * ???????????????Fastdfs
     *
     * @param order
     * @param transportationMode
     * @param logisticsConf
     */
    public ResponseJson saveLabelFastdfs(Order order, TransportationMode transportationMode, LogisticsConf logisticsConf)
    {
        return getLabel(order, transportationMode, logisticsConf, Boolean.TRUE);
    }

    /**
     *
     * ?????????????????????
     *
     * @param order
     * @param transportationMode
     * @param logisticsConf
     */
    public ResponseJson printLables(Order order, TransportationMode transportationMode, LogisticsConf logisticsConf)
    {
        return getLabel(order, transportationMode, logisticsConf, Boolean.FALSE);
    }

    /**
     *
     * ????????????
     *
     * @param order
     * @param transportationMode
     * @param logisticsConf
     * @param isUpLoadFastdf
     */
    private ResponseJson getLabel(Order order, TransportationMode transportationMode, LogisticsConf logisticsConf, Boolean isUpLoadFastdf)
    {
        ResponseJson responseJson = new ResponseJson();
        responseJson.setStatus(StatusCode.FAIL);

        // ??????????????????-??????????????????
        String lableSize = "A6LC";
        String url = MessageFormat.format("{0}/Users/{1}/Expresses/{2}Label", END_POINT, logisticsConf.getUserId(), lableSize);

        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Authorization", "basic " + logisticsConf.getToken());
        httpPost.addHeader("Content-Type", "text/xml; charset=utf-8");
        String requestBody = "<string>" + order.getTrackingNumber() + "</string>";
        HttpEntity requestEntity = new StringEntity(requestBody, "UTF-8");
        httpPost.setEntity(requestEntity);

        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse httpResponse = null;
        InputStream labelInputStream = null;
        InputStream inputStream = null;
        File tempFile = null;

        try
        {
            httpResponse = httpClient.execute(httpPost);
            // json
            String resultJson = EntityUtils.toString(rsp.getEntity());

            JSONObject resultJsonObject = JSON.parseObject(resultJson);

            if (rsp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                JSONArray orderInfos = resultJsonObject.getJSONArray(WishPostConstants.ORDERS);

                if (orderInfos != null && orderInfos.size() > 0)
                {
                    JSONObject orderInfo = orderInfos.getJSONObject(0);

                    String labelURL = orderInfo.getString(WishPostConstants.LABEL_URL);

                    if (StringUtils.isNotBlank(labelURL))
                    {
                        HttpGet httpGet = new HttpGet(labelURL);
                        labelRsp = getHttpClient().execute(httpGet);
            // content stream
            inputStream = httpResponse.getEntity().getContent();

            tempFile = PdfUtils.addText(inputStream, "(" + transportationMode
                    .getLogisticsCode() + ")", com.itextpdf.text.Element.ALIGN_LEFT, 240, 65, 0, 15);

            inputStream = new FileInputStream(tempFile);

            if (isUpLoadFastdf)
            {
                FastdfsClientManager fastdfsClientManager = SpringContextUtils.getBean("fastdfsClientManager", FastdfsClientManager.class);
                if (fastdfsClientManager != null)
                {
                    StorePath storePath = fastdfsClientManager.upload(IOUtils.toByteArray(inputStream), Constants.PDF_FORMAT);
                    if (storePath != null)
                    {
                        responseJson.getBody().put(LogisticsConstants.FASTDFS_GROUP, storePath.getGroup());
                        responseJson.getBody().put(LogisticsConstants.FASTDFS_PATH, storePath.getPath());
                        responseJson.setStatus(StatusCode.SUCCESS);
                    }
                }
            }
            else
            {
                responseJson.getBody().put(LogisticsConstants.LABEL_STREAM, IOUtils.toByteArray(inputStream));
                responseJson.setStatus(StatusCode.SUCCESS);
            }
        }

        catch (Exception e)
        {
            responseJson.setMessage(e.getMessage());
        }
        finally
        {
            HttpClientUtils.closeQuietly(httpResponse);
            IOUtils.closeQuietly(labelInputStream);
            IOUtils.closeQuietly(inputStream);

            PdfUtils.deleteFile(tempFile);
        }

        return responseJson;
    }


    @Override
    public LogisticsCompany category()
    {
        return LogisticsCompany.YANWEN;
    }

    @Override
    public ResponseJson execute(LogisticsCallContext context)
    {
        Order order = context.getOrder();
        TransportationMode transportationMode = context.getTransportationMode();
        LogisticsConf logisticsConf = context.getLogisticsConf();
        ExpressDeclareValueConf expressDeclareValueConf = context.getLogisticsExpressDeclare();

        RelatedProduct product = order.getOrderItems().get(0).getRelatedProduct();

        // ??????-???????????????????????????????????????????????????
        if (product != null)
        {
            Integer warehouseId = product.getWarehouseId();

            if (HANGZHOU_CODE.contains(transportationMode.getShippingServiceCode()) || WAREHOUSE_ID == warehouseId
                    .intValue())
            {
                LogisticsConf hzLogisticsConf = new LogisticsConf();

                // ????????????
                BeanUtils.copyProperties(logisticsConf, hzLogisticsConf);
                hzLogisticsConf.setUserId(logisticsConf.getClientId());
                hzLogisticsConf.setToken(logisticsConf.getClientSecret());

                return getYWPackage(order, transportationMode, hzLogisticsConf, expressDeclareValueConf, context
                        .getDebugMode());
            }
        }

        return getYWPackage(order, transportationMode, logisticsConf, expressDeclareValueConf, context.getDebugMode());
    }
}
