package logic;

import data.DataConnector;
import data.MySqlConnector;
import http.HttpRequest;
import http.RequestQueue;
import parsers.HttpParser;
import parsers.JsonParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    private static final String ip = "192.168.43.122";
    private static final int port = 2000;
    private static final String DBurl = "jdbc:mysql://localhost:3303/server-database?serverTimezone=UTC";
    private static final  String logPass = "admin";

    private static ServerSocketChannel server;
    private static Selector SELECTOR;
    private static ExecutorService executorService;
    private static DataConnector dataConnector;

    public static void run() throws Exception {
        SELECTOR = Selector.open();

        executorService = Executors.newFixedThreadPool(12);

        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(ip, port));
        server.configureBlocking(false);
        server.register(SELECTOR, SelectionKey.OP_ACCEPT);

        dataConnector = new MySqlConnector(DriverManager.getConnection(DBurl, logPass, logPass));
        while (true) {
            SELECTOR.select();

            Set<SelectionKey> keySet = SELECTOR.selectedKeys();

            Iterator<SelectionKey> keysIterator = keySet.iterator();

            while (keysIterator.hasNext()) {
                SelectionKey key = keysIterator.next();

                if (key.isAcceptable()) {
                    ServerSocketChannel ServerSocketChannel= (ServerSocketChannel) key.channel();
                    SocketChannel user = ServerSocketChannel.accept();
                    user.configureBlocking(false);
                    user.register(SELECTOR, SelectionKey.OP_READ);
                }else if (key.isReadable()) {
                    readRequest(key);
                }else if (key.isWritable()) {
                    sendResponse(key);
                }
                keysIterator.remove();
            }
        }
    }
    private static void sendResponse(SelectionKey key) throws Exception {
        SocketChannel userChannel = (SocketChannel) key.channel();
        HttpRequest request = RequestQueue.getFirstOnChannel(userChannel);

        try {
            userChannel.write(SendibleContent.doResponse(request));
        }catch (IOException e){
            key.cancel();
            userChannel.close();
        }

        key.cancel();
        userChannel.close();
    }

    private static void readRequest(SelectionKey key) throws Exception {
        SocketChannel userChannel = (SocketChannel) key.channel();
        int checkLength = -1;
        ByteBuffer buffer = null;
        StringBuilder httpRequest = new StringBuilder();
        try {
            do {
                buffer = ByteBuffer.allocate(1024);
                checkLength = userChannel.read(buffer);
                httpRequest.append(new String(buffer.array()), 0, checkLength);
            }while (buffer.capacity()==buffer.position());
            buffer.clear(); } catch (Exception ignored){}
        if (checkLength==-1){
            key.cancel();
            userChannel.close();
            return;
        }
        HttpParser httpParser = HttpParser.parseHttp(httpRequest.toString());

        Map<String,String> cookies = httpParser.getCookiesMap();
        String method = httpParser.getMethod();
        String mapping = httpParser.getMapping();
        Map<String,String> jsonMap = new HashMap<>();
        Map<String,String> requestParameters = httpParser.getParameters();
        if(httpParser.methodIsPost()) {
            JsonParser jsonParser = new JsonParser(httpParser.getBody());
            jsonMap.putAll(jsonParser.jsonHashMap());
        }

        HttpRequest builtRequest = HttpRequest.newRequest()
            .setDataConnector(dataConnector)
            .setParameters(requestParameters)
            .setChannel(userChannel)
            .setCookies(cookies)
            .setMapping(mapping)
            .setMethod(method)
            .setBody(jsonMap)
            .build();
        RequestQueue.put(builtRequest);
        userChannel.register(SELECTOR,SelectionKey.OP_WRITE);
    }

}
