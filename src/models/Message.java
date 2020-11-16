package models;

public class Message {
    private String text;
    private String author;
    private String sendTime;
    private String authorRole;

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

    public void setAuthor(String author) {
        this.author = author;
    }

    public String toJsonFormat(){
        return "{\"author\":\""+author+"\",\"text\":\""+text+"\",\"sendTime\":\""+sendTime+"\",\"role\":\""+authorRole+"\"}";
    }

    public Message(String text, String author, String sendTime,String authorRole) {
        this.text = text;
        this.author = author;
        this.sendTime = sendTime;
        this.authorRole = authorRole;
    }
}
