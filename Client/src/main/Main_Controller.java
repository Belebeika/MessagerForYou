package com.example.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main_Controller {

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
    private Button button_send;

    @FXML
    private Hyperlink logout_link;

    @FXML
    private TextField message;

    @FXML
    private Hyperlink search_link;

    @FXML
    private Hyperlink user_nick;

    @FXML
    private HBox vbox_your_chat;

    @FXML
    private VBox friends_box;

    @FXML
    void OpenChatWithYourself(MouseEvent event) {

    }

    @FXML
    void SendMessage(ActionEvent event) {

    }

    @FXML
    void logout(ActionEvent event) {
        Childscene("Login_scene.fxml");
    }

    @FXML
    void searchfriend(ActionEvent event) {
        Childscene("SearchFriend_scene.fxml");
    }

    @FXML
    void initialize() {
        user_nick.setText(Main.Nick);
        this.socket=Main.socket;
        this.in=Main.in;
        this.out=Main.out;
        this.nickname=Main.Nick;
        if (true) {
            new Thread(() -> {
                try {
                    sendMsg("/friends");
                    while (true) {
                        String str = in.readUTF();
                        StringBuilder temp = new StringBuilder();
                        String[] tokens = str.split("\\s");
                        for(int i = 1; i<tokens.length; i++){
                            temp.append(tokens[i]).append(" ");
                        }
                        if (str.startsWith("/friendsok")){
                            String[] b = str.split(" ");
                            ArrayList<String> friends= new ArrayList<>(Arrays.asList(b));
                            friends.remove(0);
                            for (String o: friends){
                                HBox hb = new HBox();
                                Label lb = new Label();
                                lb.setText(o);
                                lb.getStyleClass().add("Label-Friends");
                                hb.getStyleClass().add("HBox-Friends");
                                hb.getChildren().add(lb);
                                hb.getStyleClass().add("Hbox-Friends");
                                Platform.runLater(() -> friends_box.getChildren().add(hb));
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                }
            }).start();
        }
        assert Field != null : "fx:id=\"Field\" was not injected: check your FXML file 'Main_scene.fxml'.";
        assert Name_app != null : "fx:id=\"Name_app\" was not injected: check your FXML file 'Main_scene.fxml'.";
        assert Up_Field != null : "fx:id=\"Up_Field\" was not injected: check your FXML file 'Main_scene.fxml'.";
        assert button_send != null : "fx:id=\"button_send\" was not injected: check your FXML file 'Main_scene.fxml'.";
        assert logout_link != null : "fx:id=\"logout_link\" was not injected: check your FXML file 'Main_scene.fxml'.";
        assert message != null : "fx:id=\"message\" was not injected: check your FXML file 'Main_scene.fxml'.";
        assert search_link != null : "fx:id=\"search_link\" was not injected: check your FXML file 'Main_scene.fxml'.";
        assert vbox_your_chat != null : "fx:id=\"vbox_your_chat\" was not injected: check your FXML file 'Main_scene.fxml'.";

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
