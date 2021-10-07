package http;//package http;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.bessky.erp.core.common.type.LogisticsCompany;
//import com.bessky.erp.track.auto.AbstractTrackCall;
//import com.bessky.erp.track.auto.TrackContext;
//import com.bessky.erp.track.auto.TrackInfo;
//import com.bessky.erp.track.auto.TrackResponseJson;
//import com.bessky.erp.track.bean.StableOrderTrack;
//import com.bessky.erp.track.utils.TrackUtils;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.utils.HttpClientUtils;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.util.EntityUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//
//public class ZoodMallTrackCall extends AbstractTrackCall
//{
//	private static final Logger LOGGER = LoggerFactory
//			.getLogger(com.bessky.erp.track.api.zoodmall.ZoodMallTrackCall.class);
//
//	private static final String TRACE_QUERY_URL = "https://api.ship24.com/api/partners";
//
//	public static final ZoodMallTrackCall SINGLE = new ZoodMallTrackCall();
//
//	public static ZoodMallTrackCall getInstance()
//	{
//		return SINGLE;
//	}
//
//	public List<TrackResponseJson> getBatchTrackingInfo(List<StableOrderTrack> stableOrderTracks)
//	{
//		List<TrackResponseJson> trackResponseJson = new ArrayList<TrackResponseJson>();
//		for (StableOrderTrack stableOrderTrack : stableOrderTracks)
//		{
//			getTrackingInfo(stableOrderTrack.getTrackingNumber(), trackResponseJson);
//		}
//		return trackResponseJson;
//	}
//
//	/**
//	 * 获取轨迹数据
//	 *
//	 * @param trackNumStr
//	 * @param trackResponseJson
//	 * @author 丁光辉
//	 */
//	public void getTrackingInfo(String trackNumStr, List<TrackResponseJson> trackResponseJson)
//	{
//
//		Map<String, Object> paraMap = new HashMap<>();
//		paraMap.put("query", "{findOneParcel(TN:\"" + trackNumStr + "\") {tracking_number,quotaLeft, events {status, datetime, location}}}");
//		String jsonPara = JSON.toJSONString(paraMap);
//
//		CloseableHttpClient httpClient = getHttpClient();
//		HttpPost httpPost = new HttpPost(TRACE_QUERY_URL);
//		httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
//		httpPost.addHeader("Authorization", "Bearer XsD8K3zHA7p5zyqg2P0L5s9qy3KZubkr5UWza3LDDf");
//		httpPost.setEntity(new StringEntity(jsonPara, "UTF-8"));
//
//		CloseableHttpResponse response = null;
//		try
//		{
//			response = httpClient.execute(httpPost);
//
//			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
//			{
//				String responseStr = EntityUtils.toString(response.getEntity());
//				JSONObject jsonObject = JSON.parseObject(responseStr);
//				if (jsonObject == null || jsonObject.size() == 0)
//				{
//					TrackUtils.buildFailResponseJson(trackNumStr, jsonObject.getString("msg"), trackResponseJson);
//				}
//				else
//				{
//					parseTrackData(jsonObject, trackResponseJson);
//				}
//			}
//		}
//		catch (Exception e)
//		{
//			LOGGER.error(e.getMessage(), e);
//			TrackUtils.buildFailResponseJson(trackNumStr, e.getMessage(), trackResponseJson);
//		}
//		finally
//		{
//			HttpClientUtils.closeQuietly(response);
//		}
//	}
//
//	/**
//	 * 解析轨迹数据
//	 *
//	 * @param jsonObject
//	 * @param trackResponseJson
//	 * @author 丁光辉
//	 */
//	private void parseTrackData(JSONObject jsonObject, List<TrackResponseJson> trackResponseJson)
//	{
//		JSONObject data = jsonObject.getJSONObject("data");
//		JSONObject findOneParcel = data.getJSONObject("findOneParcel");
//		String tracking_number = findOneParcel.getString("tracking_number");
//		JSONArray traceList = findOneParcel.getJSONArray("events");
//		List<TrackInfo> trackInfos = new ArrayList<>();
//		if (null != traceList && traceList.size() >= 1)
//		{
//			for (int j = 0; j < traceList.size(); j++)
//			{
//				JSONObject traceObject = traceList.getJSONObject(j);
//
//				String status = traceObject.getString("status");
//				String datetime = traceObject.getString("datetime");
//				String location = traceObject.getString("location");
//
//				TrackInfo trackInfo = new TrackInfo();
//				trackInfo.setDate(TrackUtils.DateFormatStr(datetime));
//				trackInfo.setInfo(location + "," + status);
//				trackInfos.add(trackInfo);
//			}
//		}
//		TrackResponseJson resJson = new TrackResponseJson(tracking_number, null, trackInfos);
//		trackResponseJson.add(resJson);
//	}
//
//	@Override
//	public LogisticsCompany category()
//	{
//		return LogisticsCompany.ZOODMALL;
//	}
//
//	@Override
//	public List<TrackResponseJson> execute(TrackContext context)
//	{
//		List<StableOrderTrack> stableOrderTracks = context.getStableOrderTracks();
//		return getBatchTrackingInfo(stableOrderTracks);
//	}
//
//	/**
//	 * 轻量级的请求客户端
//	 *
//	 * @author 丁光辉
//	 */
//	public static CloseableHttpClient createMinimalClient()
//	{
//		int timeout = 15000;
//
//		RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout)
//				.setSocketTimeout(timeout).setConnectTimeout(timeout).build();
//
//		CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
//
//		return httpClient;
//	}
//
//}
//
//	public static void main(String[] args)
//	{
//		//String jsonPara = '{"query":"{ findOneParcel(TN:\"UH678775518GE\") {tracking_number,quotaLeft, events {status, datetime, location}}}"}';
//		CloseableHttpClient httpClient = createMinimalClient();
//		Map<String, Object> paraMap = new HashMap<>();
//		paraMap.put("query", "{findOneParcel(TN:\"UH678775518GE\") {tracking_number,quotaLeft, events {status, datetime, location}}}");
//		String jsonPara = JSON.toJSONString(paraMap);
//		HttpPost httppost = new HttpPost(TRACE_QUERY_URL);
//
//		httppost.addHeader("Content-Type", "application/json");
//		httppost.addHeader("Authorization", "Bearer XsD8K3zHA7p5zyqg2P0L5s9qy3KZubkr5UWza3LDDf");
//		httppost.setEntity(new StringEntity(jsonPara, "UTF-8"));
//		CloseableHttpResponse response = null;
//
//		try
//		{
//			response = httpClient.execute(httppost);
//
//			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
//			{
//				String responseStr = EntityUtils.toString(response.getEntity());
//				JSONObject jsonObject = JSON.parseObject(responseStr);
//				if (jsonObject == null || jsonObject.size() == 0)
//				{
//					System.out.println("对象为空");
//				}
//
//				JSONObject data = jsonObject.getJSONObject("data");
//				JSONObject findOneParcel = data.getJSONObject("findOneParcel");
//				String tracking_number = findOneParcel.getString("tracking_number");
//				JSONArray traceList = findOneParcel.getJSONArray("events");
//				List<TrackInfo> trackInfos = new ArrayList<>();
//				if (null != traceList && traceList.size() >= 1)
//				{
//					for (int j = 0; j < traceList.size(); j++)
//					{
//						JSONObject traceObject = traceList.getJSONObject(j);
//
//						String status = traceObject.getString("status");
//						String datetime = traceObject.getString("datetime");
//						String location = traceObject.getString("location");
//
//						TrackInfo trackInfo = new TrackInfo();
//						trackInfo.setDate(TrackUtils.DateFormatStr(datetime));
//						trackInfo.setInfo(location + "," + status);
//						trackInfos.add(trackInfo);
//					}
//				}
//				TrackResponseJson resJson = new TrackResponseJson(tracking_number, null, trackInfos);
//				System.out.println("测试成功");
//			}
//		}
//		catch (Exception e)
//		{
//			LOGGER.error(e.getMessage(), e);
//			//TrackUtils.buildFailResponseJson(agentMailno, e.getMessage(), trackResponseJson);
//		}
//		finally
//		{
//			HttpClientUtils.closeQuietly(response);
//		}
//	}
//}
