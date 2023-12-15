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
        stmt.execute("CREATE TABLE if not exists 'messages' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'author_id' INTEGER NOT NULL, 'address_id' INTEGER NOT NULL, 'message' text NOT NULL, is_read INTEGER NOT NULL, FOREIGN KEY('author_id') REFERENCES 'users' (id), FOREIGN KEY('address_id') REFERENCES 'users' (id));");


        System.out.println("Таблицы созданы или уже существуют.");
    }

    public static void addmessage(String author, String address, String text) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT user.id, friend.id FROM " +
                "users AS user JOIN users AS friend ON friend.nickname = '" + address + "' AND user.nickname = '" + author + "';");
        if (rs.next()){
            stmt.executeUpdate("INSERT INTO messages (author_id, address_id, text, is_read) VALUES ('" + rs.getString(1) + "', '" + rs.getString(2) + "', '" + text + "', 0);");
        }
    }

    public static boolean checknick(String nick) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT id FROM users WHERE nickname = '" + nick + "';");
        if(rs.next()){
            return true;
        }
        return false;
    }

    public  static void addfriend(String usernick, String friendnick) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT user.id, friend.id FROM " +
                "users AS user JOIN users AS friend ON friend.nickname = '" + friendnick + "' AND user.nickname = '" + usernick + "';");
        ResultSet check = stmt.executeQuery("SELECT id FROM friends WHERE user_id = '" + rs.getString(1) + "' AND friend_id = '" + rs.getString(2) + "';");
        if (!check.next()){
            ResultSet rs1 = stmt.executeQuery("SELECT user.id, friend.id FROM " +
                    "users AS user JOIN users AS friend ON friend.nickname = '" + friendnick + "' AND user.nickname = '" + usernick + "';");
            if (rs1.next()) {
                stmt.executeUpdate("INSERT INTO friends (user_id, friend_id) VALUES ('" + rs1.getString(1) + "', '" + rs1.getString(2) + "');");
            }
            ResultSet rs2 = stmt.executeQuery("SELECT user.id, friend.id FROM " +
                    "users AS user JOIN users AS friend ON friend.nickname = '" + friendnick + "' AND user.nickname = '" + usernick + "';");
            if (rs2.next()) {
                stmt.executeUpdate("INSERT INTO friends (user_id, friend_id) VALUES ('" + rs2.getString(2) + "', '" + rs2.getString(1) + "');");
            }
        }
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
