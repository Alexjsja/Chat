package factories;

import models.User;

import java.util.HashMap;
import java.util.Map;

public class ramUserFactory {
    private static int ID;
    private static Map<String,User> hm;

    public static ramUserFactory startFactory(){
        ID = 0;
        hm = new HashMap<>();
        return new ramUserFactory();
    }

    public static HashMap<String,User> getHashMap(){
        return (HashMap<String, User>) hm;
    }
    public static User getUserForName(String name){
        return hm.get(name);
    }
    public static final boolean containsUser(String name){
        return hm.containsKey(name);
    }
    public static int getLastId(){
        return ID;
    }
    
    public static boolean userLogin(String name, String password) {
        if (hm.containsKey(name)) {
            return hm.get(name).getPassword().equals(password);
        }
        return false;
    }

    public static boolean regUser(String name, String password){
        if(!hm.containsKey(name)){
            User user = new User(name,ID,password);
            hm.put(user.getName(),user);
            ID++;
            return true;
        }else {
         System.err.println("User exists");
         return false;
        }
    }

}
