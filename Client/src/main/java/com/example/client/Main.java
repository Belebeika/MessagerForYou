package com.example.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

public class Main extends Application {
    public static String IP = "10.23.4.28";
    public static Socket socket;
    public static DataInputStream in;
    public static DataOutputStream out;
    public static String Nick;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Login_scene.fxml"));
        if (!netIsAvailable()){
            fxmlLoader = new FXMLLoader(Main.class.getResource("NetIsNotAvaible_scene.fxml"));
        }
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static boolean netIsAvailable() {
        try {
            final URL url = new URL("http://www.google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void stop(){
        if (socket != null){
            sendMsg("/end");
        }
    }

    private void sendMsg(String msg) {
        try {
            if (socket != null && !socket.isClosed()) {
                if (!msg.isEmpty()) {
                    out.writeUTF(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch();
    }
}