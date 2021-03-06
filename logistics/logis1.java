package com.bessky.erp.logistics.api.jingdong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bessky.erp.core.common.type.LogisticsCompany;
import com.bessky.erp.core.common.util.DateUtils;
import com.bessky.erp.core.logistics.LogisticsConstants;
import com.bessky.erp.logistics.api.ozon.OzonCall;
import com.bessky.erp.logistics.auto.AbstractLogisticsCall;
import com.bessky.erp.logistics.auto.config.LogisticsCallContext;
import com.bessky.erp.logistics.utils.*;
import com.bessky.erp.order.manager.bean.Order;
import com.bessky.erp.order.manager.bean.OrderItem;
import com.bessky.erp.order.manager.bean.RelatedProduct;
import com.bessky.erp.starter.core.component.StatusCode;
import com.bessky.erp.starter.core.context.SpringContextUtils;
import com.bessky.erp.starter.core.json.ResponseJson;
import com.bessky.erp.starter.fastdfs.FastdfsClientManager;
import com.bessky.erp.starter.fastdfs.StorePath;
import com.bessky.erp.system.logistics.bean.LogisticsConf;
import com.bessky.erp.system.logistics.bean.TransportationMode;
import com.itextpdf.text.Element;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * ??????????????????????????????
 *
 * @author ?????????
 * @version Bessky V100R001 2021???02???23???
 * @since Bessky V100R001C00
 */
public class JDWLNewCall extends AbstractLogisticsCall
{
    private static final String URL = "https://us-api.jd.com/routerjson";

    /**
     * ??????????????????api?????????
     */
    private static final String API_CREATE_ORDER_SYNC = "jingdong.fop.service.deliveryWaybill";

    /**
     * ??????????????????api?????????
     */
    private static final String API_PRINT_ORDER_SYNC = "jingdong.fop.waybill.printWaybill";

    /**
     * ??????
     */
    private static final String VERSION = "2.0";

    private static final Logger LOGGER = LoggerFactory.getLogger(JDWLNewCall.class);

    private static final JDWLNewCall SINGLE = new JDWLNewCall();

    public static JDWLNewCall getInstance()
    {
        return SINGLE;
    }

    /**
     *
     * ????????????
     *
     * @author ?????????
     * @param order
     * @param transportationMode
     * @param logisticsConf
     * @param isDebugMode
     * @return
     */
    public ResponseJson creatOrder(Order order, TransportationMode transportationMode, LogisticsConf logisticsConf,  Boolean isDebugMode)
    {
        ResponseJson responseJson = new ResponseJson(StatusCode.FAIL);

        //??????????????????,???????????????
        String customCode = order.getOrderId().toString() + order.getLogisticsType().toString();
        //??????????????????
        String jsonStr = buildParam(customCode, order, transportationMode, logisticsConf);
        //??????url
        String url = buildUrl(API_CREATE_ORDER_SYNC, jsonStr, logisticsConf);
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse httpResponse = null;

        try
        {
            httpPost.setEntity(new StringEntity(jsonStr,"UTF-8"));
            httpPost.addHeader(HTTP.CONTENT_TYPE,"application/json;charset=utf-8");

            httpResponse = getHttpClient().execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();

            // ????????????????????? ??????????????? ??????if ??????
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK || entity == null)
            {
                responseJson.setMessage(httpResponse.getStatusLine().toString());
                return responseJson;
            }

            String responseStr = EntityUtils.toString(entity);
            if (BooleanUtils.isTrue(isDebugMode))
            {
                responseJson.getBody().put(LogisticsConstants.REQUEST_JSON, jsonStr);
                responseJson.getBody().put(LogisticsConstants.RESPONSE_JSON, responseStr);
            }

            responseJson.getBody().put(LogisticsConstants.CUSTOM_NUMBER, customCode);

            JSONObject parseObject = JSON.parseObject(responseStr);

            JSONObject response = parseObject.getJSONObject("response");

            JSONObject content = response.getJSONObject("content");

            Boolean success = content.getBoolean("success");

            String message = content.getString("errorMsg");

            if (success)
            {
                responseJson.setStatus(StatusCode.SUCCESS);
                responseJson.getBody().put(LogisticsConstants.AGENT_MAILNO, content.getString("data"));
            }
            else
            {
                responseJson.setMessage(message);
            }
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            responseJson.setMessage(e.getMessage());
            return responseJson;
        }
        finally
        {
            HttpClientUtils.closeQuietly(httpResponse);
        }

        return responseJson;
    }

    /**
     *
     * ???????????????????????????
     *
     * @author ?????????
     * @param order
     * @param transportationMode
     * @return
     */
    private String buildParam(String customCode, Order order, TransportationMode transportationMode, LogisticsConf logisticsConf)
    {
        JSONArray param = new JSONArray();

        JSONObject requestInfo = new JSONObject();

        //?????????????????????
        requestInfo.put("customerCode", transportationMode.getShippingServiceExpressType());
        //???????????????????????????PIN
        requestInfo.put("gatewayUser", logisticsConf.getRedirectUri());
        //??????????????????
        requestInfo.put("operator", logisticsConf.getUserId());
        //?????????????????????
        requestInfo.put("sourceSystem", logisticsConf.getOrderNoPrefix());
        //????????????????????? ?????????10-??????
        requestInfo.put("systemType", 10);

        param.add(requestInfo);

        JSONObject waybillDTO = new JSONObject();

        //????????????????????? ?????????31-????????????
        waybillDTO.put("billType", 31);

        //??????????????? 1-????????????
        waybillDTO.put("createType", "1");

        //?????????????????????
        waybillDTO.put("customerCode", transportationMode.getShippingServiceExpressType());

        //???????????????????????????
        waybillDTO.put("customerWaybillNo", customCode);

        //????????????????????? 1-?????? 2-??????
        waybillDTO.put("invokeType", "1");

        //??????????????????????????????
        waybillDTO.put("orderCode", order.getPlatformOrderId());

        //????????????????????? ?????????????????????1
        waybillDTO.put("packageCount", 1);

        //????????????????????? 1-????????????
        waybillDTO.put("packageIden", 1);

        //???????????????????????????
        List<JSONObject> performanceSpDTOList = new ArrayList<>();
        JSONObject performanceSpDTO = new JSONObject();
        performanceSpDTO.put("code", transportationMode.getShippingServiceCs());
        performanceSpDTOList.add(performanceSpDTO);
        waybillDTO.put("performanceSpDTOList", performanceSpDTOList);

        //??????????????????????????????????????????35??????
        String receiverAddress = "";
        if (!transportationMode.getShippingServiceCs().equals("SPB9900i1") && !transportationMode.getShippingServiceCs().equals("SPB9900i3"))
        {
            receiverAddress = order.getBuyerStateOrProvince();
            receiverAddress = StringUtils.isNotBlank(receiverAddress) ? receiverAddress : "";
            receiverAddress += StringUtils.isNotBlank(order.getBuyerCity()) ? order.getBuyerCity() : "";
        }

        waybillDTO.put("receiverAddress", receiverAddress + OrderUtils.getFullStreet(order));

        //????????????
        waybillDTO.put("receiverCity", order.getBuyerCity());

        //????????????????????????
        String buyerCountryCode = order.getBuyerCountryCode();
        waybillDTO.put("receiverCountry", buyerCountryCode);


        //????????????/???
        if (StringUtils.equals(buyerCountryCode,"CN"))
        {
            waybillDTO.put("receiverCounty", order.getBuyerCity());
        }

        //???????????????
        waybillDTO.put("receiverMail", order.getBuyerEmail());

        //????????????????????????
        waybillDTO.put("receiverName", order.getBuyerName());

        //??????????????????
        waybillDTO.put("receiverPhone", OrderUtils.getShipPhone(order));

        //?????????????????????
        waybillDTO.put("receiverProvince", order.getBuyerStateOrProvince());

        //????????????????????????
        waybillDTO.put("receiverZipCode", order.getBuyerPostalCode());

        //???????????????????????????
        waybillDTO.put("salesPlatform", order.getPlatform());

        //?????????????????????????????????
        waybillDTO.put("senderCompanyNameEn", transportationMode.getConsignerCompanyen());

        //????????????????????????
        String senderCountry = transportationMode.getCountryCode();
        waybillDTO.put("senderCountry", senderCountry);

        //?????????????????????
        String senderProvince = transportationMode.getConsignerProvince();
        waybillDTO.put("senderProvince", senderProvince);

        //?????????????????????
        String senderCity = transportationMode.getConsignerCity();
        waybillDTO.put("senderCity", senderCity);

        //?????????????????????
        String senderCounty = transportationMode.getConsignerStreet();
        if (StringUtils.equals("CN",senderCountry))
        {
            waybillDTO.put("senderCounty", senderCity);
        }

        //??????????????????????????????
        waybillDTO.put("senderAddress", senderCounty);

        //????????????????????????
        String returnerName = transportationMode.getConsignerName();
        waybillDTO.put("senderName", returnerName);

        //???????????????????????????
        waybillDTO.put("senderPhone", transportationMode.getConsignerTel());

        //????????????????????????
        waybillDTO.put("senderZipCode", transportationMode.getConsignerPostcode());

        //?????????????????????,???????????? 0-??? 1-???
        int isDiscard = 0;
        waybillDTO.put("isDiscard", isDiscard);

        if (isDiscard == 0)
        {
            waybillDTO.put("returnerCity", senderCity);
            waybillDTO.put("returnerAddress", senderProvince + senderCity + senderCounty);
            waybillDTO.put("returnerProvince", senderProvince);
            waybillDTO.put("returnerName", returnerName);
            waybillDTO.put("returnerCountry", senderCountry);
            waybillDTO.put("returnerArea", senderCity);
            waybillDTO.put("returnerZipCode", transportationMode.getConsignerPostcode());
            waybillDTO.put("returnerPhone", transportationMode.getConsignerTel());
            waybillDTO.put("returnerTel", transportationMode.getConsignerTel());
        }

        //????????????
        JSONObject waybillPackDTO = new JSONObject();

        List<JSONObject> waybillPackDTOList = new ArrayList<>();

        waybillPackDTO.put("customerPackCode", customCode);

        //?????? ??????1-??????
        waybillPackDTO.put("lengthUnit", 1);

        Double totalLength = 0.0;
        Double totalWidth = 0.0;
        Double totalHeight = 0.0;

        //????????????????????? 1-??? 2-?????? 3-???
        String weightUnit = transportationMode.getShippingServiceSd();
        if (StringUtils.isBlank(weightUnit))
        {
            weightUnit = "1";
        }
        waybillPackDTO.put("weightUnit", Integer.valueOf(weightUnit));

        if (StringUtils.equals(weightUnit, "1"))
        {
            //?????????????????????
            waybillPackDTO.put("buWeight", OrderUtils.getTotalCustomsWeight(order.getOrderItems()));
        }
        else if (StringUtils.equals(weightUnit, "2"))
        {
            //?????????????????????
            waybillPackDTO.put("buWeight", OrderUtils.getTotalCustomsWeightKg(order.getOrderItems()));
        }


        //??????????????????
        List<JSONObject> waybillWareList = new ArrayList<>();
        List<OrderItem> orderItems = order.getOrderItems();
        if (CollectionUtils.isNotEmpty(orderItems))
        {
            for (OrderItem orderItem : orderItems)
            {
                JSONObject waybillWareDTO = new JSONObject();
                RelatedProduct product = orderItem.getRelatedProduct();
                if (product != null)
                {
                    // ?????????
                    Double length = product.getProductLength() == null ? 0.0 : product.getProductLength();
                    if (length > totalLength)
                    {
                        totalLength = length;
                    }

                    // ?????????
                    Double width = product.getProductWidth() == null ? 0.0 : product.getProductWidth();
                    if (width > totalWidth)
                    {
                        totalWidth = width;
                    }

                    // ?????????
                    Double height = product.getProductHeight() == null ? 0.0 : product.getProductHeight();
                    totalHeight += (height * orderItem.getSaleQuantity());
                }
                
                //??????????????????
                String feature = OrderUtils.getProductFeatures(order);
                //????????????
                waybillWareDTO.put("chargedStatus", StringUtils.contains(feature, "??????") ? 1 : 0);
                //?????????????????????
                waybillWareDTO.put("cosmeticStatus", StringUtils.contains(feature, "??????") ? 1 : 0);
                //??????????????????
                waybillWareDTO.put("liquidStatus", StringUtils.contains(feature, "??????") ? 1 : 0);
                //????????????
                waybillWareDTO.put("powderStatus", StringUtils.contains(feature, "??????") ? 1 : 0);
                //???????????????
                waybillWareDTO.put("dangerousStatus", StringUtils.contains(feature, "??????") ? 1 : 0);

                //?????????????????????
                waybillWareDTO.put("currency", order.getCurrency());

                //????????????????????????
                waybillWareDTO.put("customerPackCode", customCode);

                //???????????????????????????
                waybillWareDTO.put("goodsCode", product.getProductSku());

                //?????????????????????
                waybillWareDTO.put("goodsCount", orderItem.getSaleQuantity());

                //?????????????????????
                waybillWareDTO.put("goodsName", product.getCustomsNameCn());

                //?????????????????????????????????
                waybillWareDTO.put("goodsNameEn", product.getCustomsNameEn());

                //??????????????????
                waybillWareDTO.put("sourceCountry", "CN");

                //???????????????????????????(??????)
                waybillWareDTO.put("worth", BigDecimal.valueOf(OrderUtils.getTotalCustomsValue(orderItem)));
                waybillWareList.add(waybillWareDTO);
            }
        }
        waybillPackDTO.put("forecastHeight", BigDecimal.valueOf(totalHeight));
        waybillPackDTO.put("forecastLength", BigDecimal.valueOf(totalLength));
        waybillPackDTO.put("forecastWidth", BigDecimal.valueOf(totalWidth));

        waybillPackDTO.put("waybillWareList", waybillWareList);

        waybillPackDTOList.add(waybillPackDTO);

        waybillDTO.put("waybillPackDTOList", waybillPackDTOList);
        param.add(waybillDTO);

        return JSON.toJSONString(param);
    }

    /**
     *
     * ??????????????????
     *
     * @author ?????????
     * @param order
     * @param transportationMode
     * @param logisticsConf
     * @return
     */
    public ResponseJson getAgentMailno(Order order, TransportationMode transportationMode, LogisticsConf logisticsConf)
    {
        ResponseJson responseJson = new ResponseJson();
        responseJson.setStatus(StatusCode.FAIL);

        //??????????????????,???????????????
        String customCode = order.getOrderId().toString() + order.getLogisticsType().toString();
        //??????????????????
        String jsonStr = getTrackingNumber(customCode, transportationMode, logisticsConf);

        if (StringUtils.isNotBlank(order.getTrackingNumber()))
        {
            responseJson.setMessage("?????????????????????,??????????????????");
            return responseJson;
        }

        //??????url
        String url = buildUrl(API_PRINT_ORDER_SYNC, jsonStr, logisticsConf);

        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse httpResponse = null;

        try
        {
            httpPost.setEntity(new StringEntity(jsonStr,"UTF-8"));

            httpResponse = httpClient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();

            // ????????????????????? ??????????????? ??????if ??????
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK || entity == null)
            {
                responseJson.setMessage(httpResponse.getStatusLine().toString());
                return responseJson;
            }

            responseJson.getBody().put(LogisticsConstants.CUSTOM_NUMBER, customCode);

            String returnResponse = EntityUtils.toString(entity, Constants.CHARSET_UTF8);
            JSONObject parseObject = JSON.parseObject(returnResponse);

            JSONObject response = parseObject.getJSONObject("response");
            JSONObject content = response.getJSONObject("content");

            JSONObject dataJson = content.getJSONObject("data");
            Boolean success = content.getBoolean("success");
            String message = content.getString("errorMsg");
            if (dataJson != null)
            {
                JSONObject packageSheetInfoMap = dataJson.getJSONObject("packageSheetInfoMap");

                JSONObject jsonObject = packageSheetInfoMap.getJSONObject(customCode);
                String trackingNo = jsonObject.getString("trackingNo");
                String billUrl = jsonObject.getString("billUrl");

                //????????????
                ResponseJson labelStreams = getLabelStreams(billUrl, transportationMode, true);

                Map<String, Object> body = labelStreams.getBody();

                if (success)
                {
                    responseJson.setStatus(StatusCode.SUCCESS);
                    responseJson.setMessage(trackingNo);
                    responseJson.getBody().putAll(body);
                }
                else
                {
                    responseJson.setMessage(message);
                }
            }
            else
            {
                responseJson.setMessage(message);
            }

        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            responseJson.setMessage(e.getMessage());
        }
        finally
        {
            HttpClientUtils.closeQuietly(httpResponse);
        }

        return responseJson;
    }


    /**
     *
     * ????????????
     *
     * @param order
     * @param transportationMode
     * @param logisticsConf
     */
    public ResponseJson printLables(Order order, TransportationMode transportationMode, LogisticsConf logisticsConf)
    {
        ResponseJson responseJson = new ResponseJson();
        Integer logisticsType = order.getLogisticsType();

        String customCode = order.getOrderId().toString() + logisticsType.toString();
        String jsonStr = getTrackingNumber(customCode, transportationMode, logisticsConf);

        //??????url
        String url = buildUrl(API_PRINT_ORDER_SYNC, jsonStr, logisticsConf);

        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse httpResponse = null;

        try
        {
            httpPost.setEntity(new StringEntity(jsonStr, "UTF-8"));

            httpResponse = httpClient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();

            // ????????????????????? ??????????????? ??????if ??????
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK || entity == null)
            {
                responseJson.setMessage(httpResponse.getStatusLine().toString());
                return responseJson;
            }

            responseJson.getBody().put(LogisticsConstants.CUSTOM_NUMBER, customCode);

            String returnResponse = EntityUtils.toString(entity, Constants.CHARSET_UTF8);
            JSONObject parseObject = JSON.parseObject(returnResponse);

            JSONObject response = parseObject.getJSONObject("response");
            JSONObject content = response.getJSONObject("content");
            JSONObject dataJson = content.getJSONObject("data");
            String message = content.getString("errorMsg");
            if (dataJson != null)
            {
                JSONObject packageSheetInfoMap = dataJson.getJSONObject("packageSheetInfoMap");

                JSONObject jsonObject = packageSheetInfoMap.getJSONObject(customCode);
                String billUrl = jsonObject.getString("billUrl");

                //????????????
                responseJson = getLabelStreams(billUrl, transportationMode, false);
            }
            else
            {
                responseJson.setMessage(message);
            }

        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            responseJson.setMessage(e.getMessage());
        }
        finally
        {
            HttpClientUtils.closeQuietly(httpResponse);
        }

        return responseJson;
    }


    /**
     *
     * ????????????
     *
     * @author ?????????
     * @param billUrl
     * @param transportationMode
     * @return
     */
    public ResponseJson getLabelStreams(String billUrl, TransportationMode transportationMode, Boolean flag)
    {
        ResponseJson responseJson = new ResponseJson(StatusCode.FAIL);
        CloseableHttpClient httpClient = getHttpClient();
        HttpGet httpGet = new HttpGet(billUrl);
        CloseableHttpResponse httpResponse = null;
        InputStream labelInputStream = null;
        FileInputStream pdfInputStream = null;
        File tempFile = null;
        try
        {
            httpResponse = httpClient.execute(httpGet);

            HttpEntity entity = httpResponse.getEntity();
            labelInputStream = entity.getContent();

            if (labelInputStream != null)
            {
                String logisticsCode = transportationMode.getLogisticsCode();

                if (transportationMode.getShippingServiceCs().equals("SPB9900i1") || transportationMode.getShippingServiceCs().equals("SPB9900i3"))
                {
                    tempFile = PdfUtils.addText(labelInputStream, "(" + logisticsCode + ")", Element.ALIGN_LEFT, 60, 260, 0, false);
                }
                else
                {
                    tempFile = PdfUtils.addText(labelInputStream, "(" + logisticsCode + ")", Element.ALIGN_RIGHT, 200, 30, 0, false);
                }

                pdfInputStream = new FileInputStream(tempFile);

                if (flag)
                {
                    FastdfsClientManager fastdfsClientManager = SpringContextUtils.getBean("fastdfsClientManager", FastdfsClientManager.class);
                    StorePath storePath = fastdfsClientManager.upload(IOUtils.toByteArray(pdfInputStream), Constants.PDF_FORMAT);
                    if (storePath != null)
                    {
                        responseJson.getBody().put(LogisticsConstants.FASTDFS_GROUP, storePath.getGroup());
                        responseJson.getBody().put(LogisticsConstants.FASTDFS_PATH, storePath.getPath());
                        responseJson.setStatus(StatusCode.SUCCESS);
                    }
                }
                else
                {
                    responseJson.getBody().put(LogisticsConstants.LABEL_STREAM, IOUtils.toByteArray(pdfInputStream));
                    responseJson.setStatus(StatusCode.SUCCESS);
                }
            }
            else
            {
                responseJson.setMessage("??????????????????????????? ???????????????????????????");
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
            IOUtils.closeQuietly(pdfInputStream);
            // ??????????????????
            PdfUtils.deleteFile(tempFile);
        }

        return responseJson;
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param customCode
     * @return
     */
    private String getTrackingNumber(String customCode, TransportationMode transportationMode, LogisticsConf logisticsConf)
    {
        JSONArray param = new JSONArray();
        JSONObject requestInfo = new JSONObject();
        JSONObject waybillPrintDTO = new JSONObject();

        //?????????????????????
        requestInfo.put("customerCode", transportationMode.getShippingServiceExpressType());
        //???????????????????????????PIN
        requestInfo.put("gatewayUser", logisticsConf.getRedirectUri());
        //???????????????????????????
        requestInfo.put("customerBillCode", customCode);
        //??????????????????
        requestInfo.put("operator", logisticsConf.getUserId());
        //?????????????????????
        requestInfo.put("sourceSystem", logisticsConf.getOrderNoPrefix());
        //?????????????????????
        requestInfo.put("systemType", 10);

        //?????????????????????
        waybillPrintDTO.put("customerCode", transportationMode.getShippingServiceExpressType());
        //???????????????????????????
        waybillPrintDTO.put("customerWaybillNo", customCode);
        //???????????????????????????PIN
        waybillPrintDTO.put("pin", logisticsConf.getRedirectUri());
        //?????????????????????
        waybillPrintDTO.put("printAllCarrierBill", true);

        param.add(requestInfo);
        param.add(waybillPrintDTO);

        return JSON.toJSONString(param);
    }


    /**
     *
     * ??????url
     *
     * @author ?????????
     * @param apiMethod
     * @param jsonData
     * @return
     */
    private String buildUrl(String apiMethod, String jsonData, LogisticsConf logisticsConf)
    {
        Map<String, String> sysParams = getSysParams(apiMethod, logisticsConf);

        Map<String, String> pmap = new TreeMap<>();
        pmap.put("param_json", jsonData);
        pmap.putAll(sysParams);
        String sign = sign(pmap, logisticsConf.getClientSecret());

        sysParams.put("sign", sign);
        StringBuilder sb = new StringBuilder(URL);
        sb.append("?");
        sb.append(JingDongCall.buildQuery(sysParams, Constants.CHARSET_UTF8));

        return sb.toString();
    }

    /**
     *
     * ??????????????????
     *
     * @author ?????????
     * @param apiMethod
     * @param logisticsConf
     * @return
     */
    public static Map<String, String> getSysParams(String apiMethod, LogisticsConf logisticsConf)
    {
        Map<String, String> sysParams = new HashMap<>();
        sysParams.put("method", apiMethod);
        sysParams.put("timestamp", DateUtils.timeToString(new Timestamp(new Date().getTime())));
        sysParams.put("v", VERSION);
        sysParams.put("access_token", logisticsConf.getToken());
        sysParams.put("app_key", logisticsConf.getClientId());

        return sysParams;
    }

    /**
     *
     * ????????????
     *
     * @param pmap
     * @param appSecret
     * @return
     */
    private String sign(Map<String, String> pmap, String appSecret)
    {
        StringBuilder sb = new StringBuilder(appSecret);

        for (Entry<String, String> entry : pmap.entrySet())
        {
            String name = (String) entry.getKey();
            String value = (String) entry.getValue();

            if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value))
            {
                sb.append(name).append(value);
            }
        }

        sb.append(appSecret);
        String result = md5(sb.toString());

        return result;
    }

    /**
     *
     * md5??????
     *
     * @param source
     * @return
     */
    public static String md5(String source)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(source.getBytes(Constants.CHARSET_UTF8));
            return byte2hex(bytes);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * ???????????????
     *
     * @param bytes
     * @return
     */
    public static String byte2hex(byte[] bytes)
    {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++)
        {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1)
            {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    protected CloseableHttpClient getHttpClient()
    {
        // ????????????????????? closeableHttpClientFactoryBean
        return SpringContextUtils.getBean("closeableHttpClientFactoryBean", CloseableHttpClient.class);
    }

    @Override
    public LogisticsCompany category()
    {
        return LogisticsCompany.NEW_JDWL;
    }

    @Override
    public ResponseJson execute(LogisticsCallContext context)
    {
        Order order = context.getOrder();
        TransportationMode transportationMode = context.getTransportationMode();
        LogisticsConf logisticsConf = context.getLogisticsConf();

        if (StringUtils.equals(transportationMode.getShippingServiceExpressType(),"KH20000001785"))
        {
            return OzonCall.getInstance().creatOrder(order, transportationMode, logisticsConf, context.getDebugMode());
        }
        else
        {
            return JDWLNewCall.getInstance().creatOrder(order, transportationMode, logisticsConf, context.getDebugMode());
        }

    }
}

get?????????
    /**
     * ???????????????
     *
     * @param agentMailNo
     * @return
     */
    public ResponseJson getTrackNumber(String agentMailNo)
    {
        ResponseJson response = new ResponseJson(StatusCode.FAIL);

        CloseableHttpClient httpClient = getHttpClient();
        //  http://39.108.221.61/cgi-bin/GInfo.dll?EmsApiTrack&cno=BA738975998211143
        String url = MessageFormat.format("{0}/GInfo.dll?EmsApiTrack&cno={1}", REQUEST_URL, agentMailNo);
        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse httpResponse = null;
        try
        {
            httpResponse = httpClient.execute(httpGet);

            String responseStr = EntityUtils.toString(httpResponse.getEntity());
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                String referNumber = StringUtils.substringBetween(responseStr, "<REFER_NBR>", "</REFER_NBR>");
                String trackNumber = StringUtils.substringBetween(responseStr, "<TRANS_NBR>", "</TRANS_NBR>");

                if (StringUtils.isNotEmpty(referNumber))
                {
                    response.setStatus(StatusCode.SUCCESS);
                    response.getBody().put(LogisticsConstants.AGENT_MAILNO, referNumber);
                    response.setMessage(trackNumber);
                }
                else
                {
                    response.setMessage("?????????????????????");
                }
            }
        }
        catch (IOException e)
        {
            LOGGER.error(e.getMessage(), e);
            response.setMessage(e.getMessage());
        }
        finally
        {
            HttpClientUtils.closeQuietly(httpResponse);
        }

        return response;
    }