package logic;

import database.dbConnector;
import parsers.HttpParser;
import parsers.JsonParser;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class Server {
    private static final String ip = "localhost";
    private static final int port = 2000;
    private static final String DBurl = "jdbc:mysql://localhost:3306/serverdatabase?serverTimezone=UTC";
    private static final  String logPass = "admin";

    private static ServerSocketChannel server;
    private static Selector SELECTOR;

    private static Map<SocketChannel,HashMap<String,String>> channelHeader;
    private static Map<SocketChannel,String> channelBody;
    private static Map<SocketChannel,HashMap<String,String>> channelCookies;

    public static void run() throws Exception {
        SELECTOR = Selector.open();

        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(ip, port));
        server.configureBlocking(false);
        server.register(SELECTOR, SelectionKey.OP_ACCEPT);

        channelHeader = new ConcurrentHashMap<>();
        channelBody = new ConcurrentHashMap<>();
        channelCookies = new ConcurrentHashMap<>();

//        dbConnector.connect(DriverManager.getConnection(DBurl, logPass, logPass));

        while (true) {
            SELECTOR.select();

            Set<SelectionKey> keySet = SELECTOR.selectedKeys();
            Iterator<SelectionKey> keysIterator = keySet.iterator();

            while (keysIterator.hasNext()) {
                SelectionKey key = (SelectionKey) keysIterator.next();
                keysIterator.remove();

                if (key.isAcceptable()) {
                    ServerSocketChannel ssc= (ServerSocketChannel) key.channel();
                    SocketChannel user = ssc.accept();
                    user.configureBlocking(false);
                    user.register(SELECTOR, SelectionKey.OP_READ);
                }
                if(!key.isValid())continue;

                if (key.isReadable()) {
                    SocketChannel userChannel = (SocketChannel) key.channel();
                    userChannel.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(1024);

                    int checkLength = userChannel.read(buffer);
                    buffer.clear();
                    if (checkLength == -1) {
                        continue;
                    }

                    byte[] byteRequest = new byte[checkLength];
                    System.arraycopy(buffer.array(), 0, byteRequest, 0, checkLength);
                    String httpRequest = new String(byteRequest);

                    HttpParser.parseHttp(httpRequest);

                    String method = HttpParser.getMethod();
                    String mapping = HttpParser.getMapping();

                    HashMap<String,String> hm = new HashMap<>();
                    hm.put(method,mapping);

                    if(method.equals("POST"))
                        channelBody.put(userChannel, HttpParser.getBody());


                    channelCookies.put(userChannel,HttpParser.getCookiesMap());
                    channelHeader.put(userChannel,hm);

                    userChannel.register(SELECTOR,SelectionKey.OP_WRITE);
                }
                if (key.isWritable()) {
                    SocketChannel userChannel = (SocketChannel) key.channel();
                    if (channelHeader.containsKey(userChannel)) {

                        HashMap<String,String> method_mapping = channelHeader.get(userChannel);
                        HashMap<String, String> cookiesMap = channelCookies.get(userChannel);

                        if (method_mapping.containsKey("GET")) {

                            String mapping = method_mapping.get("GET");

                            sendibleContent someContent = sendibleContent.getContentOfMapping(mapping);

                            ByteBuffer outBuffer = someContent.getContentInBytes(cookiesMap);

                            userChannel.write(outBuffer);

                        }else if(method_mapping.containsKey("POST")){
                            HashMap<String,String> jsonPost = JsonParser.jsonHashMap(channelBody.get(userChannel));
                            String mapping = method_mapping.get("POST");

                            sendibleContent someContent = sendibleContent.getContentOfMapping(mapping);

                            ByteBuffer outBuffer = someContent.postContentInBytes(jsonPost,cookiesMap,mapping);

                            userChannel.write(outBuffer);

                        }
                    }
                    channelBody.remove(userChannel);
                    channelCookies.remove(userChannel);
                    channelHeader.remove(userChannel);
                    userChannel.register(SELECTOR, SelectionKey.OP_READ);
                }
            }
        }
    }


}
