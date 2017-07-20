package com.uphn.upMQ.dao;

import com.uphn.upMQ.pojo.MqMessage;

public interface MqMessageMapper {
    int deleteByPrimaryKey(String msgId);

    int insert(MqMessage record);

    int insertSelective(MqMessage record);

    MqMessage selectByPrimaryKey(String msgId);

    int updateByPrimaryKeySelective(MqMessage record);

    int updateByPrimaryKey(MqMessage record);
}