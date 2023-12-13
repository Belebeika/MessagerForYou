package com.example.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SearchFriend_Controller {

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
    private Hyperlink back;

    @FXML
    void SearchFriend(ActionEvent event) {
        System.out.println(event.getEventType().toString());
    }

    @FXML
    void backmainscene(ActionEvent event) {
        Childscene("Main_scene.fxml");
    }

    @FXML
    void initialize() {
        assert Field != null : "fx:id=\"Field\" was not injected: check your FXML file 'SearchFriend_scene.fxml'.";
        assert Name_app != null : "fx:id=\"Name_app\" was not injected: check your FXML file 'SearchFriend_scene.fxml'.";
        assert Up_Field != null : "fx:id=\"Up_Field\" was not injected: check your FXML file 'SearchFriend_scene.fxml'.";

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
