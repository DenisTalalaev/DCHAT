package com.project.dchat.Server;

import com.project.dchat.Entities.Message;
import com.project.dchat.Entities.MessageType;
import com.project.dchat.Entities.TCPConnection;
import com.project.dchat.Entities.User;

import java.util.ArrayList;

public class ServerAlgorythms {
    public static boolean excistsRequestHandler(TCPConnection connection, ArrayList<User> users, User sender) {
        for (User user : users
        ) {
            if (user.subEquals(sender)) {
//                System.out.println(sender.getUsername() + " excists");
                connection.sendMessage(new Message(true, sender));
                return false;
            }
        }
        connection.sendMessage(new Message(false, sender));
//                System.out.println(sender.getUsername() + " not excists");
        return true;
    }

    public static void loginHandler(ArrayList<User> users, User sender, TCPConnection connection) {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getUsername().equals(sender.getUsername())) { //login matched
                if (user.getPassword().equals(sender.getPassword())) { //password matched
                    if (user.isActive()) {
//                        System.out.println(user.getUsername() + " access granted");
                        connection.sendMessage(new Message(MessageType.LOGIN_RESPONSE_ACCESS_GRANTED, user));
                    } else {
//                        System.out.println(user.getUsername() + " not verified");
                        connection.sendMessage(new Message(MessageType.LOGIN_RESPONSE_NOT_VERIFIED, user));
                    }
                    return;
                }
            }
        }
//        System.out.println(sender.getUsername() + " access denied");
        connection.sendMessage(new Message(MessageType.LOGIN_RESPONSE_ACCESS_DENIED, sender));
    }

    public static void findOrAdd(User sender, ArrayList<User> users) {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if(user.loginAccess(sender)) {
                user.activate();
                return;
            }
        }
        sender.activate();
        users.add(sender);
    }

    public static boolean isUserExcist(User sender, ArrayList<User> users) {
        for (User user: users
             ) {
            if(user.getUsername().equals(sender.getUsername()))
                return true;
        }
        return false;
    }

    public static User findUser(User sender, ArrayList<User> users) {
        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).getUsername().equals(sender.getUsername())){
                return  users.get(i);
            }
        }
        return null;
    }

    public static boolean changePassword(User sender, ArrayList<User> users) {
        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).subEquals(sender)) {
                users.get(i).setPassword(sender.getPassword());
                return true;
            }
        }
        return false;
    }

    public static int findUserIndex(User sender, ArrayList<User> users) {
        for (int i = 0; i < users.size(); i++) {
            if(users.get(i).getUsername().equals(sender.getUsername())){
                return i;
            }
        }
        return -1;
    }
}
