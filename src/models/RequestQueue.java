package models;

import database.DataConnector;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RequestQueue {
    private SocketChannel socketChannel;
    private Map<String,String> jsonBody;
    private Map<String,String> cookies;
    private Map<String,String> parameters;
    private String method;
    private String mapping;
    private DataConnector dataConnector;

    private static final List<RequestQueue> fakeQueue = new ArrayList<>();

    RequestQueue(){}

    /*------------------------------------Queue Methods------------------------------------------------*/
    public static synchronized RequestQueue getFirstOnChannel(SocketChannel channel){
        RequestQueue frq = fakeQueue.stream().filter(el->el.socketChannel==channel).findFirst()
            .orElseThrow(()->new RuntimeException(" Channel not found!"));
        fakeQueue.remove(frq);
        return frq;
    }

    public static synchronized void put(RequestQueue frq){
        fakeQueue.add(frq);
    }

    public static synchronized RequestQueue getFirst(){
        return fakeQueue.remove(0);
    }

    public static synchronized boolean isEmpty(){
        return fakeQueue.isEmpty();
    }

    public static synchronized boolean containsKey(SelectionKey key){
        SocketChannel channel = (SocketChannel) key.channel();
        if (isEmpty())return false;
        Optional<RequestQueue> request = fakeQueue.stream().filter(el -> el.socketChannel == channel).findFirst();
        return request.isPresent();
    }
    public static synchronized List<RequestQueue> getList(){
        return fakeQueue;
    }

    /*------------------------------------Concrete request Methods------------------------------------------------*/
    public static requestBuilder newRequest(){
        return new RequestQueue().new requestBuilder();
    }

    public DataConnector getDataConnector() { return dataConnector; }
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
    public Map<String, String> getParameters(){
        return this.parameters;
    }



    public class requestBuilder{
        requestBuilder(){}

        public requestBuilder setCookies(Map<String,String> cookies){
            RequestQueue.this.cookies=cookies;
            return this;
        }
        public requestBuilder setParameters(Map<String,String> parameters){
            RequestQueue.this.parameters=parameters;
            return this;
        }
        public requestBuilder setChannel(SocketChannel channel){
            RequestQueue.this.socketChannel=channel;
            return this;
        }
        public requestBuilder setBody(Map<String,String> body){
            RequestQueue.this.jsonBody = body;
            return this;
        }
        public requestBuilder setMapping(String mapping){
            RequestQueue.this.mapping=mapping;
            return this;
        }
        public requestBuilder setMethod(String method){
            RequestQueue.this.method=method;
            return this;
        }
        public requestBuilder setDataConnector(DataConnector dataConnector) {
            RequestQueue.this.dataConnector=dataConnector;
            return this;
        }

        public RequestQueue build(){
            return RequestQueue.this;
        }
    }
}

