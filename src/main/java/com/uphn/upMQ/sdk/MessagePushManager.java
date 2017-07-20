package com.uphn.upMQ.sdk;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.uphn.upMQ.pojo.MqMessage;

public class MessagePushManager {

	private static Logger logger = Logger.getLogger(MessagePushManager.class);
	private static String ENCODING = "utf-8";

	/**
	 * 请求，只请求一次，不做重试
	 * @param urlSuffix
	 * @param data
	 * @param connectTimeoutMs
	 * @param readTimeoutMs
	 * @param useCert
	 *            是否使用证书,暂时不用
	 * @return
	 * @throws Exception
	 */
	private static JSONObject sendOnce(String urlSuffix, String data, int connectTimeoutMs, int readTimeoutMs,
			boolean useCert) throws Exception {
		CloseableHttpClient httpClient;
		if (urlSuffix.contains("https"))
			httpClient = createSSLClientDefault();
		else
			httpClient = HttpClients.createDefault();
		HttpPost postMethod = new HttpPost(urlSuffix);
		postMethod.setConfig(RequestConfig.custom().setConnectTimeout(connectTimeoutMs)
				.setConnectionRequestTimeout(connectTimeoutMs).setSocketTimeout(readTimeoutMs).build());

		StringEntity entity = new StringEntity(data, ENCODING);
		entity.setContentEncoding(ENCODING);
		entity.setContentType("application/json");
		postMethod.setEntity(entity);
		JSONObject respJson;
		try {
			HttpResponse response = httpClient.execute(postMethod);
			String body = EntityUtils.toString(response.getEntity(), ENCODING);
			respJson = JSONObject.parseObject(body);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}
		return (respJson == null) ? null : respJson;
	}

	/**
	 * @param apiAddr
	 *            请求的地址
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static JSONObject push(String data, String apiAddr) throws Exception {
		JSONObject result;
		Exception exception;
		boolean shouldRetry = false;
		boolean useCert = false;

		try {
			logger.info("push content：" + data);
			result = sendOnce(apiAddr, data, PushConstants.CONNECTION_TIMEOUT, PushConstants.SOCKET_TIMEOUT, useCert);
			logger.info("push result:" + result);
			return result;
		} catch (ConnectTimeoutException ex) {
			exception = ex;
			logger.info("connect timeout happened for domain:" + apiAddr);
			shouldRetry = true;
		} catch (Exception ex) {
			exception = ex;
			shouldRetry = false;
		}

		if (shouldRetry) {
			logger.info("repush content:" + data);
			result = sendOnce(apiAddr, data, PushConstants.CONNECTION_TIMEOUT, PushConstants.SOCKET_TIMEOUT, useCert);
			logger.info("repush result:" + result);
			return result;
		} else {
			throw exception;
		}
	}

	/**
	 * 基于https协议的客户端对象创建
	 * 
	 * @return
	 */
	private static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
					SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}

	public static void main(String[] args) {
		// MessagePushManager.push("www.baidu.com", null);
	}
}
