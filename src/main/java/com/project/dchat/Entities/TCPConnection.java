package com.project.dchat.Entities;

import com.project.dchat.Client.ChatController;

import java.io.*;
import java.net.Socket;

public class TCPConnection implements Serializable{

    private User user;

    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnectionListener eventListener;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;


    public TCPConnection(TCPConnectionListener eventListener, String ip, int port) throws IOException {
        this(eventListener, new Socket(ip, port));
    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.user = null;
        this.socket = socket;
        this.eventListener = eventListener;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        rxThread = new Thread(new ThreadHandler());
        rxThread.start();
    }

    public TCPConnection(ChatController chatController, String s, int i, User user) throws IOException {
        this(chatController, s, i);
        this.user = user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public synchronized void sendMessage(Message message){
       try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        try {
            rxThread.interrupt();
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "TCPConnection{" +
                "socket=" + socket +
                ", rxThread=" + rxThread +
                ", eventListener=" + eventListener +
                ", in=" + in +
                ", out=" + out +
                '}';
    }

    private class ThreadHandler implements Runnable {
        @Override
        public void run() {
            try {
                eventListener.onConnectionReady(TCPConnection.this);
                while (!rxThread.isInterrupted()) {
                    try {
                        Object o = in.readObject();
                        eventListener.onRecieveMessage(TCPConnection.this, (Message) o);
                    } catch (IOException e) {
                        eventListener.onException(TCPConnection.this, e);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                eventListener.onDisconnect(TCPConnection.this);
            }
        }
    }

}
