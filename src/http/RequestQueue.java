package http;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestQueue {

    private static final List<HttpRequest> fakeQueue = new ArrayList<>();

    public static synchronized HttpRequest getFirstOnChannel(SocketChannel channel){
        HttpRequest request = fakeQueue.stream().filter(el->el.getChannel()==channel).findFirst()
            .orElseThrow(()->new RuntimeException(" Channel not found!"));
        fakeQueue.remove(request);
        return request;
    }

    public static synchronized void put(HttpRequest request){
        fakeQueue.add(request);
    }

    public static synchronized HttpRequest getFirst(){
        return fakeQueue.remove(0);
    }

    public static synchronized boolean isEmpty(){
        return fakeQueue.isEmpty();
    }

    public static synchronized boolean containsKey(SocketChannel channel){
        if (isEmpty())return false;
        Optional<HttpRequest> request = fakeQueue.stream().filter(el -> el.getChannel() == channel).findFirst();
        return request.isPresent();
    }
    public static synchronized List<HttpRequest> getList(){
        return fakeQueue;
    }
}
