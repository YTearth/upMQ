package com.uphn.upMQ.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//StringUtils工具类，区别于
public class MyStringUtils {

	public static int compareTo(final String str1, final String str2) {
		return str1.compareTo(str2);
	}

	public static String getCurrentDate() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}

	public static boolean isMatch(String str, String regEx) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	
	public static boolean isUrlMatch(String url) {
		return isMatch(url, "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$");
	}
	
//	@Deprecated 
//	public static boolean isUrlMatch(String url) {
//		String[] schemes = {"http","https"}; // DEFAULT schemes = "http", "https"
//		UrlValidator urlValidator = new UrlValidator(schemes);
//		return urlValidator.isValid(url);
//	}
}
