package com.project.dchat.Client.ClientAlgorythms;

import com.project.dchat.Entities.TCPConnection;
import com.project.dchat.Entities.User;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterAlgorythms {

    public static String style = "-fx-effect: dropshadow(three-pass-box, red, 10, 0, 0, 0);";

    public static boolean isValidEmail(TextField email, Text notificationLabel) {
        String regex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email.getText());
        boolean res = email.getText().length() > 2 & matcher.matches();
        if (res) {
            email.setStyle(null);
        } else {
            email.setStyle(style);
            notificationLabel.setText(notificationLabel.getText() + "Некорректный адрес почты\n");
        }
        return res;
    }

    public static boolean isValidPort(String port) {
        try {
            int portNumber = Integer.parseInt(port);
            if (portNumber >= 0 && portNumber <= 65535) {
                return true;
            }
        } catch (NumberFormatException e) {}

        return false;
    }


    public static boolean isValidIPAddress(String ipAddress) {
        String[] octets = ipAddress.split("\\.");

        // IP-адрес должен содержать 4 октета
        if (octets.length != 4) {
            return false;
        }

        try {
            // Проверка каждого октета на наличие в диапазоне от 0 до 255
            for (String octet : octets) {
                int octetValue = Integer.parseInt(octet);
                if (octetValue < 0 || octetValue > 255) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }


    public static boolean isValidName(TextField usernameField, Text notificationLabel) {
        String regex = "[a-zA-Z_$][a-zA-Z0-9_$]*";
        Pattern pattern = Pattern.compile(regex);
        boolean res = pattern.matcher(usernameField.getText()).matches() & usernameField.getText().length() > 2 & usernameField.getText().length() <= 20;
        if (res) {
            usernameField.setStyle(null);
        } else {
            usernameField.setStyle(style);
            if (usernameField.getText().length() <= 2)
                notificationLabel.setText(notificationLabel.getText() + "Имя пользователя слишком короткое\n");
            else if (usernameField.getText().length() > 20)
                notificationLabel.setText(notificationLabel.getText() + "Имя пользователя слишком длинное\n");
            else
                notificationLabel.setText(notificationLabel.getText() + "Некорректное имя пользователя\n");
        }
        return res;
    }

    public static boolean arePasswordsMatch(PasswordField pass1, PasswordField pass2, Text notificationLabel) {
        boolean res = pass1.getText().length() > 3
                & pass1.getText().equals(pass2.getText());
        if (res) {
            pass1.setStyle(null);
            pass2.setStyle(null);

        } else {
            pass1.setStyle(style);
            pass2.setStyle(style);
            if (pass1.getText().length() < 4)
                notificationLabel.setText(notificationLabel.getText() + "Пароль не может быть короче 4 символов\n");
            else
                notificationLabel.setText(notificationLabel.getText() + "Пароли не совпадают\n");
        }
        return res;
    }


    public static boolean sendEmail(User user, String s) {
        // Настройки SMTP-сервера

        String host = "smtp.beget.com";
        String port = "2525";
        String username = "ksis@dtalalaev.site";
        String password = "*****************";

        // Настройки свойств для создания сессии
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        String confirmationMessage = s;

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getMail()));
            message.setSubject("DChat уведомление");
            message.setContent(confirmationMessage, "text/html; charset=utf-8");

            // Отправляем сообщение
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean areFieldsNotNull(TextField usernameField, PasswordField passwordField, PasswordField confirmPasswordField, TextField mailField, Text notificationLabel) {
        boolean res = true;
        if (usernameField.getText().length() == 0) {
            usernameField.setStyle(style);
            res = false;
        }

        if (passwordField.getText().length() == 0) {
            passwordField.setStyle(style);
            res = false;
        }

        if (confirmPasswordField.getText().length() == 0) {
            confirmPasswordField.setStyle(style);
            res = false;
        }
        if (mailField.getText().length() == 0) {
            mailField.setStyle(style);
            res = false;
        }
        notificationLabel.setText(res ? "" : "Заполните все поля");
        return res;
    }

    public static boolean validateRegistration(TCPConnection connection, com.project.dchat.Entities.Message message) {
        if (message.getData() == null) return true;
        return false;
    }

    public static void sendEmail(User user) {
        String confirmationMessage = "<html><body style=\"font-family: Arial, sans-serif;\">" +
                "<h2>Добро пожаловать в DChat, " + user.getUsername() + "!</h2>" +
                "<p>Спасибо за регистрацию в нашем приложении. Ваш код подтверждения:<br> " +
                "<strong>" + user.getActivationCode() + "</strong></p>" +
                "<p>Код подтверждения действует 5 минут.</p>" +
                "<p>С уважением, DChat.</p>" +
                "</body></html>";
        sendEmail(user, confirmationMessage);
    }
}
