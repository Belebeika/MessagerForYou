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

public class Registration_Controller {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

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
    private Hyperlink login_link;

    @FXML
    private TextField nickname_field;

    @FXML
    private PasswordField password_field;

    @FXML
    void login(ActionEvent event) {
        Childscene("Login_scene.fxml");
    }

    @FXML
    void registration(ActionEvent event) {
        if (!Main.netIsAvailable()){
            Childscene("NetIsNotAvaible_scene.fxml");
        } else {
            try {
                if (socket == null || socket.isClosed()) {
                    socket = new Socket("10.23.2.161", 8189);
                    in = new DataInputStream(socket.getInputStream());
                    out = new DataOutputStream(socket.getOutputStream());
                    if (checkCombination(login_field.getText()) && checkCombination(nickname_field.getText())) {
                        out.writeUTF("/registration " + login_field.getText() + " " + sha256(password_field.getText()) + " " + nickname_field.getText());
                        System.out.println("Отправили в регу данные и ;ltv jndtnf");
                        String answer = in.readUTF();
                        if (answer == "Регистрация прошла успешно") {
                            Main.Nick = nickname_field.getText();
                            Childscene("Main_scene.fxml");
                        }
                    } else System.out.println("Использованы недопустимые символы");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
    //
    private boolean checkCombination(String comb){
        char[] arr = comb.toCharArray();
        char[] arrEx = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','_','-','.'};
        if(arr.length>13) return false;
        if(arr[0]=='.') return false;
        outer: for(char o : arr){
            for(char p : arrEx ){
                if(o==p) continue outer;
            }
            return false;
        }
        return true;
    }

    @FXML
    void initialize() {
        assert Field != null : "fx:id=\"Field\" was not injected: check your FXML file 'Login_scene.fxml'.";
        assert Name_app != null : "fx:id=\"Name_app\" was not injected: check your FXML file 'Login_scene.fxml'.";
        assert Up_Field != null : "fx:id=\"Up_Field\" was not injected: check your FXML file 'Login_scene.fxml'.";
        assert button_login != null : "fx:id=\"button_login\" was not injected: check your FXML file 'Login_scene.fxml'.";
        assert login_field != null : "fx:id=\"login_field\" was not injected: check your FXML file 'Login_scene.fxml'.";
        assert login_link != null : "fx:id=\"login_link\" was not injected: check your FXML file 'Login_scene.fxml'.";
        assert nickname_field != null : "fx:id=\"nickname_field\" was not injected: check your FXML file 'Login_scene.fxml'.";
        assert password_field != null : "fx:id=\"password_field\" was not injected: check your FXML file 'Login_scene.fxml'.";

    }

    public void Childscene(String fxml){
        Stage stage = (Stage) Field.getScene().getWindow();
        Scene parent_scene = Field.getScene();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), parent_scene.getWidth(), parent_scene.getHeight());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(scene);
    }
}
