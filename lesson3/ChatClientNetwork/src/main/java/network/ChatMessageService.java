package network;

import ru.geekbrains.messages.MessageDTO;
import ru.geekbrains.messages.MessageType;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ChatMessageService implements MessageService {
    private final String MESS_PREFIX = "ClientPort ";
    private String host;
    private int port;
    private NetworkService networkService = new NetworkService();
    private MessageProcessor processor = null;
    public LogService log = null;

//    private void get(String logStr) {System.out.println(MESS_PREFIX + portLocal + " > " +logStr);}
//    private void get(MessageDTO mess, boolean flgReceive) {
//        get((flgReceive ? "receive " : "send ") + mess.getMessageType());
//    }

    public ChatMessageService(String host, int port, MessageProcessor processor) {
        this.host = host;
        this.port = port;
        this.processor = processor;
//        connectToServer();
    }

    @Override
    public void connectToServer() {
        int port;
        port = networkService.openConnection(this.host, this.port, this);
        if (port != -1)
            log = new LogService(port);
    }

    @Override
    public void disconnectFromServer() {
        networkService.closeConnection();
        log.close();
        log = null;
    }

    @Override
    public void sendMessage(MessageDTO dto) {
        if (dto == null) return;

        log.get(dto, false);

        networkService.writeMessage(dto.convertToJson());
    }

    @Override
    public void receiveMessage(String msg) {
        MessageDTO dto = MessageDTO.convertFromJson(msg);

        log.get(dto, true);

        processor.receiveMessage(dto);
    }
    @Override
    public void getChatHistory() {
        ListIterator<String> li = log.getLogBuf().listIterator();
        while (li.hasNext()) {
            MessageDTO dto = MessageDTO.convertFromJson(li.next());
            processor.receiveMessage(dto);
        }
    }
}
