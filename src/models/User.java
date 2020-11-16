package models;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private long id;
    private String password;
    private String role;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(String name, long id, String password) {
        this.name = name;
        this.id = id;
        this.password = password;
    }
    public User(String name, String password,String role) {
        this.name = name;
        this.password = password;
        this.role=role;
    }
    public String toJsonFormat(){
        return "{\"id\":"+id+",\"name\":\""+name+"\",\"role\":\""+role+"\"}";
    }

}
