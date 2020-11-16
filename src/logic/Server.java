package logic;

import database.dbConnector;
import models.formattedRequestQueue;
import parsers.HttpParser;
import parsers.JsonParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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

    public static void run() throws Exception {
        SELECTOR = Selector.open();

        executorService = Executors.newFixedThreadPool(4);

        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(ip, port));
        server.configureBlocking(false);
        server.register(SELECTOR, SelectionKey.OP_ACCEPT);

        dbConnector.connect(DriverManager.getConnection(DBurl, logPass, logPass));

        while (true) {
            SELECTOR.select();

            Set<SelectionKey> keySet = SELECTOR.selectedKeys();

            Iterator<SelectionKey> keysIterator = keySet.iterator();

            while (keysIterator.hasNext()) {
                SelectionKey key = keysIterator.next();

                if (!key.isValid())continue;

                if (key.isAcceptable()) {
                    ServerSocketChannel ServerSocketChannel= (ServerSocketChannel) key.channel();
                    SocketChannel user = ServerSocketChannel.accept();
                    user.configureBlocking(false);
                    user.register(SELECTOR, SelectionKey.OP_READ);
                }

                if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    readRequest(socketChannel);
                    /*executorService.submit(()-> {
                        try {
                            readRequest((SocketChannel) key.channel());
                        } catch (IOException | SQLException e) {
                            e.printStackTrace();
                        }
                    });*/
                }
                if (key.isWritable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    sendResponse(socketChannel);
                    /*if (formattedRequestQueue.containsChannel(socketChannel))
                        executorService.submit(()-> {
                            try {
                                sendResponse(socketChannel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });*/
                }
                keysIterator.remove();
            }
        }
    }
    private static void sendResponse(SocketChannel userChannel) throws Exception {
        formattedRequestQueue request = formattedRequestQueue.getFirstOnChannel(userChannel);
        String method = request.getMethod();
        String mapping = request.getMapping();
        Map<String, String> cookiesMap = request.getCookies();
        Map<String, String> jsonMap = request.getJson();

        if (method.equals("GET")) {

            sendibleContent someContent = sendibleContent.getContentOfMapping(mapping);
            ByteBuffer outBuffer = someContent.getContentInBytes(cookiesMap);
            userChannel.write(outBuffer);

        }else if(method.equals("POST")){

            sendibleContent someContent = sendibleContent.getContentOfMapping(mapping);
            ByteBuffer outBuffer = someContent.postContentInBytes(jsonMap,cookiesMap,mapping);
            userChannel.write(outBuffer);

        }

        userChannel.register(SELECTOR, SelectionKey.OP_READ);
    }

    private static void readRequest(SocketChannel userChannel) throws IOException, SQLException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int checkLength = 0;

        try {
            checkLength = userChannel.read(buffer);
            if (checkLength==-1||checkLength==0){
                userChannel.register(SELECTOR,SelectionKey.OP_CONNECT);
                return;
            }
        }catch (Exception e){
            userChannel.close();
            return;
        }
        byte[] byteRequest = new byte[checkLength];
        System.arraycopy(buffer.array(), 0, byteRequest, 0, checkLength);
        buffer.clear();
        String httpRequest = new String(byteRequest);

        HttpParser httpParser = HttpParser.parseHttp(httpRequest);

        Map<String,String> cookies = httpParser.getCookiesMap();
        String method = httpParser.getMethod();
        String mapping = httpParser.getMapping();
        Map<String,String> jsonMap = new HashMap<>();
        if(httpParser.methodIsPost()) {
            JsonParser jsonParser = new JsonParser(httpParser.getBody());
            jsonMap.putAll(jsonParser.jsonHashMap());
        }
        formattedRequestQueue builtRequest = formattedRequestQueue.newRequest()
            .setBody(jsonMap)
            .setChannel(userChannel)
            .setCookies(cookies)
            .setMapping(mapping)
            .setMethod(method)
            .build();
        formattedRequestQueue.put(builtRequest);

        userChannel.register(SELECTOR,SelectionKey.OP_WRITE);
    }

}
