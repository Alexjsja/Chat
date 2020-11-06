package logic;

import factories.ramUserFactory;
import parsers.HttpParser;
import parsers.JsonParser;
import models.Message;

import java.util.concurrent.ConcurrentHashMap;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.util.*;

import static factories.jsonFactory.jsonInBytes;


public class Server {
    private static Selector SELECTOR;
    private static ServerSocketChannel server;
    private static final String ip = "192.168.43.122";
    private static final int port = 2000;

    private static Map<SocketChannel,HashMap<String,String>> channelHeader;
    private static Map<SocketChannel,String> channelBody;
    private static Map<SocketChannel,String> authChanel;

    private static ramUserFactory userFactory;

    public static void run() throws Exception {
        SELECTOR = Selector.open();

        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(ip, port));
        server.configureBlocking(false);
        server.register(SELECTOR, SelectionKey.OP_ACCEPT);

        channelHeader = new ConcurrentHashMap<>();
        channelBody = new ConcurrentHashMap<>();
        authChanel = new ConcurrentHashMap<>();
        userFactory = ramUserFactory.startFactory();

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

                    String cookieUser = HttpParser.getCookieValue();

                    if(method.equals("POST"))
                        channelBody.put(userChannel, HttpParser.getBody());
                    if (ramUserFactory.containsUser(cookieUser))
                        authChanel.put(userChannel,cookieUser);

                    channelHeader.put(userChannel,hm);

                    userChannel.register(SELECTOR,SelectionKey.OP_WRITE);
                }
                if (key.isWritable()) {
                    SocketChannel userChannel = (SocketChannel) key.channel();


                    if (channelHeader.containsKey(userChannel)) {
                        HashMap<String,String> method_mapping = channelHeader.get(userChannel);
                        String project = System.getProperty("user.dir");
                        if (method_mapping.containsKey("GET")) {

                            pageLogic content = pageLogic.valueOf(method_mapping.get("GET"));
                            userChannel.write(content.getContentInBytes());

                        }else if(method_mapping.containsKey("POST")){
                            String postMapping = method_mapping.get("POST");
                            HashMap<String,String> jsonPost = JsonParser.jsonHashMap(channelBody.get(userChannel));

                            switch (postMapping){
                                case "login":
                                    boolean loginSuccessful = ramUserFactory.userLogin(jsonPost.get("name"),jsonPost.get("password"));
                                    String logResponse = "{\"loged\":\""+loginSuccessful+"\"}";
                                    userChannel.write(jsonInBytes(logResponse,jsonPost.get("name")));
                                    break;
                                case "register":
                                    boolean registerSuccessful = ramUserFactory.regUser(jsonPost.get("name"),jsonPost.get("password"));
                                    String regResponse = "{\"reg\":\""+registerSuccessful+"\"}";
                                    userChannel.write(jsonInBytes(regResponse,jsonPost.get("name")));
                                    break;
                                case "home":
                                        String name = authChanel.get(userChannel);
                                        Message msg = new Message(jsonPost.get("text"),name, LocalTime.now());
                                        userChannel.write(jsonInBytes(msg.toJsonFormat()));
                                    break;
                                default:
                                    break;
                            }
                        }
                        channelBody.remove(userChannel);
                        channelHeader.remove(userChannel);
                        authChanel.remove(userChannel);
                    }
                    userChannel.register(SELECTOR, SelectionKey.OP_READ);
                }
            }
        }
    }


}
