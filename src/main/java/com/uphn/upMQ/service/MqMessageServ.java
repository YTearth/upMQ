package com.uphn.upMQ.service;

import com.uphn.upMQ.pojo.MqMessage;

public interface MqMessageServ extends AbstractServ {
	
	boolean isMsgExist(String msgId);
	
	int deleteByPrimaryKey(String msgId);

	int insert(MqMessage record);

	int updateByPrimaryKey(MqMessage record);
}
