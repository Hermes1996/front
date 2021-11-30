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
 * 京东中英中美标准专线
 *
 * @author 赵亚航
 * @version Bessky V100R001 2021年02月23日
 * @since Bessky V100R001C00
 */
public class JDWLNewCall extends AbstractLogisticsCall
{
    private static final String URL = "https://us-api.jd.com/routerjson";

    /**
     * 同步创建运单api方法名
     */
    private static final String API_CREATE_ORDER_SYNC = "jingdong.fop.service.deliveryWaybill";

    /**
     * 同步打印面单api方法名
     */
    private static final String API_PRINT_ORDER_SYNC = "jingdong.fop.waybill.printWaybill";

    /**
     * 版本
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
     * 创建订单
     *
     * @author 赵亚航
     * @param order
     * @param transportationMode
     * @param logisticsConf
     * @param isDebugMode
     * @return
     */
    public ResponseJson creatOrder(Order order, TransportationMode transportationMode, LogisticsConf logisticsConf,  Boolean isDebugMode)
    {
        ResponseJson responseJson = new ResponseJson(StatusCode.FAIL);

        //获取客户单号,不支持假标
        String customCode = order.getOrderId().toString() + order.getLogisticsType().toString();
        //构建请求参数
        String jsonStr = buildParam(customCode, order, transportationMode, logisticsConf);
        //构建url
        String url = buildUrl(API_CREATE_ORDER_SYNC, jsonStr, logisticsConf);
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse httpResponse = null;

        try
        {
            httpPost.setEntity(new StringEntity(jsonStr,"UTF-8"));
            httpPost.addHeader(HTTP.CONTENT_TYPE,"application/json;charset=utf-8");

            httpResponse = getHttpClient().execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();

            // 返回内容错误， 直接返回， 减少if 嵌套
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
     * 封装跟踪号请求参数
     *
     * @author 赵亚航
     * @param order
     * @param transportationMode
     * @return
     */
    private String buildParam(String customCode, Order order, TransportationMode transportationMode, LogisticsConf logisticsConf)
    {
        JSONArray param = new JSONArray();

        JSONObject requestInfo = new JSONObject();

        //必填，客户编码
        requestInfo.put("customerCode", transportationMode.getShippingServiceExpressType());
        //必填，物流网关用户PIN
        requestInfo.put("gatewayUser", logisticsConf.getRedirectUri());
        //必填，操作人
        requestInfo.put("operator", logisticsConf.getUserId());
        //必填，来源系统
        requestInfo.put("sourceSystem", logisticsConf.getOrderNoPrefix());
        //必填，系统类型 默认为10-商家
        requestInfo.put("systemType", 10);

        param.add(requestInfo);

        JSONObject waybillDTO = new JSONObject();

        //必填，运单类型 默认为31-跨境小包
        waybillDTO.put("billType", 31);

        //必填，默认 1-客户创建
        waybillDTO.put("createType", "1");

        //必填，客户编码
        waybillDTO.put("customerCode", transportationMode.getShippingServiceExpressType());

        //必填，客户运单编码
        waybillDTO.put("customerWaybillNo", customCode);

        //必填，调用类型 1-新增 2-修改
        waybillDTO.put("invokeType", "1");

        //必填，销售平台订单号
        waybillDTO.put("orderCode", order.getPlatformOrderId());

        //必填，包裹总数 若不传则默认为1
        waybillDTO.put("packageCount", 1);

        //必填，包装标识 1-不需包装
        waybillDTO.put("packageIden", 1);

        //必填，服务产品明细
        List<JSONObject> performanceSpDTOList = new ArrayList<>();
        JSONObject performanceSpDTO = new JSONObject();
        performanceSpDTO.put("code", transportationMode.getShippingServiceCs());
        performanceSpDTOList.add(performanceSpDTO);
        waybillDTO.put("performanceSpDTOList", performanceSpDTOList);

        //收货人详细地址，中英渠道限制35字符
        String receiverAddress = "";
        if (!transportationMode.getShippingServiceCs().equals("SPB9900i1") && !transportationMode.getShippingServiceCs().equals("SPB9900i3"))
        {
            receiverAddress = order.getBuyerStateOrProvince();
            receiverAddress = StringUtils.isNotBlank(receiverAddress) ? receiverAddress : "";
            receiverAddress += StringUtils.isNotBlank(order.getBuyerCity()) ? order.getBuyerCity() : "";
        }

        waybillDTO.put("receiverAddress", receiverAddress + OrderUtils.getFullStreet(order));

        //收货人市
        waybillDTO.put("receiverCity", order.getBuyerCity());

        //必填，收货人国家
        String buyerCountryCode = order.getBuyerCountryCode();
        waybillDTO.put("receiverCountry", buyerCountryCode);


        //收货人区/县
        if (StringUtils.equals(buyerCountryCode,"CN"))
        {
            waybillDTO.put("receiverCounty", order.getBuyerCity());
        }

        //收件人邮箱
        waybillDTO.put("receiverMail", order.getBuyerEmail());

        //必填，收货人姓名
        waybillDTO.put("receiverName", order.getBuyerName());

        //必填，手机号
        waybillDTO.put("receiverPhone", OrderUtils.getShipPhone(order));

        //必填，收货人省
        waybillDTO.put("receiverProvince", order.getBuyerStateOrProvince());

        //必填，收件人邮编
        waybillDTO.put("receiverZipCode", order.getBuyerPostalCode());

        //必填，销售平台名称
        waybillDTO.put("salesPlatform", order.getPlatform());

        //必填，发货人公司英文名
        waybillDTO.put("senderCompanyNameEn", transportationMode.getConsignerCompanyen());

        //必填，发货人国家
        String senderCountry = transportationMode.getCountryCode();
        waybillDTO.put("senderCountry", senderCountry);

        //必填，发货人省
        String senderProvince = transportationMode.getConsignerProvince();
        waybillDTO.put("senderProvince", senderProvince);

        //必填，发货人市
        String senderCity = transportationMode.getConsignerCity();
        waybillDTO.put("senderCity", senderCity);

        //必填，发货人县
        String senderCounty = transportationMode.getConsignerStreet();
        if (StringUtils.equals("CN",senderCountry))
        {
            waybillDTO.put("senderCounty", senderCity);
        }

        //必填，发货人详细地址
        waybillDTO.put("senderAddress", senderCounty);

        //必填，发货人姓名
        String returnerName = transportationMode.getConsignerName();
        waybillDTO.put("senderName", returnerName);

        //必填，发件人手机号
        waybillDTO.put("senderPhone", transportationMode.getConsignerTel());

        //必填，发件人邮编
        waybillDTO.put("senderZipCode", transportationMode.getConsignerPostcode());

        //必填，无法妥投,是否销毁 0-否 1-是
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

        //包裹明细
        JSONObject waybillPackDTO = new JSONObject();

        List<JSONObject> waybillPackDTOList = new ArrayList<>();

        waybillPackDTO.put("customerPackCode", customCode);

        //长度 默认1-厘米
        waybillPackDTO.put("lengthUnit", 1);

        Double totalLength = 0.0;
        Double totalWidth = 0.0;
        Double totalHeight = 0.0;

        //必填，重量单位 1-克 2-千克 3-磅
        String weightUnit = transportationMode.getShippingServiceSd();
        if (StringUtils.isBlank(weightUnit))
        {
            weightUnit = "1";
        }
        waybillPackDTO.put("weightUnit", Integer.valueOf(weightUnit));

        if (StringUtils.equals(weightUnit, "1"))
        {
            //客户预报包裹重
            waybillPackDTO.put("buWeight", OrderUtils.getTotalCustomsWeight(order.getOrderItems()));
        }
        else if (StringUtils.equals(weightUnit, "2"))
        {
            //客户预报包裹重
            waybillPackDTO.put("buWeight", OrderUtils.getTotalCustomsWeightKg(order.getOrderItems()));
        }


        //运单商品明细
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
                    // 取最长
                    Double length = product.getProductLength() == null ? 0.0 : product.getProductLength();
                    if (length > totalLength)
                    {
                        totalLength = length;
                    }

                    // 取最宽
                    Double width = product.getProductWidth() == null ? 0.0 : product.getProductWidth();
                    if (width > totalWidth)
                    {
                        totalWidth = width;
                    }

                    // 高叠加
                    Double height = product.getProductHeight() == null ? 0.0 : product.getProductHeight();
                    totalHeight += (height * orderItem.getSaleQuantity());
                }
                
                //产品特性标签
                String feature = OrderUtils.getProductFeatures(order);
                //是否带电
                waybillWareDTO.put("chargedStatus", StringUtils.contains(feature, "带电") ? 1 : 0);
                //是否化妆品标识
                waybillWareDTO.put("cosmeticStatus", StringUtils.contains(feature, "化妆") ? 1 : 0);
                //是否液体标识
                waybillWareDTO.put("liquidStatus", StringUtils.contains(feature, "液体") ? 1 : 0);
                //是否粉末
                waybillWareDTO.put("powderStatus", StringUtils.contains(feature, "粉末") ? 1 : 0);
                //是否危险品
                waybillWareDTO.put("dangerousStatus", StringUtils.contains(feature, "危险") ? 1 : 0);

                //必填，申报币种
                waybillWareDTO.put("currency", order.getCurrency());

                //必填，客户包裹号
                waybillWareDTO.put("customerPackCode", customCode);

                //必填，客户商品编码
                waybillWareDTO.put("goodsCode", product.getProductSku());

                //必填，商品数量
                waybillWareDTO.put("goodsCount", orderItem.getSaleQuantity());

                //必填，商品名称
                waybillWareDTO.put("goodsName", product.getCustomsNameCn());

                //必填，商品英文报关名称
                waybillWareDTO.put("goodsNameEn", product.getCustomsNameEn());

                //必填，原产国
                waybillWareDTO.put("sourceCountry", "CN");

                //必填，商品申报价值(单个)
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
     * 获取服务商号
     *
     * @author 赵亚航
     * @param order
     * @param transportationMode
     * @param logisticsConf
     * @return
     */
    public ResponseJson getAgentMailno(Order order, TransportationMode transportationMode, LogisticsConf logisticsConf)
    {
        ResponseJson responseJson = new ResponseJson();
        responseJson.setStatus(StatusCode.FAIL);

        //获取客户单号,不支持假标
        String customCode = order.getOrderId().toString() + order.getLogisticsType().toString();
        //构建请求参数
        String jsonStr = getTrackingNumber(customCode, transportationMode, logisticsConf);

        if (StringUtils.isNotBlank(order.getTrackingNumber()))
        {
            responseJson.setMessage("服务商号已存在,请勿重复获取");
            return responseJson;
        }

        //构建url
        String url = buildUrl(API_PRINT_ORDER_SYNC, jsonStr, logisticsConf);

        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse httpResponse = null;

        try
        {
            httpPost.setEntity(new StringEntity(jsonStr,"UTF-8"));

            httpResponse = httpClient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();

            // 返回内容错误， 直接返回， 减少if 嵌套
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

                //获取面单
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
     * 获取面单
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

        //构建url
        String url = buildUrl(API_PRINT_ORDER_SYNC, jsonStr, logisticsConf);

        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse httpResponse = null;

        try
        {
            httpPost.setEntity(new StringEntity(jsonStr, "UTF-8"));

            httpResponse = httpClient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();

            // 返回内容错误， 直接返回， 减少if 嵌套
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

                //获取面单
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
     * 获取面单
     *
     * @author 赵亚航
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
                responseJson.setMessage("获取面单内容为空， 请重试或联系物流商");
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
            // 删除临时文件
            PdfUtils.deleteFile(tempFile);
        }

        return responseJson;
    }

    /**
     * 构建获取服务商号和打印面单请求参数
     *
     * @param customCode
     * @return
     */
    private String getTrackingNumber(String customCode, TransportationMode transportationMode, LogisticsConf logisticsConf)
    {
        JSONArray param = new JSONArray();
        JSONObject requestInfo = new JSONObject();
        JSONObject waybillPrintDTO = new JSONObject();

        //必填，客户编码
        requestInfo.put("customerCode", transportationMode.getShippingServiceExpressType());
        //必填，物流网关用户PIN
        requestInfo.put("gatewayUser", logisticsConf.getRedirectUri());
        //必填，客户运单编码
        requestInfo.put("customerBillCode", customCode);
        //必填，操作人
        requestInfo.put("operator", logisticsConf.getUserId());
        //必填，来源系统
        requestInfo.put("sourceSystem", logisticsConf.getOrderNoPrefix());
        //必填，系统类型
        requestInfo.put("systemType", 10);

        //必填，客户编码
        waybillPrintDTO.put("customerCode", transportationMode.getShippingServiceExpressType());
        //必填，客户运单编码
        waybillPrintDTO.put("customerWaybillNo", customCode);
        //必填，物流网关用户PIN
        waybillPrintDTO.put("pin", logisticsConf.getRedirectUri());
        //必填，打印类型
        waybillPrintDTO.put("printAllCarrierBill", true);

        param.add(requestInfo);
        param.add(waybillPrintDTO);

        return JSON.toJSONString(param);
    }


    /**
     *
     * 构建url
     *
     * @author 赵亚航
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
     * 获取系统参数
     *
     * @author 赵亚航
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
     * 获取签名
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
     * md5算法
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
     * 转十六进制
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
        // 返回请求客户端 closeableHttpClientFactoryBean
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
