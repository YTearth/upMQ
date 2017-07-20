package com.uphn.upMQ.upsdk.cosumer;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.uphn.upMQ.upsdk.producer.UPProducerConfig;
import com.uphn.upMQ.upsdk.producer.UPProducerReport;
import com.uphn.upMQ.upsdk.producer.UPProducerUtils;

/**
 * @author tianyin
 *
 */
public class UPConsumerRequest {
	
	class RequestInfo {
		//采用Map组明文
		private String apiKey;
		private String timestamp; //用时间戳
		private String randomStr;//随机字符串,保护appSecret
		private String uuid; //此处标识ID号
		private String notify_url; //消息通知地址
		private String sign; //报文签名
		
		public RequestInfo(String apiKey, String timesTamp, String randomStr
				, String uuid, String notify_url, String sign) {
			this.apiKey = apiKey;
			this.timestamp = timesTamp;
			this.randomStr = randomStr;
			this.uuid = uuid;
			this.notify_url = notify_url;
			this.sign = sign;
		}
		
		public Map<String, String> toMap() {
			Map<String, String> report = new HashMap<String, String>();
			report.put("apiKey", apiKey);
			report.put("timestamp", timestamp);
			report.put("randomStr", randomStr);
			report.put("uuid", uuid);
			report.put("notify_url", notify_url);
			report.put("sign", sign);
			return report;
		}
	}
	
	private volatile static UPConsumerRequest INSTANCE;
	private UPConsumerConfig config;
	private UPConsumerRequest() {}
	private UPConsumerRequest(final UPConsumerConfig config) {
		this.config = config;
	}
	public static UPConsumerRequest getInstance(UPConsumerConfig config) {
		if (INSTANCE == null) {
			synchronized (UPConsumerRequest.class) {
				if (INSTANCE == null) {
					INSTANCE = new UPConsumerRequest(config);
				}
			}
		}
		return INSTANCE;
	}
	
	/**
	 * http请求,添加随机字符串，密钥签名等。
	 * 
	 * @param topic_name
	 * @param notify_url
	 * @return
	 * @throws Exception
	 */
	public String httpRequest(String topic_name, String notify_url) throws Exception {
		UPConsumerUtils.getLogger().info("request {}", topic_name, notify_url);
		String timestamp = UPTextUtils.getDateTimeNow();
		String randomStr = UPTextUtils.getRandomString(32);
		String uuid = UPConsumerUtils.generateUUID();
		RequestInfo requestInfo =new RequestInfo(config.getAppKey(), timestamp, randomStr, uuid, notify_url, null);
		String requestBody = UPConsumerUtils.generateSignedStringInMD5(requestInfo.toMap(), config.getAppSecret());
		UPConsumerUtils.getLogger().info("signed request {}", requestBody);
		return sendRequest(requestBody, config.getUnionPayDomain(),
				config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs());
	}
	
	 private String sendRequest(String data, String urlStr, int connectTimeoutMs, int readTimeoutMs) throws Exception{
	        BasicHttpClientConnectionManager connManager;
	        connManager = new BasicHttpClientConnectionManager(
	                RegistryBuilder.<ConnectionSocketFactory>create()
	                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
	                        .register("https", SSLConnectionSocketFactory.getSocketFactory())
	                        .build(),
	                null,
	                null,
	                null
	        );
	        HttpClient httpClient = HttpClientBuilder.create()
	                .setConnectionManager(connManager)
	                .build();

	        HttpPost httpPost = new HttpPost(urlStr);

	        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs).setConnectTimeout(connectTimeoutMs).build();
	        httpPost.setConfig(requestConfig);

	        StringEntity postEntity = new StringEntity(data, "UTF-8");
	        httpPost.addHeader("Content-Type", "text/xml");
	        httpPost.setEntity(postEntity);

	        HttpResponse httpResponse = httpClient.execute(httpPost);
	        HttpEntity httpEntity = httpResponse.getEntity();
	        return EntityUtils.toString(httpEntity, "UTF-8");
	    }
	
}
