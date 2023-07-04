package com.project.dchat.Client.ClientAlgorythms;

import com.project.dchat.Entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class ChatAlgorythms {


    public static void addStringMessage(Message message, VBox chatVBox, User user, ScrollPane chatScrollBox) {
        Platform.runLater(() -> {
            boolean shouldIScroll = chatScrollBox.getVmax() == chatScrollBox.getVvalue();


            VBox messageBox = new VBox();
            VBox.setMargin(messageBox, new Insets(5, 0, 0, 0));

            VBox container = new VBox();
            container.setPrefWidth(Control.USE_COMPUTED_SIZE);
            container.setMaxWidth(Control.USE_PREF_SIZE);
            VBox.setVgrow(container, Priority.ALWAYS);
            VBox messageData = new VBox();
            VBox.setVgrow(messageBox, Priority.NEVER);


            Label senderLabel = new Label(message.getSender().getUsername() + ": ");
            senderLabel.setStyle("-fx-font-weight: bold");
            Text messageLabel = new Text(new String(message.getData()));
            double textWidth = messageLabel.getLayoutBounds().getWidth();
            if (textWidth > 400) {
                if (textWidth < 1200) {
                    messageLabel.setWrappingWidth(Math.max(300, textWidth / 3 + 2));
                } else {
                    messageLabel.setWrappingWidth(400);
                }
            }
            if (user.getUsername().equalsIgnoreCase(message.getSender().getUsername())) { //me sent
                messageBox.setAlignment(Pos.TOP_RIGHT);
                container.setStyle("-fx-background-color: #cac7ff; -fx-background-radius: 15;");
                container.setPadding(new Insets(5, 10, 5, 5));
            } else { //another sent
                messageBox.setAlignment(Pos.TOP_LEFT);
                container.setStyle("-fx-background-color: #91baff; -fx-background-radius: 15;");
                container.setPadding(new Insets(5, 5, 5, 10));
            }

            messageData.getChildren().addAll(senderLabel, messageLabel);

            container.getChildren().add(messageData);
            messageBox.getChildren().add(container);

            chatVBox.getChildren().add(messageBox);

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                if (shouldIScroll || user.getUsername().equalsIgnoreCase(message.getSender().getUsername())) //me sent
                    chatScrollBox.setVvalue(chatScrollBox.getVmax());
            });
        });
    }

    public static void addNotification(Message message, VBox chatVBox, User user, ScrollPane chatScrollBox) {

        Platform.runLater(() -> {
            boolean shouldIScroll = chatScrollBox.getVmax() == chatScrollBox.getVvalue();
            VBox messageBox = new VBox();
            Label notice = new Label();
            notice.setText(new String(message.getData()));
            notice.setTextAlignment(TextAlignment.CENTER);
            messageBox.getChildren().add(notice);
            messageBox.setAlignment(Pos.CENTER);
            VBox.setVgrow(messageBox, Priority.NEVER);
            chatVBox.getChildren().add(messageBox);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                if (shouldIScroll || user.getUsername().equalsIgnoreCase(message.getSender().getUsername()))
                    chatScrollBox.setVvalue(chatScrollBox.getVmax());
            });
        });
    }

    public static void addCode(Message message, VBox chatVBox, User user, ScrollPane chatScrollBox, TextArea codeTextArea) {
        Platform.runLater(() -> {
            boolean shouldIScroll = chatScrollBox.getVmax() == chatScrollBox.getVvalue();

            Code recieveCode = new Code(message.getData());

            if (!containsNonWhitespace(recieveCode.getCode())) return;

            VBox messageBox = new VBox();
            VBox.setMargin(messageBox, new Insets(5, 0, 0, 0));

            VBox container = new VBox();
            container.setPrefWidth(Control.USE_COMPUTED_SIZE);
            container.setMaxWidth(Control.USE_PREF_SIZE);
            VBox.setVgrow(container, Priority.ALWAYS);
            VBox messageData = new VBox();
            VBox.setVgrow(messageBox, Priority.NEVER);

            Label senderLabel = new Label(message.getSender().getUsername() + ": ");
            senderLabel.setStyle("-fx-font-weight: bold");

            TextArea code = new TextArea();
            code.setEditable(false);
            code.setWrapText(true);
            code.setText(recieveCode.getCode());
            TextArea result = new TextArea();
            result.setWrapText(true);
            result.setEditable(false);

            HBox toolbar = new HBox(10);

            Button runButton = new Button();
            runButton.setText("run");
            runButton.setOnAction(event -> {
                result.setText(new Code(code.getText()).executePythonCode());
            });
            runButton.setStyle("-fx-font-size: 12px; -fx-background-color: #1e90ff; -fx-text-fill: white; -fx-padding: 4px 8px; -fx-border-radius: 6px;");

            Button copyCode = new Button();
            copyCode.setText("to redactor");
            copyCode.setOnAction(event -> {
                codeTextArea.setText(recieveCode.getCode());
            });
            copyCode.setStyle("-fx-font-size: 12px; -fx-background-color: #1e90ff; -fx-text-fill: white; -fx-padding: 4px 8px; -fx-border-radius: 6px;");


            toolbar.getChildren().addAll(runButton, copyCode);

            if (user.getUsername().equalsIgnoreCase(message.getSender().getUsername())) { //me sent
                messageBox.setAlignment(Pos.TOP_RIGHT);
                container.setStyle("-fx-background-color: #cac7ff; -fx-background-radius: 15;");
                container.setPadding(new Insets(5, 10, 5, 5));
            } else { //another sent
                messageBox.setAlignment(Pos.TOP_LEFT);
                container.setStyle("-fx-background-color: #91baff; -fx-background-radius: 15;");
                container.setPadding(new Insets(5, 5, 5, 10));
            }
            code.setPrefWidth(350);
            messageData.getChildren().addAll(senderLabel, code, result, toolbar);

            container.getChildren().add(messageData);
            messageBox.getChildren().add(container);

            chatVBox.getChildren().add(messageBox);


            Platform.runLater(() -> {
                if (shouldIScroll || user.getUsername().equalsIgnoreCase(message.getSender().getUsername())) //me sent
                    chatScrollBox.setVvalue(chatScrollBox.getVmax());
            });
        });
    }

    public static void addFile(Message message, VBox chatVBox, User user, ScrollPane chatScrollBox) {
        Platform.runLater(() -> {
            Platform.runLater(() -> {
                boolean shouldIScroll = chatScrollBox.getVmax() == chatScrollBox.getVvalue();

                VBox messageBox = new VBox();
                VBox.setMargin(messageBox, new Insets(5, 0, 0, 0));
                VBox.setVgrow(messageBox, Priority.ALWAYS);

                VBox container = new VBox();
                container.setPrefWidth(Control.USE_COMPUTED_SIZE);
                container.setMaxWidth(Control.USE_PREF_SIZE);
                VBox.setVgrow(container, Priority.NEVER);
                VBox messageData = new VBox();
                VBox.setVgrow(messageBox, Priority.ALWAYS);

                try {
                    createFile("D://DChat/" + user.getUsername() + "/" + message.getExtension(), message.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }



                Label senderLabel = new Label(message.getSender().getUsername() + ": ");
                senderLabel.setStyle("-fx-font-weight: bold");

                HBox fileToolbar = new HBox(10);
                Label fileName = new Label();
                fileName.setText(message.getExtension());

                Button open = new Button();
                open.setText("Показать файл");
                open.setOnAction((ActionEvent event) -> {
                    try {
//                    Desktop.getDesktop().open(new File("D:/DChat/" + user.getUsername() + "/" + message.getExtension()).getParentFile());
                        Desktop.getDesktop().open(new File("D:/DChat/" + user.getUsername()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                open.setStyle("-fx-font-size: 12px; -fx-background-color: #1e90ff; -fx-text-fill: white; -fx-padding: 4px 8px; -fx-border-radius: 6px;");

                fileToolbar.setAlignment(Pos.CENTER);

                fileToolbar.getChildren().addAll(fileName, open);

                if (user.getUsername().equalsIgnoreCase(message.getSender().getUsername())) { //me sent
                    messageBox.setAlignment(Pos.TOP_RIGHT);
                    container.setStyle("-fx-background-color: #cac7ff; -fx-background-radius: 15;");
                    container.setPadding(new Insets(5, 10, 5, 5));
                } else { //another sent
                    messageBox.setAlignment(Pos.TOP_LEFT);
                    container.setStyle("-fx-background-color: #91baff; -fx-background-radius: 15;");
                    container.setPadding(new Insets(5, 5, 5, 10));
                }

                messageData.getChildren().addAll(senderLabel, fileToolbar);

                container.getChildren().add(messageData);
                messageBox.getChildren().add(container);

                chatVBox.getChildren().add(messageBox);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    if (shouldIScroll || user.getUsername().equalsIgnoreCase(message.getSender().getUsername())) //me sent
                        chatScrollBox.setVvalue(chatScrollBox.getVmax());
                });

            });


        });


    }

    public static void addImage(Message message, VBox chatVBox, User user, ScrollPane chatScrollBox) {
        Platform.runLater(() -> {
            boolean shouldIScroll = chatScrollBox.getVmax() == chatScrollBox.getVvalue();

            VBox messageBox = new VBox();
            VBox.setMargin(messageBox, new Insets(5, 0, 0, 0));
            VBox.setVgrow(messageBox, Priority.ALWAYS);

            VBox container = new VBox();
            container.setPrefWidth(Control.USE_COMPUTED_SIZE);
            container.setMaxWidth(Control.USE_PREF_SIZE);
            VBox.setVgrow(container, Priority.NEVER);
            VBox messageData = new VBox();
            VBox.setVgrow(messageBox, Priority.ALWAYS);

            try {
                createFile("D://DChat/" + user.getUsername() + "/" + message.getExtension(), message.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }


            Button open = new Button();
            open.setText("Показать файл");
            open.setOnAction((ActionEvent event) -> {
                try {
//                    Desktop.getDesktop().open(new File("D:/DChat/" + user.getUsername() + "/" + message.getExtension()).getParentFile());
                    Desktop.getDesktop().open(new File("D:/DChat/" + user.getUsername()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            open.setStyle("-fx-font-size: 12px; -fx-background-color: #1e90ff; -fx-text-fill: white; -fx-padding: 4px 8px; -fx-border-radius: 6px;");

            Label senderLabel = new Label(message.getSender().getUsername() + ": ");
            senderLabel.setStyle("-fx-font-weight: bold");
            Image image = null;
            try {
                image = Algorythms.byteArrayToImage(message.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageView imageView = new ImageView(image);
            double scaleFactor = 200.0 / imageView.getImage().getWidth();
            imageView.setFitWidth(200);
            imageView.setFitHeight(imageView.getImage().getHeight() * scaleFactor);



            if (user.getUsername().equalsIgnoreCase(message.getSender().getUsername())) { //me sent
                messageBox.setAlignment(Pos.TOP_RIGHT);
                container.setStyle("-fx-background-color: #cac7ff; -fx-background-radius: 15;");
                container.setPadding(new Insets(5, 10, 5, 5));
            } else { //another sent
                messageBox.setAlignment(Pos.TOP_LEFT);
                container.setStyle("-fx-background-color: #91baff; -fx-background-radius: 15;");
                container.setPadding(new Insets(5, 5, 5, 10));
            }

            messageData.getChildren().addAll(senderLabel, imageView, open);

            container.getChildren().add(messageData);
            messageBox.getChildren().add(container);

            chatVBox.getChildren().add(messageBox);

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                if (shouldIScroll || user.getUsername().equalsIgnoreCase(message.getSender().getUsername())) //me sent
                    chatScrollBox.setVvalue(chatScrollBox.getVmax());
            });
        });
    }

    public static boolean containsNonWhitespace(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static void sendFile(TCPConnection connection, File file, User user) {
        if (isImage(file)) {
            try {
                Message message = new Message(new Image(new FileInputStream(file)), user, file);
                connection.sendMessage(message);
            } catch (Exception e) {
                Message message = new Message(file, user);
                connection.sendMessage(message);
            }
        } else {
            Message message = new Message(file, user);
            connection.sendMessage(message);
        }
    }

    public static boolean isImage(File file) {
        try {
            ImageIO.read(file);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void createFile(String path, byte[] data) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();
    }

}
