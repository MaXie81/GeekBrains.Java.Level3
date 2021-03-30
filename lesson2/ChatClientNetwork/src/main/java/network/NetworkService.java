package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetworkService {
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;

    public NetworkService(String address, int port, MessageService messageService) throws IOException {
        this.socket = new Socket(address, port);
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());

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
    }

    public void writeMessage(String msg) {
        try {
            outputStream.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
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

    public int getPort() {
        if (socket.isClosed()) return -1;
        return socket.getLocalPort();
    }
}
