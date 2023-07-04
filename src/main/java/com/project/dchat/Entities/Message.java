package com.project.dchat.Entities;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Message implements Serializable{
    private MessageType messageType;
    private String extension;
    private byte[] data;
    private User sender = null;
    private User receiver = null;

    public Message(String string, User sender){
        this.messageType = MessageType.STRING;
        this.data = string.getBytes(StandardCharsets.UTF_8);
        this.extension = "strMsg";
        this.sender = sender;
    }

    public Message(Image image, User sender, File file) throws IOException {
        this.messageType = MessageType.IMAGE;
        this.data = Algorythms.imageToByteArray(image, Algorythms.getFileExtension(file));
        extension = file.getName();
        this.sender = sender;
    }

    public Message(MessageType type, String notification, boolean isServerNotice, User sender) {
        this.messageType = type;
        this.data = notification.getBytes(StandardCharsets.UTF_8);
        extension = "str";
        this.sender = sender;
    }

    public Message(File file, User sender) {
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            this.data = fileInputStream.readAllBytes();
            this.messageType = MessageType.FILE;
            this.extension = file.getName();
            this.sender = sender;
        } catch (IOException e) {}
    }

    public Message(Code code, User sender){
        this.messageType = MessageType.CODE;
        this.data = code.getCode().getBytes(StandardCharsets.UTF_8);
        this.extension = "py";
        this.sender = sender;
    }

    public Message(User user, User sender) {
        this.messageType = MessageType.EXCISTS_REQUEST;
        this.data = user.getUsername().getBytes(StandardCharsets.UTF_8);
        this.extension = "request";
        this.sender = sender;
    }

    public Message(boolean isExcist, User sender) {
        this.messageType = MessageType.EXCISTS_RESPONSE;
        this.data = isExcist?new byte[1]: null;
        this.extension = "request";
        this.sender = sender;
    }

    public Message(User user, boolean active, User sender) {
        this.messageType = MessageType.ACTIVATE_ACCOUNT;
        this.data = null;
        this.extension = null;
        this.sender = sender;
    }

    public Message(MessageType loginRequest, User sender, String notActual){
        this.messageType = MessageType.LOGIN_REQUEST;
        this.sender = sender;
        this.data = null;
        this.extension = notActual;
    }

    public Message(MessageType response, User sender){
        this.messageType = response;
        this.extension = "loginResponse";
        this.data = null;
        this.sender = sender;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getExtension() {
        return extension;
    }

    public byte[] getData() {
        return data;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageType=" + messageType +
                ", extension='" + extension + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
