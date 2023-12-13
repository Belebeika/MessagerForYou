package com.example.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main_Controller {

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
        user_nick.setText("Ваше имя: " + Main.Nick);
        assert Field != null : "fx:id=\"Field\" was not injected: check your FXML file 'Main_scene.fxml'.";
        assert Name_app != null : "fx:id=\"Name_app\" was not injected: check your FXML file 'Main_scene.fxml'.";
        assert Up_Field != null : "fx:id=\"Up_Field\" was not injected: check your FXML file 'Main_scene.fxml'.";
        assert button_send != null : "fx:id=\"button_send\" was not injected: check your FXML file 'Main_scene.fxml'.";
        assert logout_link != null : "fx:id=\"logout_link\" was not injected: check your FXML file 'Main_scene.fxml'.";
        assert message != null : "fx:id=\"message\" was not injected: check your FXML file 'Main_scene.fxml'.";
        assert search_link != null : "fx:id=\"search_link\" was not injected: check your FXML file 'Main_scene.fxml'.";
        assert vbox_your_chat != null : "fx:id=\"vbox_your_chat\" was not injected: check your FXML file 'Main_scene.fxml'.";

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
