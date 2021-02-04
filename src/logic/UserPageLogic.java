package logic;

import data.DataConnector;
import http.HttpBuilder;
import http.HttpRequest;
import models.User;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static http.HttpBuilder.ContentType.*;

public class UserPageLogic {
    //todo fix this shit

    public static ByteBuffer showMyPage(HttpRequest request, String filePath) throws Exception {
        Path contentPath = Paths.get(filePath);
        String html = String.join("\n", Files.readAllLines(contentPath));
        DataConnector dataConnector = request.getDataConnector();
        Map<String, String> cookies = request.getCookies();

        User user = dataConnector.getUserInfo(cookies.get("session"));
        String[] splintedPage = html.split("<template/>");
        StringBuilder content = new StringBuilder();
        content.append(splintedPage[0])
            .append("<h1>Ваша страница</h1>")
            .append("<h2>Ваше имя:").append(user.getName()).append("</h2>")
            .append("<h2>Ваша почта:").append(user.getMail()).append("</h2>")
            .append("<h2>Ваша роль:").append(user.getRole()).append("</h2>")
            .append("<h2>Ваш id:").append(user.getId()).append("</h2>")
            .append(splintedPage[1]);

        String headers = new HttpBuilder(200)
            .setResponseLength(content.toString())
            .setResponseType(HTML)
            .setServer().setServer()
            .build();
        String fullResponse = headers + content;

        return ByteBuffer.wrap(fullResponse.getBytes());
    }

    public static ByteBuffer showUserPage(HttpRequest request, String filePath) throws Exception {
        Path contentPath = Paths.get(filePath);
        String html = String.join("\n", Files.readAllLines(contentPath));
        Map<String, String> params = request.getParameters();
        DataConnector dataConnector = request.getDataConnector();


        int userId = Integer.parseInt(params.get("id"));
        User user = dataConnector.getUserInfo(userId);
        String[] splintedPage = html.split("<template/>");
        StringBuilder content = new StringBuilder();
        content.append(splintedPage[0])
            .append("<h1>Имя юзера:").append(user.getName()).append("</h1>")
            .append("<h1>Почта юзера:").append(user.getMail()).append("</h1>")
            .append("<h1>Роль юзера:").append(user.getRole()).append("</h1>")
            .append("<h1>Id юзера:").append(userId).append("</h1>")
            .append("<a class=\"userHref\" href=\"/personal?id=").append(userId).append("\">написать</a>")
            .append(splintedPage[1]);

        String headers = new HttpBuilder(200)
            .setResponseLength(content.toString())
            .setResponseType(HTML)
            .setServer().setServer()
            .build();
        String fullResponse = headers + content;

        return ByteBuffer.wrap(fullResponse.getBytes());
    }
}

