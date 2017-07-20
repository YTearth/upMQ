package com.uphn.upMQ.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.uphn.upMQ.sdk.MessagePushManager;
import com.uphn.upMQ.util.MyBase64Utils;
import com.uphn.upMQ.util.HttpClientServ;
import com.uphn.upMQ.util.MyStringUtils;
import com.uphn.upMQ.util.SpSecurityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

public class HttpTest {
     
	//签名私钥
	private static String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAK0UY6lGAaSNpA8V9G+oQGKyayPMGmmJO9ezuG9gFoG6nniJWg9hhsV4TdJEsrfM7EjMnyqtp0AFmpQg4jFyFhtM+NKhpHvAsOBv2mTWSZ8HvUeah8b2m7JI9O+lNWQIxpwtkR6Fuk7jHHaPX0a/+n7GUGoFUix43NOnzZyOSnPtAgMBAAECgYBcQ5o9Ckyl47upLxL20sI/2syycIND7xwviGaxOI/G6CzCJLYVrO+jJNaXWHfM8ziiNjJDFf8qadJVVJI/uYl+cy91TyoiC2O5CFkSsNSgl5S/E/5Uc0wrv151NX6om8SLzeJF4dESu7ivuvUmSAZYhmmH9cGsLt/vjRNxTQRVAQJBAOJHJcRLATJn3j5hhueaLVrGICEAdxFTKPmLfbUx1kWBcsOU+Ibq9W/DVh3WRzA9sY7linsfpOBKLkwrtNGQM/kCQQDD0GOoERKFVRxeAk/g6g4bBHiZ9xmZqDavxPpwIdD73FmGQS1LBomaMGsKvGZ2fxG1ylImaWKaERqfb4CtYtSVAkBGdzOaqmToBpKeSI7TZx8CqrpsrJFn0sbq13bBS5DXulU79RNkKJ1gPat+xTEMI9o8jt0ONK+KrW83h1DbBhY5AkEAsCtCHakOcqq6FNIbr4ykGCaTomGvxJCUctrTPiMOdCow2Rq2dzNwhSpeg5Aw1xdHhbh65FgX/+i3fQ3CRTwPaQJATndht+4F+iiZtnamuHshoutV4lHGxVxaG0T0ycDrZMiHJKvRFDlBPq2XhhBMt8pqFZcrGNfQ63xhkk5PJFgerw==";
    
	private static String pushUrl = "http://localhost:8080/hnupppr/upMQTest.do";

	private static Logger logger = Logger.getLogger(HttpTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void upMQTest() {
		StringBuffer response = new StringBuffer();
		String url = "http://localhost:8080/upMQ/msgPost.do";
		long t1=System.currentTimeMillis();  
		ExecutorService service = Executors.newCachedThreadPool(); // 创建一个线程池
		final CountDownLatch cdOrder = new CountDownLatch(1);// 构造方法参数指定计数的次数
		final CountDownLatch cdAnswer = new CountDownLatch(5);// 构造方法参数指定计数的次数
		for (int i = 0; i < 5; i++) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						logger.info("线程" + Thread.currentThread().getName() + "正准备接受命令");
						cdOrder.await(); 
						logger.info("线程" + Thread.currentThread().getName() + "已接受命令");
						
			            JSONObject message = new JSONObject();
						message.put("msgSN", RandomStringUtils.randomNumeric(30));
			            message.put("msgDt", MyStringUtils.getCurrentDate());
			            
			            //模拟要推送的完整内容，包含appId,version,msgId,data域，sign等
						JSONObject json = new JSONObject();
						json.put("appId", "00005500");
						json.put("version", "1.0");
						json.put("msgId", "PT00");

						JSONObject subJson = new JSONObject();
						subJson.put("transSeq", MyStringUtils.getCurrentDate().substring(2, 8) + RandomStringUtils.randomNumeric(15));
						subJson.put("submitTime", MyStringUtils.getCurrentDate());
						subJson.put("msgContent",
								"private final static List<ConsumerThread> consumerThreadQueue = new ArrayList<ConsumerThread>();");
						json.put("data", subJson);
						String sign = SpSecurityUtils.sign(subJson.toJSONString().getBytes("utf-8"), privateKey);
					    json.put("sign", sign);
					    byte[] strByte = MyBase64Utils.encodeSafe(json.toString().getBytes());
					    message.put("msgBody", new String(strByte));
						byte[] urlByte =  MyBase64Utils.encodeSafe(pushUrl.getBytes());
						message.put("msgUrl", new String(urlByte));
						try {
							logger.info("返回值：" + HttpClientServ.send(url, message).toJSONString());
						} catch (Exception e) {
							System.err.print("http failure !");
						}
						logger.info("线程" + Thread.currentThread().getName() + "回应命令处理结果");

					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						cdAnswer.countDown(); 
					}
				}
			};
			service.execute(runnable);// 为线程池添加任务
		}
		try {
			Thread.sleep((long) (Math.random() * 1000));

			logger.info("线程" + Thread.currentThread().getName() + "即将发布命令");
			cdOrder.countDown(); 
            JSONObject message = new JSONObject();
            message.put("msgSN", RandomStringUtils.randomNumeric(30));
            message.put("msgDt", MyStringUtils.getCurrentDate());
			JSONObject json = new JSONObject();
			json.put("appId", "00005500");
			json.put("version", "1.0");
			json.put("msgId", "PT00");
			JSONObject subJson = new JSONObject();
			subJson.put("transSeq",  MyStringUtils.getCurrentDate().substring(2, 8) + RandomStringUtils.randomNumeric(15));
			subJson.put("submitTime", MyStringUtils.getCurrentDate());
			subJson.put("msgContent",
					"中文测试private final static List<ConsumerThread> consumerThreadQueue = new ArrayList<ConsumerThread>();");
			
			json.put("data", subJson);
			String sign = SpSecurityUtils.sign(subJson.toJSONString().getBytes("utf-8"), privateKey);
		    json.put("sign", sign);
		    byte[] strByte = MyBase64Utils.encodeSafe(json.toString().getBytes());
			message.put("msgBody", new String(strByte));
			
			byte[] urlByte =  MyBase64Utils.encodeSafe(pushUrl.getBytes());
			message.put("msgUrl", new String(urlByte));
			try {
				logger.info("返回值：" + HttpClientServ.send(url, message).toJSONString());
			} catch (Exception e) {
				logger.info("http failure !");
			}
			cdAnswer.await(); 
			long t2=System.currentTimeMillis();  
			logger.info("线程耗时：" + (t2 -t1) + "已收到所有响应结果");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
}