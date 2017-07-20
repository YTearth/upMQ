package com.uphn.upMQ.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uphn.upMQ.sdk.MessageQueue;
import com.uphn.upMQ.dao.MqMessageMapper;
import com.uphn.upMQ.pojo.MqMessage;
import com.uphn.upMQ.service.MqMessageServ;

@Service
public class MqMessageServImpl implements MqMessageServ {

	private static Logger logger = Logger.getLogger(MqMessageServImpl.class);

	@Autowired
	MqMessageMapper msgMapper;
	
	@Override
	public boolean isMsgExist(String msgId) {
		return (msgMapper.selectByPrimaryKey(msgId) != null);
	}
	
	@Override
	public int deleteByPrimaryKey(String msgId) {
		return msgMapper.deleteByPrimaryKey(msgId);
	}

	@Override
	public int insert(MqMessage record) {
		return msgMapper.insert(record);
	}

	@Override
	public void msgStartPush(MqMessage record) throws Exception{
		//QueueChooser queueScheduler = RandQueueChooser.getInstance(msgMapper);
		//MessageQueue msgQueue = queueScheduler.chooseredQueue(null);
		//MessageQueue msgQueue = MessageQueue.getSingleton(msgMapper);
		//msgQueue.addMessage(record);
	}

	@Override
	public MqMessage selectByPrimaryKey(String msgId) {
		return msgMapper.selectByPrimaryKey(msgId);
	}

	@Override
	public int updateByPrimaryKeySelective(MqMessage record) {
		return msgMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MqMessage record) {
		return msgMapper.updateByPrimaryKey(record);
	}

}
