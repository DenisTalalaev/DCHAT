package com.project.dchat.Entities;

public interface TCPConnectionListener {
    void onConnectionReady(TCPConnection connection);
    void onRecieveMessage(TCPConnection connection, Message message);
    void onDisconnect(TCPConnection connection);
    void onException(TCPConnection connection, Exception e);
}
