package ru.geekbrains.server;

import ru.geekbrains.messages.MessageDTO;
import ru.geekbrains.messages.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {
    private final String MESS_PREFIX = "ClientPort ";
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private ChatServer chatServer;
    private int id = -1;
    private String currentUserName = "";
    private MessDTO mess = new MessDTO();

    private void log(String logStr) {System.out.println(MESS_PREFIX + socket.getPort() + " > " +logStr);}
    private void log(MessageDTO mess, boolean flgReceive) {
        log((flgReceive ? "receive " : "send ") + mess.getMessageType());
    }

    private ExecutorService threadService = Executors.newFixedThreadPool(2);
    private Runnable taskAuthTimeout = new TimerCloseConnection();
    private Runnable taskAuth = new Runnable() {
        @Override
        public void run() {
            authenticate();
        }
    };
    private Runnable taskReceiveMessage = new Runnable() {
        @Override
        public void run() {
            receiveMessages();
        }
    };

    public ClientHandler(Socket socket, ChatServer chatServer) {
        try {
            this.chatServer = chatServer;
            this.socket = socket;
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            log("ClientPort created");

            threadService.execute(taskAuthTimeout);
            threadService.execute(taskAuth);
            threadService.execute(taskReceiveMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authenticate() {
        log("Waiting for authentication...");
        try {
            while (true) {
                String authMessage = inputStream.readUTF();
                MessageDTO dto = MessageDTO.convertFromJson(authMessage);
                
                log(dto, true);
                
                if (dto.getMessageType() != MessageType.SEND_AUTH_MESSAGE) {
                    sendMessage(mess.getError("Error message type! Receive: " + dto.getMessageType()));
                    closeHandler();
                    break;
                } else {
                    mess.setAuth(dto);
                }

                if (currentUserName != "") {
                    chatServer.subscribe(this);
                    break;
                }
            }
        } catch (IOException e) {
            closeHandler();
        }
    }
    private void receiveMessages() {
        try {
            while (!Thread.currentThread().isInterrupted() || socket.isConnected()) {
                String msg = inputStream.readUTF();
                MessageDTO dto = MessageDTO.convertFromJson(msg);

                log(dto, true);
                if (dto.getMessageType() == MessageType.DISCONNECT_MESSAGE) {
                    log(dto.getBody());
                }
                mess.set(dto);
            }
        } catch (IOException e) {
            Thread.currentThread().interrupt();
        } finally {
            closeHandler();
        }
    }
    public void sendMessage(MessageDTO dto) {
        try {
            log(dto, false);
            outputStream.writeUTF(dto.convertToJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendClientList(List<String> list) {
        sendMessage(mess.getList(list));
    }

    synchronized public void closeHandler() {
        try {
            if (socket.isClosed()) return;
            chatServer.unsubscribe(this);
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            socket.close();
            log("Connection closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentUserName() {return currentUserName;}
    public int getId() {return id;}

    class MessDTO {
        void set(MessageDTO dto) {
            switch (dto.getMessageType()) {
                case PUBLIC_MESSAGE :
                    setMess(dto);
                    break;
                case PRIVATE_MESSAGE :
                    setMess(dto);
                    break;
                case AUTH_CONFIRM :
                    setAuth(dto);
                    break;
                case CHANGE_NICK :
                    setNick(dto);
                    break;
                case DISCONNECT_MESSAGE :
                    setDisconn(dto);
                    break;
            }
        }
        void setAuth(MessageDTO dto) {
            int id = chatServer.getLoginRepository().getId(dto.getLogin(), dto.getPassword());

            if (id == -1) {
                sendMessage(mess.getError("Wrong login or pass!"));
            } else if (chatServer.isUserBusy(id)) {
                sendMessage(mess.getError("U're clone!"));
            } else {
                ru.geekbrains.server.ClientHandler.this.id = id;
                ru.geekbrains.server.ClientHandler.this.currentUserName = chatServer.getLoginRepository().getNickById(id);
                sendMessage(mess.getAuth());
            }
        }
        void setMess(MessageDTO dto) {
            dto.setFrom(currentUserName);
            switch (dto.getMessageType()) {
                case PUBLIC_MESSAGE :
                    chatServer.sendPublicMessage(dto);
                    break;
                case PRIVATE_MESSAGE :
                    chatServer.sendPrivateMessage(dto);
                    break;
            }
        }
        void setNick(MessageDTO dto) {
            if (chatServer.getLoginRepository().setNickForId(id, dto.getBody()))
                sendMessage(mess.getDisconn("Nick changed! Need reconnect!"));
            else
                sendMessage(mess.getError("Nick busy!"));
        }
        void setDisconn(MessageDTO dto) {
            closeHandler();
        }

        MessageDTO getAuth() {
            MessageDTO dto = new MessageDTO();
            dto.setMessageType(MessageType.AUTH_CONFIRM);
            dto.setBody(currentUserName);

            return dto;
        }
        MessageDTO getList(List<String> list) {
            MessageDTO dto = new MessageDTO();
            dto.setMessageType(MessageType.CLIENTS_LIST_MESSAGE);
            dto.setUsersOnline(list);

            return dto;
        }
        MessageDTO getDisconn(String cause) {
            MessageDTO dto = new MessageDTO();
            dto.setMessageType(MessageType.DISCONNECT_MESSAGE);
            dto.setBody(cause);

            return dto;
        }
        MessageDTO getError(String cause) {
            MessageDTO dto = new MessageDTO();
            dto.setMessageType(MessageType.ERROR_MESSAGE);
            dto.setBody(cause);

            return dto;
        }
    }

    class TimerCloseConnection implements Runnable{
        private final int WAIT_TIME = 60_000;
        @Override
        synchronized public void run() {
            try {
                wait(WAIT_TIME);
                if (id == -1) {
                    sendMessage(mess.getDisconn("Timeout: " + (WAIT_TIME / 1_000) + " sec"));
                    closeHandler();
                }
            } catch (InterruptedException e) {}
        }
    }
}
