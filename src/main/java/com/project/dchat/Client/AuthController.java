package com.project.dchat.Client;

import com.project.dchat.Client.ClientAlgorythms.AuthAlgorythms;
import com.project.dchat.Client.ClientAlgorythms.RegisterAlgorythms;
import com.project.dchat.Config;
import com.project.dchat.Entities.Message;
import com.project.dchat.Entities.MessageType;
import com.project.dchat.Entities.TCPConnection;
import com.project.dchat.Entities.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthController extends Client {

    @FXML
    private GridPane fullPane;

    @FXML
    private Button loginButton;

    @FXML
    private Text notificationText;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField verificationCodeEdit;

    @FXML
    private Label verificationCodeLabel;

    private Stage stage;


    @FXML
    void forgetPasswordBtnClick(){
        if(usernameField.getText().length() == 0) {
            notificationText.setText("Введите имя пользователя для восстановления пароля");
            return;
        }
//        System.out.println("reset pass");
        User newUser = new User(usernameField.getText(), "", null);
        user = newUser;
        connection.sendMessage(new Message(MessageType.REMEMBER_PASSWORD, user));
    }

    @FXML
    private void initialize() {
        try {
            if (connection != null) connection.disconnect();
            connection = new TCPConnection(AuthController.this, Config.ip, Config.port);
            connection.setUser(user);
        } catch (IOException e) {
            verificationCodeEdit.setEditable(false);
            usernameField.setEditable(false);
            passwordField.setEditable(false);
            notificationText.setText("Сервер сейчас недоступен. Попробуйте подключиться позже");
        }
    }

    @FXML
    protected void switchToRegister(ActionEvent event) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("/com/project/dchat/register.fxml"));
        Scene loginScene = new Scene(loginParent, fullPane.getWidth(), fullPane.getHeight());
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setMinWidth(600);
        window.setScene(loginScene);
//        window.setResizable(false);
        window.setOnCloseRequest(windowEvent -> {
            if (connection != null)
                connection.disconnect();
        });
        window.show();
    }

    private enum Action {
        LOGIN,
        VERIFICATION,
        RESTORE_PASS
    }

    private static Action action = Action.LOGIN;


    @FXML
    protected void loginButtonClick(ActionEvent event) {
        if (stage == null) stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        switch (action) {
            case LOGIN: {
                User newUser = new User(usernameField.getText(), passwordField.getText(), null);
                user = newUser;
                connection.sendMessage(new Message(MessageType.LOGIN_REQUEST, user, null));
                break;
            }
            case VERIFICATION: {
//                System.out.println("User code " + user.getActivationCode());
//                System.out.println("Verification edit " + verificationCodeEdit.getText());
//                System.out.println("Comparation " + user.getActivationCode().equals(verificationCodeEdit.getText()));
                if (AuthAlgorythms.verificationHandler(connection, user, verificationCodeEdit, notificationText)) {
                    action = Action.LOGIN;
                    Parent loginParent = null;
                    try {
                        loginParent = FXMLLoader.load(getClass().getResource("/com/project/dchat/chat.fxml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Scene chatScene = new Scene(loginParent, 1400, 780);
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(chatScene);
                    window.setOnCloseRequest(windowEvent -> {
                        if (ChatController.connection != null) ChatController.connection.disconnect();
                    });
                    window.centerOnScreen();
                    window.show();
                }
                break;
            }
            case RESTORE_PASS: {
                if(verificationCodeEdit.getText().equalsIgnoreCase(user.getActivationCode())){
                    user.generateNewPassword();
                    action = Action.LOGIN;
                    connection.sendMessage(new Message(MessageType.RESET_PASSWORD, user));
                    Platform.runLater(() -> {
                        verificationCodeEdit.setVisible(false);
                        verificationCodeLabel.setVisible(false);
                        verificationCodeEdit.setText("");
                        passwordField.setText("");
                        notificationText.setText("Отправили вам на почту новый пароль.");
                        loginButton.setText("Войти");
                    });
                } else  {
                    notificationText.setText("Некорректный код подтверждения");
                }
                break;
            }
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection connection) {
    }

    @Override
    public synchronized void onRecieveMessage(TCPConnection connection, Message message) {
//        System.out.println(message);
        switch (message.getMessageType()) {
            case LOGIN_RESPONSE_NOT_VERIFIED: {
                Platform.runLater(() -> {
//                    System.out.println("Not verified");
                    action = Action.VERIFICATION;
                    User sender = message.getSender();
                    Platform.runLater(() -> {
                        loginButton.setText("Подтвердить");
                        verificationCodeEdit.setVisible(true);
                        verificationCodeLabel.setVisible(true);
                    });
                    user = sender;

                    user.generateActivationCode();
                    notificationText.setText("На почту " + AuthAlgorythms.formatEmail(sender.getMail()) + " отправлен код подтверждения. " +
                            "Если код не пришёл, проверьте папку 'спам'.");
                    RegisterAlgorythms.sendEmail(sender);
//                    System.out.println(user);
//                    System.out.println("Sender " + sender.getActivationCode());
//                    System.out.println("User " + user.getActivationCode());
                });
                break;
            }
            case LOGIN_RESPONSE_ACCESS_DENIED: {
                action = Action.LOGIN;
                notificationText.setText("Неверный логин или пароль");
                break;
            }
            case LOGIN_RESPONSE_ACCESS_GRANTED: {
                Platform.runLater(() -> {
                    Parent loginParent = null;
                    try {
                        loginParent = FXMLLoader.load(getClass().getResource("/com/project/dchat/chat.fxml"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Scene loginScene = new Scene(loginParent, 1400, 780);
                    Stage window = stage;
                    window.setScene(loginScene);
                    window.setOnCloseRequest(windowEvent -> {
                        if (ChatController.connection != null) ChatController.connection.disconnect();
                    });
                    window.centerOnScreen();
                    window.show();

                });
                break;
            }
            case REMEMBER_PASSWORD_RESPONSE: {
                verificationCodeEdit.setVisible(true);
                verificationCodeLabel.setVisible(true);
                user = message.getSender();
                user.generateActivationCode();
                action = Action.RESTORE_PASS;
                RegisterAlgorythms.sendEmail(user, "Код подтверждения восстановления пароля: \n" + user.getActivationCode());
                Platform.runLater(() -> {
                    loginButton.setText("Отправить пароль");
                    notificationText.setText("На почту " + AuthAlgorythms.formatEmail(user.getMail()) + " отправлен код подтверждения. " +
                            "Если код не пришёл, проверьте папку 'спам'.");
                });
                break;
            }
            case RESTORE_REQUEST_VERIFIED:{
                Platform.runLater(() -> {
                    verificationCodeEdit.setVisible(false);
                    verificationCodeEdit.setText("");
                    verificationCodeLabel.setVisible(false);
                    loginButton.setText("Войти");
                    action = Action.LOGIN;
                    user = message.getSender();
                    notificationText.setText("Вам на почту был выслан новый пароль. Если не пришло сообщение, проверьте папку 'спам'.");
                });
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
