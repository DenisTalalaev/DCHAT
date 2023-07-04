package com.project.dchat.Server;

import com.project.dchat.Client.ClientAlgorythms.RegisterAlgorythms;
import com.project.dchat.Entities.*;
import com.project.dchat.controllerAlgorythms.BinaryDataController;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server implements TCPConnectionListener, Serializable {

    public static void main(String[] args) {
        connectionsFile = new File("D:/connections.bin");
        if (!connectionsFile.exists()) {
            try {
                connectionsFile.createNewFile();
                System.out.println("Файл создан: " + connectionsFile.getName());
            } catch (IOException e) {
                System.out.println("Ошибка при создании файла: " + e.getMessage());
            }
        }

        Server server = new Server();
    }

    private static File connectionsFile;

    ArrayList<TCPConnection> connections = new ArrayList<>();
    ArrayList<User> users = new ArrayList<>();

    private Server() {
        System.out.println("Starting the server...");
        try {
            users = BinaryDataController.loadDataFromFile(connectionsFile);
        } catch (Exception e) {
            users = new ArrayList<>();
        }
        try (ServerSocket serverSocket = new ServerSocket(3141)) {
            System.out.println("Server started");
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection connection) {
//        System.out.println(connection);
        connections.add(connection);
    }

    @Override
    public synchronized void onRecieveMessage(TCPConnection connection, Message message) {
//        System.out.println(Arrays.toString(users.toArray()));
        switch (message.getMessageType()) {
            case EXCISTS_REQUEST: {
                if(ServerAlgorythms.excistsRequestHandler(connection, users, message.getSender())) { // if not Excist
                    users.add(message.getSender());
                }
                break;
            }
            case ACTIVATE_ACCOUNT: {
                connection.setUser(message.getSender());
                message.getSender().activate();
                ServerAlgorythms.findOrAdd(message.getSender(), users);
                BinaryDataController.saveDataToFile(users, connectionsFile);
                break;
            }
            case LOGIN_REQUEST: {
                connection.setUser(message.getSender());
                ServerAlgorythms.loginHandler(users, message.getSender(), connection);
                break;
            }
            case STRING:
            case FILE:
            case CODE:
            case IMAGE:
            case NOTIFICATION: {
                sendToAllConnections(message);
                break;
            }
            case REMEMBER_PASSWORD: {

                if(ServerAlgorythms.isUserExcist(message.getSender(), users)) {
                    User user = ServerAlgorythms.findUser(message.getSender(), users);
                    connection.sendMessage(new Message(MessageType.REMEMBER_PASSWORD_RESPONSE, user));
                }
                break;
            }
            case RESET_PASSWORD:{
                Algorythms.resetPassword(message.getSender(), users);
                BinaryDataController.saveDataToFile(users, connectionsFile);
                RegisterAlgorythms.sendEmail(message.getSender(), "Сгенерировали для вас новый пароль: \n" + message.getSender().getPassword());
                break;
            }
            case CHANGE_PASSWORD: {
                int usr_index = ServerAlgorythms.findUserIndex(message.getSender(), users);
                users.get(usr_index).setPassword(message.getSender().getPassword());
                if(ServerAlgorythms.changePassword(message.getSender(), users))
                    connection.sendMessage(new Message(MessageType.CHANGE_PASSWORD_CHANGED, users.get(usr_index)));
            }
        }
    }

    @Override
    public synchronized void onDisconnect(TCPConnection connection) {
        connections.remove(connection);
        BinaryDataController.saveDataToFile(users, connectionsFile);
    }

    @Override
    public synchronized void onException(TCPConnection connection, Exception e) {
//        e.printStackTrace();
        connection.disconnect();
    }

    private void sendToAllConnections(Message message) {
        for (TCPConnection connection : connections
        ) {
            connection.sendMessage(message);
        }
    }

}
