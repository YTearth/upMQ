package com.uphn.upMQ.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TextUtils {

	public static String getDateTimeNow() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}

	public static int getRandom() {
		int max = 9999;
		int min = 1000;
		Random random = new Random();
		int s = random.nextInt(max) % (max - min + 1) + min;
		return s;
	}

	public static void filterNull(JSONArray array) {
		for (int i = 0; i < array.size(); i++) {
			JSONObject json = array.getJSONObject(i);

			Set<String> it = json.keySet();
			Object obj = null;
			String key = null;

			while (it.iterator().hasNext()) {
				key = it.iterator().next();
				obj = json.get(key);
				System.out.println("key: " + key + "object: " + obj);
				if (obj.equals(null)) {
					json.put(key, "");
				}
			}
		}
	}
}
