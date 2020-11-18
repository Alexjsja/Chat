package models;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private long id;
    private String role;
    private String mail;

    public void setId(long id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public User(){}

    public User(String name, long id,String mail,String role) {
        this.role=role;
        this.mail=mail;
        this.name = name;
        this.id = id;
    }

    public String toJsonFormat(){
        return "{\"id\":"+id+",\"name\":\""+name+"\",\"role\":\""+role+"\",\"mail\":\""+mail+"\"}";
    }

}
