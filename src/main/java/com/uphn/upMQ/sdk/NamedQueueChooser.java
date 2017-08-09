package com.uphn.upMQ.sdk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tianyin
 *
 */
public class NamedQueueChooser {
    
	private Map<String, MessageQueue> queueMap;
	private static NamedQueueChooser INSTANCE = null;
	
	private NamedQueueChooser() {
		queueMap = new ConcurrentHashMap<String, MessageQueue>();
	}
	
	public static NamedQueueChooser getInstance() {
		if (INSTANCE == null) {
			synchronized (NamedQueueChooser.class) {
				if (INSTANCE == null) {
					INSTANCE = new NamedQueueChooser();
				}
			}
		}
		return INSTANCE;
	}
	/**
	 * 生产方拿队列,若队列不存在则创建。消费方拿队列，若队列不存在则返回null
	 * @param queueName
	 * @param autoCreate
	 * @return
	 */
	public MessageQueue fetctMessageQueue(String queueName, boolean autoCreate) {
		if (queueName == null) {
			return null;
		}
		MessageQueue messageQueue = queueMap.get(queueName);
		//TODO 当前采用ConcurrentHashMap保证线程安全,可选仅在messageQueue ==null加同步锁,后者实际效率可能会更高,待测。
		if (messageQueue == null && autoCreate) { 
			messageQueue = new MessageQueue(queueName);
			MessageQueue oldQueue = queueMap.putIfAbsent(queueName, messageQueue);
			if (oldQueue != null) {
				messageQueue = oldQueue;
			}
		}
		return messageQueue;
	}
}
