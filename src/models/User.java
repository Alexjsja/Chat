package models;

import jdk.nashorn.internal.ir.debug.JSONWriter;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private int id;
    private String role;
    private String password;
    private String mail;

    public String getMail() {
        return mail;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    User(){}

    public String toJsonFormat(){
        return String.format(
            "{\"name\":\"%s\",\"password\":\"%s\",\"mail\":\"%s\",\"role\":\"%s\",\"id\":%d"
            ,name,password,mail,role,id);
    }
    public static UserBuilder newUser(){
        return new User().new UserBuilder();
    }

    public class UserBuilder{
        UserBuilder(){}

        public UserBuilder setName(String name){
            User.this.name=name;
            return this;
        }
        public UserBuilder setMail(String mail){
            User.this.mail=mail;
            return this;
        }
        public UserBuilder setId(int id){
            User.this.id=id;
            return this;
        }
        public UserBuilder setPassword(String password){
            User.this.password=password;
            return this;
        }
        public UserBuilder setRole(String role){
            User.this.role=role;
            return this;
        }

        public User build(){
            return User.this;
        }
    }

}
