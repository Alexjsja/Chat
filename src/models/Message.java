package models;

import java.time.LocalTime;

public class Message {
    private String text;
    private String author;
    private String receiver;
    private LocalTime sendTime;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public LocalTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalTime sendTime) {
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
        return "{\"author\":\""+author+",\"receiver\":\""+receiver+"\",\"text\":\""+text+"\",\"sendTime\":\""+sendTime+"\"}";
    }

    public Message(String text, String author, LocalTime sendTime, String receiver) {
        this.receiver=receiver;
        this.text = text;
        this.author = author;
        this.sendTime = sendTime;
    }
}
