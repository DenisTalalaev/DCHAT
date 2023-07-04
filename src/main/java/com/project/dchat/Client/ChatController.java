package com.project.dchat.Client;

import com.project.dchat.Client.ClientAlgorythms.ChatAlgorythms;
import com.project.dchat.Client.ClientAlgorythms.MessageController;
import com.project.dchat.Client.ClientAlgorythms.RegisterAlgorythms;
import com.project.dchat.Config;
import com.project.dchat.Entities.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChatController extends Client {


    @FXML
    private Button attachFileButton;

    @FXML
    private Button changePass;

    @FXML
    private ScrollPane chatScrollBox;

    @FXML
    private VBox chatVBox;

    @FXML
    private BorderPane codeRedactorPane;

    @FXML
    private TextArea codeResultTextArea;

    @FXML
    private TextArea codeTextArea;

    @FXML
    private Button exitButton;

    @FXML
    private BorderPane fullPane;

    @FXML
    private BorderPane messageInterfacePane;

    @FXML
    private TextArea messageTextField;

    @FXML
    private PasswordField newPass;

    @FXML
    private PasswordField oldPassword;

    @FXML
    private Button runCOdeButton;

    @FXML
    private Button sendButton;

    @FXML
    private Button sendCodeButton;

    @FXML
    private Label usernameLabel;

    @FXML
    private PasswordField verifyPass;


    Tooltip tooltip = new Tooltip("Подсказка");

    @FXML
    void changePasswordButtonClick(){
        oldPassword.setStyle(null);
        newPass.setStyle(null);
        verifyPass.setStyle(null);
        if(user.getPassword().equals(oldPassword.getText())) {
            if(newPass.getText().equals(verifyPass.getText())) {
                if(newPass.getText().length() < 4) {
                    newPass.setStyle(RegisterAlgorythms.style);
                    verifyPass.setStyle(RegisterAlgorythms.style);
                    tooltip.setText("Пароль слишком короткий");
                    tooltip.show(Main.mainStage);
                    PauseTransition delay = new PauseTransition(Duration.seconds(3));
                    delay.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            tooltip.hide();
                        }
                    });
                    delay.play();
                } else {
                    user.setPassword(newPass.getText());
                    connection.sendMessage(new Message(MessageType.CHANGE_PASSWORD, user));
                }
            } else  {
                newPass.setStyle(RegisterAlgorythms.style);
                verifyPass.setStyle(RegisterAlgorythms.style);
                tooltip.setText("Пароли не совпадают");
                tooltip.show(Main.mainStage);

                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        tooltip.hide();
                    }
                });
                delay.play();
            }
        } else {
            oldPassword.setStyle(RegisterAlgorythms.style);
            tooltip.setText("Неверный текущий пароль");
            tooltip.show(Main.mainStage);

            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    tooltip.hide();
                }
            });
            delay.play();
        }
    }

    @FXML
    void sendCodeButtonClick() {
        if(codeTextArea.getText().length() > 0)
            connection.sendMessage(new Message(new Code(codeTextArea.getText()), user));
    }

    @FXML
    void runCodeButtonClick() {
        codeResultTextArea.setText(new Code(codeTextArea.getText()).executePythonCode());
    }

    @FXML
    void attachFileButtonClick(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(Main.mainStage);
        if(file != null) {
            ChatAlgorythms.sendFile(connection, file, user);
        }
    }

    @FXML
    protected void onDragDropDropped(DragEvent event){
        if(event.getDragboard().hasFiles()){
            List<File> files = event.getDragboard().getFiles();
            if (files.size() > 0) {
                File file = files.get(0);
                connection.sendMessage(new Message(file, user));
            }
        }
    }

    @FXML
    protected void onCodeFileDragDropped(DragEvent event){
        if(event.getDragboard().hasFiles()){
            List<File> files = event.getDragboard().getFiles();
            if (files.size() > 0) {
                File file = files.get(0);
                if(Algorythms.getFileExtension(file).equalsIgnoreCase("py") || Algorythms.getFileExtension(file).equalsIgnoreCase("txt")) {
                    try {
                        codeTextArea.setText(Algorythms.readFileToString(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    @FXML
    protected void onDragDropEntered(DragEvent event){
        if(event.getDragboard().hasFiles()){
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    void onMessageFieldChange(KeyEvent keyEvent) {
        if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.ENTER)) {
            sendButtonClick();
        }
        int numLines = messageTextField.getText().split("\n").length;
        double width = messageTextField.getWidth() - 2 * messageTextField.getPadding().getLeft();
        double lineWidth = messageTextField.getFont().getSize() * 0.6;
        for (String line : messageTextField.getText().split("\n")) {
            int linesToAdd = (int) Math.ceil(line.length() * lineWidth / width);
            numLines += linesToAdd - 1;
        }
        if(numLines > 1) {
            messageTextField.setPrefHeight(120);
        } else {
            messageTextField.setPrefHeight(30);
        }
    }

    @FXML
    void exitButtonClick(ActionEvent event) throws IOException {
        connection.sendMessage(new Message(MessageType.NOTIFICATION, user.getUsername() + " disconnected", true, user));
        if(ChatController.connection != null) ChatController.connection.disconnect();
        Parent loginParent = FXMLLoader.load(getClass().getResource("/com/project/dchat/auth.fxml"));
        Scene loginScene = new Scene(loginParent, fullPane.getWidth(), fullPane.getHeight());
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setOnCloseRequest(event1 -> {
            if(AuthController.connection != null) AuthController.connection.disconnect();
        });
        window.setScene(loginScene);
        window.show();
    }

    @FXML
    void sendButtonClick() {
        if (messageTextField.getText().length() == 0) return;
        connection.sendMessage(MessageController.generateMessage(messageTextField.getText(), connection.getUser()));
        messageTextField.setText("");
    }

    @FXML
    private void initialize() {
        try {
            if (connection != null) connection.disconnect();
            connection = new TCPConnection(ChatController.this, Config.ip, Config.port, user);
            usernameLabel.setText(connection.getUser().getUsername());
        } catch (IOException e) {
        }
        chatScrollBox.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        messageTextField.setOnKeyPressed(this::onMessageFieldChange);
        Tooltip tooltip = new Tooltip("ctrl + enter");
        tooltip.setStyle("-fx-font-size: 12px;");
        sendButton.setTooltip(tooltip);
        VBox.setVgrow(chatVBox, Priority.NEVER);
        Platform.runLater(() -> {
            connection.sendMessage(new Message(MessageType.NOTIFICATION, user.getUsername() + " connected", true, user));
            sendCodeButton.setStyle("-fx-font-size: 12px; -fx-background-color: #1e90ff; -fx-text-fill: white; -fx-padding: 4px 8px; -fx-border-radius: 6px;");
            runCOdeButton.setStyle("-fx-font-size: 12px; -fx-background-color: #1e90ff; -fx-text-fill: white; -fx-padding: 4px 8px; -fx-border-radius: 6px;");

        });


        tooltip.setHideDelay(Duration.seconds(3));
        tooltip.hideDelayProperty().setValue(Duration.seconds(3));
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection connection) {
    }

    @Override
    public synchronized void onRecieveMessage(TCPConnection tcpConnection, Message message) {
        user = message.getSender();
        switch (message.getMessageType()) {
            case STRING: {
                ChatAlgorythms.addStringMessage(message, chatVBox, connection.getUser(), chatScrollBox);
                break;
            }
            case NOTIFICATION: {
                ChatAlgorythms.addNotification(message, chatVBox, connection.getUser(), chatScrollBox);
                break;
            }
            case CODE: {
                ChatAlgorythms.addCode(message, chatVBox, connection.getUser(), chatScrollBox, codeTextArea);
                break;
            }
            case FILE: {
                ChatAlgorythms.addFile(message, chatVBox, connection.getUser(), chatScrollBox);
                break;
            }
            case IMAGE: {
                ChatAlgorythms.addImage(message, chatVBox, connection.getUser(), chatScrollBox);
            }
            case CHANGE_PASSWORD_CHANGED: {
                Platform.runLater(() -> {
                    oldPassword.setText("");
                    newPass.setText("");
                    verifyPass.setText("");
                    tooltip.setText("Пароль успешно изменён");
                    tooltip.show(Main.mainStage);

                    PauseTransition delay = new PauseTransition(Duration.seconds(3));
                    delay.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            tooltip.hide();
                        }
                    });
                    delay.play();
                });

                RegisterAlgorythms.sendEmail(user, "Пароль от вашего аккаунта был изменён. Если это сделали не вы, рекомендуем как можно скорее восстановить пароль.");
                break;
            }

        }
    }

    @Override
    public synchronized void onDisconnect(TCPConnection connection) {
    }

    @Override
    public synchronized void onException(TCPConnection connection, Exception e) {
        connection.disconnect();
    }
}
