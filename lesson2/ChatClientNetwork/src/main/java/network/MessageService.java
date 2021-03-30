package network;

import ru.geekbrains.messages.MessageDTO;

public interface MessageService {
    void sendMessage(MessageDTO dto);
    void receiveMessage(String msg);
    void connectToServer();
    void disconnectFromServer();
}
