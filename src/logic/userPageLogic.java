package logic;

import database.DataConnector;
import http.httpBuilder;
import models.RequestQueue;
import models.User;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;

import static http.httpBuilder.HTML;

public class userPageLogic {
    public static ByteBuffer showUserPage(RequestQueue request, String filePath) throws SQLException, IOException {
        Path contentPath = Paths.get(filePath);
        String html = String.join("\n", Files.readAllLines(contentPath));
        Map<String, String> params = request.getParameters();
        DataConnector dataConnector = request.getDataConnector();


        int userId = Integer.parseInt(params.get("id"));
        User user = dataConnector.getUserInfo(userId);
        String[] splintedPage = html.split("<code/>");
        StringBuilder content = new StringBuilder();
        content.append(splintedPage[0])
            .append("<h1>Имя юзера:").append(user.getName()).append("</h1>")
            .append("<h1>Почта юзера:").append(user.getMail()).append("</h1>")
            .append("<h1>Роль юзера:").append(user.getRole()).append("</h1>")
            .append("<h1>Id юзера:").append(userId).append("</h1>")
            .append("<a class=\"userHref\" href=\"/personal?id=").append(userId).append("\">написать</a>")
            .append(splintedPage[1]);

        String headers = new httpBuilder(200)
            .setResponseLength(content.toString())
            .setResponseType(HTML)
            .setServer().setServer()
            .build();
        String fullResponse = headers + content;

        return ByteBuffer.wrap(fullResponse.getBytes());
    }
}

