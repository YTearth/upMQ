package com.uphn.upMQ.pojo;

public class MqMessage {
    private String msgId;

    private String msgRecTime;

    private String msgContent;

    private String msgSendTime;

    private String msgSendStatus;

    private String msgSendDesc;

    private int msgSendNum;

    private String msgPushUrl;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId == null ? null : msgId.trim();
    }

    public String getMsgRecTime() {
        return msgRecTime;
    }

    public void setMsgRecTime(String msgRecTime) {
        this.msgRecTime = msgRecTime == null ? null : msgRecTime.trim();
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent == null ? null : msgContent.trim();
    }

    public String getMsgSendTime() {
        return msgSendTime;
    }

    public void setMsgSendTime(String msgSendTime) {
        this.msgSendTime = msgSendTime == null ? null : msgSendTime.trim();
    }

    public String getMsgSendStatus() {
        return msgSendStatus;
    }

    public void setMsgSendStatus(String msgSendStatus) {
        this.msgSendStatus = msgSendStatus == null ? null : msgSendStatus.trim();
    }

    public String getMsgSendDesc() {
        return msgSendDesc;
    }

    public void setMsgSendDesc(String msgSendDesc) {
        this.msgSendDesc = msgSendDesc == null ? null : msgSendDesc.trim();
    }

    public int getMsgSendNum() {
        return msgSendNum;
    }

    public void setMsgSendNum(int msgSendNum) {
        this.msgSendNum = msgSendNum;
    }

    public String getMsgPushUrl() {
        return msgPushUrl;
    }

    public void setMsgPushUrl(String msgPushUrl) {
        this.msgPushUrl = msgPushUrl == null ? null : msgPushUrl.trim();
    }
}