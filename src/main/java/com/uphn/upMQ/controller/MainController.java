package com.uphn.upMQ.controller;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.uphn.upMQ.sdk.PushConstants;
import com.uphn.upMQ.config.RespCd;
import com.uphn.upMQ.config.RespMsg;
import com.uphn.upMQ.exception.MQException;
import com.uphn.upMQ.pojo.MqMessage;
import com.uphn.upMQ.service.MqMessageServ;
import com.uphn.upMQ.util.MyBase64Utils;
import com.uphn.upMQ.util.MyStringUtils;
import com.uphn.upMQ.util.PackageVerifyUtils;

@Controller
public class MainController {
	
	private static Logger logger = Logger.getLogger(MainController.class);
    
	@Autowired
	MqMessageServ msgServ;
	
	@ResponseBody
	@RequestMapping("/msgPost.do")
	public JSONObject messagePost(@RequestBody JSONObject reqJson, HttpSession session) {
		logger.info("begin to verify and post");
		JSONObject respJson = new JSONObject();
		try {
			PackageVerifyUtils.VerifyPackage(reqJson);
			//判断msg是否重复
			if (msgServ.isMsgExist(reqJson.getString("msgSN"))) {
				throw new MQException(RespCd.RESPCD_01, "Message repetition error");
			}
			//获取推送url,并单独校验格式
			byte[] urlByte = MyBase64Utils.decode(reqJson.getString("msgUrl").getBytes());
			String pushUrl = new String(urlByte);
			if (!MyStringUtils.isUrlMatch(pushUrl)) {
				throw new MQException(RespCd.RESPCD_01, "Filed [msgUrl] format error");
			}
			
			MqMessage message = new MqMessage();
			message.setMsgId(reqJson.getString("msgSN"));
			message.setMsgContent(reqJson.getString("msgBody")); //推送的时候只需要推送content即可
			message.setMsgRecTime(MyStringUtils.getCurrentDate());
			message.setMsgSendStatus(PushConstants.UNSEND);
			message.setMsgSendNum(1);
			message.setMsgPushUrl(pushUrl);
			msgServ.msgStartPush(message);
			respJson.put(RespCd.RESP_CD, RespCd.RESPCD_00);
			respJson.put(RespMsg.RESP_MSG, RespMsg.RESPMSG_00);
		} catch (MQException ex) {
			logger.info("message error：" + ex.getMessage());
			respJson.put(RespCd.RESP_CD, ex.getRespCd());
			respJson.put(RespMsg.RESP_MSG, ex.getRespMsg());
		} catch (Exception ex) {
			logger.info("message error：" + ex.getMessage());
			respJson.put(RespCd.RESP_CD, RespCd.RESPCD_10);
			respJson.put(RespMsg.RESP_MSG, RespMsg.RESPMSG_10);
		}
		logger.info("end to verify and post");
		return respJson;
	}
}
