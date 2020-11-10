package database;

import models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//fixme
public class dbConnector {

    private static Connection connection;


    public static void connect(Connection connect){
        connection = connect;
    }

    public static boolean userLogin(String name,String password) throws SQLException {
        boolean success=false;

        String request = String.format("select * from users where name='%s'and password='%s'",name,password);

        if (userReturned(send(request))) {
            success = true;
        }

        return success;
    }
    public static boolean userRegister(String name,String password) throws SQLException {
        boolean success=false;
        if(!containsUser(name)){

            String request = String.format("insert into users(name,password) values('%s','%s')",name,password);

            PreparedStatement statement = connection.prepareStatement(request);

            statement.execute();

            success = true;
        }
        return success;
    }
    public static boolean containsUser(String name) throws SQLException {
        boolean success=false;

        String request = String.format("select * from users where name='%s'",name);

        if (userReturned(send(request))) {
            success = true;
        }

        return success;
    }

    private static boolean userReturned(ResultSet response) throws SQLException {
        User user = null;
        while (response.next()) {
            String nm = response.getString("name");
            String ps = response.getString("password");
            user = new User(nm, ps);
        }
        return user==null?false:true;
    }
    private static ResultSet send(String str) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(str);

        statement.execute();

        return statement.executeQuery();
    }
}
