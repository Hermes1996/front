package com.bessky.erp.track.api.chinapost;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bessky.erp.core.common.type.LogisticsCompany;
import com.bessky.erp.starter.core.component.StatusCode;
import com.bessky.erp.starter.core.json.ResponseJson;
import com.bessky.erp.system.logistics.bean.LogisticsConf;
import com.bessky.erp.track.auto.AbstractTrackCall;
import com.bessky.erp.track.auto.TrackContext;
import com.bessky.erp.track.auto.TrackInfo;
import com.bessky.erp.track.auto.TrackResponseJson;
import com.bessky.erp.track.bean.StableOrderTrack;
import com.bessky.erp.track.bean.StableTrackConf;
import com.bessky.erp.track.utils.TrackUtils;


public class ShenZhenPostTrackCall extends AbstractTrackCall
{
    // 正式环境：http://211.156.195.237
    // 测试环境：http://211.156.195.238
    private static final String end_point = "http://211.156.195.237";

    private static ShenZhenPostTrackCall SINGLE = new ShenZhenPostTrackCall();

    private ShenZhenPostTrackCall()
    {
    }

    public static ShenZhenPostTrackCall getInstance()
    {
        return SINGLE;
    }
    
    public List<TrackResponseJson> getRouteInfos(StableTrackConf stableTrackConf, List<StableOrderTrack> orderTracks, LogisticsConf logisticsConf)
    {
        List<TrackResponseJson> trackResponseJson = new ArrayList<TrackResponseJson>();
        for (StableOrderTrack stableOrderTrack : orderTracks)
        {
            if (StringUtils.isNoneBlank(stableOrderTrack.getTrackingNumber()))
            {
                getRouteInfo(stableOrderTrack, trackResponseJson, stableTrackConf);
            }
        }
        return trackResponseJson;
    }

    private void getRouteInfo(StableOrderTrack stableOrderTrack, List<TrackResponseJson> trackResponseJson, StableTrackConf stableTrackConf)
    {
        ResponseJson responseJson = new ResponseJson();
        responseJson.setStatus(StatusCode.FAIL);
        String trackingNumber = stableOrderTrack.getTrackingNumber();// 跟踪号
        String responseStr = "";
        try
        {
            String sendID = stableTrackConf.getApiCode(); // 大客户号:81100090206313
            String key = stableTrackConf.getApiToken(); // 轨迹密钥 (登陆https://my.ems.com.cn个人中心查看)
            String dateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

            String url = MessageFormat.format("{0}/querypush-pcpw/mailTrackProtocolPortal/queryMailTrackWn", end_point);

            Map<String, Object> traceNoMap = new HashMap<>();
            traceNoMap.put("traceNo", trackingNumber);
            String jsonTraceNo = JSON.toJSONString(traceNoMap);
            String dataDigest = MD5(jsonTraceNo + key);
            String msgBody = URLEncoder.encode(jsonTraceNo, "utf-8");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("sendID", sendID));
            params.add(new BasicNameValuePair("proviceNo", "99"));
            params.add(new BasicNameValuePair("msgKind", "XXX_JDPT_TRACE"));
            params.add(new BasicNameValuePair("serialNo", stableOrderTrack.getOrderId().toString()));
            params.add(new BasicNameValuePair("sendDate", dateStr));
            params.add(new BasicNameValuePair("receiveID", "JDPT"));
            params.add(new BasicNameValuePair("dataType", "1"));
            params.add(new BasicNameValuePair("dataDigest", dataDigest));
            params.add(new BasicNameValuePair("msgBody", msgBody));
            CloseableHttpResponse httpResponse = null;

            CloseableHttpClient httpClient = getHttpClient();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            httpResponse = httpClient.execute(httpPost);
            responseStr = EntityUtils.toString(httpResponse.getEntity());

            JSONObject jsonObject = JSONObject.parseObject(responseStr);
            if (jsonObject.getBooleanValue("responseState"))
            {
                analyseTrackInfos(trackingNumber, jsonObject, trackResponseJson);
            }
            else
            {
                TrackUtils.buildFailResponseJson(trackingNumber, responseStr, trackResponseJson);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            TrackUtils.buildFailResponseJson(trackingNumber, e.getMessage() + ";" + responseStr, trackResponseJson);
        }

    }

    /**
     *  
     * 32位 MD5加密
     * 
     * @author 雷智新
     * @param str
     * @return
     */
    public static String MD5(String str)
    {
        String md5Str = null;
        String md5Str1 = null;
        if (str != null && str.length() != 0)
        {
            try
            {
                MessageDigest md = MessageDigest.getInstance("MD5");
                try
                {
                    md.update(str.getBytes("UTF-8"));
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                byte b[] = md.digest();

                StringBuffer buf = new StringBuffer("");
                for (int offset = 0; offset < b.length; offset++)
                {
                    int i = b[offset];
                    if (i < 0)
                        i += 256;
                    if (i < 16)
                        buf.append("0");
                    buf.append(Integer.toHexString(i));
                }
                // 32位
                md5Str = buf.toString();

                md5Str1 = Base64.encodeBase64String(md5Str.getBytes());
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
            }
        }
        return md5Str1;
    }

    /**
     * 
     * 解析中邮物流轨迹信息
     * 
     * @author 雷智新
     * @param trackingNumber
     * @param jsonObject
     * @param trackResponseJson
     */
    public static void analyseTrackInfos(String trackingNumber, JSONObject jsonObject, List<TrackResponseJson> trackResponseJson)
    {
        JSONArray responseItems = jsonObject.getJSONArray("responseItems");

        List<TrackInfo> trackInfos = new ArrayList<TrackInfo>();

        for (int i = 0; i < responseItems.size(); i++)
        {
            JSONObject responseItem = responseItems.getJSONObject(i);

            String dateStr = responseItem.getString("opTime");
            String info = responseItem.getString("opDesc");
            TrackInfo trackInfo = new TrackInfo();
            trackInfo.setDate(TrackUtils.DateFormatStr(dateStr));
            trackInfo.setInfo(info);
            trackInfos.add(trackInfo);
        }

        TrackResponseJson resJson = new TrackResponseJson(trackingNumber, null, trackInfos);
        trackResponseJson.add(resJson);
    }

    @Override
    public LogisticsCompany category()
    {
        return LogisticsCompany.SHENZHEN_POST_OFFICE;
    }

    @Override
    public List<TrackResponseJson> execute(TrackContext context)
    {
        List<StableOrderTrack> orderTracks = context.getStableOrderTracks();
        StableTrackConf stableTrackConf = context.getStableTrackConf();
        LogisticsConf logisticsConf = context.getLogisticsConf();
        return getRouteInfos(stableTrackConf, orderTracks, logisticsConf);
    }
    /**
     * 轻量级的请求客户端
     *
     * @author 丁光辉
     */
    public static CloseableHttpClient createMinimalClient()
    {
        int timeout = 15000;

        RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).setConnectTimeout(timeout).build();

        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();

        return httpClient;
    }
    //测试
    public static void main(String[] args) throws IOException
    {
        CloseableHttpClient httpClient = createMinimalClient();
        String url = MessageFormat.format("{0}/querypush-pcpw/mailTrackProtocolPortal/queryMailTrackWn", end_point);
        Map<String, Object> traceNoMap = new HashMap<>();
        traceNoMap.put("traceNo", "LY845039167CN");
        String jsonTraceNo = JSON.toJSONString(traceNoMap);
        String dataDigest = MD5(jsonTraceNo + "1100088431556");
        String msgBody = URLEncoder.encode(jsonTraceNo, "utf-8");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sendID", "0Ut4X85Q5TtyzW2O"));
        params.add(new BasicNameValuePair("proviceNo", "99"));
        params.add(new BasicNameValuePair("msgKind", "XXX_JDPT_TRACE"));
        params.add(new BasicNameValuePair("serialNo", "111"));
        params.add(new BasicNameValuePair("sendDate", "2021-08-09"));
        params.add(new BasicNameValuePair("receiveID", "JDPT"));
        params.add(new BasicNameValuePair("dataType", "1"));
        params.add(new BasicNameValuePair("dataDigest", dataDigest));
        params.add(new BasicNameValuePair("msgBody", msgBody));
        CloseableHttpResponse httpResponse = null;
        String responseStr = "";

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
        httpResponse = httpClient.execute(httpPost);
        responseStr = EntityUtils.toString(httpResponse.getEntity());

        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        List<TrackResponseJson> trackResponseJson =new ArrayList<TrackResponseJson>();
        if (jsonObject.getBooleanValue("responseState"))
        {
            analyseTrackInfos("LY845039167CN", jsonObject, trackResponseJson);
        }
        else
        {
            TrackUtils.buildFailResponseJson("LY845039167CN", responseStr, trackResponseJson);
        }
    }

}