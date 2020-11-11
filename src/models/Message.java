package models;

import java.time.LocalTime;

public class Message {
    private String text;
    private String author;
    private String receiver;
    private String sendTime;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String toJsonFormat(){
        return "{\"author\":\""+author+"\",\"text\":\""+text+"\",\"sendTime\":\""+sendTime+"\"}";
    }

    public Message(String text, String author, String sendTime) {
        this.text = text;
        this.author = author;
        this.sendTime = sendTime;
    }
}
