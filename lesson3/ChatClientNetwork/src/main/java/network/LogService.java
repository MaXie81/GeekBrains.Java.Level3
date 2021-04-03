package network;

import ru.geekbrains.messages.MessageDTO;
import ru.geekbrains.messages.MessageType;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;

public class LogService {
    private final String MESS_PREFIX = "ClientPort ";
    private final int portLocal;
    static final String path = "C:\\java\\Core3\\lesson3\\ChatClientNetwork\\src\\log/";
    static final String ext = ".log";
    static final String extBuf = ".buf";

    private File fileLog;
    private File fileBuf;

    private PrintWriter pw = null;

    private LinkedList<String> listBuf = new LinkedList<>();

    public LogService(int portLocal) {
        this.portLocal = portLocal;
    }

    private void log(String logStr) {
        System.out.println(getTime() + " " + MESS_PREFIX + portLocal + " " + logStr);
    }

    private void setLog(String login) {
        String fileName;

        fileName = path + login + ext;
        fileLog = new File(fileName);

        fileName = path + login + extBuf;
        fileBuf = new File(fileName);

        if (!fileLog.exists()) {
            try {
                fileLog.createNewFile();
                log("Create new log file " + fileLog.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            closePW(pw);
            pw = new PrintWriter(new FileWriter(fileLog, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        listBuf = getLogBuf();
    }

    private void logFile(MessageDTO dto, boolean flgReceive) {
        if (dto.getMessageType() == MessageType.SEND_AUTH_MESSAGE) {
            setLog(dto.getLogin());
        }
        if (pw == null) return;
        pw.println(getTime() + " " + (flgReceive ? "< " : "> ") + dto.convertToJson());
        pw.flush();
    }

    private void logBuf(MessageDTO dto) {
        listBuf.addLast(dto.convertToJson());
        if (listBuf.size() > 10) listBuf.removeFirst();
    }

    public void get(MessageDTO dto, boolean flgReceive) {
        logFile(dto, flgReceive);

        log((flgReceive ? "< " : "> ") + dto.getMessageType());
        if (flgReceive) {
            if (dto.getMessageType() == MessageType.ERROR_MESSAGE) log(dto.getBody());
            if (dto.getMessageType() == MessageType.DISCONNECT_MESSAGE) log(dto.getBody());
            if (dto.getMessageType() == MessageType.PRIVATE_MESSAGE) logBuf(dto);
            if (dto.getMessageType() == MessageType.PUBLIC_MESSAGE) logBuf(dto);
        } else {
            if (dto.getMessageType() == MessageType.SEND_AUTH_MESSAGE) {
                log(dto.getLogin() + "/" + dto.getPassword());
            }
            if (dto.getMessageType() == MessageType.CHANGE_NICK) {
                log("New nick: " + dto.getBody());
            }
        }
    }
    public void close() {
        closePW(pw);

        if (fileBuf.exists()) fileBuf.delete();

        try {
            fileBuf.createNewFile();
            pw = new PrintWriter(new FileWriter(fileBuf, true));
            ListIterator<String> li = listBuf.listIterator();
            while (li.hasNext()) {
                pw.println(li.next());
            }
            closePW(pw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public LinkedList<String> getLogBuf() {
        LinkedList<String> listMsg = new LinkedList<>();

        if (fileBuf.exists()) {
            String str;

            try (BufferedReader br = new BufferedReader(new FileReader(fileBuf))){
                while ((str = br.readLine()) != null) listMsg.addLast(str);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return listMsg;
    }
    private void closePW(PrintWriter pw) {
        if (pw == null) return;
        pw.flush();
        pw.close();
        pw = null;
    }
    private String getTime() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()).toString();
    }
}
