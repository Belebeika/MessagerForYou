package com.example.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.*;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class Main_Controller {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;
    private String address_nickname;
    public Thread thread;

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
    private VBox chat;

    @FXML
    void OpenChatWithYourself(MouseEvent event) {

    }

    void AddYourMess(String mg){
        HBox hb = new HBox();
        hb.getStyleClass().add("Hbox-your-mess");
        hb.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        TextFlow flow = new TextFlow();
        flow.getStyleClass().add("Flow-your-mess");
        flow.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        Text mess = new Text();
        mess.getStyleClass().add("Mess");
        mess.setText(mg);
        flow.getChildren().add(mess);
        hb.getChildren().add(flow);
        chat.getChildren().add(hb);
    }

    void AddFriendMess(String mg){
        HBox hb = new HBox();
        hb.getStyleClass().add("Hbox-friend-mess");
        TextFlow flow = new TextFlow();
        flow.getStyleClass().add("Flow-friend-mess");
        Text mess = new Text();
        mess.getStyleClass().add("Mess");
        mess.setText(mg);
        flow.getChildren().add(mess);
        hb.getChildren().add(flow);
        chat.getChildren().add(hb);
    }
    void OpenChat(){
//        Text text = new Text();
//        text.setText(address_nickname);
//        chat.getChildren().add(text);
        thread = new Thread(() -> {
            try {
                sendMsg("/getchat " + address_nickname);
                while (true) {
                    String str = in.readUTF();
                    System.out.println(str);
                    if (str.startsWith("/chatok")){
                        String msg = new String();
                        String[] b = str.split(" ");
                        ArrayList<String> msgs= new ArrayList<>(Arrays.asList(b));
                        msgs.remove(0);
                        for (String o: msgs){
                            if (!Objects.equals(o, "0") && !Objects.equals(o, "1")){
                                if (msg != null){
                                    msg += " " + o;
                                }
                                else{
                                    msg = o;
                                }
                            }
                            else{
                                if (o.equals("1")){
                                    String finalMsg = msg;
                                    msg = null;
                                    Platform.runLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            AddYourMess(finalMsg);
                                        }
                                    });
                                }
                                else{
                                    String finalMsg1 = msg;
                                    msg = null;
                                    Platform.runLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            AddFriendMess(finalMsg1);
                                        }
                                    });                                }
                            }
                        }
                    }
                    else if(str.equals("/overthread")){
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @FXML
    void SendMessage(ActionEvent event) {
        if (address_nickname != null && !message.getText().isEmpty()){
            String[] a = message.getText().split("");
            String msg = "";
            for (int i = 0; i < a.length; i++){
                if (!(a[i].equals(" ") && msg.equals(""))){
                    msg += a[i];
                }
            }
            sendMsg("/addmess " + address_nickname  + " " + msg);
            AddYourMess(msg);
            message.clear();
        }
    }

    @FXML
    void logout(ActionEvent event) throws IOException {
        if (thread != null){
            sendMsg("/unconnect");
            while(thread.isAlive()){
            }
        }
        sendMsg("/end");
        Childscene("Login_scene.fxml");
    }

    @FXML
    void searchfriend(ActionEvent event) {
        if (thread != null){
            sendMsg("/unconnect");
            while(thread.isAlive()){
            }
        }
        Childscene("SearchFriend_scene.fxml");
    }

    void connect(){
        user_nick.setText(Main.Nick);
        this.socket=Main.socket;
        this.in=Main.in;
        this.out=Main.out;
        this.nickname=Main.Nick;
        try {
            sendMsg("/friends");
            String str = in.readUTF();
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
                    hb.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            Label lable = (Label) hb.getChildren().getFirst();
                            address_nickname = lable.getText();
                            chat.getChildren().clear();
                            if (thread != null){
                                sendMsg("/unconnect");
                                while(thread.isAlive()){
                                }
                            }
                            OpenChat();
                        }
                    });
                    Platform.runLater(() -> friends_box.getChildren().add(hb));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        connect();
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
