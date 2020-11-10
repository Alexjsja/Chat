package factories;

import models.Message;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class messageFactory {
    private static Map<String,Message> messages;
    public static messageFactory startFactory(){
       messages = new HashMap<>();
       return new messageFactory();
    }

    public static void putMes(String text, String author, LocalTime time){
//        Message message = new Message(text,author,time);
//        messages.put(text,message);
    }
    public static ArrayList<Message> getMessagesAfterLast(LocalTime lastTime){

        return null;
    }
    public static Message getMes(String text){
        return messages.get(text);
    }


}
