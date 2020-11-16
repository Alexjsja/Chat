package models;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class formattedRequestQueue {
    private SocketChannel socketChannel;
    private Map<String,String> jsonBody;
    private Map<String,String> cookies;
    private String method;
    private String mapping;

    private static final List<formattedRequestQueue> fakeQueue = new ArrayList<>();

    formattedRequestQueue(){}

    /*------------------------------------Queue Methods------------------------------------------------*/
    public static formattedRequestQueue getFirstOnChannel(SocketChannel channel){
        formattedRequestQueue frq = fakeQueue.stream().filter(el->el.socketChannel==channel).findFirst()
            .orElseThrow(()->new RuntimeException("Channel not found!"));
        fakeQueue.remove(frq);
        return frq;
    }

    public static void put(formattedRequestQueue frq){
        fakeQueue.add(frq);
    }

    public static formattedRequestQueue getFirst(){
        return fakeQueue.remove(0);
    }

    public static boolean isEmpty(){
        return fakeQueue.isEmpty();
    }
    //fixme
    public static boolean containsChannel(SocketChannel channel){
        if (isEmpty())return false;
        Optional<formattedRequestQueue> request = fakeQueue.stream().filter(el -> el.socketChannel == channel).findFirst();
        return request.isPresent();
    }
    public static List<formattedRequestQueue> getList(){
        return fakeQueue;
    }
    /*------------------------------------Concrete request Methods------------------------------------------------*/
    public static requestBuilder newRequest(){
        return new formattedRequestQueue().new requestBuilder();
    }

    public Map<String, String> getCookies(){
        return this.cookies;
    }

    public Map<String, String> getJson(){
        return this.jsonBody;
    }

    public SocketChannel getChannel(){
        return this.socketChannel;
    }

    public String getMapping(){
        return this.mapping;
    }

    public String getMethod(){
        return this.method;
    }

    public class requestBuilder{
        requestBuilder(){}

        public requestBuilder setCookies(Map<String,String> cookies){
            formattedRequestQueue.this.cookies=cookies;
            return this;
        }
        public requestBuilder setChannel(SocketChannel channel){
            formattedRequestQueue.this.socketChannel=channel;
            return this;
        }
        public requestBuilder setBody(Map<String,String> body){
            formattedRequestQueue.this.jsonBody = body;
            return this;
        }
        public requestBuilder setMapping(String mapping){
            formattedRequestQueue.this.mapping=mapping;
            return this;
        }
        public requestBuilder setMethod(String method){
            formattedRequestQueue.this.method=method;
            return this;
        }
        public formattedRequestQueue build(){
            return formattedRequestQueue.this;
        }

    }
}

