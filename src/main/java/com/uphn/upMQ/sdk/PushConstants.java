package com.uphn.upMQ.sdk;

public class PushConstants {
	// 推送状态：成功，连接超时，数据超时，其它内部错误
	public enum PushState {
		SUCCESS, CONNECTIONTIMEOUT, SOCKETTIMEOUT, ERROR;
	}
	// 推送地址,最多推送次数 //
	public final static int MAX_PUSH_NUM = 10;
	//连接超时时间,socket超时
	public final static int CONNECTION_TIMEOUT = 3000;
	public final static int SOCKET_TIMEOUT = 3000;

	public final static String UNSEND = "9999"; // 未推送
	public final static String SUCCESS = "0000"; // 推送成功
	public final static String FAIL = "0001"; // 推送失败
	
	public final static String RESP_CD  =  "respCd"; //推送返回码key值
	public final static String RESP_MSG = "respMsg";//推送返回描述key值
}
