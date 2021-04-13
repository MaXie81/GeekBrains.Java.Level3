package ru.geekbrains.server;

import lombok.extern.slf4j.Slf4j;
import ru.geekbrains.messages.MessageDTO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
@Slf4j
public class ChatServer {
    private static final String MESS_PREFIX = "Server ";
    private static final int PORT = 65500;
    private List<ClientHandler> onlineClientsList;
    private LoginRepository loginRepository;

    public ChatServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log.info(MESS_PREFIX + "> started");
            loginRepository = new LoginRepository();
            onlineClientsList = new LinkedList<>();
            while (true) {
                log.trace(MESS_PREFIX + "> Waiting for connection...");
                Socket socket = serverSocket.accept();
                log.info(MESS_PREFIX + "< Client from port " + socket.getPort() + " connected");
                new ClientHandler(socket, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler c) {
        onlineClientsList.add(c);
        broadcastOnlineClients();
    }

    public synchronized void unsubscribe(ClientHandler c) {
        if (onlineClientsList.remove(c))
            broadcastOnlineClients();
        log.info(MESS_PREFIX + "< Client from port " + c.getPort() + " disconnected");
    }

    public LoginRepository getLoginRepository() {
        return loginRepository;
    }

    public synchronized void sendPrivateMessage(MessageDTO dto) {
        for (ClientHandler clientHandler : onlineClientsList) {
            if (clientHandler.getCurrentUserName().equals(dto.getTo())) {
                clientHandler.sendMessage(dto);
                break;
            }
        }
    }
    public synchronized void sendPublicMessage(MessageDTO dto) {
        for (ClientHandler clientHandler : onlineClientsList) {
            clientHandler.sendMessage(dto);
        }
    }

    public synchronized void broadcastOnlineClients() {
        List<String> onlines = new LinkedList<>();
        for (ClientHandler clientHandler : onlineClientsList) {
            onlines.add(clientHandler.getCurrentUserName());
        }
        for (ClientHandler clientHandler : onlineClientsList) {
            clientHandler.sendClientList(onlines);
        }
    }

    public synchronized boolean isUserBusy(int id) {
        for (ClientHandler clientHandler : onlineClientsList) {
            if (clientHandler.getId() == id) return true;
        }
        return false;
    }
}
