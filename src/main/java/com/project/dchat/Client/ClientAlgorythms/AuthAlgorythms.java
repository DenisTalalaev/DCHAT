package com.project.dchat.Client.ClientAlgorythms;

import com.project.dchat.Entities.Message;
import com.project.dchat.Entities.MessageType;
import com.project.dchat.Entities.TCPConnection;
import com.project.dchat.Entities.User;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class AuthAlgorythms {


    public static String formatEmail(String email) {
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        int nameLength = name.length();
        String formattedName = name.substring(0, 2) + "****" + name.substring(nameLength - 2, nameLength);
        return formattedName + "@" + domain;
    }

    public static boolean verificationHandler(TCPConnection connection, User user, TextField verificationCodeEdit, Text notificationText) {
        if(user.getActivationCode().equals(verificationCodeEdit.getText())) { //verification passed
            user.activate();
            connection.sendMessage(new Message(user, true, user)); // to server
            connection.sendMessage(new Message(MessageType.LOGIN_REQUEST, user, null)); // to user
            return true;
        } else {
            notificationText.setText("Вы ввели неверный код подтверждения.");
            return false;
        }
    }
}
