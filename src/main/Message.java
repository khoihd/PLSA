package main;

import java.util.Arrays;

public class Message {
  public int getSenderId() {
    return senderId;
  }

  public int getRecieverId() {
    return recieverId;
  }

  public int getMESSAGE_TYPE() {
    return MESSAGE_TYPE;
  }

  private int senderId, recieverId, MESSAGE_TYPE;

  private double[] msgDoubleContent;

  public double[] getMsgDoubleContent() {
    return msgDoubleContent;
  }

  public Message(int senderId, int recieverId, int MESSAGE_TYPE, double[] msg) {
    this.senderId = senderId;
    this.recieverId = recieverId;
    this.MESSAGE_TYPE = MESSAGE_TYPE;
    this.msgDoubleContent = msg;
  }

  private double msgValueContent;

  public double getMsgValueContent() {
    return msgValueContent;
  }

  public Message(int senderId, int recieverId, int MESSAGE_TYPE, double msg) {
    this.senderId = senderId;
    this.recieverId = recieverId;
    this.MESSAGE_TYPE = MESSAGE_TYPE;
    this.msgValueContent = msg;
  }

  private String msgStringContent;

  public String getMsgStringContent() {
    return msgStringContent;
  }

  public Message(int senderId, int recieverId, int MESSAGE_TYPE, String msg) {
    this.senderId = senderId;
    this.recieverId = recieverId;
    this.MESSAGE_TYPE = MESSAGE_TYPE;
    this.msgStringContent = msg;
  }

  @Override
  public String toString() {
    return "Message{" + "senderId=" + senderId + ", recieverId=" + recieverId + ", MESSAGE_TYPE=" + MESSAGE_TYPE
        + ", msgContent=" + Arrays.toString(msgDoubleContent) + '}';
  }

}