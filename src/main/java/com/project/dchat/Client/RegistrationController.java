package com.project.dchat.Client;

import com.project.dchat.Client.ClientAlgorythms.RegisterAlgorythms;
import com.project.dchat.Config;
import com.project.dchat.Entities.Message;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;


public class RegistrationController extends Client {


    @FXML
    private GridPane configPane;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private GridPane fullPane;

    @FXML
    private Label ipLabel;

    @FXML
    private TextField ipTextField;

    @FXML
    private TextField mailField;

    @FXML
    private Text notificationLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label portLabel;

    @FXML
    private TextField portTextField;

    @FXML
    private Button registerButton;

    @FXML
    private VBox registrationPane;

    @FXML
    private Button reloadButton;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField verificationCodeEdit;

    @FXML
    private Label verificationCodeLabel;


    @FXML
    void reloadButtonClick(ActionEvent event) {
        try {
            if(connection != null) connection.disconnect();
            if(RegisterAlgorythms.isValidIPAddress(ipTextField.getText())){
                Config.ip = ipTextField.getText();
            }
            if(RegisterAlgorythms.isValidPort(portTextField.getText())){
                Config.port = Integer.parseInt(portTextField.getText());
            }
            connection = new TCPConnection(RegistrationController.this, Config.ip, Config.port);
            user = null;
            registrationPane.setVisible(true);
            notificationLabel.setText("");
            reloadButton.setVisible(false);
            configPane.setVisible(false);
        } catch (IOException e) {
            registrationPane.setVisible(false);
            notificationLabel.setText("Сервер сейчас недоступен. Попробуйте подключиться позже");
        }
    }


    @FXML
    private void initialize() {
        try {
            if(connection != null) connection.disconnect();
                connection = new TCPConnection(RegistrationController.this, Config.ip, Config.port);
            user = null;
        } catch (IOException e) {
            configPane.setVisible(true);
            ipTextField.setText(Config.ip);
            portTextField.setText(String.valueOf(Config.port));
            registrationPane.setVisible(false);
            notificationLabel.setText("Сервер сейчас недоступен. Попробуйте подключиться позже");
            reloadButton.setVisible(true);
        }
    }


    @FXML
    private void switchToLogin(ActionEvent event) throws IOException {
        Parent loginParent = FXMLLoader.load(getClass().getResource("/com/project/dchat/auth.fxml"));
        Scene loginScene = new Scene(loginParent, fullPane.getWidth(), fullPane.getHeight());
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setOnCloseRequest(event1 -> {
            if(AuthController.connection != null) AuthController.connection.disconnect();
        });
        window.setScene(loginScene);
        window.show();
    }

    private enum Action {
        REGISTRATION,
        VERIFICATION
    }

    private Action action = Action.REGISTRATION;

    @FXML
    private void registerBtnClick(ActionEvent event) {
        switch (action) {
            case REGISTRATION: {
                registrationAction(event);
                break;
            }
            case VERIFICATION: {
                verificationAction(event);
                break;
            }
        }
    }

    private void verificationAction(ActionEvent event) {
//        System.out.println(user.getActivationCode());
        if (verificationCodeEdit.getText().equals(user.getActivationCode())) {
            user.activate();
            connection.sendMessage(new Message(user, user.isActive(), user));
            try {
                switchToLogin(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            verificationCodeEdit.setStyle(RegisterAlgorythms.style);
            notificationLabel.setText("Неверный код подтверждения");
        }
    }

    private void registrationAction(ActionEvent event) {
        if (!RegisterAlgorythms.areFieldsNotNull(usernameField, passwordField, confirmPasswordField, mailField, notificationLabel))
            return;
        if (RegisterAlgorythms.isValidName(usernameField, notificationLabel)
                & RegisterAlgorythms.arePasswordsMatch(passwordField, confirmPasswordField, notificationLabel)
                & RegisterAlgorythms.isValidEmail(mailField, notificationLabel)) {
            user = new User(
                    usernameField.getText(),
                    passwordField.getText(),
                    mailField.getText());
            connection.setUser(user);
            connection.sendMessage(new Message(user, user)); //EXCISTS_REQUEST
        }
    }


    private void setVerificationInterfaceVisible(boolean visible) {
        verificationCodeLabel.setVisible(visible);
        verificationCodeEdit.setVisible(visible);
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection connection) {
    }

    @Override
    public synchronized void onRecieveMessage(TCPConnection connection, Message message) {
        switch (message.getMessageType()) {
            case EXCISTS_RESPONSE: {
                if (RegisterAlgorythms.validateRegistration(connection, message)) {
                    user.generateActivationCode();
                    setVerificationInterfaceVisible(true);
                    action = Action.VERIFICATION;
                    Platform.runLater(() -> {
                        registerButton.setText("Подтвердить");
                        notificationLabel.setText("На указанную почту отправлено письмо с кодом подтверждения. Если письмо не пришло проверьте папку 'спам'.");
                    });
                    RegisterAlgorythms.sendEmail(user);
                } else {
                    action = Action.REGISTRATION;
                    Platform.runLater(() -> {
                        notificationLabel.setText("Данный логин или почта уже используются");
                    });
                }
            }
        }
    }

    @Override
    public synchronized void onDisconnect(TCPConnection connection) {
        user = null;
    }

    @Override
    public synchronized void onException(TCPConnection connection, Exception e) {
        connection.disconnect();
    }
}