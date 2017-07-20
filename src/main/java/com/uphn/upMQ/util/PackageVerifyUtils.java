package com.uphn.upMQ.util;

import com.alibaba.fastjson.JSONObject;
import com.uphn.upMQ.config.RespCd;
import com.uphn.upMQ.exception.MQException;


//参数校验工具类
public class PackageVerifyUtils {
	
	public static void VerifyPackage(JSONObject reqJson) throws MQException {
		
		if (!reqJson.containsKey("msgUrl")) {
			throw new MQException(RespCd.RESPCD_10, "Missing field [msgUrl]");
		}
		
		if (!reqJson.containsKey("msgBody"))
			throw new MQException(RespCd.RESPCD_10, "Missing field [msgBody]");
		
		if (!reqJson.containsKey("msgSN")) {
			throw new MQException(RespCd.RESPCD_10, "Missing field [msgSN]");
		} else if (!MyStringUtils.isMatch(reqJson.getString("msgSN"), "^\\d{10,40}")) {
			throw new MQException(RespCd.RESPCD_10,
					String.format("Invalid msgSN [%s]", reqJson.getString("msgSN")));
		}
		
		if (!reqJson.containsKey("msgDt")) {
			throw new MQException(RespCd.RESPCD_10, "Missing field [msgDt]");
		} else if (!MyStringUtils.isMatch(reqJson.getString("msgDt"), "^\\d{14}")) {
			throw new MQException(RespCd.RESPCD_10,
					String.format("Invalid msgDt [%s]", reqJson.getString("msgDt")));
		}
	}
}
