package com.uphn.upMQ.upsdk.producer;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.uphn.upMQ.upsdk.producer.UPProducerConstants.SignType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//XML parse
public class UPProducerUtils {

	public static String generateSignedStringInMD5(Map<String, String> data, String key) throws Exception {
		filterNull(data);
		return generateSignedString(data, key, SignType.MD5);
	}

	public static String generateSignedString(final Map<String, String> data, String key, SignType signType) throws Exception {
		String sign = generateSignature(data, key, signType);
		data.put(UPProducerConstants.SIGN, sign);
		return data.toString();
	}

	/**
	 * 生成签名. 注意，若含有sign_type字段，必须和signType参数保持一致。
	 *
	 * @param data
	 *            待签名数据
	 * @param key
	 *            API密钥
	 * @param signType
	 *            签名方式
	 * @return 签名
	 */
	public static String generateSignature(final Map<String, String> data, String key, SignType signType) throws Exception {
		Set<String> keySet = data.keySet();
		String[] keyArray = keySet.toArray(new String[keySet.size()]);
		Arrays.sort(keyArray);
		StringBuilder sb = new StringBuilder();
		for (String k : keyArray) {
			if (k.equals(UPProducerConstants.SIGN)) {
				continue;
			}
			if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
				sb.append(k).append("=").append(data.get(k).trim()).append("&");
		}
		sb.append("key=").append(key);
		if (SignType.MD5.equals(signType)) {
			return MD5(sb.toString()).toUpperCase();
		} else if (SignType.HMACSHA256.equals(signType)) {
			return HMACSHA256(sb.toString(), key);
		} else {
			throw new Exception(String.format("Invalid sign_type: %s", signType));
		}
	}

	/**
	 * 生成 MD5
	 *
	 * @param data
	 *            待处理数据
	 * @return MD5结果
	 */
	public static String MD5(String data) throws Exception {
		java.security.MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] array = md.digest(data.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte item : array) {
			sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * 生成 HMACSHA256
	 * 
	 * @param data
	 *            待处理数据
	 * @param key
	 *            密钥
	 * @return 加密结果
	 * @throws Exception
	 */
	public static String HMACSHA256(String data, String key) throws Exception {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
		sha256_HMAC.init(secret_key);
		byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte item : array) {
			sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * 日志
	 * 
	 * @return
	 */
	public static Logger getLogger() {
		Logger logger = LoggerFactory.getLogger("wxpay java sdk");
		return logger;
	}
	
	/**
	 * 获取当前时间戳，单位毫秒
	 * 
	 * @return
	 */
	public static long getCurrentTimestampMs() {
		return System.currentTimeMillis();
	}

	/**
	 * 生成 uuid， 即用来标识一笔推送,流水号
	 * 
	 * @return
	 */
	public static String generateUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
	}
    
	 public static void filterNull(Map<String, String> json) {
	    	Set<String> keys = json.keySet();
	    	for (String key: keys) {
	    		if (json.get(key) == null) {
	    			json.put(key, "");
	    		}
	    	}
	}
	public static void main(String[] args) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		String apiKey = "oBOrD3sJfpZKuwKCgtMeapMq3vkHfePS";
		String url = "http://113.240.244.122:60088/vip/vip.action";
		map.put("Service", "ScoreExCancel");
		map.put("RangeID", "00000001");
		map.put("Phone", "13975815679");

		map.put("InstitutionID", "00015510");
		map.put("Timestamp", "20160630161300");
		map.put("OutNO", "000003");

		map.put("OriginalOutNO", "000001");
		map.put("Score", "1000");
		String xmlStr = generateSignedStringInMD5(map, apiKey);
		System.out.println("json：" + xmlStr + "\n\n\n\n");
	}
}
