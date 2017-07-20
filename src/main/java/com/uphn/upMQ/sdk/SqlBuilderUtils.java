package com.uphn.upMQ.sdk;

import com.uphn.upMQ.pojo.MqMessage;

/**
 * @author tianyin
 *SQL 拼装工具类
 */
public class SqlBuilderUtils {
	
   public static String insertSqlBuilder(MqMessage message) {
	   StringBuilder  sql = new StringBuilder("INSERT INTO MQ_MESSAGE (MSG_ID, MSG_REC_TIME, MSG_CONTENT, MSG_SEND_TIME, MSG_SEND_STATUS, "
				+ "MSG_SEND_DESC, MSG_SEND_NUM, MSG_PUSH_URL) VALUES ('");
		if (message.getMsgId() != null) {
			sql.append(message.getMsgId()).append("','");
		}
		if (message.getMsgRecTime() != null)  sql.append(message.getMsgRecTime()).append("','");
		else sql.append("NULL','");
		if (message.getMsgContent() != null) sql.append(message.getMsgContent()).append("','");
		else sql.append("NULL','");
		if (message.getMsgSendTime() != null) sql.append(message.getMsgSendTime()).append("','");
		else sql.append("NULL','");
		if (message.getMsgSendStatus() != null) sql.append(message.getMsgSendStatus()).append("','");
		else sql.append("NULL','");
		if (message.getMsgSendDesc() != null) sql.append(message.getMsgSendDesc()).append("','");
		else sql.append("NULL',");
		sql.append(message.getMsgSendNum()).append(",'");
		if (message.getMsgPushUrl() != null) sql.append(message.getMsgPushUrl()).append("');");
		else sql.append("NULL');");
		return sql.toString();
   }
	
   public static String updateSqlBuilder(MqMessage message) {
	   StringBuilder sql = new StringBuilder("UPDATE MQ_MESSAGE a SET ");
		if (message.getMsgRecTime() != null) {
			sql.append("MSG_REC_TIME = case when MSG_REC_TIME is null then '").append(message.getMsgRecTime()).append("' else a. MSG_REC_TIME end, "); 
		}
		if (message.getMsgContent() != null) {
			sql.append(" MSG_CONTENT = case when MSG_CONTENT is null then '").append(message.getMsgContent()).append("' else a.MSG_CONTENT end, ");
		}
		if (message.getMsgSendTime() !=null) {
			sql.append("MSG_SEND_TIME = case when MSG_SEND_TIME is null then '").append(message.getMsgSendTime()).append("' else a.MSG_SEND_TIME end, "); 
		}
		if (message.getMsgSendStatus() != null) {
			sql.append(" MSG_SEND_STATUS = case when MSG_SEND_STATUS is null then '").append(message.getMsgSendStatus()).append("' else a.MSG_SEND_STATUS end, ");
		}
		if (message.getMsgSendDesc() != null) {
			sql.append(" MSG_SEND_DESC = case when MSG_SEND_DESC is null then '").append(message.getMsgSendDesc()).append("' else a.MSG_SEND_DESC end, ");
		}
			sql.append("MSG_SEND_NUM = ").append(message.getMsgSendNum()).append(",");
		if (message.getMsgPushUrl() != null) {
			sql.append(" MSG_PUSH_URL = case when MSG_PUSH_URL is null then '").append(message.getMsgPushUrl()).append("' else a.MSG_PUSH_URL end ");
		}
			sql.append("WHERE MSG_ID = '").append(message.getMsgId()).append("';");
		return sql.toString();
   }
}
