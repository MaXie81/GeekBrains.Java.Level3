package network;

import ru.geekbrains.messages.MessageDTO;
import ru.geekbrains.messages.MessageType;

import java.io.IOException;

public class ChatMessageService implements MessageService {
    private final String MESS_PREFIX = "ClientPort ";
    private String host;
    private int port;
    private NetworkService networkService = null;
    private MessageProcessor processor = null;

    private void log(String logStr) {System.out.println(MESS_PREFIX + networkService.getPort() + " > " +logStr);}
    private void log(MessageDTO mess, boolean flgReceive) {
        log((flgReceive ? "receive " : "send ") + mess.getMessageType());
    }

    public ChatMessageService(String host, int port, MessageProcessor processor) {
        this.host = host;
        this.port = port;
        this.processor = processor;
//        connectToServer();
    }

    @Override
    public void connectToServer() {
        try {
            try {
                if (networkService.getPort() != -1) return;
            } catch (Exception e) {}
            networkService = new NetworkService(host, port, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnectFromServer() {
        networkService.closeConnection();
    }

    @Override
    public void sendMessage(MessageDTO dto) {
        if (dto == null) return;

        log(dto, false);
        if (dto.getMessageType() == MessageType.SEND_AUTH_MESSAGE) {
            log(dto.getLogin() + "/" + dto.getPassword());
        }
        if (dto.getMessageType() == MessageType.CHANGE_NICK) {
            log("New nick: " +dto.getBody());
        }
        networkService.writeMessage(dto.convertToJson());
    }

    @Override
    public void receiveMessage(String msg) {
        MessageDTO dto = MessageDTO.convertFromJson(msg);
        log(dto, true);
        if (dto.getMessageType() == MessageType.ERROR_MESSAGE) {
            log(dto.getBody());
        }
        if (dto.getMessageType() == MessageType.DISCONNECT_MESSAGE) {
            log(dto.getBody());
        }
        processor.receiveMessage(dto);
    }
}
