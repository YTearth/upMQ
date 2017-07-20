package com.uphn.upMQ.upsdk.producer;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class UPProducerReport {
    
	class ReportInfo {
		//TODO 利用Map组明文
		private String apiKey;
		private String timestamp; //用时间戳
		private String randomStr;//随机字符串,保护appSecret
		private String uuid; //此处标识ID号
		private String reportBody; //消息主体
		private String sign; //报文签名
		
		public ReportInfo(String apiKey, String timesTamp, String randomStr
				, String uuid, String reportBody, String sign) {
			this.apiKey = apiKey;
			this.timestamp = timesTamp;
			this.randomStr = randomStr;
			this.uuid = uuid;
			this.reportBody = reportBody;
			this.sign = sign;
		}
		
		public Map<String, String> toMap() {
			Map<String, String> report = new HashMap<String, String>();
			report.put("apiKey", apiKey);
			report.put("timestamp", timestamp);
			report.put("randomStr", randomStr);
			report.put("uuid", uuid);
			report.put("reportBody", reportBody);
			report.put("sign", sign);
			return report;
		}
	}
	
	
	private UPProducerConfig config;
	private LinkedBlockingQueue<String> reportMsgQueue = null;
	private ExecutorService executorService;
	private volatile static UPProducerReport INSTANCE;

	private UPProducerReport(final UPProducerConfig config) {
		this.config = config;
		reportMsgQueue = new LinkedBlockingQueue<String>(config.getReportQueueMaxSize());

		// 添加处理线程
		executorService = Executors.newFixedThreadPool(config.getReportWorkNum(), new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = Executors.defaultThreadFactory().newThread(r);
				t.setDaemon(true);
				return t;
			}
		});

		if (config.shouldAutoReport()) {
			UPProducerUtils.getLogger().info("report worker num: {}", config.getReportWorkNum());
			for (int i = 0; i < config.getReportWorkNum(); ++i) {
				executorService.execute(new Runnable() {
					public void run() {
						while (true) {
							// 先用 take 获取数据
							try {
								StringBuffer sb = new StringBuffer();
								String firstMsg = reportMsgQueue.take();
								UPProducerUtils.getLogger().info("get first report msg: {}", firstMsg);
								String msg = null;
								sb.append(firstMsg); // 会阻塞至有消息
								int remainNum = config.getReportBatchSize() - 1;
								for (int j = 0; j < remainNum; ++j) {
									UPProducerUtils.getLogger().info("try get remain report msg");
									msg = reportMsgQueue.take();
									UPProducerUtils.getLogger().info("get remain report msg: {}", msg);
									if (msg == null) {
										break;
									} else {
										sb.append("\n");
										sb.append(msg);
									}
								}
								UPProducerReport.httpRequest(config.getUnionPayDomain(), sb.toString(),
										config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs());
							} catch (Exception ex) {
								UPProducerUtils.getLogger().warn("report fail. reason: {}", ex.getMessage());
							}
						}
					}
				});
			}
		}

	}

	public static UPProducerReport getInstance(UPProducerConfig config) {
		if (INSTANCE == null) {
			synchronized (UPProducerReport.class) {
				if (INSTANCE == null) {
					INSTANCE = new UPProducerReport(config);
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * 添加随机字符串，密钥签名，base64处理body报文等
	 * @param data
	 * @throws  
	 */
	public void report(String data) throws  Exception{
		if (data == null) {
			return ;
		}
		UPProducerUtils.getLogger().info("report {}", data);
		//组装消息报文，含apiKey,时间戳,base64明文,sign字段
		String currentTimestamp = UPTextUtils.getDateTimeNow();
		String reportBody = new  String(UPTextUtils.encodeSafe(data.getBytes("utf-8")));
		ReportInfo reportInfo = new ReportInfo(config.getAppKey(), currentTimestamp, 
				UPTextUtils.getRandomString(32), UPProducerUtils.generateUUID(), reportBody, null);
		String sendData = UPProducerUtils.generateSignedStringInMD5(reportInfo.toMap(), config.getAppSecret());
		UPProducerUtils.getLogger().info("sign report {}", sendData);
		reportMsgQueue.offer(reportInfo.toString());
	}

	private static String httpRequest(String urlStr, String data, int connectTimeoutMs, int readTimeoutMs)
			throws Exception {
		Exception exception;
		boolean shouldRetry = false;
		String res;
		try {
			res = sendOnce(urlStr, data, connectTimeoutMs, readTimeoutMs);
			return res;
		} catch (ConnectTimeoutException ex) {
			exception = ex;
			shouldRetry = true;
			connectTimeoutMs *= 2; // 如果超时，则连接时间，读时间扩大两倍
			readTimeoutMs *= 2;
		} catch (UnknownHostException e) {
			UPProducerUtils.getLogger().warn("UnknownHostException for domain {}, try to use {}");
			exception = e;
			shouldRetry = true;
			// TODO 切换域名
		} catch (Exception e) {
			exception = e;
			shouldRetry = false;
		}

		if (shouldRetry) {
			res = sendOnce(urlStr, data, connectTimeoutMs, readTimeoutMs);
			return res;
		} else {
			throw exception;
		}
	}

	private static String sendOnce(String apiAddr, String data, int connectTimeoutMs, int readTimeoutMs)
			throws Exception {
		BasicHttpClientConnectionManager connManager;
		connManager = new BasicHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", SSLConnectionSocketFactory.getSocketFactory()).build(), null, null, null);
		HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connManager).build();

		HttpPost httpPost = new HttpPost(apiAddr);

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs)
				.setConnectTimeout(connectTimeoutMs).build();
		httpPost.setConfig(requestConfig);

		StringEntity postEntity = new StringEntity(data, "UTF-8");
		httpPost.addHeader("Content-Type", "text/xml");
		httpPost.setEntity(postEntity);
		HttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity httpEntity = httpResponse.getEntity();
		return EntityUtils.toString(httpEntity, "UTF-8");
	}
	
	public static void main(String[] args) throws Exception {
		UPProducerConfig config = new UPProducerConfig();
		config.setUnionPayDomain("www.baidu.com");
		UPProducerReport report = new UPProducerReport(config);
	    report.report("二是要“两手抓、两手硬”，实现党建工作与业务工作相互融合相互促进。要充分发挥党组的领导核心、党支部的战斗堡垒和党员的先锋模范作用，以党建工作引领、助力、保障业务发展。");
	}
}
