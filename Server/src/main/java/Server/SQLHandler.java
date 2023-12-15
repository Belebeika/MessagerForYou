package Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLHandler {
    private static Connection connection;
    private static Statement stmt;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void CreateDB() throws SQLException {
        stmt = connection.createStatement();
        stmt.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'login' text UNIQUE, 'password' text, 'nickname' text UNIQUE);");
        stmt.execute("CREATE TABLE if not exists 'friends' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'user_id' INTEGER NOT NULL, 'friend_id' INTEGER NOT NULL, FOREIGN KEY('user_id') REFERENCES 'users' (id), FOREIGN KEY('friend_id') REFERENCES 'users' (id));");

        System.out.println("Таблицы созданы или уже существуют.");
    }

    public static boolean checknick(String nick) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT id FROM users WHERE nickname = '" + nick + "';");
        if(rs.next()){
            return true;
        }
        return false;
    }

    public  static void addfriend(String usernick, String friendnick) throws SQLException {
        stmt.executeUpdate("INSERT INTO friends (user_id, friend_id) VALUES (user.id, friend.id) JOIN " +
                "users AS user ON user.nickname = '" + usernick + "' JOIN users AS friend ON friend.nickname = '" + friendnick + "';");
        stmt.executeUpdate("INSERT INTO friends (user_id, friend_id) VALUES (friend.id, user.id) JOIN " +
                "users AS user ON user.nickname = '" + usernick + "' JOIN users AS friend ON friend.nickname = '" + friendnick + "';");
    }
    public static String getNickByLoginAndPassword(String login, String password) {
        try {
            ResultSet rs = stmt.executeQuery("SELECT nickname FROM users WHERE login = '" + login + "' AND password = '" + password + "';");
            if(rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean tryToRegister(String login, String password, String nickname) {
        try {
            System.out.println("INSERT INTO users (login, password, nickname) VALUES ('"+login+"', '"+password+"', '"+nickname+"');");
            stmt.executeUpdate(String.format("INSERT INTO users (login, password, nickname) VALUES ('%s', '%s', '%s');", login, password, nickname));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
//
    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void changeNick(String oldNick ,String newNick){
        try {
            stmt.executeUpdate("UPDATE users SET nickname = '"+ newNick+"' WHERE nickname = '" + oldNick + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public  static List<String> getFriends(String nickname){
        List<String> str = new ArrayList<>();
        try {
            ResultSet rs = stmt.executeQuery("SELECT friend.nickname FROM users AS user JOIN users AS friend " +
                    "JOIN friends ON user.id = friends.user_id AND user.nickname = '" + nickname + "' AND friend.id = friends.friend_id;");
            while (rs.next()){
                str.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return str;
    }
}
