package com.uphn.upMQ.util;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

public class HttpClientServ {

	
	/**
	 * http或https请求
	 * data域是json对象
	 * @param apiAddr
	 * @param msgId
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static JSONObject send(String apiAddr,JSONObject data)
			throws Exception {
		JSONObject sendJson = data;
		CloseableHttpClient httpClient;
		if (apiAddr.contains("https"))
			httpClient = createSSLClientDefault();
		else
			httpClient = HttpClients.createDefault();
		try{
			HttpPost postMethod = new HttpPost(apiAddr);
		;
			StringEntity entity = new StringEntity(sendJson.toString(), "utf-8");
			entity.setContentEncoding("utf-8");
			entity.setContentType("application/json");
			postMethod.setEntity(entity);
			HttpResponse response = httpClient.execute(postMethod);
			String body = EntityUtils.toString(response.getEntity(), "utf-8");
			//System.out.println(body);
			JSONObject respJson = JSONObject.parseObject(body);
			//对返回的结果进行异常判断处理
			httpClient.close();
			return (respJson == null) ? new JSONObject() : respJson;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 基于https协议的客户端对象创建
	 * @return
	 */
	private static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
					null, new TrustStrategy() {
						// 信任所有
						public boolean isTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							return true;
						}
					}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
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
}
