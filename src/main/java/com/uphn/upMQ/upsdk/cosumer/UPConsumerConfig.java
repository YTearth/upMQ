package com.uphn.upMQ.upsdk.cosumer;

public class UPConsumerConfig {
	private String appKey;

	private String appSecret;

	private int httpConnectTimeoutMs = 3000;

	private int httpReadTimeoutMs = 3000;

	private String UnionPayDomain; // 银联主域,dns解析错误时，采用备用域

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public int getHttpConnectTimeoutMs() {
		return httpConnectTimeoutMs;
	}

	public void setHttpConnectTimeoutMs(int httpConnectTimeoutMs) {
		this.httpConnectTimeoutMs = httpConnectTimeoutMs;
	}

	public int getHttpReadTimeoutMs() {
		return httpReadTimeoutMs;
	}

	public void setHttpReadTimeoutMs(int httpReadTimeoutMs) {
		this.httpReadTimeoutMs = httpReadTimeoutMs;
	}

	public String getUnionPayDomain() {
		return UnionPayDomain;
	}

	public void setUnionPayDomain(String unionPayDomain) {
		UnionPayDomain = unionPayDomain;
	}
}
