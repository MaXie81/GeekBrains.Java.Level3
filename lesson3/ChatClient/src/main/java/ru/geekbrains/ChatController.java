package ru.geekbrains;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import network.ChatMessageService;
import network.MessageProcessor;
import network.MessageService;
import ru.geekbrains.messages.MessageDTO;
import ru.geekbrains.messages.MessageType;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable, MessageProcessor {

    private final String ALL = "SEND TO ALL";

    private String nameNick;
    private MessDTO mess = new MessDTO();

    @FXML
    public TextArea chatArea;
    @FXML
    public ListView onlineUsers;
    @FXML
    public Button btnSendMessage;
    @FXML
    public TextField input;
    @FXML
    public HBox chatBox;
    @FXML
    public HBox inputBox;
    @FXML
    public MenuBar menuBar;
    @FXML
    public HBox authPanel;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passField;
    @FXML
    public HBox changePanel;
    @FXML
    public TextField newNick;

    private MessageService messageService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageService = new ChatMessageService("localhost", 65500, this);
    }

    @Override
    public void receiveMessage(MessageDTO dto) {
        Platform.runLater(() -> {mess.set(dto);});
    }

    public void exit(ActionEvent actionEvent) {
        messageService.sendMessage(mess.getDisconn());
        messageService.disconnectFromServer();
        Platform.exit();
    }

    public void btnSend(ActionEvent actionEvent) {
        messageService.sendMessage(mess.getMess());
        input.clear();
    }
    public void pressEnter(ActionEvent actionEvent) {
        messageService.sendMessage(mess.getMess());
        input.clear();
    }

    public void btnConnect(ActionEvent actionEvent) {
        messageService.connectToServer();
        messageService.sendMessage(mess.getAuth());
    }

    public void btnChange(ActionEvent actionEvent) {
        messageService.sendMessage(mess.getNick());
        newNick.clear();
    }

    class MessDTO {
        void set(MessageDTO dto) {
            switch (dto.getMessageType()) {
                case PUBLIC_MESSAGE :
                    setMess(dto);
                    break;
                case PRIVATE_MESSAGE :
                    setMess(dto);
                    break;
                case CLIENTS_LIST_MESSAGE :
                    setList(dto);
                    break;
                case AUTH_CONFIRM :
                    setAuth(dto);
                    break;
                case ERROR_MESSAGE :
                    setError(dto);
                    break;
                case DISCONNECT_MESSAGE :
                    setDisconn(dto);
                    break;
            }
        }
        void setAuth(MessageDTO dto) {
            nameNick = dto.getBody();
            setActive(true);
        }
        void setMess(MessageDTO dto) {
            String msg = String.format("[%s] [%s] -> %s\n", dto.getMessageType(), dto.getFrom(), dto.getBody());
            chatArea.appendText(msg);
        }
        void setList(MessageDTO dto) {
            dto.getUsersOnline().add(0, ALL);
            onlineUsers.setItems(FXCollections.observableArrayList(dto.getUsersOnline()));
            onlineUsers.getSelectionModel().selectFirst();
        }
        void setDisconn(MessageDTO dto) {
            messageService.disconnectFromServer();
            setActive(false);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Something went wrong!");
            alert.setHeaderText(dto.getMessageType().toString());
            VBox dialog = new VBox();
            Label label = new Label("Trace:");
            TextArea textArea = new TextArea();
            textArea.setText(dto.getBody());
            dialog.getChildren().addAll(label, textArea);
            alert.getDialogPane().setContent(dialog);
            alert.showAndWait();
        }
        void setError(MessageDTO dto) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Something went wrong!");
            alert.setHeaderText(dto.getMessageType().toString());
            VBox dialog = new VBox();
            Label label = new Label("Trace:");
            TextArea textArea = new TextArea();
            textArea.setText(dto.getBody());
            dialog.getChildren().addAll(label, textArea);
            alert.getDialogPane().setContent(dialog);
            alert.showAndWait();
        }

        MessageDTO getAuth() {
            String log = loginField.getText();
            String pass = passField.getText();
            if (log.equals("") || pass.equals("")) return null;

            MessageDTO dto = new MessageDTO();
            dto.setLogin(log);
            dto.setPassword(pass);
            dto.setMessageType(MessageType.SEND_AUTH_MESSAGE);

            return dto;
        }
        MessageDTO getMess() {
            String msg = input.getText();
            if (msg.length() == 0) return null;

            MessageDTO dto = new MessageDTO();
            String selected = (String) onlineUsers.getSelectionModel().getSelectedItem();

            if (selected.equals(ALL))
                dto.setMessageType(MessageType.PUBLIC_MESSAGE);
            else {
                dto.setMessageType(MessageType.PRIVATE_MESSAGE);
                dto.setTo(selected);
            }

            dto.setBody(msg);
            
            return dto;
        }
        MessageDTO getDisconn() {
            MessageDTO dto = new MessageDTO();
            dto.setMessageType(MessageType.DISCONNECT_MESSAGE);
            dto.setBody("Exit");

            return dto;
        }
        MessageDTO getNick() {
            if (nameNick.equals(newNick.getText())) return null;

            MessageDTO dto = new MessageDTO();
            dto.setMessageType(MessageType.CHANGE_NICK);
            dto.setBody(newNick.getText());

            return dto;
        }
    }
    private void setActive(boolean flg) {
        if (flg) {
            ChatApp.getStage().setTitle(nameNick);
            chatArea.clear();
            messageService.getChatHistory();
        } else {
            passField.clear();
            nameNick = "";
            ChatApp.getStage().setTitle("My chat");

        }
        authPanel.setVisible(!flg);
        changePanel.setVisible(flg);
        chatBox.setVisible(flg);
        inputBox.setVisible(flg);
        menuBar.setVisible(flg);
    }
}
