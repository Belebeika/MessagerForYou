package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            System.out.println(socket.getInetAddress().toString());
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        // /auth login1 pass1
                        if (str.startsWith("/auth ")) {
                            System.out.println("[INFO] Попытка авторизации от: "+socket);
                            String[] tokens = str.split("\\s");
                            if (tokens.length == 3) {
                                System.out.println("[INFO] Проверка данных в БД: "+socket);
                                String nickFromDB = SQLHandler.getNickByLoginAndPassword(tokens[1], tokens[2]);
                                if (nickFromDB != null) {
                                    if (!server.isNickBusy(nickFromDB)) {
                                        sendMsg("/authok " + nickFromDB);
                                        nickname = nickFromDB;
                                        System.out.println("[INFO] Успешная авторизация: "+nickname+"; "+socket);
                                        break;
                                    } else {
                                        sendMsg("Учетная запись уже используется");
                                        System.out.println("[WARNING] Учетная запись уже используется: "+socket);
                                    }
                                } else {
                                    sendMsg("Неверный логин/пароль");
                                    System.out.println("[WARNING] Неверные данные авторизиции(логин,пароль): "+socket);
                                }
                            } else {
                                sendMsg("Неверный формат данных авторизации");
                                System.out.println("[WARNING] Комманда разбита более или менее чем на три части: "+socket);
                            }
                        }
                        if (str.startsWith("/registration ")) {
                            System.out.println("[INFO] Попытка регистрации: "+socket);
                            String[] tokens = str.split("\\s");
                            if (tokens.length == 4) {
                                if (SQLHandler.tryToRegister(tokens[1], tokens[2], tokens[3])) {
                                    sendMsg("Регистрация прошла успешно");
                                    nickname = tokens[3];
                                    System.out.println("[INFO] Регистрация прошла успешно: "+socket);
                                    break;
                                } else {
                                    sendMsg("Логин или ник уже заняты");
                                    System.out.println("[WARNING] : Логин или ник уже заняты: "+socket);
                                }
                            }
                        }
                    }
                    while (true) {
                        System.out.println("[INFO] Ожидание сообщения от: "+nickname+"; "+socket);
                        String str = in.readUTF();
                        System.out.println("[INFO] Сообщение от : " + nickname + ": " + str+"; "+socket);
                        if(str.equals("/authok")) {
                            System.out.println("[INFO] Клиент ответил об успешной авторизации: "+nickname+"; "+socket);}

                    if (!str.startsWith("/")) {
                            System.out.println("[INFO] Клиент "+nickname+" оправил всем сообщение: \""+str+"\"; "+socket);

                    } else {
                            if (str.equals("/end")) {
                                System.out.println("[INFO] Клиент завершил сессию: "+nickname+"; "+socket);
                                server.unsubscribe(this);
                                break;
                            }
                            if (str.startsWith("/w ")) {
                                // /w nick2 hello java
                                String[] tokens = str.split("\\s", 3);
                                if (tokens.length == 3) {
                                    server.personalMsg(this, tokens[1], tokens[2]);
                                    System.out.println("[INFO] Клиент "+nickname+" отправил личное сообщение "+tokens[1]+": "+str+"; "+socket);
                                } else {
                                    sendMsg("/log Неверный формат личного сообщения");
                                    System.out.println("[WARNING] Неверный формат личного cообщения(/w): "+nickname+"; "+socket);
                                }
                            }
                            if (str.startsWith("/cn ")){
                                // /changenick newNick
                                System.out.println("[INFO] Клиент отправил запрос на смену nickname: "+nickname+"; "+socket);
                                String[] tokens = str.split("\\s", 3);
                                if (tokens.length == 2) {
                                    if(server.isNickBusy(tokens[1])){ sendMsg("/log Никнейм уже занят");
                                    System.out.println("[WARNING] Nickname на смену уже занят: "+nickname+"; "+socket);
                                    }
                                    else{ SQLHandler.changeNick(nickname ,tokens[1]);
                                        System.out.println("[INFO] Клиент "+nickname+" сменил ник на "+tokens[1]+": "+socket);
                                          nickname=tokens[1];
                                        sendMsg("/cn "+nickname);

                                    }
                                } else {
                                    sendMsg("/log Неверный формат сообщения");
                                    System.out.println("[WARNING] Неверный формат смены ника(/cn): "+nickname+"; "+socket);
                                }
                            }
                            if (str.equals("/friends")){
                                System.out.println("[INFO] Клиент отправил запрос на друзей: "+nickname+"; "+socket);
                                List<String> friends = SQLHandler.getFriends(nickname);
                                String msg = "/friendsok";
                                for (String o: friends){
                                    msg += " " + o;
                                }
                                sendMsg(msg);
                            }
                            if (str.startsWith("/checknick ")){
                                System.out.println("[INFO] Клиент отправил запрос на имя: "+nickname+"; "+socket);
                                String[] name = str.split(" ");
                                String msg = new String();
                                if (SQLHandler.checknick(name[1])){
                                    msg = "Друг успешно найден";
                                }
                                else{
                                    msg = "Пользователя с таким ником не существует";
                                }
                                sendMsg(msg);
                            }
                            if (str.startsWith("/addfriend ")){
                                System.out.println("[INFO] Клиент отправил запрос на добавление друга: "+nickname+"; "+socket);
                                String[] name = str.split(" ");
                                SQLHandler.addfriend(getNickname(), name[1]);
                                sendMsg("Добавил");
                            }
                            if (str.startsWith("/getchat ")){
                                System.out.println("[INFO] Клиент отправил запрос на получение сообщений: "+nickname+"; "+socket);
                                String[] name = str.split(" ");
                                List<List<String>> friends = SQLHandler.getchat(getNickname(), name[1]);
                                String msg = "/chatok";
                                for (List<String> o: friends){
                                    msg += " " + o.get(0) + " " + o.get(1);
                                }
                                server.subscribe(this);
                                sendMsg(msg);
                            }
                            if (str.startsWith("/addmess ")){
                                System.out.println("[INFO] Клиент отправил запрос на отправление сообщения: "+nickname+"; "+socket);
                                String[] name = str.split(" ");
                                System.out.println(name[1] + " " +  name[2]);
                                String msg = name[2];
                                for (int i = 3; i < Arrays.stream(name).count(); i++){
                                    msg += " " + name[i];
                                }
                                SQLHandler.addmessage(getNickname(), name[1], msg);
                                server.personalMsg(this, name[1], name[2]);
                            }
                            if (str.equals("/unconnect")){
                                server.unsubscribe(this);
                                sendMsg("/overthread");
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error CH#001");
                    e.printStackTrace();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        System.out.println("Error CH#002");
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        System.out.println("Error CH#003");
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.out.println("Error CH#004");
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//
    public void sendMsg(String msg) {
        try {
            System.out.println(socket+"\n"+msg);
            out.writeUTF(msg);
        } catch (IOException e) {
            System.out.println("Error CH#005");
            e.printStackTrace();
        }
    }
}
