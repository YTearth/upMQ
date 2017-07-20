package com.uphn.upMQ.upsdk.producer;

public class UPProducerConfig {
	
	private String appKey;
	
	private String appSecret;
	
	private int httpConnectTimeoutMs = 3000;
	
	private int httpReadTimeoutMs = 3000;
	
	private String UnionPayDomain; // 银联推送主域名
	
	private int reportWorkNum = 1; // 批量推送线程数量，默认为1
	
	private int reportBatchSize = 1;//批量上传数量，默认为1
    
	private int reportQueueMaxSize = 50;//默认容量大小
	
	private boolean shouldAutoReport = true;
	
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

	public int getReportWorkNum() {
		return reportWorkNum;
	}

	public void setReportWorkNum(int reportWorkNum) {
		this.reportWorkNum = reportWorkNum;
	}

	public int getReportBatchSize() {
		return reportBatchSize;
	}

	public void setReportBatchSize(int reportBatchSize) {
		this.reportBatchSize = reportBatchSize;
	}
	
	public int getReportQueueMaxSize() {
		return reportQueueMaxSize;
	}

	public void setReportQueueMaxSize(int reportQueueMaxSize) {
		this.reportQueueMaxSize = reportQueueMaxSize;
	}
	
	public void setAutoReport(boolean autoReport) {
		this.shouldAutoReport = autoReport;
	}
	
	public boolean shouldAutoReport() {
		return shouldAutoReport;
	}
}
