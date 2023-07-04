package com.project.dchat.Client;

import com.project.dchat.Entities.TCPConnection;
import com.project.dchat.Entities.TCPConnectionListener;
import com.project.dchat.Entities.User;

public abstract class Client implements TCPConnectionListener {

    protected static TCPConnection connection;
    protected static User user;

}
