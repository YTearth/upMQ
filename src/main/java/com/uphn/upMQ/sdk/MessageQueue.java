package com.uphn.upMQ.sdk;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONObject;
import com.uphn.upMQ.dao.MqMessageMapper;
import com.uphn.upMQ.pojo.MqMessage;
import com.uphn.upMQ.util.HttpClientServ;
import com.uphn.upMQ.util.MyBase64Utils;
import com.uphn.upMQ.util.MyStringUtils;
import com.uphn.upMQ.util.SpSecurityUtils;

/**
 * @author tianyin 完成两个工作：1.出/入队列，2入库/更新库
 */
/**
 * @author tianyin
 *
 */
public class MessageQueue {

	private static Logger logger = Logger.getLogger(MessageQueue.class);

	private String msgQueueName;
	// 消息核心队列
	private final BlockingQueue<MqMessage> msgQueue = new LinkedBlockingQueue<MqMessage>();
	// 最大消费者线程
	private static int MaxConsumerThread;
	// 轮询
	private boolean isRunning = true;

	private MessageQueue() {
	}

	private MessageQueue(String queueName) {
		this.msgQueueName = queueName;
	}

	/**
	 * 向队列中添加一条数据
	 * 
	 * @param message
	 * @throws Exception
	 */
	public void addMessage(MqMessage message) throws Exception {
		if (message != null) {
			msgQueue.put(message);
		}
		saveMessage2db(message);
	}

	private int saveMessage2db(MqMessage message) {
		if (message == null) {
			return 0;
		}
		int res = 0;
		String sql = SqlBuilderUtils.insertSqlBuilder(message);
		try {
			Connection connection = DBPoolManager.getConnection();
			Statement statement = connection.createStatement();
			logger.info("sql statement:" + sql);
			res = statement.executeUpdate(sql);
			// connection.commit();
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			logger.info("SQL insert exception：" + ex.getMessage());
		}
		return res;
	}

	public int updateMessage2db(MqMessage message) {
		if (message == null) {
			return 0;
		}
		int res = 0;
		String sql = SqlBuilderUtils.updateSqlBuilder(message);
		try {
			Connection connection = DBPoolManager.getConnection();
			Statement statement = connection.createStatement();
			logger.info("sql Statement:" + sql);
			res = statement.executeUpdate(sql);
			// connection.commit();
			connection.close();
		} catch (SQLException ex) {
			logger.info("SQL update exception：" + ex.getMessage());
		}
		return res;
	}

	/**
	 * 设置是否主动推送
	 * 
	 * @param isRunning
	 */
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	/**
	 * 取消息
	 * 
	 * @return
	 */
	public MqMessage fetchMessage() {
		MqMessage msg = null;
		while (msgQueue.isEmpty()) {
			logger.info("message queue is empty");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				logger.info("thread sleep interrupt exception：" + e.getMessage());
			}
		}
		try {
			msg = msgQueue.take();
		} catch (InterruptedException e) {
			logger.info("queue take interrupt exception：" + e.getMessage());
		}
		return msg;
	}

	/**
	 * @param result
	 *            消息的结果返回
	 * @param message
	 * @return 返回更新是否成功
	 */
	public int updateMessage(JSONObject result, MqMessage msg) {
		if (result == null || msg == null) {
			return 0;
		}
		int res = 0;
		if ((PushConstants.SUCCESS).equals(result.getString(PushConstants.RESP_CD))) {
			msg.setMsgSendStatus(PushConstants.SUCCESS);
			msg.setMsgSendDesc(result.getString(PushConstants.RESP_MSG));
		} else {
			msg.setMsgSendStatus(PushConstants.FAIL);
			msg.setMsgSendDesc(result.getString(PushConstants.RESP_MSG));
		}
		try {
			if (0 == (res = updateMessage2db(msg))) { // maybe update error
				saveMessage2db(msg);
			}
		} catch (Exception ex) {
			logger.info("update exception:" + ex.getMessage());
		}
		return res;
	}

	public BlockingQueue<MqMessage> getMessageQueue() {
		return this.msgQueue;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(msgQueue.toString());
		return new String(result);
	}

	public static void main(String[] args) throws Exception {
//		MqMessage message = new MqMessage();
//		message.setMsgId("356868207253720558343127612612");
//		message.setMsgRecTime(MyStringUtils.getCurrentDate());
//		message.setMsgContent("herundong");
//		message.setMsgPushUrl("www.baidu.com");
//		message.setMsgSendNum(200);
		MessageQueue queue = new MessageQueue("tianyin-001");
	//	queue.addMessage(message);
		
		ExecutorService service = Executors.newCachedThreadPool(); // 创建一个线程池
		final CountDownLatch cdOrder = new CountDownLatch(1);// 构造方法参数指定计数的次数
		final CountDownLatch cdAnswer = new CountDownLatch(100);// 构造方法参数指定计数的次数
		for (int i = 0; i < 100; i++) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						logger.info("线程" + Thread.currentThread().getName() + "正准备接受命令");
						cdOrder.await(); 
						logger.info("线程" + Thread.currentThread().getName() + "已接受命令");
						MqMessage message = new MqMessage();
						message.setMsgId(RandomStringUtils.randomNumeric(30));
						message.setMsgRecTime(MyStringUtils.getCurrentDate());
						message.setMsgContent("China");
						message.setMsgPushUrl("www.baidu.com");
						message.setMsgSendNum(200);
						queue.addMessage(message);
			              
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
			MqMessage message = new MqMessage();
			message.setMsgId(RandomStringUtils.randomNumeric(30));
			message.setMsgRecTime(MyStringUtils.getCurrentDate());
			message.setMsgContent("America");
			message.setMsgPushUrl("www.google.com");
			message.setMsgSendNum(33);
			queue.addMessage(message);
			cdAnswer.await(); 
			long t2=System.currentTimeMillis();  
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
		for (int i = 0; i < 100; i++) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						logger.info("线程" + Thread.currentThread().getName() + "正准备接受命令");
						cdOrder.await(); 
						logger.info("线程" + Thread.currentThread().getName() + "已接受命令");
						MqMessage message = queue.fetchMessage();
						logger.info("取出的消息: " + message.getMsgId());
						message.setMsgSendStatus("0000");
						queue.updateMessage2db(message);
			              
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
	}
}