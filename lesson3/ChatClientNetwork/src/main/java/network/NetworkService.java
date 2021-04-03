package network;

import ru.geekbrains.messages.MessageType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkService {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public void writeMessage(String msg) {
        try {
            outputStream.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int openConnection(String address, int port, MessageService messageService) {
        if (socket == null || socket.isClosed()) {
            try {
                this.socket = new Socket(address, port);
                this.inputStream = new DataInputStream(socket.getInputStream());
                this.outputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        String msg = inputStream.readUTF();
                        messageService.receiveMessage(msg);
                    } catch (IOException e) {
//                    e.printStackTrace();
                        System.out.println("Connection close");
                        break;
                    }
                }
            });
            t.setDaemon(true);
            t.start();

            return socket.getLocalPort();
        } else {
            return -1;
        }
    }
    public void closeConnection() {
        try {
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
