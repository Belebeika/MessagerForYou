package com.example.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Login_Controller {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane Field;

    @FXML
    private Text Name_app;

    @FXML
    private AnchorPane Up_Field;

    @FXML
    private Button button_login;

    @FXML
    private TextField login_field;

    @FXML
    private PasswordField password_field;

    @FXML
    private Hyperlink registration_link;

    @FXML
    void login(ActionEvent event) {
        if (!Main.netIsAvailable()){
            Childscene("NetIsNotAvaible_scene.fxml");
        } else {
            try {
                if (socket == null || socket.isClosed()) {
                    socket = new Socket("10.23.2.161", 8189);
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());
                }
                sendAuth();
                String str = in.readUTF();
                if (str.startsWith("/authok ")) {
                    nickname = str.split("\\s")[1];
                    System.out.println(nickname);
                    Main.socket = socket;
                    Main.in = in;
                    Main.out = out;
                    Main.Nick = nickname;
                    System.out.println(5);   //////////////
                    Childscene("Main_scene.fxml");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void registration(ActionEvent event) {
        Childscene("Registration_scene.fxml");
    }

    @FXML
    void initialize() {
        assert Field != null : "fx:id=\"Field\" was not injected: check your FXML file 'Login_scene.fxml'.";
        assert Name_app != null : "fx:id=\"Name_app\" was not injected: check your FXML file 'Login_scene.fxml'.";
        assert Up_Field != null : "fx:id=\"Up_Field\" was not injected: check your FXML file 'Login_scene.fxml'.";
        assert button_login != null : "fx:id=\"button_login\" was not injected: check your FXML file 'Login_scene.fxml'.";
        assert login_field != null : "fx:id=\"login_field\" was not injected: check your FXML file 'Login_scene.fxml'.";
        assert password_field != null : "fx:id=\"password_field\" was not injected: check your FXML file 'Login_scene.fxml'.";
        assert registration_link != null : "fx:id=\"registration_link\" was not injected: check your FXML file 'Login_scene.fxml'.";

    }

    private void sendAuth() {
        // /auth login pass
        sendMsg("/auth " + login_field.getText() + " " + sha256(password_field.getText()));
        login_field.clear();
        password_field.clear();
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

    private String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public void Childscene(String fmxl){
        Stage stage = (Stage) Field.getScene().getWindow();
        Scene parent_scene = Field.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fmxl));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), parent_scene.getWidth(), parent_scene.getHeight());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(scene);
    }
}

