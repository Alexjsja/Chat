package models;

import java.time.LocalTime;

public class Message {
    private String text;
    private String author;
    private String recipient;
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

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String toJsonFormat(){
        return "{\"text\":\""+this.text+"\",\"author\":\""+this.author+"\","+"\"sendTime\":"+"\""+sendTime+"\"}";
    }

    public Message(String text, String author, LocalTime sendTime) {
        this.text = text;
        this.author = author;
        this.sendTime = sendTime;
    }
}
