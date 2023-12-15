package com.example.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SearchFriend_Controller {

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
    private TextField SearchField;

    @FXML
    private AnchorPane Up_Field;

    @FXML
    private Button add_friend;

    @FXML
    private Text answer;

    @FXML
    private StackPane MainPane;

    @FXML
    private Hyperlink back;

    @FXML
    void SearchFriend(){
        try {
            String str = in.readUTF();
            System.out.println(str);
            StringBuilder temp = new StringBuilder();
            String[] tokens = str.split("\\s");
            for(int i = 1; i<tokens.length; i++){
                temp.append(tokens[i]).append(" ");
            }
            if (str.startsWith("Друг ")){
                String nick = SearchField.getText();
                Platform.runLater(() -> answer.setText(str + ": " + SearchField.getText()));
                Button btn = new Button();
                btn.setText("Добавить в друзья");
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        sendMsg("/addfriend " + nick);
                        SearchFriend();
                    }
                });
                MainPane.setMargin(btn, new Insets(220, 0, 0, 0));
                Platform.runLater(() -> MainPane.getChildren().add(btn));
            }
            else if (str.startsWith("Пользователя ")){
                Platform.runLater(() -> answer.setText(str));
            }
            else if (str.equals("Добавил")){
                Platform.runLater(() -> Childscene("Main_scene.fxml"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void SearchFriend(ActionEvent event) {
        sendMsg("/checknick " + SearchField.getText());
        SearchFriend();
    }

    @FXML
    void backmainscene(ActionEvent event) throws IOException {
        Childscene("Main_scene.fxml");
    }

    @FXML
    void initialize() {
        this.socket=Main.socket;
        this.in=Main.in;
        this.out=Main.out;
        this.nickname=Main.Nick;
        assert Field != null : "fx:id=\"Field\" was not injected: check your FXML file 'SearchFriend_scene.fxml'.";
        assert Name_app != null : "fx:id=\"Name_app\" was not injected: check your FXML file 'SearchFriend_scene.fxml'.";
        assert Up_Field != null : "fx:id=\"Up_Field\" was not injected: check your FXML file 'SearchFriend_scene.fxml'.";

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
}
