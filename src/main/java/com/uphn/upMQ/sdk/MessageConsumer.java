package com.uphn.upMQ.sdk;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageConsumer {
   
	private static ExecutorService  threadPool = Executors.newFixedThreadPool(QueueConstants.CONSUMER_NUM);
    
	public static boolean msgConsumer(String topic, String consumerUrl) {
		threadPool.execute(new Runnable() {
             @Override
             public void run() {
                     try {
                        //取出消息，并且推送之
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 }
             });
		return true;
	}
}
