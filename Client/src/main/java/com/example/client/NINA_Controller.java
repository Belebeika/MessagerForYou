package com.example.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ResourceBundle;

import com.example.client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class NINA_Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane Field;

    @FXML
    private Text Name_app;

    @FXML
    private Text Reload_title;

    @FXML
    private AnchorPane Up_Field;

    @FXML
    private Button button_reload;

    @FXML
    void Reload_page(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert Field != null : "fx:id=\"Field\" was not injected: check your FXML file 'Default_scene.fxml'.";
        assert Name_app != null : "fx:id=\"Name_app\" was not injected: check your FXML file 'Default_scene.fxml'.";
        assert Reload_title != null : "fx:id=\"Reload_title\" was not injected: check your FXML file 'Default_scene.fxml'.";
        assert Up_Field != null : "fx:id=\"Up_Field\" was not injected: check your FXML file 'Default_scene.fxml'.";
        assert button_reload != null : "fx:id=\"button_reload\" was not injected: check your FXML file 'Default_scene.fxml'.";

    }

    @FXML
    public void Reloadpage() {
        if (netIsAvailable()){
            Stage stage = (Stage) Field.getScene().getWindow();
            Scene parent_scene = Field.getScene();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Login_scene.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), parent_scene.getWidth(), parent_scene.getHeight());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setScene(scene);
        }
        Reload_title.setText("Всё ещё не подключены(");
    }

    private static boolean netIsAvailable() {
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
}
