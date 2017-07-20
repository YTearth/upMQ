package com.uphn.upMQ.service;

import com.uphn.upMQ.pojo.MqMessage;

public interface AbstractServ {	
	//消息推送到url
	void msgStartPush(MqMessage record) throws Exception;
    //从数据库读消息，保留
	MqMessage selectByPrimaryKey(String msgId);
    //更新消息, 保留
	int updateByPrimaryKeySelective(MqMessage record);
}
