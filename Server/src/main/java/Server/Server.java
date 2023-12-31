package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server {
    private Vector<ClientHandler> clients;

    Server() {
        try {
            ServerSocket serverSocket = new ServerSocket(8189);
            clients = new Vector<>();
            SQLHandler.connect();
            SQLHandler.CreateDB();
            while (true) {
                System.out.println("Ждем подключения клиента");
                Socket socket = serverSocket.accept();
                ClientHandler c = new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            System.out.println("Error Se#001");
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            SQLHandler.disconnect();
        }
    }
//


    public void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNickname().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public void personalMsg(ClientHandler from, String to, String msg) {
        for (ClientHandler o : clients) {
            if(o.getNickname().equals(to)) {
                o.sendMsg("from " + from.getNickname() + ": " + msg);
                from.sendMsg("to " + to + ": " + msg);
                return;
            }
        }
        from.sendMsg("/log "+"Клиента с ником " + to + " нет в чате");
    }

}
